package com.publish.service.request;

import lombok.Data;

@Data
public class IncrementCourseParam {
    private String courseId;
    private String courseName;
    private Integer courseType;
    private Double courseCredits;
    private Integer courseCreditHour;
    private String courseLocation;
    private String teacherId;
    private Integer courseCapacity;
    private Integer publishStatus;
    private Integer courseGrade;
    private String courseCollege;
}
