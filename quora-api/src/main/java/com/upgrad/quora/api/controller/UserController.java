package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationBusinessService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import java.util.Base64;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    UserBusinessService userBusinessService;

    @Autowired
    AuthenticationBusinessService authService;


    @RequestMapping(path = "/user/signup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> userSignUp(SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        UserEntity usersEntity = new UserEntity();
        usersEntity.setUuid(UUID.randomUUID().toString());
        usersEntity.setFirstname(signupUserRequest.getFirstName());
        usersEntity.setLastname(signupUserRequest.getLastName());
        usersEntity.setUsername(signupUserRequest.getUserName());
        usersEntity.setPassword(signupUserRequest.getPassword());
        usersEntity.setAboutMe(signupUserRequest.getAboutMe());
        usersEntity.setContactNumber(signupUserRequest.getContactNumber());
        usersEntity.setEmail(signupUserRequest.getEmailAddress());
        usersEntity.setCountry(signupUserRequest.getCountry());
        usersEntity.setDob(signupUserRequest.getDob());
        usersEntity.setRole("nonadmin");

        UserEntity createdUsersEntity = userBusinessService.signup(usersEntity);
        SignupUserResponse signupUserResponse = new SignupUserResponse().id(createdUsersEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(signupUserResponse, HttpStatus.CREATED);
    }


    @RequestMapping(path = "/user/signin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> userSignIn(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        byte[] decoded = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedAuth = new String(decoded);
        String[] decodedArray = decodedAuth.split(":");
        UserAuthEntity userAuthEntity = authService.authenticate(decodedArray[0], decodedArray[1]);
        UserEntity usersEntity = userAuthEntity.getUser();
        SigninResponse signinResponse = new SigninResponse().id(userAuthEntity.getUuid()).message("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access_token", userAuthEntity.getAccessToken());
        return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK);
    }


    @RequestMapping(path = "/user/signout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> userSignOut(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {
        UserEntity userEntity = userBusinessService.signOut(authorization);
        SignoutResponse signoutResponse = new SignoutResponse().id(userEntity.getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }
}
