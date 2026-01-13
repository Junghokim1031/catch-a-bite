package com.deliveryapp.catchabite.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/** 라이더 세션 인증 인터셉터 */
public class RiderAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("LOGIN_RIDER_ID") == null) {
            response.sendRedirect("/auth/rider/signup?error");
            return false;
        }
        return true;
    }
}
