/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

@RequestMapping(path = "/")
public class AnswerController {

    @Autowired
    AnswerService ansService;

    @Autowired
    QuestionService questionService;

    @RequestMapping(path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestHeader("authorization") final String authorization, @PathVariable("questionId") final String quesUuid, final AnswerRequest answerRequest) throws AuthorizationFailedException, InvalidQuestionException {
        
        String authorizationToken = authorization.split("Bearer")[1];

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity.setUuid(UUID.randomUUID().toString());
        ansService.createAnswerForQuestion(quesUuid, answerEntity, authorizationToken);
        AnswerResponse answerRsp = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerRsp, HttpStatus.CREATED);

    }

    @RequestMapping(path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(@RequestHeader("authorization") final String authorization, @PathVariable("answerId") final String ansUuid, final AnswerEditRequest answerEditRequest) throws AuthorizationFailedException, AnswerNotFoundException {

        String authorizationToken = authorization.split("Bearer")[1];
        ansService.editAnswer(ansUuid, answerEditRequest.getContent(), authorizationToken);
        AnswerEditResponse answerRsp = new AnswerEditResponse().id(ansUuid).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerRsp, HttpStatus.OK);

    }

    @RequestMapping(path = "/answer/delete/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@RequestHeader("authorization") final String authorization, @PathVariable("answerId") final String ansUuid) throws AuthorizationFailedException, AnswerNotFoundException {

        String authorizationToken = authorization.split("Bearer")[1];
        AnswerEntity ansEntity = ansService.deleteAnswer(ansUuid, authorizationToken);
        AnswerDeleteResponse answerRsp = new AnswerDeleteResponse().id(ansEntity.getUuid()).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerRsp, HttpStatus.OK);

    }

    @RequestMapping(path = "answer/all/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDetailsResponse> getAllAnswer(@RequestHeader("authorization") final String authorization, @PathVariable("questionId") final String quesUuid) throws AuthorizationFailedException, InvalidQuestionException {

        String authorizationToken = authorization.split("Bearer")[1];
        List<AnswerEntity> ansEntity = ansService.getAllAnswerForQuestion(quesUuid, authorizationToken);
        QuestionEntity quesEntity = questionService.getQuestionByUuid(quesUuid);

        String answerStr = new String();
        for (int i = 0; i < ansEntity.size(); i++) {
            answerStr = answerStr.concat(ansEntity.get(i).getAnswer());
        }

        AnswerDetailsResponse answerRsp = new AnswerDetailsResponse().id(quesUuid).questionContent(quesEntity.getContent()).answerContent(answerStr);
        return new ResponseEntity<>(answerRsp, HttpStatus.OK);

    }

}
