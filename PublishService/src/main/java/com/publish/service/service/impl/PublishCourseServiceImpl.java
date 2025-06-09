package com.publish.service.service.impl;

import com.alibaba.nacos.shaded.com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.exception.entity.publishCourse.*;
import com.common.rabbitmq.rabbitMQContent.CourseSelectMQContent;
import com.common.rabbitmq.rabbitMQContent.PublishCourseMQContent;
import com.common.rabbitmq.rabbitMQContent.SelectionCourseMQContent;
import com.common.response.entity.BaseResponse;
import com.common.response.entity.BaseResponseUtil;
import com.common.utils.JsonSerialization;
import com.publish.data.entity.CourseEntity;
import com.publish.service.entity.PublishCourseEntity;
import com.publish.service.entity.PublishCourseRecord;
import com.publish.service.feignClients.UserFeignClient;
import com.publish.service.repository.PublishCourseEntityRepository;
import com.publish.service.repository.PublishCourseRecordRepository;
import com.publish.service.request.IncrementCourseParam;
import com.publish.service.request.UpdateCourseParam;
import com.publish.service.service.PublishCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@SuppressWarnings("all")
@RequiredArgsConstructor
public class PublishCourseServiceImpl extends ServiceImpl<PublishCourseEntityRepository, PublishCourseEntity>
        implements PublishCourseService {
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            10, 20, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000),
            new ThreadFactoryBuilder().setNameFormat("course-publish-pool-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );
    private static final Integer TaskTimeoutPeriod = 30;

    @Autowired
    private final RabbitTemplate rabbitTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PublishCourseEntityRepository publishCourseEntityRepository;
    private final PublishCourseRecordRepository publishCourseRecordRepository;
    private final UserFeignClient userFeignClient;

    @Override
    public String getPublishCourse(Integer quantity, Integer pages) throws SelectPublishCourseException {
        try {
            if (quantity <= 0 || pages <= 0) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误"
                ));
            }
            Page<PublishCourseEntity> page = new Page<>(pages, quantity);
            Map<String, String> teacherMap = new HashMap<>();
            Map<String, String> collegeMap = new HashMap<>();
            Page<PublishCourseEntity> publishCourseEntityPage =
                    publishCourseEntityRepository.selectPage(page, new QueryWrapper<PublishCourseEntity>());
            publishCourseEntityPage.getRecords().stream().forEach(publishCourseEntity -> {
                if (!teacherMap.containsKey(publishCourseEntity.getTeacherId())) {
                    teacherMap.put(publishCourseEntity.getTeacherId(), "");
                }
                if (!collegeMap.containsKey(publishCourseEntity.getCourseCollege())) {
                    collegeMap.put(publishCourseEntity.getCourseCollege(), "");
                }
            });
            Map<String, Map<String, String>> requestBody = new HashMap<>();
            requestBody.put("teacher", teacherMap);
            requestBody.put("college", collegeMap);
            Map<String, Map<String, String>> courseBatch = userFeignClient.getCourseBatch(requestBody);
            publishCourseEntityPage.getRecords().stream().forEach(publishCourseEntity -> {
                publishCourseEntity.setTeacherId(courseBatch.get("teacher").get(publishCourseEntity.getTeacherId()));
                publishCourseEntity.setCourseCollege(courseBatch.get("college").get(publishCourseEntity.getCourseCollege()));
            });
            return JsonSerialization.toJson(new BaseResponse<Page<PublishCourseEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, publishCourseEntityPage
            ));
        } catch (Exception exception) {
            throw new SelectPublishCourseException(exception.getMessage());
        }
    }

    @Override
    public String getSelectCourse(String content, Integer quantity, Integer pages) throws SelectPublishCourseException {
        try {
            if (quantity <= 0 || pages <= 0) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误"
                ));
            }
            Page<PublishCourseEntity> page = new Page<>(pages, quantity);
            Map<String, String> teacherMap = new HashMap<>();
            Map<String, String> collegeMap = new HashMap<>();
            Page<PublishCourseEntity> publishCourseEntityPage =
                    publishCourseEntityRepository.selectPage(page, new LambdaQueryWrapper<PublishCourseEntity>()
                            .like(PublishCourseEntity::getCourseName, content));
            publishCourseEntityPage.getRecords().stream().forEach(publishCourseEntity -> {
                if (!teacherMap.containsKey(publishCourseEntity.getTeacherId())) {
                    teacherMap.put(publishCourseEntity.getTeacherId(), "");
                }
                if (!collegeMap.containsKey(publishCourseEntity.getCourseCollege())) {
                    collegeMap.put(publishCourseEntity.getCourseCollege(), "");
                }
            });
            Map<String, Map<String, String>> requestBody = new HashMap<>();
            requestBody.put("teacher", teacherMap);
            requestBody.put("college", collegeMap);
            Map<String, Map<String, String>> courseBatch = userFeignClient.getCourseBatch(requestBody);
            publishCourseEntityPage.getRecords().stream().forEach(publishCourseEntity -> {
                publishCourseEntity.setTeacherId(courseBatch.get("teacher").get(publishCourseEntity.getTeacherId()));
                publishCourseEntity.setCourseCollege(courseBatch.get("college").get(publishCourseEntity.getCourseCollege()));
            });
            return JsonSerialization.toJson(new BaseResponse<Page<PublishCourseEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, publishCourseEntityPage
            ));
        } catch (Exception exception) {
            throw new SelectPublishCourseException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String incrementCourse(IncrementCourseParam incrementCourseParam) throws IncrementPublishCourseException {
        try {
            PublishCourseEntity publishCourseEntity = new PublishCourseEntity();
            publishCourseEntity.setId(UUID.randomUUID().toString() + System.currentTimeMillis());
            publishCourseEntity.setCourseId(incrementCourseParam.getCourseId());
            publishCourseEntity.setCourseName(incrementCourseParam.getCourseName());
            publishCourseEntity.setCourseType(incrementCourseParam.getCourseType());
            publishCourseEntity.setCourseCredits(incrementCourseParam.getCourseCredits());
            publishCourseEntity.setCourseCreditHour(incrementCourseParam.getCourseCreditHour());
            publishCourseEntity.setCourseLocation(incrementCourseParam.getCourseLocation());
            publishCourseEntity.setTeacherId(incrementCourseParam.getTeacherId());
            publishCourseEntity.setCourseCapacity(incrementCourseParam.getCourseCapacity());
            publishCourseEntity.setPublishStatus(incrementCourseParam.getPublishStatus());
            publishCourseEntity.setCourseGrade(incrementCourseParam.getCourseGrade());
            publishCourseEntity.setCourseCollege(incrementCourseParam.getCourseCollege());
            publishCourseEntity.setCourseCapacityTemp(0);
            publishCourseEntityRepository.insert(publishCourseEntity);
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "添加成功"
            ));
        } catch (Exception exception) {
            throw new IncrementPublishCourseException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String updateCourse(UpdateCourseParam updateCourseParam) throws UpdatePublishCourseException {
        try {
            PublishCourseEntity publishCourseEntity = publishCourseEntityRepository.selectOne(new LambdaQueryWrapper<PublishCourseEntity>().eq(
                    PublishCourseEntity::getId, updateCourseParam.getId()
            ));
            if (publishCourseEntity == null) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "课程不存在"
                ));
            }
            if (publishCourseEntity.getPublishStatus() == 1) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "课程发布时禁止修改，请先撤回发布"
                ));
            }
            publishCourseEntity.setCourseType(updateCourseParam.getCourseType());
            publishCourseEntity.setCourseCollege(updateCourseParam.getCourseCollege());
            publishCourseEntity.setCourseGrade(updateCourseParam.getCourseGrade());
            publishCourseEntity.setTeacherId(updateCourseParam.getTeacherId());
            publishCourseEntityRepository.update(publishCourseEntity, new LambdaUpdateWrapper<PublishCourseEntity>().eq(
                    PublishCourseEntity::getId, updateCourseParam.getId()
            ));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "修改成功"
            ));
        } catch (Exception exception) {
            throw new UpdatePublishCourseException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String deleteCourse(String id) throws DeletePublishCourseException {
        try {
            PublishCourseEntity publishCourseEntity = publishCourseEntityRepository.selectOne(new LambdaQueryWrapper<PublishCourseEntity>().eq(
                    PublishCourseEntity::getId, id
            ));
            if (publishCourseEntity == null) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "课程不存在"
                ));
            }
            if (publishCourseEntity.getPublishStatus() == 1) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "课程发布时禁止删除，请先撤回发布"
                ));
            }
            publishCourseEntityRepository.delete(new LambdaQueryWrapper<PublishCourseEntity>().eq(
                    PublishCourseEntity::getId, id
            ));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "删除成功"
            ));
        } catch (Exception exception) {
            throw new DeletePublishCourseException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String publishCourse(Date startDate, Date endDate, List<String> publishCourseList) throws PublishCourseException {
        try {
            if (startDate == null || endDate == null || publishCourseList == null || publishCourseList.size() == 0) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误"
                ));
            }
            long difference = endDate.getTime() - System.currentTimeMillis();
            Map<String, PublishCourseEntity> courseMap = publishCourseEntityRepository.selectList(
                    new LambdaQueryWrapper<PublishCourseEntity>()
                            .in(PublishCourseEntity::getId, publishCourseList)
            ).stream().collect(Collectors.toMap(PublishCourseEntity::getId, Function.identity()));
            List<CompletableFuture<Void>> futures = publishCourseList.stream()
                    .map(courseId -> CompletableFuture.runAsync(() -> {
                        PublishCourseEntity course = courseMap.get(courseId);
                        if (course == null || course.getPublishStatus() == 1) {
                            return;
                        }
                        course.setPublishStatus(1);
                        publishCourseEntityRepository.updateById(course);
                        CourseEntity courseEntity = new CourseEntity();
                        BeanUtils.copyProperties(course, courseEntity);
                        if (!publishCourseRecordRepository.exists(new LambdaQueryWrapper<PublishCourseRecord>().eq(
                                PublishCourseRecord::getCourseId, courseId
                        ))) {
                            rabbitTemplate.convertAndSend(PublishCourseMQContent.EXCHANGE_NAME, PublishCourseMQContent.ROUTING_KEY, courseEntity,
                                    message -> {
                                        message.getMessageProperties().setDelay((int) difference);
                                        return message;
                                    }
                            );
                            rabbitTemplate.convertAndSend(CourseSelectMQContent.EXCHANGE_NAME, CourseSelectMQContent.ROUTING_KEY, courseEntity);
                            publishCourseRecordRepository.insert(new PublishCourseRecord(course.getId(), endDate));
                        }
                    }, threadPoolExecutor))
                    .collect(Collectors.toList());
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(TaskTimeoutPeriod, TimeUnit.SECONDS);
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "发布成功"
            ));
        } catch (Exception exception) {
            throw new PublishCourseException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String revokePublishedCourse(List<String> courseIds) throws RevokeCourseException {
        try {
            if (courseIds == null || courseIds.isEmpty()) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE,
                        "请求参数错误"
                ));
            }
            List<PublishCourseRecord> records = publishCourseRecordRepository.selectList(
                    new LambdaQueryWrapper<PublishCourseRecord>()
                            .in(PublishCourseRecord::getCourseId, courseIds)
            );
            if (records.isEmpty()) {
                return JsonSerialization.toJson(new BaseResponse<String>(BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE,
                        "没有可撤回的课程发布记录"
                ));
            }
            List<CompletableFuture<Void>> futures = records.stream().map(record -> CompletableFuture.runAsync(() -> {
                        CourseEntity courseEntity = new CourseEntity();
                        courseEntity.setCourseId(record.getCourseId());
                        publishCourseRecordRepository.delete(new LambdaQueryWrapper<PublishCourseRecord>().eq(
                                PublishCourseRecord::getCourseId, record.getCourseId()
                        ));
                        rabbitTemplate.convertAndSend(PublishCourseMQContent.EXCHANGE_NAME, PublishCourseMQContent.ROUTING_KEY,
                                courseEntity,
                                message -> {
                                    message.getMessageProperties().setHeader("action", "revoke");
                                    return message;
                                }
                        );
                    }, threadPoolExecutor))
                    .collect(Collectors.toList());
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(TaskTimeoutPeriod, TimeUnit.SECONDS);
            return JsonSerialization.toJson(new BaseResponse<String>(BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "撤回成功"));
        } catch (Exception exception) {
            throw new RevokeCourseException(exception.getMessage());
        }
    }

    @RabbitListener(queues = SelectionCourseMQContent.QUEUE_NAME)
    @DSTransactional(rollbackFor = Exception.class)
    public void changePublishCorseStatus(String id) throws UpdatePublishCourseException {
        try {
            PublishCourseEntity publishCourseEntity = publishCourseEntityRepository.selectOne(new LambdaQueryWrapper<PublishCourseEntity>().eq(
                    PublishCourseEntity::getId, id
            ));
            publishCourseEntity.setPublishStatus(2);
            publishCourseEntityRepository.updateById(publishCourseEntity);
            simpMessagingTemplate.convertAndSend("/topic/courseUpdates", Map.of("id", id, "status", 2, "action", "refresh"));
        } catch (Exception exception) {
            throw new UpdatePublishCourseException(exception.getMessage());
        }
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void deleteTheReleaseRecord() {
        Date now = new Date();
        publishCourseRecordRepository.delete(new LambdaQueryWrapper<PublishCourseRecord>()
                .lt(PublishCourseRecord::getExpirationTime, now));
    }
}
