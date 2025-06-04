package com.user.management.service.request;

import lombok.Data;

@Data
public class IncrementTeacherParam {
    private String teacherId;
    private String teacherName;
    private String teacherCollege;
    private Boolean isCounselor;
    private Integer teacherJob;
}
