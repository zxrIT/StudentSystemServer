package com.publish.service.controller;

import com.publish.service.request.IncrementCourseParam;
import com.publish.service.request.PublishCourseParam;
import com.publish.service.request.RevokeCourse;
import com.publish.service.request.UpdateCourseParam;
import com.publish.service.service.PublishCourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/publish")
@SuppressWarnings("all")
@RequiredArgsConstructor
@Tag(name = "发布课程管理", description = "发布课程相关操作API")
public class PublishServiceController {
    @Autowired
    private final PublishCourseService publishCourseService;

    @Operation(summary = "查询代发布课程数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getCourse/{quantity}/{page}")
    private String getCourse(@PathVariable Integer quantity, @PathVariable Integer page) {
        return publishCourseService.getPublishCourse(quantity, page);
    }

    @Operation(summary = "查询代发布搜索课程数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getSelectCourse/{content}/{quantity}/{page}")
    private String getSelectCourse(@PathVariable String content,
                                   @PathVariable Integer quantity, @PathVariable Integer page) {
        return publishCourseService.getSelectCourse(content, quantity, page);
    }

    @Operation(summary = "发布课程数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "添加成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PostMapping("/publishCourse")
    private String publishCourse(@RequestBody PublishCourseParam publishCourseParam) {
        return publishCourseService.publishCourse(publishCourseParam.getStartTime()
                , publishCourseParam.getEndTime(), publishCourseParam.getCourseIds());
    }

    @Operation(summary = "撤回发布课程数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PutMapping("/revokeCourse")
    private String revokeCourse(@RequestBody RevokeCourse revokeCourse) {
        return publishCourseService.revokePublishedCourse(revokeCourse.getCourseIds());
    }

    @Operation(summary = "添加代发布课程数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "添加成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PostMapping("/incrementCourse")
    private String incrementCourse(@RequestBody IncrementCourseParam incrementCourseParam) {
        return publishCourseService.incrementCourse(incrementCourseParam);
    }

    @Operation(summary = "修改代发布课程数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PutMapping("/updateCourse")
    private String updateCourse(@RequestBody UpdateCourseParam updateCourseParam) {
        return publishCourseService.updateCourse(updateCourseParam);
    }

    @Operation(summary = "删除代发布课程数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @DeleteMapping("/deleteCourse/{id}")
    private String deleteCourse(@PathVariable String id) {
        return publishCourseService.deleteCourse(id);
    }
}
