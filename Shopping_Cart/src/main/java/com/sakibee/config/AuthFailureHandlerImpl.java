package com.sakibee.config;

import com.sakibee.model.User;
import com.sakibee.repository.UserRepository;
import com.sakibee.service.UserService;
import com.sakibee.utils.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String email = request.getParameter("username");
        User user = userRepository.findByEmail(email);

        if(user.getIsEnable()) {
            if(user.getAccountNonLocked()) {
                if(user.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
                    userService.increaseFailedAttempt(user);
                } else {
                    userService.userAccountLock(user);
                    exception = new LockedException("Your account is locked!! faild attempt 3");
                }
            } else {
                if(userService.unlockAccountTimeExpired(user)) {
                    exception = new LockedException("Your account is unlocked. Please try to login");
                } else {
                    exception = new LockedException(("Your account is locked. Please try again later"));
                }
            }
        } else {
            exception = new LockedException("Your account is inactive");
        }
        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request, response, exception);

    }
}
