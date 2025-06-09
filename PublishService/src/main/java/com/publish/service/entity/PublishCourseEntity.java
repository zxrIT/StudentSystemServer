package com.publish.service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.publish.data.entity.CourseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("course_entity")
public class PublishCourseEntity extends CourseEntity {
}
