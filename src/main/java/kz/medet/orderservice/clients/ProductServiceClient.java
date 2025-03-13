package kz.medet.orderservice.clients;

import kz.medet.orderservice.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("/products/{id}")
    String getProductById(@PathVariable Long id);

    @PostMapping("/products")
    ProductDto createProduct(@RequestParam String productName,
                             @RequestParam double productPrice);

    @PostMapping("/products/byIds")
    List<ProductDto> getProductsByIds(@RequestBody List<Long> productIds);
}
