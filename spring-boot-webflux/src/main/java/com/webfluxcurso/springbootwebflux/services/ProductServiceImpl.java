package com.webfluxcurso.springbootwebflux.services;

import com.webfluxcurso.springbootwebflux.models.documents.Product;
import com.webfluxcurso.springbootwebflux.models.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService{
    
    @Autowired
    ProductRepository productRepository;
    
    @Override
    public Flux<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Flux<Product> findAllToUpperCaseName() {
        return  productRepository.findAll()
                .map(product -> {product.setName(product.getName().toUpperCase());
                    return product;
                });      
    }

    @Override
    public Flux<Product> findAllToUpperCaseNameWithRepeat() {
         return  productRepository.findAll()
                .map(product -> {product.setName(product.getName().toUpperCase());
                    return product;
                }).repeat(10000);      
    }
    

    @Override
    public Mono<Product> findById(String id) {
        return productRepository.findById(id);
    }

    @Override
    public Mono<Product> save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Mono<Void> delete(Product product) {
        return productRepository.delete(product);
    }
}
