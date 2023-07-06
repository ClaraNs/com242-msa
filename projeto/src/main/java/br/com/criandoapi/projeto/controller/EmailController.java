package br.com.criandoapi.projeto.controller;

import javax.mail.MessagingException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public String enviaEmail(@RequestBody String requestBody) {
        try {
            JSONObject json = new JSONObject(requestBody);
            String email = json.getString("email");
            String assunto = json.getString("assunto");
            String mensagem = json.getString("mensagem");

            emailService.enviarEmail(email, assunto, mensagem);
            return "E-mail enviado com sucesso.";
        } catch (MessagingException e) {
            return "Erro ao enviar o e-mail: " + e.getMessage();
        }
    }

}
