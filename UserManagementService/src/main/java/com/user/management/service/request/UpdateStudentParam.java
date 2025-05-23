package com.user.management.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStudentParam {
    private String studentId;
    private String studentName;
    private String studentClass;
    private Integer studentSex;
    private Integer studentGrade;
    private Integer roleId;
}
