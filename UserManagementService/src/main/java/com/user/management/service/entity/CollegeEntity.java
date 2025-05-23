package com.user.management.service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.user.data.entity.College;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("college")
public class CollegeEntity extends College {
}
