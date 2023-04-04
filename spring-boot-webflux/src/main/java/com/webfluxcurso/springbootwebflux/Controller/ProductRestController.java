package com.webfluxcurso.springbootwebflux.Controller;

import com.webfluxcurso.springbootwebflux.models.documents.Product;
import com.webfluxcurso.springbootwebflux.models.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductRepository productRepository;
    
    @GetMapping()
    public Flux<Product> indeFlux(){
        Flux<Product> products = productRepository.findAll().map(
                product -> {
                    product.setName(product.getName().toUpperCase());
                    return product;
                }
        ).doOnNext(product -> log.info(product.getName()));
        return products;
    }


    @GetMapping("/{id}")
    public Mono<Product> indeFlux(@PathVariable String id){
        //Mono<Product> product = productRepository.findById(id);         
        Flux<Product> products = productRepository.findAll();
        Mono<Product> product = products.filter(product1 -> product1.getId().equals(id)).next()
                .doOnNext(producta -> log.info(producta.getName()));
        return product;
    }

}
