package com.sakibee.service;

import com.sakibee.model.User;

import java.util.List;

public interface UserService {

    public User saveUser(User user);
    public User getUserByEmail(String email);
    public List<User> getAllUsers(String role);
    public Boolean deleteById(int id);
    public Boolean updateAccountStatus(int id, Boolean status);

    public void increaseFailedAttempt(User user);
    public void userAccountLock(User user);
    public boolean unlockAccountTimeExpired(User user);
    public void resetAttempt(int userId);

    public void updateUserResetToken(String email, String token);

    public User getUserByToken(String token);

    public User updateUser(User user);

}
