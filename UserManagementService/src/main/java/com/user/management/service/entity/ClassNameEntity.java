package com.user.management.service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.user.data.entity.ClassName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("className")
public class ClassNameEntity extends ClassName {
}
