package com.course.selection.service.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.exception.entity.courseSelection.SelectCourseSelectionException;
import com.common.exception.entity.publishCourse.PublishCourseException;
import com.common.rabbitmq.rabbitMQContent.CourseSelectMQContent;
import com.common.rabbitmq.rabbitMQContent.PublishCourseMQContent;
import com.common.rabbitmq.rabbitMQContent.SelectionCourseMQContent;
import com.common.response.entity.BaseResponse;
import com.common.response.entity.BaseResponseUtil;
import com.common.userInfo.context.UserContext;
import com.common.utils.JsonSerialization;
import com.common.utils.JsonUtil;
import com.course.selection.service.entity.CourseSelectionEntity;
import com.course.selection.service.feignClients.UserManagementClients;
import com.course.selection.service.repository.CourseSelectionEntityRepository;
import com.course.selection.service.service.CourseSelection;
import com.publish.data.entity.CourseEntity;
import com.user.data.entity.College;
import com.user.data.entity.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings("all")
@RequiredArgsConstructor
public class CourseSelectionImpl extends ServiceImpl<CourseSelectionEntityRepository, CourseSelectionEntity>
        implements CourseSelection {
    @Autowired
    private final UserManagementClients userManagementClients;
    private final RabbitTemplate rabbitTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final CourseSelectionEntityRepository courseSelectionEntityRepository;

    @RabbitListener(queues = PublishCourseMQContent.QUEUE_NAME)
    @DSTransactional(rollbackFor = Exception.class)
    public void publishCourse(CourseEntity courseEntity, Message message) throws PublishCourseException {
        try {
            if (courseEntity == null) {
                log.error("RabbitMQ接收到空消息");
                return;
            }
            String action = message.getMessageProperties().getHeader("action");
            if ("revoke".equals(action)) {
                log.info("管理员撤回选课id{}课程名字{}", courseEntity.getId(), courseEntity.getCourseName());
                courseSelectionEntityRepository.delete(new LambdaQueryWrapper<CourseSelectionEntity>()
                        .eq(CourseSelectionEntity::getId, courseEntity.getCourseId()));
                rabbitTemplate.convertAndSend(SelectionCourseMQContent.EXCHANGE_NAME, SelectionCourseMQContent.ROUTING_KEY,
                        courseEntity.getCourseId());
            } else {
                log.info("选课id{}课程名字{}选课已结束", courseEntity.getId(), courseEntity.getCourseName());
                courseSelectionEntityRepository.deleteById(courseEntity.getId());
                rabbitTemplate.convertAndSend(SelectionCourseMQContent.EXCHANGE_NAME, SelectionCourseMQContent.ROUTING_KEY,
                        courseEntity.getId());
            }
            Set<String> keys = stringRedisTemplate.keys("student:course:selection:*");
            stringRedisTemplate.delete(keys);
        } catch (Exception exception) {
            throw new PublishCourseException(exception.getMessage());
        }
    }

    @RabbitListener(queues = CourseSelectMQContent.QUEUE_NAME)
    @DSTransactional(rollbackFor = Exception.class)
    public void courseSelect(CourseEntity courseEntity) {
        try {
            log.info("选课id{}课程名字{}选课开始", courseEntity.getId(), courseEntity.getCourseName());
            CourseSelectionEntity courseSelectionEntity = new CourseSelectionEntity();
            BeanUtils.copyProperties(courseEntity, courseSelectionEntity);
            courseSelectionEntityRepository.insert(courseSelectionEntity);
            Set<String> keys = stringRedisTemplate.keys("student:course:selection:*");
            stringRedisTemplate.delete(keys);
        } catch (Exception exception) {
            throw new PublishCourseException(exception.getMessage());
        }
    }


    @Override
    public String getStudentCourse(Integer quantity, Integer pages) throws SelectCourseSelectionException {
        try {
            if (UserContext.getUserId() == null) {
                log.error("请求参数错误,用户不存在");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "用户不存在或未登录，请联系管理员"
                ));
            }
            if (quantity == null || quantity <= 0 || pages == null || pages <= 0) {
                log.warn("分页参数无效 - quantity: {}, pages: {}", quantity, pages);
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "分页参数必须大于0"
                ));
            }
            String redisCachingKey = "student:course:selection:user:" + UserContext.getUserId() + ":page:" + pages +
                    ":quantity:" + quantity;
            String cachedData = stringRedisTemplate.opsForValue().get(redisCachingKey);
            if (!(cachedData == null)) {
                log.info("redis缓存命中");
                Page<CourseSelectionEntity> cachedPage = (Page<CourseSelectionEntity>) JsonUtil.jsonToObject(cachedData, Page.class);
                return JsonSerialization.toJson(new BaseResponse<Page<CourseSelectionEntity>>(
                        BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, cachedPage
                ));
            }
            Student studentByStudentId = userManagementClients.getStudentByStudentId(UserContext.getUserId());
            College collegeByCollegeName = userManagementClients.getCollegeByCollegeName(studentByStudentId.getCollege());
            if (studentByStudentId == null || collegeByCollegeName == null) {
                log.error("找不到学生信息 - userId: {}", UserContext.getUserId());
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "学生信息不存在，请联系管理员"
                ));
            }
            long offset = (long) (pages - 1) * quantity;
            System.out.println(courseSelectionEntityRepository.selectList(new LambdaQueryWrapper<CourseSelectionEntity>()));
            System.out.println(studentByStudentId.getStudentGrade());
            System.out.println(collegeByCollegeName.getCollegeId());
            List<CourseSelectionEntity> courseList = courseSelectionEntityRepository.selectStudentCourses(
                    studentByStudentId.getStudentGrade(),
                    collegeByCollegeName.getCollegeId(),
                    offset,
                    quantity
            );
            Map<String, String> teacherMap = new HashMap<>();
            Map<String, String> collegeMap = new HashMap<>();
            courseList.stream().forEach(courseSelectionEntity -> {
                if (!teacherMap.containsKey(courseSelectionEntity.getTeacherId())) {
                    teacherMap.put(courseSelectionEntity.getTeacherId(), "");
                }
                if (!collegeMap.containsKey(courseSelectionEntity.getCourseCollege())) {
                    collegeMap.put(courseSelectionEntity.getCourseCollege(), "");
                }
            });
            Map<String, Map<String, String>> requestBody = new HashMap<>();
            requestBody.put("teacher", teacherMap);
            requestBody.put("college", collegeMap);
            Map<String, Map<String, String>> courseBatch = userManagementClients.getCourseBatch(requestBody);
            courseList.stream().forEach(courseSelectionEntity -> {
                courseSelectionEntity.setTeacherId(courseBatch.get("teacher").get(courseSelectionEntity.getTeacherId()));
                courseSelectionEntity.setCourseCollege(courseBatch.get("college").get(courseSelectionEntity.getCourseCollege()));
            });
            int total = courseSelectionEntityRepository.countStudentCourses(
                    studentByStudentId.getStudentGrade(),
                    collegeByCollegeName.getCollegeId()
            );
            Page<CourseSelectionEntity> courseSelectionEntityPage = new Page<>(pages, quantity);
            courseSelectionEntityPage.setRecords(courseList);
            courseSelectionEntityPage.setTotal(total);
            CompletableFuture.runAsync(() -> {
                log.info("缓存未命中更新数据至redis");
                stringRedisTemplate.opsForValue().set(redisCachingKey, JsonUtil.objectToJson(courseSelectionEntityPage),
                        1, TimeUnit.HOURS);
            });
            return JsonSerialization.toJson(new BaseResponse<Page<CourseSelectionEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, courseSelectionEntityPage
            ));
        } catch (Exception exception) {
            throw new SelectCourseSelectionException(exception.getMessage());
        }
    }
}
