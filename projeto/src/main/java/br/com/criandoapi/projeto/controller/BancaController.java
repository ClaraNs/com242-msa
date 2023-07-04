package br.com.criandoapi.projeto.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

import br.com.criandoapi.projeto.DAO.IBanca;
import br.com.criandoapi.projeto.model.Artigo;
import br.com.criandoapi.projeto.model.Banca;
import br.com.criandoapi.projeto.model.Disponibilidade;
import br.com.criandoapi.projeto.model.Professor;
import br.com.criandoapi.projeto.model.StatusBanca;
import br.com.criandoapi.projeto.service.AlunoService;
import br.com.criandoapi.projeto.service.ArtigoService;
import br.com.criandoapi.projeto.service.BancaService;
import br.com.criandoapi.projeto.service.DisponibilidadeService;
import br.com.criandoapi.projeto.service.EmailService;
import br.com.criandoapi.projeto.service.ProfessorService;
import br.com.criandoapi.projeto.service.StatusBancaService;

@RestController
@CrossOrigin("*")
@RequestMapping
public class BancaController {

    private IBanca dao;
    private final ProfessorService professorService;
    private final ArtigoService artigoService;
    private final BancaService bancaService;
    private final DisponibilidadeService disponibilidadeService;
    private final StatusBancaService statusBancaService;
    private final AlunoService alunoService;
    private final EmailService emailService;

    @Autowired
    public BancaController(IBanca dao, ProfessorService professorService, ArtigoService artigoService,
            BancaService bancaService, DisponibilidadeService disponibilidadeService, 
            StatusBancaService statusBancaService, AlunoService alunoService, EmailService emailService) {
        this.dao = dao;
        this.professorService = professorService;
        this.artigoService = artigoService;
        this.bancaService = bancaService;
        this.disponibilidadeService = disponibilidadeService;
        this.statusBancaService = statusBancaService;
        this.alunoService = alunoService;
        this.emailService = emailService;
    }

    @GetMapping("/banca")
    public List<Banca> listaBancas() {
        return (List<Banca>) dao.findAll();
    }

    @GetMapping("/bancaporprofessor/{matricula}")
    public List<Banca> listaBancasPorProfessor(@PathVariable String matricula) {
        return bancaService.getBancasPorProfessor(matricula);
    }

    @PostMapping("/bancacadastro")
    public String cadastrarBancas(@RequestParam("professor1") String nomeProfessor1,
            @RequestParam("professor2") String nomeProfessor2,
            @RequestParam("idartigo") Integer idartigo) {

        // Encontre os objetos Professor com base nos nomes informados
        Professor professor1 = professorService.findProfessorByNome(nomeProfessor1);
        Professor professor2 = professorService.findProfessorByNome(nomeProfessor2);

        if (professor1 != null && professor2 != null) {

            LocalDateTime dia = LocalDateTime.now();
            // Crie as novas bancas
            Banca banca1 = new Banca();
            Artigo artigo = artigoService.findArtigoByid(idartigo);
            StatusBanca status = new StatusBanca();
            status = statusBancaService.findStatusArtigoById(0);
            /*status = statusArtigoService.findStatusArtigoById(idStatus);
            artigo.setStatus(status); // ? */
            //status.setId(0); // Set the ID of the desired status

            banca1.setProfessorAvaliador(professor1);
            banca1.setDataRegistro(dia);
            banca1.setDataAtualizacao(dia);
            banca1.setArtigoAvaliado(artigo);
            banca1.setStatus(status);

            Banca banca2 = new Banca();
            banca2.setProfessorAvaliador(professor2);
            banca2.setDataRegistro(dia);
            banca2.setDataAtualizacao(dia);
            banca2.setArtigoAvaliado(artigo);
            banca2.setStatus(status);

            // Salve as bancas no banco de dados
            dao.save(banca1);
            dao.save(banca2);

            // mudar para email do Orientador depois
            String destinatario = alunoService.getEmailAlunoByArtigoId(idartigo);
            String destinatario2 = alunoService.getEmailOrientadorByArtigoId(idartigo);
            String assunto = "Solicitação de Banca - MSA";
            String mensagem = "Prezado usuário,\n\n\tA solicitação da banca para defesa do artigo \"" + artigo.getTitulo() + "\" foi submetido na plataforma e está aguardando liberação do coordenador de TFG.\n\nObrigado.";

            try {
                emailService.enviarEmail(destinatario, assunto, mensagem);
                emailService.enviarEmail(destinatario2, assunto, mensagem);
                System.out.println("E-mail enviado com sucesso.");
            } catch (MessagingException e) {
                System.out.println("Erro ao enviar o e-mail: " + e.getMessage());
            }

            return "Bancas cadastradas com sucesso.";
        } else {
            return "Erro ao cadastrar as bancas. Verifique os nomes dos professores informados.";
        }
    }

    // Verificar se existe alguma data que coincide, se sim, marca a avaliacao
    @PostMapping("/bancacadastraavaliacao/{idArtigo}")
    public String cadastrarAvaliacao(@PathVariable Integer idArtigo) {

        List<Integer> bancas = bancaService.getBancasByArtigoAvaliado(idArtigo);

        // pegar professores da banca e o orientador, pegar a disponibilidade deles
        List<Disponibilidade> disponibilidadesProfessor1 = new ArrayList<>();
        List<Disponibilidade> disponibilidadesProfessor2 = new ArrayList<>();
        List<Disponibilidade> disponibilidadesProfessor3 = new ArrayList<>();
        int counter = 1; // Counter variable

        for (Integer bancaId : bancas) {
            Banca banca = bancaService.getBancaById(bancaId);
            Professor professor = new Professor();
            professor = banca.getProfessorAvaliador();
            String matricula = professor.getMatricula();

            //List<Disponibilidade> disponibilidades;

            if (counter == 1) {
                disponibilidadesProfessor1 = disponibilidadeService.getDisponibilidadesPorMatricula(matricula, bancaId);
            } else if (counter == 2) {
                disponibilidadesProfessor2 = disponibilidadeService.getDisponibilidadesPorMatricula(matricula, bancaId);
            }

            // Increment the counter
            counter = (counter % 2) + 1;

        }

        Artigo artigo = new Artigo();
        artigo = artigoService.findArtigoByid(idArtigo);
    
        Professor orientador = new Professor();
        orientador = artigo.getOrientador();

        // Como fica o orientador?? Está disponível em relação a qual banca? A banca de menor número
        disponibilidadesProfessor3 = disponibilidadeService.getDisponibilidadesPorMatricula(orientador.getMatricula(), bancaService.getPrimeiraBancaByArtigoAvaliado(idArtigo));

        // chamar encontrarDataHoraEmComum passando as 3 disponibilidades
        LocalDateTime dataAvaliacao = disponibilidadeService.encontrarDataEmComum(disponibilidadesProfessor1, disponibilidadesProfessor2, disponibilidadesProfessor3);
        //LocalTime horaAvaliacao = disponibilidadeService.encontrarHoraEmComum(disponibilidadesProfessor1, disponibilidadesProfessor2, disponibilidadesProfessor3/*, dataAvaliacao*/);

        if( dataAvaliacao != null){
            
            for (Integer bancaId : bancas) {
                Banca banca = new Banca();
                banca = bancaService.getBancaById(bancaId);
                banca.setDataAvaliacao(dataAvaliacao);
                StatusBanca status = new StatusBanca();
                status = statusBancaService.findStatusArtigoById(2);
                banca.setStatus(status);
                
                dao.save(banca);
            }

            String destinatario = alunoService.getEmailAlunoByArtigoId(idArtigo);
            String destinatario2 = alunoService.getEmailOrientadorByArtigoId(idArtigo);
            //getEmailBancaByArtigoId
            String assunto = "Data para defesa confirmada - MSA";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormatada = dataAvaliacao.format(formatter);
            String mensagem = "Prezado usuário,\n\n\tA data para avaliação da defesa do artigo \"" + artigo.getTitulo() + "\" foi confirmada na plataforma para o dia " + dataFormatada + " às " + dataAvaliacao.toLocalTime() + "\n\nObrigado.";

            try {
                emailService.enviarEmail(destinatario, assunto, mensagem);
                emailService.enviarEmail(destinatario2, assunto, mensagem);
                System.out.println("E-mail enviado com sucesso.");
            } catch (MessagingException e) {
                System.out.println("Erro ao enviar o e-mail: " + e.getMessage());
            }

            return "Horário de avaliação cadastrado com sucesso" + dataAvaliacao;
        } else {
            return "Problema para o cadastro de horário da avaliação, não foi possível achar um horario em comum.";
        }
    }
}
