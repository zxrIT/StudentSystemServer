package com.curriculum.management.service.controller;

import com.curriculum.management.service.service.CurriculumService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/curriculum")
@SuppressWarnings("all")
@RequiredArgsConstructor
@Tag(name = "课程管理", description = "课程相关操作API")
public class CurriculumController {
    @Autowired
    private final CurriculumService curriculumService;

    @GetMapping("/getCurriculum/{quantity}/{page}")
    private String getCurriculum(@PathVariable Integer quantity, @PathVariable Integer page) {
        return curriculumService.getCurriculum(quantity, page);
    }

    @GetMapping("/getSelectCurriculum/{content}/{quantity}/{page}")
    private String getSelectCurriculum(@PathVariable String content, @PathVariable Integer quantity, @PathVariable Integer page) {
        return curriculumService.getSelectCurriculum(content, quantity, page);
    }
}
