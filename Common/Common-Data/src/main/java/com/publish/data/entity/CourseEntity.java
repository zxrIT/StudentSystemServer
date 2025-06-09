package com.publish.data.entity;

import lombok.Data;

@Data
public class CourseEntity {
    private String id;
    private String courseId;
    private String courseName;
    private Integer courseType;
    private Double courseCredits;
    private Integer courseCreditHour;
    private String courseLocation;
    private String teacherId;
    private Integer courseCapacity;
    private Integer courseCapacityTemp;
    private Integer publishStatus;
    private Integer courseGrade;
    private String courseCollege;
    private Integer remainderCourseCapacity;
}
