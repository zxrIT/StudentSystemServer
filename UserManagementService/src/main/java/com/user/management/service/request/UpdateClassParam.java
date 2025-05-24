package com.user.management.service.request;

import lombok.Data;

@Data
public class UpdateClassParam {
    private String classId;
    private String className;
    private String college;
    private String counselor;
    private String headTeacher;
}
