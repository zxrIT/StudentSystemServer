package com.publish.service.feignClients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("user-management-service")
public interface UserFeignClient {

    @PostMapping("/api/user/getCourseBatch")
    Map<String, Map<String, String>> getCourseBatch(@RequestBody Map<String, Map<String, String>> requestBody);
}
