//package com.sparksupport.productsales.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//@Component
//public class IpFilter extends OncePerRequestFilter {
//    private static final List<String> ALLOWED_IPS = List.of(
//            "192.168.39.196",
//            "127.0.0.1"
//            );
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String remoteAddr = request.getRemoteAddr();
//
//        if (!ALLOWED_IPS.contains(remoteAddr)) {
//            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Unauthorized IP address");
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
