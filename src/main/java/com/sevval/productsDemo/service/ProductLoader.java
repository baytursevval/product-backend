package com.sevval.productsDemo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sevval.productsDemo.model.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Component
public class ProductLoader {
    private List<Product> products = Collections.emptyList();

    @PostConstruct
    public void load() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = new ClassPathResource("products.json").getInputStream();
            products = mapper.readValue(is, new TypeReference<List<Product>>(){});
            System.out.println("Loaded products: " + products.size());
        } catch (Exception e) {
            e.printStackTrace();
            products = Collections.emptyList();
        }
    }

    public List<Product> getProducts() { return products; }
}
