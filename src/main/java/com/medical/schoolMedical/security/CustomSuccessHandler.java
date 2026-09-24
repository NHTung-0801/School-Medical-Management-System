package com.medical.schoolMedical.security;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String source = request.getParameter("source"); // "admin" hoặc "user"

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority auth : authorities) {
            String role = auth.getAuthority();

                // Sai trang login thì không cho phép
                if (role.equals("ROLE_ADMIN") && "user".equals(source)) {
                    response.sendRedirect("/yte/access-denied");
                    return;
                }

                if (!role.equals("ROLE_ADMIN") && "admin".equals(source)) {
                    response.sendRedirect("/yte/access-denied");
                    return;
                }

                switch (role) {
                    case "ROLE_ADMIN":
                        response.sendRedirect("/yte/admin/dashboard");
                        return;
                    case "ROLE_PARENT":
                        response.sendRedirect("/yte/parent/parent-home");
                        return;
                    case "ROLE_MANAGER":
                        response.sendRedirect("/yte/manager/manager-home");
                        return;
                    case "ROLE_NURSE":

                        response.sendRedirect("/yte/nurse/nurse-home");

                        return;
                    default:
                        response.sendRedirect("/yte/access-denied");
                        return;
                }
            }
        }
    }
