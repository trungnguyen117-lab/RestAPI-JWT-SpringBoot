package com.example.restapijwt.repository;

import com.example.restapijwt.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}