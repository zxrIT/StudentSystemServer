package com.user.management.service.request;

import lombok.Data;

@Data
public class IncrementCollegeParam {
    private String collegeName;
    private String collegeId;
    private Integer classCount;
}
