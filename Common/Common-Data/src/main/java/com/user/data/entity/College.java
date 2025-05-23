package com.user.data.entity;

import lombok.Data;

@Data
public class College {
    private String id;
    private String collegeId;
    private String collegeName;
    private Integer classCount;
    private Boolean isExistence;
}
