/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity signup(UserEntity userEntity) {

        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserEntity getUserByUsername(String username) {

        try {
            return entityManager.createNamedQuery("userByUsername", UserEntity.class).setParameter("username", username).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public UserEntity getUserByEmail(String email) {

        try {
            return entityManager.createNamedQuery("userByEmailId", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public UserAuthEntity createAuthToken(UserAuthEntity authEntity) {

        entityManager.persist(authEntity);
        return authEntity;
    }

    public UserAuthEntity updateAuthToken(UserAuthEntity authEntity) {

        entityManager.merge(authEntity);
        return authEntity;
    }

    public UserAuthEntity getUserAuthByToken(String token) {

        try {
            return entityManager.createNamedQuery("userAuthByToken", UserAuthEntity.class).setParameter("token", token).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

}
