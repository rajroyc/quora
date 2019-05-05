/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.upgrad.quora.service.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author nmu
 */
@Entity
@Table(name = "answer", schema = "quora")
public class AnswerEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "ans")
    private String answer;

    @Column(name = "date")
    private ZonedDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userId;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionEntity question;

    public String getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getAnswer() {
        return answer;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public UserEntity getUserId() {
        return userId;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }

}
