package com.cardcostapi.external;

import com.cardcostapi.config.BinLookupApiConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BinLookupClientImpl implements IBinLookupClient {

    private final RestTemplate restTemplate;
    private final BinLookupApiConfig properties;

    public BinLookupClientImpl(BinLookupApiConfig properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    public BinDataResponse getBinData(String bin){
        System.out.println("========CALLING BIN LOOKUP API=============");
        String url = String.format("%s/%s", properties.getBaseUrl(), bin);
        return restTemplate.getForObject(url, BinDataResponse.class);
    }
}
