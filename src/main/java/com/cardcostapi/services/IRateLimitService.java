package com.cardcostapi.services;

public interface IRateLimitService {

    public boolean canProceed(String key);
    public void registerKey(String key);

}
