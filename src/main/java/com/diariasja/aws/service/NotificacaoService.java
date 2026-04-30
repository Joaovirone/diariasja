package com.diariasja.aws.service;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {

    @Autowired
    private SqsTemplate sqsTemplate;

    public void notificarContratante(String emailDestino, String mensagem) {
        // Na AWS, criaremos uma fila chamada "diarias-ja-notificacoes"
        String nomeDaFila = "diarias-ja-notificacoes"; 
        
        String payload = String.format("{\"email\": \"%s\", \"mensagem\": \"%s\"}", emailDestino, mensagem);
        
        // Envia para o Amazon SQS
        sqsTemplate.send(nomeDaFila, payload);
    }
}