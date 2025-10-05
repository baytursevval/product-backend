package com.sevval.productsDemo.controller;

import com.sevval.productsDemo.dto.ProductResponse;
import com.sevval.productsDemo.service.GoldPriceService;
import com.sevval.productsDemo.service.ProductLoader;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class ProductController {

    private final ProductLoader loader;
    private final GoldPriceService goldPriceService;

    public ProductController(ProductLoader loader, GoldPriceService goldPriceService) {
        this.loader = loader;
        this.goldPriceService = goldPriceService;
    }

    @GetMapping("/api/products")
    public List<ProductResponse> getProducts(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minPopularity
    ) {
        double goldPricePerGram = goldPriceService.getGoldPriceUsdPerGram();

        List<ProductResponse> list = loader.getProducts().stream().map(p -> {
            double popularityDecimal = p.getPopularityScore(); // JSON’dan 0.0–1.0 geliyor
            double rawPrice = (popularityDecimal + 1.0) * p.getWeight() * goldPricePerGram;
            double priceRounded = BigDecimal.valueOf(rawPrice)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();

            double popularityOutOf5 = Math.round((popularityDecimal * 5.0) * 10.0) / 10.0;

            return new ProductResponse(
                    p.getName(),
                    priceRounded,
                    popularityOutOf5,
                    p.getImages()
            );
        }).collect(Collectors.toList());

        // opsiyonel filtreleme
        return list.stream().filter(resp -> {
            boolean ok = true;
            if (minPrice != null) ok = ok && resp.getPriceUsd() >= minPrice;
            if (maxPrice != null) ok = ok && resp.getPriceUsd() <= maxPrice;
            if (minPopularity != null) {
                // minPopularity 0.0–1.0 arası, önce 5 üzerinden değere çeviriyoruz
                //double minPopOutOf5 = minPopularity * 5.0;
                //ok = ok && resp.getPopularityOutOf5() >= Math.round(minPopOutOf5 * 10.0) / 10.0;
                ok = ok && resp.getPopularityOutOf5() >= minPopularity;
            }
            return ok;
        }).collect(Collectors.toList());
    }
}
