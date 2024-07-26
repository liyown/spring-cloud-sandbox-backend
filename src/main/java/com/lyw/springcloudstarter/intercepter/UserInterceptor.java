package com.lyw.springcloudstarter.intercepter;


import com.lyw.springcloudstarter.common.UserHolder;
import com.lyw.springcloudstarter.domain.po.User;
import com.lyw.springcloudstarter.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class UserInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(UserInterceptor.class);
    private final UserService userService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User loginUser = null;
        try {
            loginUser = userService.getLoginUser(request);
            UserHolder.setUserId(loginUser.getId());
            UserHolder.setUserRole(loginUser.getUserRole());
        } catch (Exception e) {
            log.info("用户未登录");
            UserHolder.setUserId(null);
            UserHolder.setUserRole(null);
        }

        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.remove();
    }
    
}