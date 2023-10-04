package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

@Service
public class AccountService {
    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    public Account createAccount(Account account) {
        if (account.getUsername().isEmpty()) {
            return null;
        }
        if (account.getPassword().length() < 4) {
            return null;
        }
        return this.accountRepository.save(account);
    }

    public Account login(Account account) {
        return this.accountRepository.findAccountByUsernameAndPassword(account.getUsername(), account.getPassword());
    }

    public AccountRepository getAccountRepository() {
        return this.accountRepository;
    }
}
