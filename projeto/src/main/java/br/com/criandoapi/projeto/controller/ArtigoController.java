package br.com.criandoapi.projeto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.servlet.ServletContext;
import javax.servlet.http.Part;

import org.springframework.web.bind.annotation.PathVariable;

import br.com.criandoapi.projeto.DAO.IArtigo;
import br.com.criandoapi.projeto.config.DatabaseConfig;
import br.com.criandoapi.projeto.model.Aluno;
import br.com.criandoapi.projeto.model.Artigo;
import br.com.criandoapi.projeto.model.Professor;


import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/artigo")
public class ArtigoController {

    private final DatabaseConfig databaseConfig;
    private final IArtigo dao;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    public ArtigoController(DatabaseConfig databaseConfig, IArtigo dao) {
        this.databaseConfig = databaseConfig;
        this.dao = dao;
    }

    @GetMapping
    public List<Artigo> listaArtigos() {
        return (List<Artigo>) dao.findAll();
    }

    @GetMapping("/aluno/{matricula}")
    public Aluno findAlunoByMatricula(@PathVariable String matricula) {
        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM aluno WHERE matricula = ?")) {
            statement.setString(1, matricula);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Aluno aluno = new Aluno();
                aluno.setMatricula(resultSet.getString("matricula"));
                aluno.setNome(resultSet.getString("nome"));
                aluno.setEmail(resultSet.getString("email"));
                // Preencha os outros atributos do aluno, se houver

                return aluno;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return null; // Retorna null se o aluno não for encontrado
    }

    @GetMapping("/professor/nome")
    public Professor findProfessorByNome(@RequestParam("nome") String nomeProfessor) {
        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT * FROM professor WHERE nome LIKE ?")) {
            statement.setString(1, "%" + nomeProfessor + "%");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Professor professor = new Professor();
                professor.setMatricula(resultSet.getString("matricula"));
                professor.setNome(resultSet.getString("nome"));
                professor.setEmail(resultSet.getString("email"));
                // Preencha os outros atributos do professor, se houver

                return professor;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return null; // Retorna null se o professor não for encontrado
    }

    @PostMapping("/upload")
    public Artigo uploadFile(@RequestParam("pdfFile") Part filePart,
            @RequestParam("titulo") String titulo,
            @RequestParam("resumo") String resumo,
            @RequestParam("enviadopor") String enviadopor,
            @RequestParam("nomeOrientador") String nomeOrientador) throws IOException {
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

        // Construa a URL de download do arquivo
        String downloadUrl = "/download/" + fileName;

        /*
         * int STATUS_PENDENTE = 0;
         * 
         * // Obtém a lista de valores possíveis de status do banco de dados
         * List<StatusArtigo> listaStatus = statusArtigoDao.obterListaStatus();
         * 
         * // Encontra o status com ID igual a 0 (Pendente)
         * StatusArtigo statusPendente = listaStatus.stream()
         * .filter(status -> status.getId() == STATUS_PENDENTE)
         * .findFirst()
         * .orElse(null);
         */

        // Encontrar o objeto Aluno com base na matrícula
        Aluno aluno = findAlunoByMatricula(enviadopor);

        // Encontre o objeto Professor com base na matrícula
        Professor professor = findProfessorByNome(nomeOrientador);

        // Crie um novo objeto Artigo com os campos preenchidos
        Artigo artigo = new Artigo();
        artigo.setTitulo(titulo);
        artigo.setUrl(downloadUrl);
        artigo.setResumo(resumo);
        artigo.setDataEnvio(LocalDateTime.now());
        artigo.setAlteracao(LocalDateTime.now());
        // artigo.setStatus(statusPendente);
        artigo.setEnviadoPor(aluno);
        artigo.setOrientador(professor);
        

        // Salve o artigo com o caminho do arquivo e a URL de download no banco de dados
        Artigo novoArtigo = dao.save(artigo);

        return novoArtigo;
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

}
