package com.authentication.service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.user.data.entity.Teacher;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("teacher")
public class TeacherAuth extends Teacher {
}
