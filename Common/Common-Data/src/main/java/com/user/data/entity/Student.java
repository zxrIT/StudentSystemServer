package com.user.data.entity;

import lombok.Data;

@Data
public class Student {
    private String studentId;
    private String studentName;
    private String studentClass;
    private Integer studentSex;
    private String studentIcon;
    private Integer studentGrade;
    private String password;
    private Integer roleId;
}
