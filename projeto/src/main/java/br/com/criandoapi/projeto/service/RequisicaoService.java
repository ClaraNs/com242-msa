package br.com.criandoapi.projeto.service;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class RequisicaoService {
    public void realizaRequisicao(String email, String assunto, String mensagem) {
    try {
        // Cria um objeto JSON com os dados
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("email", email);
        jsonParams.put("assunto", assunto);
        jsonParams.put("mensagem", mensagem);

        // Cria a conexão HTTP
        URL url = new URL("http://localhost:8080/envia/email");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Escreve os dados JSON no corpo da requisição
        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] requestBody = jsonParams.toString().getBytes(StandardCharsets.UTF_8);
            outputStream.write(requestBody, 0, requestBody.length);
        }

        // Obtém a resposta da requisição
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
            System.out.println("Resposta: " + response.toString());
        } else {
            System.out.println("Falha na requisição. Código de resposta: " + responseCode);
        }

        connection.disconnect();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}

