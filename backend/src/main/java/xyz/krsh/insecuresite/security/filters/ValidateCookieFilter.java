package xyz.krsh.insecuresite.security.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.Cookie;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import xyz.krsh.insecuresite.rest.service.ESAPIValidatorService;

@Component
public class ValidateCookieFilter extends OncePerRequestFilter {
    protected static final Logger logger = LogManager.getLogger();

    @Autowired
    ESAPIValidatorService validator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if (requestURI.endsWith("/api/username/") || requestURI.endsWith("/api/check_login/")
                || requestURI.endsWith("/api/document/up/")
                || requestURI.endsWith("/api/document/down/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String cookieDocumentId = "jsessionid_v2";

        if (validator == null) { // Lazy loading the ESAPI Validator service class
            ServletContext servletContext = request.getServletContext();
            WebApplicationContext webApplicationContext = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(servletContext);
            validator = webApplicationContext.getBean(ESAPIValidatorService.class);

        }

        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (!validator.isValidString(cookieDocumentId, cookie.getValue())) {
                        return;
                    }
                }
            }

        } catch (Exception e) {
            logger.info(e + " " + e.getMessage());
            filterChain.doFilter(request, response);
        }

        filterChain.doFilter(request, response);
    }

}
