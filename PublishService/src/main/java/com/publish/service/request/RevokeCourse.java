package com.publish.service.request;

import lombok.Data;

import java.util.List;

@Data
public class RevokeCourse {
    private List<String> courseIds;
}
