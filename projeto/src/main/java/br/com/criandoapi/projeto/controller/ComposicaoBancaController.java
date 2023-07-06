package br.com.criandoapi.projeto.controller;

import java.util.List;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.criandoapi.projeto.DAO.IBanca;
import br.com.criandoapi.projeto.DAO.IComposicaoBanca;
import br.com.criandoapi.projeto.model.Artigo;
import br.com.criandoapi.projeto.model.Banca;
import br.com.criandoapi.projeto.model.ComposicaoBanca;
import br.com.criandoapi.projeto.model.Professor;
import br.com.criandoapi.projeto.model.StatusBanca;
import br.com.criandoapi.projeto.service.ArtigoService;
import br.com.criandoapi.projeto.service.BancaService;
import br.com.criandoapi.projeto.service.ComposicaoBancaService;
import br.com.criandoapi.projeto.service.EmailService;
import br.com.criandoapi.projeto.service.ProfessorService;
import br.com.criandoapi.projeto.service.StatusBancaService;

@RestController
@CrossOrigin("*")
@RequestMapping
public class ComposicaoBancaController {

    private IComposicaoBanca dao;
    private IBanca bancaRepository;
    private ProfessorService professorService;
    private BancaService bancaService;
    private EmailService emailService;
    private final ComposicaoBancaService composicaoBancaService;
    private final ArtigoService artigoService;
    private final StatusBancaService statusBancaService;

    @Autowired
    public ComposicaoBancaController(IComposicaoBanca dao, IBanca bancaRepository, ProfessorService professorService,
            BancaService bancaService, EmailService emailService, ComposicaoBancaService composicaoBancaService,
            ArtigoService artigoService, StatusBancaService statusBancaService) {
        this.dao = dao;
        this.bancaRepository = bancaRepository;
        this.professorService = professorService;
        this.bancaService = bancaService;
        this.emailService = emailService;
        this.composicaoBancaService = composicaoBancaService;
        this.artigoService = artigoService;
        this.statusBancaService = statusBancaService;
    }

    @GetMapping("/composicao")
    public List<ComposicaoBanca> listaBancas() {
        return (List<ComposicaoBanca>) dao.findAll();
    }

    // Adicionar professor na banca
    @PostMapping("/composicao/{idBanca}/cadastrar")
    public String adicionaProfessorBanca(@PathVariable Integer idBanca,
            @RequestParam("nome") String nome) {
        Professor professor = new Professor();
        professor = professorService.findProfessorByNome(nome);

        ComposicaoBanca composicao = new ComposicaoBanca();
        composicao.setBanca(bancaService.getBancaById(idBanca));
        composicao.setProfessorAvaliador(professor);
        composicao.setNota(-1);

        dao.save(composicao);

        String destinatario = professor.getEmail();

        String assunto = "Cadastro em Banca de Avaliação - MSA";
        String mensagem = "Prezado professor,\n\n\tVocê foi adiconado a banca responsável por avaliar o artigo \""
                + bancaService.getBancaById(idBanca).getArtigoAvaliado().getTitulo()
                + "\". Agora é necessário inserir as datas e horários disponíveis para que o sistema encontre a melhor data para a avaliação.\n\nObrigado.";

        try {
            emailService.enviarEmail(destinatario, assunto, mensagem);
            System.out.println("E-mail enviado com sucesso.");
        } catch (MessagingException e) {
            System.out.println("Erro ao enviar o e-mail: " + e.getMessage());
        }

        return " " + nome + " cadastrado com sucesso na banca " + idBanca;
    }

    @PostMapping("professor/{matricula}/banca/{idBanca}/cadastrar/avaliacao")
    public String cadastrarNota(@PathVariable String matricula,
            @PathVariable Integer idBanca,
            @RequestParam("nota") Float nota,
            @RequestParam("correcao") Boolean correcao,
            @RequestParam("consideracao") String consideracao) {
        ComposicaoBanca composicao = composicaoBancaService.getComposicaoByBancaId(idBanca, matricula);
        Banca banca = bancaService.getBancaById(idBanca);
        StatusBanca statusAtual = banca.getStatus();
        // Banca agendada
        if (statusAtual.getId() == 2 || statusAtual.getId() == 4) {
            if (composicao != null) {
                composicao.setNota(nota);
                composicao.setConsideracoes(consideracao);

                if(correcao){
                    banca.setCorrecao(true);
                }

                dao.save(composicao);

                Integer idArtigo = banca.getArtigoAvaliado().getIdArtigo();
                
                Float notaFinal = artigoService.getNotaByIdArtigo(idArtigo);
                System.out.println(notaFinal);
                Boolean aprovado = false;

                if(notaFinal >= 6.f)
                    aprovado = true;
                
                // Todos os membros da banca já avaliaram
                if( notaFinal >= 0.f){
                    Integer novoStatus = -1;
                    Boolean corrigir = banca.getCorrecao();

                    if(banca.getStatus().getId() == 2){// Primeira avaliação do TFG
                        if(aprovado){
                            if(correcao){// Aprovado com correções
                            artigoService.mudarStatusArtigo(idArtigo, 4);
                            novoStatus = 3;
                            } else { // Aprovado direto
                                novoStatus = 5;
                                artigoService.mudarStatusArtigo(idArtigo, 6);
                            } 
                        } else{ //Reprovado
                            System.out.println("ELE FOI REPROVADO");
                            if(corrigir){ //Possibilidade de correção
                                System.out.println("Novo status = " + novoStatus);
                                System.out.println("PODE CORRIGIR");
                                novoStatus = 4;
                                artigoService.mudarStatusArtigo(idArtigo, 7);
                            } else{ // Reprovado direto 
                                System.out.println("JÁ ERA");
                                novoStatus = 6;
                                artigoService.mudarStatusArtigo(idArtigo, 10);
                            }
                        }  
                    } else if(banca.getStatus().getId() == 4){ // Segunda avaliação
                        if(aprovado){
                            if(correcao){// Aprovado com correções
                            artigoService.mudarStatusArtigo(idArtigo, 4);
                            novoStatus = 3;
                            } else { // Aprovado direto
                                novoStatus = 5;
                                artigoService.mudarStatusArtigo(idArtigo, 6);
                            } 
                        } else{ // Reprovado sem mais chances
                            System.out.println("JÁ ERA");
                            novoStatus = 6;
                            artigoService.mudarStatusArtigo(idArtigo, 10);
                        }
                    }
                        
                    System.out.println("Novo status = " + novoStatus);
                    StatusBanca status = statusBancaService.findStatusBancaById(novoStatus);
                    banca.setStatus(status);                    
                    bancaRepository.save(banca);

                    return "Todos os membros já avaliariam, novo status da banca = " + novoStatus;
                }
                return "Artigo avaliado com sucesso" + " nota final agora está " + notaFinal;
            } else {
                return "Erro ao encontrar artigo, favor verificar banca e matrícula.";
            }
        }
        return "TFG ainda não disponível para avaliação.";
    }
}
