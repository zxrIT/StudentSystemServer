package com.user.data.entity;

import lombok.Data;

@Data
public class Teacher {
    private String teacherId;
    private String teacherName;
    private String password;
    private String teacherIcon;
    private String teacherCollege;
    private Integer teacherJob;
    private Boolean isCounselor;
    private String id;
}
