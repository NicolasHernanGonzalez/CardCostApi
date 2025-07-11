package com.cardcostapi.infrastructure;

public interface ILock {
    public Object aquire(String key);
}
