package br.com.criandoapi.projeto.controller;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.criandoapi.projeto.service.EmailService;

@RestController
@CrossOrigin("*")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/envia/email")
    public String enviaEmail(@RequestParam("email") String email,
            @RequestParam("assunto") String assunto,
            @RequestParam("mensagem") String mensagem){

            try {
                emailService.enviarEmail(email, assunto, mensagem);
                return "E-mail enviado com sucesso.";
            } catch (MessagingException e) {
                return"Erro ao enviar o e-mail: " + e.getMessage();
            }
    }

}
