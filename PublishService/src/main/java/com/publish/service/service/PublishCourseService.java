package com.publish.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.publish.service.entity.PublishCourseEntity;
import com.publish.service.request.IncrementCourseParam;
import com.publish.service.request.UpdateCourseParam;

import java.util.Date;
import java.util.List;

public interface PublishCourseService extends IService<PublishCourseEntity> {
    String getPublishCourse(Integer quantity, Integer page);

    String getSelectCourse(String content, Integer quantity, Integer page);

    String incrementCourse(IncrementCourseParam incrementCourseParam);

    String updateCourse(UpdateCourseParam updateCourseParam);

    String deleteCourse(String id);

    String publishCourse(Date startDate, Date endDate, List<String> publishCourseList);

    String revokePublishedCourse(List<String> courseIds);
}
