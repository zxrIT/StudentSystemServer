package com.user.management.service.feignService.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

@Service
@SuppressWarnings("all")
@RequiredArgsConstructor
public class FeignServiceImpl implements FeignService {
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
}
