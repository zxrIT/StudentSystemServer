package com.curriculum.management.service.controller;

import com.curriculum.management.service.request.IncrementCurriculumParam;
import com.curriculum.management.service.request.UpdateCurriculumParam;
import com.curriculum.management.service.service.CurriculumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/curriculum")
@SuppressWarnings("all")
@RequiredArgsConstructor
@Tag(name = "课程管理", description = "课程相关操作API")
public class CurriculumController {
    @Autowired
    private final CurriculumService curriculumService;

    @Operation(summary = "查询课程数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getCurriculum/{quantity}/{page}")
    private String getCurriculum(@PathVariable Integer quantity, @PathVariable Integer page) {
        return curriculumService.getCurriculum(quantity, page);
    }

    @Operation(summary = "查询回收站课程数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getDeleteCurriculum/{quantity}/{page}")
    private String getDeleteCurriculum(@PathVariable Integer quantity, @PathVariable Integer page) {
        return curriculumService.getCurriculumDelete(quantity, page);
    }

    @Operation(summary = "查询搜索课程数据数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getSelectCurriculum/{content}/{quantity}/{page}")
    private String getSelectCurriculum(@PathVariable String content, @PathVariable Integer quantity, @PathVariable Integer page) {
        return curriculumService.getSelectCurriculum(content, quantity, page);
    }

    @Operation(summary = "查询回收站搜索课程数据数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getDeleteSelectCurriculum/{content}/{quantity}/{page}")
    private String getDeleteSelectCurriculum(@PathVariable String content, @PathVariable Integer quantity, @PathVariable Integer page) {
        return curriculumService.getDeleteSelectCurriculum(content, quantity, page);
    }

    @Operation(summary = "修改课程状态")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PutMapping("/changIsExistence/{curriculumId}")
    private String changIsExistence(@PathVariable String curriculumId) {
        return curriculumService.changIsExistence(curriculumId);
    }

    @Operation(summary = "修改回收站课程状态")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PutMapping("/changDeleteIsExistence/{curriculumId}")
    private String changDeleteIsExistence(@PathVariable String curriculumId) {
        return curriculumService.changDeleteIsExistence(curriculumId);
    }

    @Operation(summary = "添加课程")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "添加成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PostMapping("/incrementCurriculum")
    private String incrementCurriculum(@RequestBody IncrementCurriculumParam incrementCurriculumParam) {
        return curriculumService.incrementCurriculum(incrementCurriculumParam);
    }

    @Operation(summary = "修改课程")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PutMapping("/updateCurriculum")
    private String updateCurriculum(@RequestBody UpdateCurriculumParam updateCurriculumParam) {
        return curriculumService.updateCurriculum(updateCurriculumParam);
    }


    @Operation(summary = "修改回收站课程")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PutMapping("/updateDeleteCurriculum")
    private String updateDeleteCurriculum(@RequestBody UpdateCurriculumParam updateCurriculumParam) {
        return curriculumService.updateDeleteCurriculum(updateCurriculumParam);
    }

    @Operation(summary = "将课程移动至回收站")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "移动成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @DeleteMapping("/removeCurriculum/{curriculumId}")
    private String removeCurriculum(@PathVariable String curriculumId) {
        return curriculumService.removeCurriculum(curriculumId);
    }

    @Operation(summary = "将课程从回收站移回")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "移回成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @DeleteMapping("/moveCurriculum/{curriculumId}")
    private String moveCurriculum(@PathVariable String curriculumId) {
        return curriculumService.moveCurriculum(curriculumId);
    }
}
