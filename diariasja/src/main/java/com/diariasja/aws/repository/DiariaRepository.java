package com.diariasja.aws.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.diariasja.aws.entity.Diaria;

public interface DiariaRepository extends JpaRepository<Diaria, Long> {
    
}
