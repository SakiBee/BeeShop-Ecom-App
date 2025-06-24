package com.sakibee.impl;

import com.sakibee.model.User;
import com.sakibee.repository.UserRepository;
import com.sakibee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        user.setRole("ROLE_USER");
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        User saveUser = userRepository.save(user);
        return saveUser;
    }
}
