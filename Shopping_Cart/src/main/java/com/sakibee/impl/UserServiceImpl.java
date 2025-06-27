package com.sakibee.impl;

import com.sakibee.model.User;
import com.sakibee.repository.UserRepository;
import com.sakibee.service.UserService;
import com.sakibee.utils.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User getUserByToken(String token) {
        return userRepository.findByResetToken(token);
    }

    @Override
    public void updateUserResetToken(String email, String token) {
        User user = userRepository.findByEmail(email);
        user.setResetToken(token);
        userRepository.save(user);
    }
    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void increaseFailedAttempt(User user) {
        int attempt = user.getFailedAttempt() + 1;
        user.setFailedAttempt(attempt);
        userRepository.save(user);
    }

    @Override
    public void userAccountLock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);
    }

    @Override
    public boolean unlockAccountTimeExpired(User user) {
        long lockTime = user.getLockTime().getTime();
        long unlockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
        long currentTime = System.currentTimeMillis();

        if(unlockTime < currentTime) {
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            user.setLockTime(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void resetAttempt(int userId) {
        // This method is not implemented, but its dependency is handled.
    }

    @Override
    public Boolean updateAccountStatus(int id, Boolean status) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) {
            User user1 = user.get();
            user1.setIsEnable(status);
            userRepository.save(user1);
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteById(int id) {
        User user = userRepository.findById(id).orElse(null);
        if (!ObjectUtils.isEmpty(user)) {
            userRepository.delete(user);
            return true;
        }
        return false;
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user;
    }

    @Override
    public List<User> getAllUsers(String role) {
        return userRepository.findByRole(role);
    }

    @Override
    public User saveUser(User user) {
        user.setRole("ROLE_USER");
        user.setIsEnable(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        user.setLockTime(null);
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        User saveUser = userRepository.save(user);
        return saveUser;
    }


}