package com.common.userInfo.interceptor;

import com.common.userInfo.utils.IsEmpty;
import com.common.userInfo.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

@SuppressWarnings("all")
public class UserInformationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        String roleId = request.getHeader("roleId");
        if (!IsEmpty.isStringEmpty(userId)) {
            UserContext.setUserId(userId);
        }
        if (!IsEmpty.isStringEmpty(roleId)) {
            UserContext.setRoleId(roleId);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.remove();
    }
}
