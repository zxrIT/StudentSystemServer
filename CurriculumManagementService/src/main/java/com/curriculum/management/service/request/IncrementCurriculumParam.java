package com.curriculum.management.service.request;

import lombok.Data;

@Data
public class IncrementCurriculumParam {
    private String curriculumId;
    private String name;
    private String location;
    private Double credits;
    private Integer capacity;
    private Integer creditHour;
}
