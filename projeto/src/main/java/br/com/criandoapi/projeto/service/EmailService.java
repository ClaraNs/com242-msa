package br.com.criandoapi.projeto.service;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {
    private static final String SMTP_HOST = "smtp.example.com"; // Host SMTP do seu provedor de e-mail
    private static final String SMTP_PORT = "587"; // Porta SMTP do seu provedor de e-mail
    private static final String SMTP_USERNAME = "seu-email@example.com"; // Seu endereço de e-mail
    private static final String SMTP_PASSWORD = "sua-senha"; // Sua senha de e-mail

    public void enviarEmail(String destinatario, String assunto, String mensagem) throws MessagingException {
        // Configurar as propriedades do JavaMail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        // Criar uma autenticação de e-mail
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        };

        // Criar uma sessão de e-mail
        Session session = Session.getInstance(props, auth);

        // Criar a mensagem de e-mail
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SMTP_USERNAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        message.setSubject(assunto);
        message.setText(mensagem);

        // Enviar o e-mail
        Transport.send(message);
    }
}

/* Como chamar no momento adequado:
    EmailService emailService = new EmailService();
    String destinatario = "aluno@example.com";
    String assunto = "Correção do artigo";
    String mensagem = "Prezado aluno, seu artigo foi corrigido. Por favor, verifique as correções e faça as devidas alterações. Obrigado!";
    try {
        emailService.enviarEmail(destinatario, assunto, mensagem);
        System.out.println("E-mail enviado com sucesso.");
    } catch (MessagingException e) {
        System.out.println("Erro ao enviar o e-mail: " + e.getMessage());
    }
 */
