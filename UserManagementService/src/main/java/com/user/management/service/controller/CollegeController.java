package com.user.management.service.controller;

import com.user.management.service.request.IncrementCollegeParam;
import com.user.management.service.request.UpdateCollegeIsExistenceParam;
import com.user.management.service.request.UpdateCollegeParam;
import com.user.management.service.service.CollegeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/college")
@SuppressWarnings("all")
@RequiredArgsConstructor
@Tag(name = "学院管理", description = "学院相关操作API")
public class CollegeController {
    private final CollegeService collegeService;

    @Operation(summary = "查询学院数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getCollege/{quantity}/{page}")
    public String getCollege(@PathVariable Integer quantity, @PathVariable Integer page) {
        return collegeService.getCollege(quantity, page);
    }

    @Operation(summary = "添加学院数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "添加成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误，学院代码已存在"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PostMapping("/incrementCollege")
    public String incrementCollege(@RequestBody IncrementCollegeParam incrementCollegeParam) {
        return collegeService.incrementCollege(incrementCollegeParam);
    }

    @Operation(summary = "修改学院数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误，该院校不存在"),
            @ApiResponse(responseCode = "300", description = "请求参数错误，学院代码已存在"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PutMapping("/updateCollege")
    public String updateCollege(@RequestBody UpdateCollegeParam updateCollegeParam) {
        return collegeService.updateCollege(updateCollegeParam);
    }

    @Operation(summary = "修改学院停用/启用状态")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误，该院校不存在"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PutMapping("/updateCollegeIsExistence")
    public String updateCollegeIsExistence(@RequestBody
                                           UpdateCollegeIsExistenceParam updateCollegeIsExistenceParam) {
        return collegeService.updateCollegeIsExistence(updateCollegeIsExistenceParam);
    }

    @Operation(summary = "查询所有学院名字")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getCollegeNames")
    public String getCollegeNames() {
        return collegeService.getCollegeNames();
    }
}
