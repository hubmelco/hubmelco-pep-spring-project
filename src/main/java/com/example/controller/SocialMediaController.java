package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;

import com.example.exception.DuplicateEntityException;
import com.example.exception.UnauthorizedException;

import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * TOoDO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
@DependsOn({"accountService", "messageService"})
public class SocialMediaController {
    private AccountService accountService;
    private MessageService messageService;

    @Autowired
    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    @PostMapping("register")
    private void register(@RequestBody Account account) {
        if (this.accountService.getAccountRepository().findAccountByUsername(account.getUsername()) != null) {
            throw new DuplicateEntityException("Duplicate account");
        }

        Account createdAccount = accountService.createAccount(account);
        if (createdAccount == null) {
            throw new RuntimeException("Bad Request");
        }
    }


    @PostMapping("login")
    private @ResponseBody Account login(@RequestBody Account account) {
        Account loggedInAccount = accountService.login(account);
        if (loggedInAccount != null) {
            return loggedInAccount;
        } 
        throw new UnauthorizedException("Unauthorized");
    }

    @PostMapping("messages")
    private @ResponseBody Message createMessages(@RequestBody Message message)  {
        Message createdMessage = this.messageService.createMessage(message);
        if (createdMessage != null) {
            return createdMessage;
        }
        
        throw new RuntimeException("Bad Request");
    }

    @GetMapping("messages")
    private @ResponseBody List<Message> getMessages() {
        return this.messageService.getAllMessages();
    }

    @GetMapping("messages/{message_id}")
    private @ResponseBody Message getMessage(@PathVariable int message_id) {
        Message message = this.messageService.getMessageById(message_id);
        return message;
    }
    
    @DeleteMapping("messages/{message_id}")
    private @ResponseBody int deleteMessage(@PathVariable int message_id) {
        int rowsAffected = this.messageService.deleteMessageById(message_id);
        return rowsAffected;
    }

    @PatchMapping("messages/{message_id}")
    private @ResponseBody int patchMessage(@PathVariable int message_id, @RequestBody Message message) {
        int rowsAffected = this.messageService.updateMessageById(message_id, message.getMessage_text());
        return rowsAffected;
    }

    @GetMapping("accounts/{account_id}/messages")
    private @ResponseBody List<Message> getAccountMessages(@PathVariable int account_id) {
        return this.messageService.getMessagesByPostedBy(account_id);
    }

    @ExceptionHandler(DuplicateEntityException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody String handleDuplicate(DuplicateEntityException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody String handleUnauthorized(UnauthorizedException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody String handleBadRequest(RuntimeException ex) {
        return ex.getMessage();
    }
}

