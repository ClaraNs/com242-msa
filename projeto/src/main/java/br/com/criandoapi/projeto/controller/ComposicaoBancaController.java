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
import br.com.criandoapi.projeto.service.RequisicaoService;
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
    private final RequisicaoService requisicaoService;

    @Autowired
    public ComposicaoBancaController(IComposicaoBanca dao, IBanca bancaRepository, ProfessorService professorService,
            BancaService bancaService, EmailService emailService, ComposicaoBancaService composicaoBancaService,
            ArtigoService artigoService, StatusBancaService statusBancaService, RequisicaoService requisicaoService) {
        this.dao = dao;
        this.bancaRepository = bancaRepository;
        this.professorService = professorService;
        this.bancaService = bancaService;
        this.emailService = emailService;
        this.composicaoBancaService = composicaoBancaService;
        this.artigoService = artigoService;
        this.statusBancaService = statusBancaService;
        this.requisicaoService = requisicaoService;
    }

    @GetMapping("/composicao")
    public List<ComposicaoBanca> listaBancas() {
        return (List<ComposicaoBanca>) dao.findAll();
    }

    // Adicionar professor na banca
    @PostMapping("/composicao/{idBanca}/cadastrar")
    public String adicionaProfessorBanca(@PathVariable Integer idBanca,
            @RequestParam("matricula") String matricula) {
        Professor professor = new Professor();
        professor = professorService.findProfessorByMatricula(matricula);

        ComposicaoBanca composicao = new ComposicaoBanca();
        composicao.setBanca(bancaService.getBancaById(idBanca));
        composicao.setProfessorAvaliador(professor);
        composicao.setNota(-1);

        dao.save(composicao);

        String email = professor.getEmail();
        System.out.println(email);
        String assunto = "Cadastro em Banca de Avaliação - MSA";
        String mensagem = "Prezado professor,\n\n\tVocê foi adiconado a banca responsável por avaliar o artigo \""
                + bancaService.getBancaById(idBanca).getArtigoAvaliado().getTitulo()
                + "\". Agora é necessário inserir as datas e horários disponíveis para que o sistema encontre a melhor data para a avaliação.\n\nObrigado.";

        requisicaoService.realizaRequisicao(email, assunto, mensagem);

        return " " + professor.getNome() + " cadastrado com sucesso na banca " + idBanca;
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

                if (correcao) {
                    banca.setCorrecao(true);
                }

                dao.save(composicao);

                Integer idArtigo = banca.getArtigoAvaliado().getIdArtigo();

                Float notaFinal = artigoService.getNotaByIdArtigo(idArtigo);
                System.out.println(notaFinal);
                Boolean aprovado = false;

                if (notaFinal >= 6.f)
                    aprovado = true;

                // Todos os membros da banca já avaliaram
                if (notaFinal >= 0.f) {
                    String email = banca.getArtigoAvaliado().getEnviadoPor().getEmail();
                    String email2 = banca.getArtigoAvaliado().getOrientador().getEmail();
                    String assunto = "";
                    String mensagem = " ";

                    Integer novoStatus = -1;
                    Boolean corrigir = banca.getCorrecao();

                    if (banca.getStatus().getId() == 2) {// Primeira avaliação do TFG
                        if (aprovado) {
                            if (correcao) {// Aprovado com correções
                                artigoService.mudarStatusArtigo(idArtigo, 4);
                                novoStatus = 3;

                                assunto = "Artigo Aprovado com Correções - MSA";
                                mensagem = "Prezado usuário,\n\n\tFoi lançada no sistema a nota para o artigo \""
                                        + bancaService.getBancaById(idBanca).getArtigoAvaliado().getTitulo()
                                        + "\". Agora são necessárias as correções sugeridas pelos membros da banca, em seguida o artigo já estará pronto. Parabéns.\n\nObrigado.";
                            } else { // Aprovado direto
                                novoStatus = 5;
                                artigoService.mudarStatusArtigo(idArtigo, 6);

                                assunto = "Artigo Aprovado - MSA";
                                mensagem = "Prezado usuário,\n\n\tFoi lançada no sistema a nota para o artigo \""
                                        + bancaService.getBancaById(idBanca).getArtigoAvaliado().getTitulo()
                                        + "\", seu TFG foi aprovado, parabéns.\n\nObrigado.";
                            }
                        } else { // Reprovado
                            System.out.println("ELE FOI REPROVADO");
                            if (corrigir) { // Possibilidade de correção
                                System.out.println("Novo status = " + novoStatus);
                                System.out.println("PODE CORRIGIR");
                                novoStatus = 4;
                                artigoService.mudarStatusArtigo(idArtigo, 7);

                                assunto = "Artigo Reprovado com Possíveis Correções - MSA";
                                mensagem = "Prezado usuário,\n\n\tFoi lançada no sistema a nota para o artigo \""
                                        + bancaService.getBancaById(idBanca).getArtigoAvaliado().getTitulo()
                                        + "\". Infelizmente, seu TFG não foi aprovado, mas ainda é possível uma resubmissão dentro do prazo.\n\nObrigado.";
                            } else { // Reprovado direto
                                System.out.println("JÁ ERA");
                                novoStatus = 6;

                                artigoService.mudarStatusArtigo(idArtigo, 10);
                                assunto = "Artigo Reprovado - MSA";
                                mensagem = "Prezado usuário,\n\n\tFoi lançada no sistema a nota para o artigo \""
                                        + bancaService.getBancaById(idBanca).getArtigoAvaliado().getTitulo()
                                        + "\". Infelizmente seu TFG não foi aprovado.\n\nObrigado.";
                            }
                        }
                    } else if (banca.getStatus().getId() == 4) { // Segunda avaliação
                        if (aprovado) {
                            if (correcao) {// Aprovado com correções
                                artigoService.mudarStatusArtigo(idArtigo, 4);
                                novoStatus = 3;

                                assunto = "Artigo Aprovado com Correções - MSA";
                                mensagem = "Prezado usuário,\n\n\tFoi lançada no sistema a nota para o artigo \""
                                        + bancaService.getBancaById(idBanca).getArtigoAvaliado().getTitulo()
                                        + "\". Agora são necessárias as correções sugeridas pelos membros da banca, em seguida o artigo já estará pronto. Parabéns.\n\nObrigado.";
                            } else { // Aprovado direto
                                novoStatus = 5;
                                artigoService.mudarStatusArtigo(idArtigo, 6);

                                assunto = "Artigo Aprovado - MSA";
                                mensagem = "Prezado usuário,\n\n\tFoi lançada no sistema a nota para o artigo \""
                                        + bancaService.getBancaById(idBanca).getArtigoAvaliado().getTitulo()
                                        + "\", seu TFG foi aprovado, parabéns.\n\nObrigado.";
                            }
                        } else { // Reprovado sem mais chances
                            System.out.println("JÁ ERA");
                            novoStatus = 6;
                            artigoService.mudarStatusArtigo(idArtigo, 10);

                            assunto = "Artigo Reprovado - MSA";
                            mensagem = "Prezado usuário,\n\n\tFoi lançada no sistema a nota para o artigo \""
                                    + bancaService.getBancaById(idBanca).getArtigoAvaliado().getTitulo()
                                    + "\". Infelizmente seu TFG não foi aprovado.\n\nObrigado.";
                        }
                    }

                    System.out.println("Novo status = " + novoStatus);
                    StatusBanca status = statusBancaService.findStatusBancaById(novoStatus);
                    banca.setStatus(status);
                    bancaRepository.save(banca);

                    requisicaoService.realizaRequisicao(email, assunto, mensagem);
                    requisicaoService.realizaRequisicao(email2, assunto, mensagem);
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
