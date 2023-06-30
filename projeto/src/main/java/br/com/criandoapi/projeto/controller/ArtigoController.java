package br.com.criandoapi.projeto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.LocalDateTime;

import br.com.criandoapi.projeto.DAO.IArtigo;
import br.com.criandoapi.projeto.model.Aluno;
import br.com.criandoapi.projeto.model.Artigo;
import br.com.criandoapi.projeto.model.Professor;
import br.com.criandoapi.projeto.model.StatusArtigo;
import br.com.criandoapi.projeto.service.AlunoService;
import br.com.criandoapi.projeto.service.ArtigoService;
import br.com.criandoapi.projeto.service.ProfessorService;
import br.com.criandoapi.projeto.service.StatusArtigoService;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping
public class ArtigoController {

    private final IArtigo dao;
    private final ProfessorService professorService;
    private final AlunoService alunoService;
    private final ArtigoService artigoService;
    private final StatusArtigoService statusArtigoService;

    @Autowired
    public ArtigoController(IArtigo dao, ProfessorService professorService, AlunoService alunoService,
            ArtigoService artigoService, StatusArtigoService statusArtigoService) {
        this.dao = dao;
        this.professorService = professorService;
        this.alunoService = alunoService;
        this.artigoService = artigoService;
        this.statusArtigoService = statusArtigoService;
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

        artigo.setUrl("/artigodownload/" + artigo.getIdArtigo());

        // Salve o artigo com o caminho do arquivo e a URL de download no banco de dados
        novoArtigo = dao.save(artigo);

        return novoArtigo;
    }

    @PostMapping("artigoavaliacao/{idArtigo}")
    public ResponseEntity<String> avaliarArtigo(@PathVariable Integer idArtigo,
        @RequestParam("idStatus") Integer idStatus,
        @RequestParam("consideracoes") String consideracoes)  {
        // Obtenha o objeto Artigo do banco de dados com base no ID
        Artigo artigo = dao.findById(idArtigo).orElse(null);

        if (artigo != null) {
            // Atualize o status e as considerações do artigo
            //Integer idStatus = (Integer) requestBody.get("idStatus");
            StatusArtigo status = new StatusArtigo();
            status = statusArtigoService.findStatusArtigoById(idStatus);

            //String consideracoes = (String) requestBody.get("consideracoes");
            artigo.setConsideracoes(consideracoes);
            artigo.setAlteracao(LocalDateTime.now());

            // Salve as alterações no artigo
            dao.save(artigo);

            return ResponseEntity.ok("Artigo avaliado com sucesso.");
        } else {
            // Caso o artigo não seja encontrado, retorne uma resposta de erro
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("artigodownload/{idArtigo}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Integer idArtigo) {
        // Obtenha o objeto Artigo do banco de dados com base no ID
        Artigo artigo = dao.findById(idArtigo).orElse(null);

        if (artigo != null && artigo.getArquivo() != null) {
            // Define os cabeçalhos da resposta
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Define o nome do arquivo para o download
            headers.setContentDispositionFormData("attachment", artigo.getTitulo() + ".pdf");

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

}
