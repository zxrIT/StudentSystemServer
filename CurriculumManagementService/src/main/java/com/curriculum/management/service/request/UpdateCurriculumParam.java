package com.curriculum.management.service.request;

import lombok.Data;

@Data
public class UpdateCurriculumParam {
    private String name;
    private String id;
    private String curriculumId;
    private Integer capacity;
    private Double credits;
    private Integer creditHour;
    private String location;
}
