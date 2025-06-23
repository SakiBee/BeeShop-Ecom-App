package com.sakibee.impl;

import com.sakibee.model.User;
import com.sakibee.repository.UserRepository;
import com.sakibee.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        User saveUser = userRepository.save(user);
        return saveUser;
    }
}
