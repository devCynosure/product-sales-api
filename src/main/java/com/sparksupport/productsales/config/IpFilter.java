package com.sparksupport.productsales.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class IpFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(IpFilter.class);
    private static final List<String> ALLOWED_IPS = List.of("192.168.39.196", "127.0.0.1", "0:0:0:0:0:0:0:1");
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        log.info("Incoming request from IP: {}", ipAddress);

        if (!ALLOWED_IPS.contains(ipAddress)) {
            log.error("Access Denied: Unauthorized IP address");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Unauthorized IP address");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
