package com.sevval.productsDemo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class GoldPriceService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${metals.api.key}")
    private String metalsApiKey;

    @Value("${gold.fallback.usdPerGram}")
    private double fallbackUsdPerGram;

    private static final double TROY_OUNCE_IN_GRAMS = 31.1034768;

    public double getGoldPriceUsdPerGram() {
        try {
            if (metalsApiKey != null && !metalsApiKey.isBlank()) {
                String url = "https://api.metalpriceapi.com/v1/latest?api_key=" + metalsApiKey + "&base=USD&currencies=XAU";

                Map<String, Object> resp = restTemplate.getForObject(url, Map.class);

                if (resp != null && resp.containsKey("rates")) {
                    Map<String, Object> rates = (Map<String, Object>) resp.get("rates");
                    Object xauObj = rates.get("XAU");
                    if (xauObj != null) {
                        double xauPerUsd = ((Number) xauObj).doubleValue(); // e.g. 0.00048 = XAU per USD
                        double pricePerOunceUsd = 1.0 / xauPerUsd;
                        double pricePerGramUsd = pricePerOunceUsd / TROY_OUNCE_IN_GRAMS;
                        BigDecimal bd = BigDecimal.valueOf(pricePerGramUsd).setScale(6, RoundingMode.HALF_UP);
                        return bd.doubleValue();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fallbackUsdPerGram;
    }
}
