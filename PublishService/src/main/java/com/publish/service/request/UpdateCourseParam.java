package com.publish.service.request;

import lombok.Data;

@Data
public class UpdateCourseParam {
    private String courseCollege;
    private String id;
    private String teacherId;
    private Integer courseGrade;
    private Integer courseType;
}
