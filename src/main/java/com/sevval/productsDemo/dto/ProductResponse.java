package com.sevval.productsDemo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ProductResponse {

    private String name;
    private double priceUsd;
    private double popularityOutOf5;
    private Map<String, String> images;

    public ProductResponse(String name, double priceUsd, double popularityOutOf5, Map<String,String> images) {
        this.name = name;
        this.priceUsd = priceUsd;
        this.popularityOutOf5 = popularityOutOf5;
        this.images = images;
    }
}
