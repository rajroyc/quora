/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    @Autowired
    UserDao userDao;

    @Autowired
    PasswordCryptographyProvider passwdCrypProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate(String username, String password) throws AuthenticationFailedException {

        UserEntity userEntity = userDao.getUserByUsername(username);
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }
        String encryptPass = passwdCrypProvider.encrypt(password, userEntity.getSalt());
        if (encryptPass.equals(password)) {
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptPass);
            String jwtToken = jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt);
            UserAuthEntity userAuth = new UserAuthEntity();
            userAuth.setAccessToken(jwtToken);
            userAuth.setLoginAt(now.toLocalDateTime());
            userAuth.setExpiresAt(expiresAt.toLocalDateTime());
            userAuth.setUser(userEntity);
            userAuth.setUuid(UUID.randomUUID().toString());
            return userDao.createAuthToken(userAuth);

        } else {
            throw new AuthenticationFailedException("ATH-001", "Password failed");
        }

    }


}
