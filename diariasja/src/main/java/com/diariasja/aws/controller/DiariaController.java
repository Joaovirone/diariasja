package com.diariasja.aws.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diariasja.aws.dto.DiariaRequestDTO;
import com.diariasja.aws.entity.Diaria;
import com.diariasja.aws.service.DiariaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/diarias")
public class DiariaController {

    @Autowired
    private DiariaService service;

    @PostMapping("/solicitar")
    public ResponseEntity<Diaria> solicitar(@Valid @RequestBody DiariaRequestDTO dto) {
        Diaria novaDiaria = service.solicitarServico(dto);
        return new ResponseEntity<>(novaDiaria, HttpStatus.CREATED);
    }
}