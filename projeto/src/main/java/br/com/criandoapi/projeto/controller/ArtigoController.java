package br.com.criandoapi.projeto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import br.com.criandoapi.projeto.DAO.IArtigo;
import br.com.criandoapi.projeto.model.Aluno;
import br.com.criandoapi.projeto.model.Artigo;
import br.com.criandoapi.projeto.model.Professor;
import br.com.criandoapi.projeto.model.StatusArtigo;
import br.com.criandoapi.projeto.service.AlunoService;
import br.com.criandoapi.projeto.service.ArtigoService;
import br.com.criandoapi.projeto.service.EmailService;
import br.com.criandoapi.projeto.service.ProfessorService;
import br.com.criandoapi.projeto.service.StatusArtigoService;
import br.com.criandoapi.projeto.service.VersaoService;

import javax.mail.MessagingException;

@RestController
@CrossOrigin("*")
@RequestMapping
public class ArtigoController {

    private final IArtigo dao;
    private final ProfessorService professorService;
    private final AlunoService alunoService;
    private final ArtigoService artigoService;
    private final StatusArtigoService statusArtigoService;
    private final EmailService emailService;
    private final VersaoService versaoService;

    @Autowired
    public ArtigoController(IArtigo dao, ProfessorService professorService, AlunoService alunoService,
            ArtigoService artigoService, StatusArtigoService statusArtigoService, EmailService emailService,
            VersaoService versaoService) {
        this.dao = dao;
        this.professorService = professorService;
        this.alunoService = alunoService;
        this.artigoService = artigoService;
        this.statusArtigoService = statusArtigoService;
        this.emailService = emailService;
        this.versaoService = versaoService;
    }

    @GetMapping("/artigo")
    public List<Artigo> listaArtigos() {
        return (List<Artigo>) dao.findAll();
    }

    @GetMapping("/aluno/{matricula}/artigos")
    public List<Artigo> listaArtigosAluno(@PathVariable String matricula) {
        return artigoService.getArtigosPorMatriculaAluno(matricula);
    }

    // Lista artigos que o aluno necessita realizar alguma correção
    @GetMapping("/aluno/{matricula}/artigosagurdandocorrecao")
    public List<Artigo> listaArtigosAlunoPendentes(@PathVariable String matricula) {
        return artigoService.getArtigosPorMatriculaAlunoPendente(matricula);
    }

    // Lista artigos por aluno
    @GetMapping("/orientador/{matricula}/artigos")
    public List<Artigo> listaArtigosOrientador(@PathVariable String matricula) {
        return artigoService.getArtigosPorMatriculaOrientador(matricula);
    }

    // Lista artigos agurdando liberação do orientador
    @GetMapping("/orientador/{matricula}/artigos/aguardandocorrecao")
    public List<Artigo> listaArtigosPendentesOrientador(@PathVariable String matricula) {
        return artigoService.getArtigosPendentesPorMatriculaOrientador(matricula);
    }

    // Lista artigos que esperam a avaliação daquele professor (banca)
    @GetMapping("/artigo/aguardandocorrecao/professor/{matricula}")
    public List<Artigo> listaArtigosAseremAvaliados(@PathVariable String matricula){
        return artigoService.getArtigoEsperandoAvaliacao(matricula);
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
        status = statusArtigoService.findStatusArtigoById(0);

        artigo.setTitulo(titulo);
        artigo.setArquivo(arquivoBytes);
        artigo.setResumo(resumo);
        artigo.setDataEnvio(LocalDateTime.now());
        artigo.setAlteracao(LocalDateTime.now());
        artigo.setStatus(status);
        artigo.setNotaFinal(-1.f);
        artigo.setEnviadoPor(aluno);
        artigo.setOrientador(professor);

        // Salve o artigo com o caminho do arquivo e a URL de download no banco de dados
        Artigo novoArtigo = dao.save(artigo);
        artigo.setUrl("/artigo/" + artigo.getIdArtigo() + "/download");

        // Salve o artigo com o caminho do arquivo e a URL de download no banco de dados
        novoArtigo = dao.save(artigo);

        versaoService.criaVersao(novoArtigo);

        String destinatario = aluno.getEmail();
        String assunto = "Artigo Submetido - MSA";
        String mensagem = "Prezado aluno,\n\n\tSeu artigo \"" + artigo.getTitulo() + "\" foi submetido na plataforma e está aguardando correção de seu orientador.\n\nObrigado.";

        try {
            emailService.enviarEmail(destinatario, assunto, mensagem);
            System.out.println("E-mail enviado com sucesso.");
        } catch (MessagingException e) {
            System.out.println("Erro ao enviar o e-mail: " + e.getMessage());
        }

        return novoArtigo;
    }

    // Reenvia artigo com correção e seta estado correspondente
    @PostMapping("/artigo/{idArtigo}/reenviar")
    public Artigo uploadFile(@PathVariable Integer idArtigo,
            @RequestParam("pdfFile") MultipartFile file) throws IOException {
        byte[] arquivoBytes = file.getBytes();
        
        // Encontra o artigo
        Artigo artigo = artigoService.findArtigoByid(idArtigo);
        StatusArtigo statusAtual = artigo.getStatus();
        Integer idAtual = statusAtual.getId();
        System.out.println(statusAtual);

        StatusArtigo status = new StatusArtigo();
        
        // Corrigindo pela primeira vez
        if( idAtual == 1)
            status = statusArtigoService.findStatusArtigoById(2); //Aguardando correção
        else if(idAtual == 4){// Corrigindo o que a banca sugeriu (aprovado)
            status = statusArtigoService.findStatusArtigoById(5);
        } else if(idAtual == 7){ //Reprovado mas com tentativa de melhorar
            status = statusArtigoService.findStatusArtigoById(8);
            artigo.setNotaFinal(-1.f);
        }
        
        // Modifica o arquivo, a data da última modificação e o status
        artigo.setArquivo(arquivoBytes);
        artigo.setAlteracao(LocalDateTime.now());
        artigo.setStatus(status);
        artigo.setUrl("/artigo/" + artigo.getIdArtigo() + "/download");

        // Salva alterações
        Artigo novoArtigo = dao.save(artigo);
        versaoService.criaVersao(novoArtigo);

        String destinatario = alunoService.getEmailAlunoByArtigoId(idArtigo);
        String assunto = "Artigo Modificado - MSA";
        String mensagem = "Prezado aluno,\n\n\tUm novo arquivo do artigo \"" + artigo.getTitulo() + "\" foi submetido na plataforma e está aguardando correção de seu orientador.\n\nObrigado.";

        try {
            emailService.enviarEmail(destinatario, assunto, mensagem);
            System.out.println("E-mail enviado com sucesso.");
        } catch (MessagingException e) {
            System.out.println("Erro ao enviar o e-mail: " + e.getMessage());
        }

        return artigo;
    }

    // Professor orientador avalia o artigo
    @PostMapping("artigo/{idArtigo}/avaliacao")
    public ResponseEntity<String> avaliarArtigo(@PathVariable Integer idArtigo,
            @RequestParam("correcao") Boolean correcao,
            @RequestParam("consideracoes") String consideracoes) {
        // Obtenha o objeto Artigo do banco de dados com base no ID
        Artigo artigo = new Artigo();
        artigo = dao.findById(idArtigo).orElse(null);
        

        if (artigo != null) {
            Integer statusAtual = artigo.getStatus().getId();
            Integer novoStatus = -1;

            // Atualize o status e as considerações do artigo
            // Integer idStatus = (Integer) requestBody.get("idStatus");
            StatusArtigo status = new StatusArtigo();

            if(correcao){
                if(statusAtual == 0) // Primeiro envio
                    novoStatus = 1;
                else if(statusAtual == 2) // Ainda possui correções a serem feitas
                    novoStatus = 1; 
                else if(statusAtual == 5) //Correções sugeridas pela banca e ainda falta correções
                    novoStatus = 4;
                else if(statusAtual == 8) // Reprovado, com possibilidade de correção, mas falta alterações
                    novoStatus = 7;
            } else {
                if(statusAtual == 0) // Primeiro envio
                    novoStatus = 3;
                else if(statusAtual == 2) // Pronto para banca
                    novoStatus = 3; 
                else if(statusAtual == 5) // Correções sugeridas pela banca feitas com sucesso
                    novoStatus = 6;
                else if(statusAtual == 8) // Reprovado, com possibilidade de recuperação, pronto para reavaliação
                    novoStatus = 9;
            }

            status = statusArtigoService.findStatusArtigoById(novoStatus);
            artigo.setStatus(status);
            artigo.setConsideracoes(consideracoes);
            artigo.setAlteracao(LocalDateTime.now());

            // Salve as alterações no artigo
            dao.save(artigo);

            String destinatario = alunoService.getEmailAlunoByArtigoId(idArtigo);
            String assunto = "Mudança no Status do Artigo - MSA";
            String mensagem = "";

            if(novoStatus == 1){
                mensagem = "Prezado aluno,\n\n\tSeu artigo \"" + artigo.getTitulo() + "\" foi revisado pelo seu orientador. Por favor, verifique as correções e faça as devidas alterações.\n\nObrigado.";
            } else if(novoStatus == 3){
                mensagem = "Prezado aluno,\n\n\tSeu artigo \"" + artigo.getTitulo() + "\" foi revisado pelo seu orientador e está pronto para ser avaliado pela banca.\n\nObrigado.";
            }
  

        try {
            emailService.enviarEmail(destinatario, assunto, mensagem);
            System.out.println("E-mail enviado com sucesso.");
        } catch (MessagingException e) {
            System.out.println("Erro ao enviar o e-mail: " + e.getMessage());
        }

            return ResponseEntity.ok("Artigo avaliado com sucesso.");
        } else {
            // Caso o artigo não seja encontrado, retorne uma resposta de erro
            return ResponseEntity.notFound().build();
        }
    }

    // Link de download
    @GetMapping("artigo/{idArtigo}/download")
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
