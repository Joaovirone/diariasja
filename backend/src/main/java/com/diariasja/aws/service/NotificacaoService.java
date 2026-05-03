package com.diariasja.aws.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.awspring.cloud.sqs.operations.SqsTemplate;

@Service
public class NotificacaoService {

    @Autowired
    private SqsTemplate sqsTemplate;

    public void notificarContratante(String emailDestino, String mensagem) {
        // Na AWS, criaremos uma fila chamada "diarias-ja-notificacoes"
        String nomeDaFila = "diarias-ja-notificacoes"; 
        
        String payload = String.format("{\"email\": \"%s\", \"mensagem\": \"%s\"}", emailDestino, mensagem);
        
        try {
            // Envia para o Amazon SQS
            sqsTemplate.send(nomeDaFila, payload);
        } catch (Exception e) {
            // Log do erro sem lançar exceção
            System.err.println("Erro ao enviar notificação para fila SQS: " + e.getMessage());
            e.printStackTrace();
            // Falha de notificação não deve quebrar o fluxo principal
        }
    }
}