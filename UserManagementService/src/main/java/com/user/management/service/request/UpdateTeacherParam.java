package com.user.management.service.request;

import lombok.Data;

@Data
public class UpdateTeacherParam {
    private String id;
    private String teacherId;
    private String teacherName;
    private String teacherCollege;
    private Integer teacherJob;
    private Boolean isCounselor;
}
