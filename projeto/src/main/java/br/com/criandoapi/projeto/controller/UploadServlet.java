/*package br.com.criandoapi.projeto.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletContext;


@CrossOrigin("*")
@WebServlet("/upload")
@MultipartConfig
public class UploadServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<body>");
        out.println("<h1>Olá, estou funcionando!</h1>");
        out.println("</body>");
        out.println("</html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtenha o arquivo enviado pelo formulário
        Part filePart = request.getPart("pdfFile");
        String fileName = getFileName(filePart);

        // Obtenha o diretório de implantação do aplicativo
        ServletContext context = getServletContext();
        String appPath = context.getRealPath("");
        
        // Crie o caminho absoluto para salvar o arquivo na pasta de implantação
        String uploadDirectory = appPath + "uploads";
        Path filePath = Path.of(uploadDirectory, fileName);
        
        // Salve o arquivo no diretório de uploads
        try (InputStream inputStream = filePart.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Redirecione para o servlet de download
        String downloadUrl = request.getContextPath() + "/download?file=" + fileName;
        response.sendRedirect(downloadUrl);
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
}*/


