package com.user.management.service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.user.data.entity.Teacher;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("teacher")
public class TeacherEntity extends Teacher {
}
