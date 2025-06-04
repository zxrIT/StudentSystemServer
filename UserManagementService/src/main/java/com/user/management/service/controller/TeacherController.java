package com.user.management.service.controller;

import com.user.management.service.request.IncrementTeacherParam;
import com.user.management.service.request.UpdateTeacherParam;
import com.user.management.service.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user/teacher")
@SuppressWarnings("all")
@RequiredArgsConstructor
@Tag(name = "教师管理", description = "教师相关操作API")
public class TeacherController {
    private final TeacherService teacherService;

    @Operation(summary = "查询教师数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getTeacher/{quantity}/{page}")
    public String getTeacher(@PathVariable Integer quantity, @PathVariable Integer page) {
        return teacherService.getTeacher(quantity, page);
    }

    @Operation(summary = "删除教师数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @DeleteMapping("/deleteTeacher/{teacherId}")
    public String deleteTeacher(@PathVariable String teacherId) {
        return teacherService.deleteTeacher(teacherId);
    }

    @Operation(summary = "新增教师")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "重置成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误，教师信息不存在"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PostMapping(value = "/incrementTeacher", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String incrementTeacher(@RequestPart("data") IncrementTeacherParam incrementTeacherParam,
                                    @RequestParam("iconImage") MultipartFile iconImage) {
        return teacherService.incrementTeacher(incrementTeacherParam, iconImage);
    }

    @Operation(summary = "修改教师数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误，教师不存在"),
            @ApiResponse(responseCode = "300", description = "请求参数错误，教师序号已存在"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PutMapping("/updateTeacher")
    public String updateTeacher(@RequestBody UpdateTeacherParam updateTeacherParam) {
        return teacherService.updateTeacher(updateTeacherParam);
    }

    @Operation(summary = "查询搜索教师数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getSelectTeacher/{payload}/{content}/{quantity}/{page}")
    public String getSelectTeacher(@PathVariable Integer payload, @PathVariable String content,
                                   @PathVariable Integer quantity, @PathVariable Integer page) {
        return teacherService.getSelectTeacher(payload, content, quantity, page);
    }

    @Operation(summary = "重置教师密码")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误，教师不存在"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PostMapping("/resetPassword/{teacherId}")
    public String resetPassword(@PathVariable String teacherId) {
        return teacherService.resetPassword(teacherId);
    }

    @Operation(summary = "查询全部辅导员")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getAllCounselor")
    public String getAllCounselor() {
        return teacherService.getAllCounselor();
    }

    @Operation(summary = "查询全部教师")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getAllTeacher")
    public String getAllTeacher() {
        return teacherService.getAllTeacher();
    }
}
