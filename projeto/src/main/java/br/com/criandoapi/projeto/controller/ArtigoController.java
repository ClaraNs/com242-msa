package br.com.criandoapi.projeto.controller;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

import java.sql.Blob;
import javax.sql.rowset.serial.SerialBlob;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;

import br.com.criandoapi.projeto.DAO.IArtigo;
import br.com.criandoapi.projeto.model.Aluno;
import br.com.criandoapi.projeto.model.Artigo;
import br.com.criandoapi.projeto.model.Professor;
import br.com.criandoapi.projeto.model.StatusArtigo;
import br.com.criandoapi.projeto.service.AlunoService;
import br.com.criandoapi.projeto.service.ArtigoService;
import br.com.criandoapi.projeto.service.ProfessorService;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping
public class ArtigoController {

    private final IArtigo dao;
    private final ProfessorService professorService;
    private final AlunoService alunoService;
    private final ArtigoService artigoService;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    public ArtigoController(IArtigo dao, ProfessorService professorService, AlunoService alunoService,
            ArtigoService artigoService) {
        this.dao = dao;
        this.professorService = professorService;
        this.alunoService = alunoService;
        this.artigoService = artigoService;
    }

    @GetMapping("/artigo")
    public List<Artigo> listaArtigos() {
        return (List<Artigo>) dao.findAll();
    }

    @GetMapping("/artigopormatricula/{matricula}")
    public List<Artigo> listaArtigosAluno(@PathVariable String matricula) {
        return artigoService.getArtigosPorMatriculaAluno(matricula);
    }

    @GetMapping("/artigopororientador/{matricula}")
    public List<Artigo> listaArtigosOrientador(@PathVariable String matricula) {
        return artigoService.getArtigosPorMatriculaOrientador(matricula);
    }

    @PostMapping("/artigoupload")
    public Artigo uploadFile(@RequestParam("pdfFile") MultipartFile file,
            @RequestParam("titulo") String titulo,
            @RequestParam("resumo") String resumo,
            @RequestParam("enviadopor") String enviadopor,
            @RequestParam("nomeOrientador") String nomeOrientador) throws IOException {
        /*
         * // Obtenha o nome do arquivo
         * String fileName = getFileName(filePart);
         * 
         * // Obtenha o diretório de implantação do aplicativo
         * String appPath = servletContext.getRealPath("");
         * 
         * // Crie o caminho absoluto para salvar o arquivo na pasta de implantação
         * String uploadDirectory = appPath + "/uploads";
         * Path filePath = Path.of(uploadDirectory, fileName);
         * 
         * // Crie o diretório de upload, se não existir
         * createUploadDirectory(uploadDirectory);
         * 
         * // Salve o arquivo no diretório de uploads
         * try (InputStream inputStream = filePart.getInputStream()) {
         * Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
         * }
         * 
         * // Construa a URL de download do arquivo
         * String downloadUrl = "/download/" + fileName;
         */
        byte[] arquivoBytes = file.getBytes();

        // Encontrar o objeto Aluno com base na matrícula
        Aluno aluno = alunoService.findAlunoByMatricula(enviadopor);

        // Encontre o objeto Professor com base no nome
        Professor professor = professorService.findProfessorByNome(nomeOrientador);

        // Crie um novo objeto Artigo com os campos preenchidos
        Artigo artigo = new Artigo();
        StatusArtigo status = new StatusArtigo();
        status.setId(0);
        artigo.setTitulo(titulo);
        artigo.setArquivo(arquivoBytes);
        artigo.setResumo(resumo);
        artigo.setDataEnvio(LocalDateTime.now());
        artigo.setAlteracao(LocalDateTime.now());
        artigo.setStatus(status);
        artigo.setEnviadoPor(aluno);
        artigo.setOrientador(professor);

        // Salve o artigo com o caminho do arquivo e a URL de download no banco de dados
        Artigo novoArtigo = dao.save(artigo);

        artigo.setUrl("/download/" + artigo.getIdArtigo());

        // Salve o artigo com o caminho do arquivo e a URL de download no banco de dados
        novoArtigo = dao.save(artigo);

        return novoArtigo;
    }

    @GetMapping("artigodownload/{idArtigo}")
     public ResponseEntity<byte[]> downloadFile(@PathVariable Integer idArtigo) {
        // Obtenha o objeto Artigo do banco de dados com base no ID
        Artigo artigo = dao.findById(idArtigo).orElse(null);

        if (artigo != null && artigo.getArquivo() != null) {
            // Defina os cabeçalhos da resposta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Defina o nome do arquivo para o download
            headers.setContentDispositionFormData("attachment", "artigo.pdf");

            // Retorne a resposta com o conteúdo do arquivo PDF
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(artigo.getArquivo());
        } else {
            // Caso o artigo não seja encontrado ou não tenha um arquivo associado,
            // retorne uma resposta de erro
            return ResponseEntity.notFound().build();
        }
    }

    /*
     * private String getFileName(Part part) {
     * String contentDisposition = part.getHeader("content-disposition");
     * String[] elements = contentDisposition.split(";");
     * for (String element : elements) {
     * if (element.trim().startsWith("filename")) {
     * return element.substring(element.indexOf('=') + 1).trim().replace("\"", "");
     * }
     * }
     * return null;
     * }
     * 
     * private void createUploadDirectory(String directory) throws IOException {
     * Path uploadPath = Path.of(directory);
     * if (!Files.exists(uploadPath)) {
     * Files.createDirectories(uploadPath);
     * }
     * }
     */

}
