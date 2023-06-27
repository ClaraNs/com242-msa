package br.com.criandoapi.projeto.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.servlet.ServletContext;
import javax.servlet.http.Part;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class UploadController {

    @Autowired
    private ServletContext servletContext;

    @GetMapping("/upload")
    @ResponseBody
    public String doGet() {
        return "<html><body><h1>Olá, estou funcionando!</h1></body></html>";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("pdfFile") Part filePart) throws IOException {
        // Obtenha o nome do arquivo
        String fileName = getFileName(filePart);

        // Obtenha o diretório de implantação do aplicativo
        String appPath = servletContext.getRealPath("");

        // Crie o caminho absoluto para salvar o arquivo na pasta de implantação
        String uploadDirectory = appPath + "/uploads";
        Path filePath = Path.of(uploadDirectory, fileName);

        // Crie o diretório de upload, se não existir
        createUploadDirectory(uploadDirectory);

        // Salve o arquivo no diretório de uploads
        try (InputStream inputStream = filePart.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Retorne a mensagem de sucesso
        return "Arquivo " + fileName + " enviado com sucesso!";
    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] elements = contentDisposition.split(";");
        for (String element : elements) {
            if (element.trim().startsWith("filename")) {
                return element.substring(element.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private void createUploadDirectory(String directory) throws IOException {
        Path uploadPath = Path.of(directory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        // Obtenha o diretório de implantação do aplicativo
        String appPath = servletContext.getRealPath("");

        // Crie o caminho absoluto para o arquivo
        String filePath = appPath + "/uploads/" + fileName;

        // Carregue o arquivo como um recurso
        Resource resource = new UrlResource(Paths.get(filePath).toUri());

        // Verifique se o arquivo existe
        if (resource.exists()) {
            // Defina o cabeçalho da resposta para fazer o download do arquivo
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");

            // Crie a resposta com o recurso e os cabeçalhos adequados
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } else {
            // Caso o arquivo não exista, retorne uma resposta de erro
            return ResponseEntity.notFound().build();
        }
    }
}
