package com.user.management.service.request;

import lombok.Data;

@Data
public class IncrementStudentParam {
    private String studentName;
    private String studentId;
    private Integer studentSex;
    private Integer roleId;
    private String studentClass;
    private String college;
    private Integer studentGrade;
}
