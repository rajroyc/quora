/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {

        if (userDao.getUserByUsername(userEntity.getUsername()) != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }

        if (userDao.getUserByEmail(userEntity.getEmail()) != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }
        String password = userEntity.getPassword();
        if (password == null) {
            userEntity.setPassword("proman@123");
        }
        String[] encryptedTxt = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedTxt[0]);
        userEntity.setPassword(encryptedTxt[1]);
        return userDao.signup(userEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signOut(String token) throws SignOutRestrictedException {

        UserAuthEntity userAuth = userDao.getUserAuthByToken(token);
        if (userAuth == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }

        UserEntity userEntity = userAuth.getUser();

        userAuth.setLogoutAt(LocalDateTime.now());
        userDao.updateAuthToken(userAuth);

        return userEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUser(String targetUuid, String token) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthEntity userAuth = userDao.getUserAuthByToken(token);
        if (userAuth == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuth.getLogoutAt() != null && userAuth.getLogoutAt().isAfter(userAuth.getLoginAt())) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        UserEntity userEntity = userDao.getUserByUuid(targetUuid);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }
        return userEntity;
    }

}
