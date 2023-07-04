package br.com.criandoapi.projeto.service;

import java.io.IOException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final String SMTP_HOST = "smtp.titan.email"; // Host SMTP do Titan
    private static final String SMTP_PORT = "465"; // Porta SMTP do Titan
    private static final String SMTP_USERNAME = "equipe@it-a-kitchen.live"; // Seu endereço de e-mail do Titan
    private static final String SMTP_PASSWORD = "unifei2023."; // Sua senha de e-mail do Titan
    private static final String IMAP_HOST = "imap.titan.email"; // Host IMAP do Titan
    private static final String IMAP_PORT = "993"; // Porta IMAP do Titan

    public void enviarEmail(String destinatario, String assunto, String mensagem) throws MessagingException {
        // Configurar as propriedades do JavaMail para envio de e-mail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.enable", "true");

        // Criar uma autenticação de e-mail para envio de e-mail
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        };

        // Criar uma sessão de e-mail para envio de e-mail
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

    public void receberEmail() throws MessagingException {
        // Configurar as propriedades do JavaMail para recebimento de e-mail
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imap.host", IMAP_HOST);
        props.put("mail.imap.port", IMAP_PORT);
        props.put("mail.imap.ssl.enable", "true");

        // Criar uma sessão de e-mail para recebimento de e-mail
        Session session = Session.getDefaultInstance(props);

        // Conectar ao servidor de e-mail IMAP
        Store store = session.getStore("imaps");
        store.connect(IMAP_HOST, SMTP_USERNAME, SMTP_PASSWORD);

        // Abrir a pasta de entrada (inbox)
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        // Ler os e-mails na pasta de entrada
        Message[] messages = inbox.getMessages();
        for (Message message : messages) {
            System.out.println("De: " + message.getFrom()[0]);
            System.out.println("Assunto: " + message.getSubject());
            System.out.println("Data: " + message.getSentDate());

            try {
                Object content = message.getContent();
                if (content instanceof String) {
                    System.out.println("Conteúdo: " + (String) content);
                } else if (content instanceof Multipart) {
                    handleMultipart((Multipart) content);
                }
            } catch (IOException e) {
                System.out.println("Erro ao ler o conteúdo do e-mail: " + e.getMessage());
            }

            System.out.println("-----------------------------------");
        }

        // Fechar a pasta de entrada e desconectar do servidor
        inbox.close(false);
        store.close();
    }

    private void handleMultipart(Multipart multipart) throws MessagingException, IOException {
        int count = multipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            String contentType = bodyPart.getContentType();
            if (contentType.startsWith("text/plain") || contentType.startsWith("text/html")) {
                System.out.println("Conteúdo: " + bodyPart.getContent());
            } else {
                // Lógica para lidar com anexos, se necessário
            }
        }
    }
}
