package com.getarrays.supportportalapplication.service;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class LoginAttemptService {

    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5; //defining the max number of times that a user can try to enter a password
    private static final int ATTEMPT_INCREMENT = 1; //everytime they try to login and it doesn't work we increment by 1 until it gets to 5
    private LoadingCache<String, Integer> loginAttemptCache;  //defining a key and a value for the cache (key is the user, attempts will be the value)

    //initializing the cache (loading cache from guava)
    public LoginAttemptService() {
        super();
        loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, MINUTES) //time that the cache expires after
                .maximumSize(100)   //at any given time we will have 100 entries inside of the actual cache
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return 0;
                    }
                });
    }

    public void evictUserFromLoginAttemptToCache(String username) { //username is our key inside of the loading cache
        loginAttemptCache.invalidate(username); //will remove the user from the cache
    }

    public void addUserToLoginAttemptCache(String username) {
        int attempts = 0;
        try {
            attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(username); //adding the user to the cache, before we do that we add 1 to the number of attempts within the 15 minutes
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        loginAttemptCache.put(username, attempts);
    }

    public boolean hasExceededMaxAttempt(String username) {
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS; //when we call get, it will return the number of attempts
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
