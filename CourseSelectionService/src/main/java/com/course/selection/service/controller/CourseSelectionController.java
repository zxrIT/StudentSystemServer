package com.course.selection.service.controller;

import com.course.selection.service.service.CourseSelection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/selection")
@SuppressWarnings("all")
@RequiredArgsConstructor
@Tag(name = "选择课程管理", description = "选择课程相关操作API")
public class CourseSelectionController {
    @Autowired
    private final CourseSelection courseSelection;

    @Operation(summary = "查询学生可选课程数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getStudentCourse/{quantity}/{page}")
    private String getStudentCourse(@PathVariable Integer quantity, @PathVariable Integer page) {
        return courseSelection.getStudentCourse(quantity, page);
    }
}
