/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerDeleteResponse;
import com.upgrad.quora.api.model.AnswerDetailsResponse;
import com.upgrad.quora.api.model.AnswerEditResponse;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/")
public class AnswerController {

    @Autowired
    AnswerBusinessService ansService;

    @Autowired
    QuestionBusinessService questionService;

    @RequestMapping(path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestHeader("authorization") final String authorization, @PathVariable("questionId") final String quesUuid, final AnswerRequest answerRequest) throws AuthorizationFailedException, InvalidQuestionException {
        
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity.setUuid(UUID.randomUUID().toString());
        ansService.createAnswerForQuestion(quesUuid, answerEntity, authorization);
        AnswerResponse answerRsp = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerRsp, HttpStatus.CREATED);

    }

    @RequestMapping(path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(@RequestHeader("authorization") final String authorization, @PathVariable("answerId") final String ansUuid, final AnswerRequest answerRequest) throws AuthorizationFailedException, AnswerNotFoundException {

        ansService.editAnswer(ansUuid, answerRequest.getAnswer(), authorization);
        AnswerEditResponse answerRsp = new AnswerEditResponse().id(ansUuid).status("ANSWER EDITED");
        return new ResponseEntity<>(answerRsp, HttpStatus.OK);

    }

    @RequestMapping(path = "/answer/delete/{answerId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@RequestHeader("authorization") final String authorization, @PathVariable("answerId") final String ansUuid) throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity ansEntity = ansService.deleteAnswer(ansUuid, authorization);
        AnswerDeleteResponse answerRsp = new AnswerDeleteResponse().id(ansEntity.getUuid()).status("ANSWER DELETED");
        return new ResponseEntity<>(answerRsp, HttpStatus.OK);

    }

    @RequestMapping(path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDetailsResponse> getAllAnswer(@RequestHeader("authorization") final String authorization, @PathVariable("questionId") final String quesUuid) throws AuthorizationFailedException, InvalidQuestionException {

        AnswerEntity ansEntity = ansService.getAllAnswerForQuestion(quesUuid, authorization);
        QuestionEntity quesEntity = questionService.getQuestionByUuid(quesUuid);
        AnswerDetailsResponse answerRsp = new AnswerDetailsResponse().id(quesUuid).questionContent(quesEntity.getContent()).answerContent(ansEntity.getAnswer());
        return new ResponseEntity<>(answerRsp, HttpStatus.OK);

    }

}
