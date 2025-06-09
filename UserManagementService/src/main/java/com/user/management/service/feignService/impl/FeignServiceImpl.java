package com.user.management.service.feignService.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.user.data.entity.College;
import com.user.data.entity.Student;
import com.user.data.entity.Teacher;
import com.user.management.service.entity.ClassNameEntity;
import com.user.management.service.entity.CollegeEntity;
import com.user.management.service.entity.StudentEntity;
import com.user.management.service.entity.TeacherEntity;
import com.user.management.service.feignService.FeignService;
import com.user.management.service.repository.ClassNameRepository;
import com.user.management.service.repository.CollegeRepository;
import com.user.management.service.repository.StudentRepository;
import com.user.management.service.repository.TeacherEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@SuppressWarnings("all")
@RequiredArgsConstructor
public class FeignServiceImpl implements FeignService {
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    @Autowired
    private final StudentRepository studentRepository;
    private final TeacherEntityRepository teacherRepository;
    private final CollegeRepository collegeRepository;
    private final ClassNameRepository classNameRepository;

    @Override
    public Student getStudentByStudentId(String studentId) {
        StudentEntity studentEntity =
                studentRepository.selectOne(new LambdaQueryWrapper<StudentEntity>().eq(
                        StudentEntity::getStudentId, studentId
                ));
        if (studentEntity == null) {
            return null;
        }
        CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                CollegeEntity::getCollegeId, studentEntity.getCollege()
        ));
        if (collegeEntity == null) {
            studentEntity.setCollege(null);
        } else {
            studentEntity.setCollege(collegeEntity.getCollegeName());
        }
        ClassNameEntity classNameEntity = classNameRepository.selectOne(new LambdaQueryWrapper<ClassNameEntity>().eq(
                ClassNameEntity::getClassId, studentEntity.getStudentClass()
        ));
        if (classNameEntity == null) {
            studentEntity.setStudentClass(null);
        } else {
            studentEntity.setStudentClass(classNameEntity.getClassName());
        }
        return studentEntity;
    }

    @Override
    public Teacher getTeacherByTeacherId(String teacherId) {
        TeacherEntity teacherEntity = teacherRepository.selectOne(new LambdaQueryWrapper<TeacherEntity>().eq(
                TeacherEntity::getTeacherId, teacherId
        ));
        if (teacherEntity == null) {
            return null;
        }
        CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                CollegeEntity::getCollegeId, teacherEntity.getTeacherCollege()
        ));
        if (collegeEntity == null) {
            teacherEntity.setTeacherCollege(null);
        } else {
            teacherEntity.setTeacherCollege(collegeEntity.getCollegeName());
        }
        return teacherEntity;
    }

    @Override
    public College getCollegeByCollegeId(String collegeName) {
        CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                CollegeEntity::getCollegeName, collegeName
        ));
        if (collegeEntity == null) {
            return null;
        }
        return collegeEntity;
    }

    @Override
    public Student getAdminByAdminId(String adminId) {
        StudentEntity studentEntity =
                studentRepository.selectOne(new LambdaQueryWrapper<StudentEntity>().eq(
                        StudentEntity::getStudentId, adminId
                ).eq(StudentEntity::getRoleId, 2));
        if (studentEntity == null) {
            return null;
        }
        CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                CollegeEntity::getCollegeId, studentEntity.getCollege()
        ));
        if (collegeEntity == null) {
            studentEntity.setCollege(null);
        } else {
            studentEntity.setCollege(collegeEntity.getCollegeName());
        }
        ClassNameEntity classNameEntity = classNameRepository.selectOne(new LambdaQueryWrapper<ClassNameEntity>().eq(
                ClassNameEntity::getClassId, studentEntity.getStudentClass()
        ));
        if (classNameEntity == null) {
            studentEntity.setStudentClass(null);
        } else {
            studentEntity.setStudentClass(classNameEntity.getClassName());
        }
        return studentEntity;
    }

    @Override
    public Map<String, Map<String, String>> getCourseBatch(@RequestBody Map<String, Map<String, String>> requestBody) {
        Future<?> teacherFuture = executor.submit(() -> processTeacherBatch(requestBody.get("teacher")));
        Future<?> collegeFuture = executor.submit(() -> processCollegeBatch(requestBody.get("college")));
        try {
            teacherFuture.get();
            collegeFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("处理过程中出错", e);
        }

        Map<String, Map<String, String>> result = new HashMap<>();
        result.put("teacher", requestBody.get("teacher"));
        result.put("college", requestBody.get("college"));
        return result;
    }


    private void processTeacherBatch(Map<String, String> teacherBatch) {
        teacherBatch.forEach((key, value) -> {
            TeacherEntity teacherEntity = teacherRepository.selectOne(
                    new LambdaQueryWrapper<TeacherEntity>()
                            .eq(TeacherEntity::getTeacherId, key)
            );
            if (teacherEntity == null) {
                teacherBatch.put(key, null);
            } else {
                teacherBatch.put(key, teacherEntity.getTeacherName());
            }
        });
    }

    private void processCollegeBatch(Map<String, String> collegeBatch) {
        collegeBatch.forEach((key, value) -> {
            CollegeEntity collegeEntity = collegeRepository.selectOne(
                    new LambdaQueryWrapper<CollegeEntity>()
                            .eq(CollegeEntity::getCollegeId, key)
            );
            if (collegeEntity == null) {
                collegeBatch.put(key, null);
            } else {
                collegeBatch.put(key, collegeEntity.getCollegeName());
            }
            if (key.equals("all")) {
                collegeBatch.put(key, "不限");
            }
        });
    }
}
