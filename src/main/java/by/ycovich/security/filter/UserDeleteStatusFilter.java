package by.ycovich.security.filter;

import by.ycovich.enums.UserStatus;
import by.ycovich.entity.UserEntity;
import by.ycovich.repository.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.IOException;


@Order(1)
public class UserDeleteStatusFilter implements Filter {
    private final UserRepository userRepository;
    public UserDeleteStatusFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        UserEntity currentUser = getCurrentUser();

        if (currentUser != null && currentUser.getActive().equals(UserStatus.DEACTIVATED)) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "user is soft-deleted");
            return;
        }
        chain.doFilter(request, response);
    }

    private UserEntity getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElse(null);
    }
}
