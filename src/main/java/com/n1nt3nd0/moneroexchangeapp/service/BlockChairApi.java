package com.n1nt3nd0.moneroexchangeapp.service;

import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockChairApi implements Serializable {
    private final RestTemplate restTemplate;

    public String fetchLastMarketPriceXmr(){
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl("https://api.blockchair.com/monero/stats");
        Object response = restTemplate.getForObject(uriComponentsBuilder.toUriString(), Object.class);
        String lastXmrMarketPrice = JsonPath.parse(response).read("$.context.market_price_usd", String.class);
        log.info(lastXmrMarketPrice);
        return lastXmrMarketPrice;
    }
}
