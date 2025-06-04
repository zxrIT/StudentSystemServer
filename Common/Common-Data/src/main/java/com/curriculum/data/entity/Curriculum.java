package com.curriculum.data.entity;

import lombok.Data;

@Data
public class Curriculum {
    private String id;
    private String curriculumId;
    private String name;
    private Integer capacity;
    private Double credits;
    private Integer creditHour;
    private String location;
    private Boolean isExistence;
}
