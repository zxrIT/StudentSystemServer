package com.user.management.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.management.service.entity.ClassNameEntity;
import com.user.management.service.request.IncrementClassParam;
import com.user.management.service.request.UpdateClassParam;

public interface ClassNameService extends IService<ClassNameEntity> {
    String getClassNames();

    String incrementClass(IncrementClassParam incrementClassParam);

    String updateClassName(UpdateClassParam updateClassParam);

    String getClassNameByCollege(String collegeName);

    String deleteClassName(String classId);
}
