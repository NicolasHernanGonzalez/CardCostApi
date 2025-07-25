package com.cardcostapi.infrastructure;

import java.util.Map;

public interface ICache {

        public String get(String key);

        public void clear();

        public void put(String key, String value);

        //Testing purposes
        void putAll (Map<String, String> map);

        //Testing purposes
        public Map<String, String> getCache();

}
