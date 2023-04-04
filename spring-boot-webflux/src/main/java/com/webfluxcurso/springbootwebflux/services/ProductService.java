package com.webfluxcurso.springbootwebflux.services;

import com.webfluxcurso.springbootwebflux.models.documents.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    
    public Flux<Product> findAll();

    public Flux<Product> findAllToUpperCaseName();

    public Flux<Product> findAllToUpperCaseNameWithRepeat();
    public Mono<Product> findById(String id);
    
    public  Mono<Product> save(Product product);
    
    public  Mono<Void> delete(Product product);
    
}
