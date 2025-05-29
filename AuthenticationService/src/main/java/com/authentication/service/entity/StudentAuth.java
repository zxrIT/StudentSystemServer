package com.authentication.service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.user.data.entity.Student;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("student")
public class StudentAuth extends Student {
}
