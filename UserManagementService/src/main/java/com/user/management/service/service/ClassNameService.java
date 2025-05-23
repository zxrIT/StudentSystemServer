package com.user.management.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.management.service.entity.ClassNameEntity;

public interface ClassNameService extends IService<ClassNameEntity> {
    String getClassNames();
    String deleteClassName(String classId);
}
