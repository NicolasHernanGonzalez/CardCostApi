package com.cardcostapi.services;

import com.cardcostapi.exception.TooManyRequestsException;
import com.cardcostapi.external.BinDataResponse;
import com.cardcostapi.external.IBinLookupClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;

@Component
@Primary
@Profile("fake_client")
public class FakeBinLookupClient implements IBinLookupClient {

    private final int maxRequests = 5;
    private final long windowMillis = 3 * 60 * 1000;
    private final Queue<Instant> accessTimestamps = new LinkedList<>();

    @Override
    public BinDataResponse getBinData(String bin) {
        Instant now = Instant.now();

        while (!accessTimestamps.isEmpty() &&
                accessTimestamps.peek().isBefore(now.minusMillis(windowMillis))) {
            accessTimestamps.poll();
        }

        if (accessTimestamps.size() >= maxRequests) {
            throw new TooManyRequestsException("API fake: rate limit exceeded. Bin: " + bin);
        }

        accessTimestamps.offer(now);

        return new BinDataResponse(new BinDataResponse.Country("AR"));
    }
}
