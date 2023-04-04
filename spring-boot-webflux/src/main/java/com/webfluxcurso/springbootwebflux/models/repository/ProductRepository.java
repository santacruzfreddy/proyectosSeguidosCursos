package com.webfluxcurso.springbootwebflux.models.repository;

import com.webfluxcurso.springbootwebflux.models.documents.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}
