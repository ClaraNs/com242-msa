package br.com.criandoapi.projeto.controller;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.criandoapi.projeto.DAO.IComposicaoBanca;
import br.com.criandoapi.projeto.model.Banca;
import br.com.criandoapi.projeto.model.ComposicaoBanca;
import br.com.criandoapi.projeto.model.Professor;
import br.com.criandoapi.projeto.service.BancaService;
import br.com.criandoapi.projeto.service.ComposicaoBancaService;
import br.com.criandoapi.projeto.service.EmailService;
import br.com.criandoapi.projeto.service.ProfessorService;

@RestController
@CrossOrigin("*")
@RequestMapping
public class ComposicaoBancaController {

    private IComposicaoBanca dao;
    private ProfessorService professorService;
    private BancaService bancaService;
    private EmailService emailService;
    private final ComposicaoBancaService composicaoBancaService;

    @Autowired
    public ComposicaoBancaController(IComposicaoBanca dao, ProfessorService professorService,
            BancaService bancaService, EmailService emailService, ComposicaoBancaService composicaoBancaService) {
        this.dao = dao;
        this.professorService = professorService;
        this.bancaService = bancaService;
        this.emailService = emailService;
        this.composicaoBancaService = composicaoBancaService;
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
    public String cadastrarAvaliacao(@PathVariable String matricula,
        @PathVariable Integer idBanca,
        @RequestParam("nota") Float nota,
        @RequestParam("consideracao") String consideracao){
            ComposicaoBanca composicao = composicaoBancaService.getComposicaoByBancaId(idBanca, matricula);

            if(composicao != null){
                composicao.setNota(nota);
                composicao.setConsideracoes(consideracao);

                dao.save(composicao);
                return "Artigo avaliado com sucesso";
            }

            return "Erro ao encontrar artigo, favor verificar banca e matrícula.";
    }
}
