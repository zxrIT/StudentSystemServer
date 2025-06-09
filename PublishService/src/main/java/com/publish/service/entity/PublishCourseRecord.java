package com.publish.service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@TableName("publish_course_record")
@AllArgsConstructor
@NoArgsConstructor
public class PublishCourseRecord {
    private String courseId;
    private Date expirationTime;
}
