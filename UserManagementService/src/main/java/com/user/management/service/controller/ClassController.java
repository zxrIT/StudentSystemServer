package com.user.management.service.controller;

import com.user.management.service.request.UpdateClassParam;
import com.user.management.service.service.ClassNameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/class")
@SuppressWarnings("all")
@RequiredArgsConstructor
@Tag(name = "班级管理", description = "班级相关操作API")
public class ClassController {
    private final ClassNameService classNameService;

    @Operation(summary = "查询班级数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getClassNames")
    public String getClassNames() {
        return classNameService.getClassNames();
    }

    @Operation(summary = "根据学院查询班级数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getClassNamesByCollege/{collegeName}")
    public String getClassNamesByCollege(@PathVariable String collegeName) {
        return classNameService.getClassNameByCollege(collegeName);
    }

    @Operation(summary = "修改班级数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误，班级信息不存在"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PutMapping("/updateClassName")
    public String updateClassName(@RequestBody UpdateClassParam updateClassParam) {
        return classNameService.updateClassName(updateClassParam);
    }

    @Operation(summary = "删除班级")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误，班级信息不存在"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @DeleteMapping("/deleteClassName/{classId}")
    public String deleteClassName(@PathVariable String classId) {
        return classNameService.deleteClassName(classId);
    }
}
