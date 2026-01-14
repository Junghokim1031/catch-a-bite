package com.deliveryapp.catchabite.config.interceptor;

import com.deliveryapp.catchabite.auth.AuthSessionKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class OwnerAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(AuthSessionKeys.LOGIN_OWNER_ID) == null) {
            response.sendRedirect("/auth/owner/signup?error");
            return false;
        }
        return true;
    }
}
