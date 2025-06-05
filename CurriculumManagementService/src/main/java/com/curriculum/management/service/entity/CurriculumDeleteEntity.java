package com.curriculum.management.service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.curriculum.data.entity.Curriculum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("curriculum_delete")
public class CurriculumDeleteEntity extends Curriculum {
}
