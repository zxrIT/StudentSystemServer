package com.user.management.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.management.service.entity.CollegeEntity;
import com.user.management.service.request.UpdateCollegeIsExistenceParam;
import com.user.management.service.request.UpdateCollegeParam;

public interface CollegeService extends IService<CollegeEntity> {
    String getCollege(Integer quantity, Integer pages);

    String updateCollege(UpdateCollegeParam updateCollegeParam);

    String getCollegeNames();

    String updateCollegeIsExistence(UpdateCollegeIsExistenceParam updateCollegeIsExistenceParam);
}
