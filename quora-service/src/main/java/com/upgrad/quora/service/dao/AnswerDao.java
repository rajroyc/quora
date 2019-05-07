/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswerForQuestion(AnswerEntity answerEntity) {

        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity getAnswerByUuid(String uuid) {

        try {
            return entityManager.createNamedQuery("getAnswerByUuid", AnswerEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public AnswerEntity editAnswer(AnswerEntity answerEntity) {

        return entityManager.merge(answerEntity);
    }

    public AnswerEntity deleteAnswer(AnswerEntity answerEntity) {

        entityManager.remove(answerEntity);
        return answerEntity;
    }

}
