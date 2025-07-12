package com.cardcostapi.infrastructure;

public interface IRateLimitService {

    public boolean canProceed(String key);
    public void registerKey(String key);
}
