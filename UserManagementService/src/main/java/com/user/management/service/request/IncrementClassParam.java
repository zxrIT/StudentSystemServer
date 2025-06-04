package com.user.management.service.request;

import lombok.Data;

@Data
public class IncrementClassParam {
    private String className;
    private String college;
    private String counselor;
    private String headTeacher;
    private Integer studentCount;
}
