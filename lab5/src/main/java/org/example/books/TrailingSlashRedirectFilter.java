package org.example.books;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TrailingSlashRedirectFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();
        String contextPath = request.getContextPath();

        if (path.length() > 1 && path.endsWith("/")) {
            String newPath = path.substring(0, path.length() - 1);
            response.sendRedirect(contextPath + newPath);
            return;
        }

        chain.doFilter(req, res);
    }
}
