package com.publish.service.request;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PublishCourseParam {
    private Date startTime;
    private Date endTime;
    private List<String> courseIds;
}
