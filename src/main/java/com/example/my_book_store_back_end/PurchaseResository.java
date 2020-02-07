package com.example.my_book_store_back_end;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource
public interface PurchaseResository extends JpaRepository<Purchase, Long> {
//    Author findByEmail(@Param("email") String email);

}