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
import br.com.criandoapi.projeto.service.ComposicaoBancaService;
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
    private final ComposicaoBancaService composicaoBancaService;

    @Autowired
    public BancaController(IBanca dao, ProfessorService professorService, ArtigoService artigoService,
            BancaService bancaService, DisponibilidadeService disponibilidadeService,
            StatusBancaService statusBancaService, AlunoService alunoService, EmailService emailService,
            ComposicaoBancaService composicaoBancaService) {
        this.dao = dao;
        this.professorService = professorService;
        this.artigoService = artigoService;
        this.bancaService = bancaService;
        this.disponibilidadeService = disponibilidadeService;
        this.statusBancaService = statusBancaService;
        this.alunoService = alunoService;
        this.emailService = emailService;
        this.composicaoBancaService = composicaoBancaService;
    }

    @GetMapping("/banca")
    public List<Banca> listaBancas() {
        return (List<Banca>) dao.findAll();
    }

    // Bancas aguardando aprovação do coordenador
    @GetMapping("/banca/aguardandoaprovacao")
    public List<Banca> listaBancasPendentes() {
        return bancaService.getBancasPorStatus(0);
    }

    // Bancas por professor, pela matrícula
    @GetMapping("/banca/{matricula}")
    public List<Banca> listaBancasPorMatriculaProfessor(@PathVariable String matricula) {
        return bancaService.getBancasPorProfessor(matricula);
    }

    // Post mudança de status pelo coordenador
    @PostMapping("/banca/aguardandoaprovacao/{idBanca}")
    public String aprovarBanca(@PathVariable Integer idBanca,
            @RequestParam("aprovacao") Boolean aprovacao) {
        Banca banca = new Banca();
        banca = bancaService.getBancaById(idBanca);

        if (aprovacao) {
            StatusBanca statusBanca = new StatusBanca();
            statusBanca = statusBancaService.findStatusBancaById(1);
            banca.setStatus(statusBanca);

            dao.save(banca);

            Integer idArtigo = banca.getArtigoAvaliado().getIdArtigo();

            String destinatario = alunoService.getEmailAlunoByArtigoId(idArtigo);
            String destinatario2 = professorService.getEmailOrientadorByArtigoId(idArtigo);
            String assunto = "Banca Liberada - MSA";
            String mensagem = "Prezado usuário,\n\n\tA solicitação da banca para defesa do artigo \""
                    + banca.getArtigoAvaliado().getTitulo()
                    + "\" foi aprovada na plataforma pelo coordenador de TFG. Agora é necessário inserir as datas e horários disponíveis para que o sistema encontre a melhor data para a avaliação.\n\nObrigado.";

            try {
                emailService.enviarEmail(destinatario, assunto, mensagem);
                emailService.enviarEmail(destinatario2, assunto, mensagem);
                System.out.println("E-mail enviado com sucesso.");
            } catch (MessagingException e) {
                System.out.println("Erro ao enviar o e-mail: " + e.getMessage());
            }

            return "Banca aprovada.";
        }

        return "Banca ainda aguardando aprovação";
    }

    /*
     * @GetMapping("/professor/{matricula}/bancas")
     * public List<Banca> listaBancasPorProfessor(@PathVariable String matricula) {
     * return bancaService.getBancasPorProfessor(matricula);
     * }
     */

    @PostMapping("/artigo/{idArtigo}/banca/cadastro")
    public String cadastrarBancas(@PathVariable Integer idArtigo) {
        Artigo artigo = artigoService.findArtigoByid(idArtigo);

        // Pronto para banca
        if (artigo.getStatus().getId() == 3) {
            Banca banca = new Banca();

            StatusBanca status = new StatusBanca();

            status = statusBancaService.findStatusBancaById(0);
            LocalDateTime dia = LocalDateTime.now();

            banca.setDataRegistro(dia);
            banca.setDataAtualizacao(dia);
            banca.setArtigoAvaliado(artigo);
            banca.setStatus(status);
            banca.setCorrecao(false);

            // Salve as bancas no banco de dados
            dao.save(banca);

            // Avisa aluno e orientador que a banca foi solicitada
            String destinatario = alunoService.getEmailAlunoByArtigoId(idArtigo);
            String destinatario2 = professorService.getEmailOrientadorByArtigoId(idArtigo);
            String assunto = "Solicitação de Banca - MSA";
            String mensagem = "Prezado usuário,\n\n\tA solicitação da banca para defesa do artigo \""
                    + artigo.getTitulo()
                    + "\" foi submetido na plataforma e está aguardando liberação do coordenador de TFG.\n\nObrigado.";

            try {
                emailService.enviarEmail(destinatario, assunto, mensagem);
                emailService.enviarEmail(destinatario2, assunto, mensagem);
                System.out.println("E-mail enviado com sucesso.");
            } catch (MessagingException e) {
                System.out.println("Erro ao enviar o e-mail: " + e.getMessage());
            }

            return "Banca cadastradas com sucesso.";
        } else {
            return "Erro ao cadastrar as bancas. O Orientador ainda não corrigiu o artigo.";
        }
    }

    @PostMapping("/banca/{idBanca}/cadastra/avaliacao/{idDisponibilidade}")
    public String setDataAvaliacao(@PathVariable Integer idBanca,
            @PathVariable Integer idDisponibilidade) {
        Disponibilidade dataAvaliacao = disponibilidadeService.getDisponibilidadePorId(idDisponibilidade);
        Banca banca = new Banca();
        banca = bancaService.getBancaById(idBanca);
        banca.setDataAvaliacao(dataAvaliacao.getData());
        StatusBanca status = new StatusBanca();
        status = statusBancaService.findStatusBancaById(2);
        banca.setStatus(status);
        banca.setDataAtualizacao(LocalDateTime.now());

        dao.save(banca);

        String destinatario = banca.getArtigoAvaliado().getEnviadoPor().getEmail();
        String destinatario2 = banca.getArtigoAvaliado().getOrientador().getEmail();
        // avisar os demais membros da banca
        String assunto = "Data para defesa confirmada - MSA";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataFormatada = dataAvaliacao.getData().format(formatter);
        String mensagem = "Prezado usuário,\n\n\tA data para avaliação da defesa do artigo \"" +
                banca.getArtigoAvaliado().getTitulo() + "\" foi confirmada na plataforma para o dia " +
                dataFormatada + " às " + dataAvaliacao.getData().toLocalTime() + "\n\nObrigado.";
        try {
            emailService.enviarEmail(destinatario, assunto, mensagem);
            emailService.enviarEmail(destinatario2, assunto, mensagem);
            System.out.println("E-mail enviado com sucesso.");
        } catch (MessagingException e) {
            System.out.println("Erro ao enviar o e-mail: " + e.getMessage());
        }

        return "Horário de avaliação cadastrado com sucesso" + dataAvaliacao;
    }

    // Verificar se existe alguma data que coincide, se sim, marca a avaliacao
    /*@PostMapping("/banca/{idBanca}/cadastra/avaliacao")
    public String cadastrarAvaliacao(@PathVariable Integer idBanca) {
        List<List<Disponibilidade>> todasDisponibilidades = new ArrayList<>();
        List<Professor> professores = composicaoBancaService.professoresByBancaId(idBanca);

        // Para cada professor, obtenha suas disponibilidades e adicione na lista de
        // listas
        for (Professor professor : professores) {
            String matricula = professor.getMatricula();
            List<Disponibilidade> disponibilidades = disponibilidadeService.getDisponibilidadesPorMatricula(matricula,
                    idBanca);
            todasDisponibilidades.add(disponibilidades);
        }

        LocalDateTime dataAvaliacao = disponibilidadeService.encontrarDataEmComum(todasDisponibilidades);
        Banca banca = new Banca();
        banca = bancaService.getBancaById(idBanca);

        if (dataAvaliacao != null) {

            banca.setDataAvaliacao(dataAvaliacao);
            StatusBanca status = new StatusBanca();
            status = statusBancaService.findStatusBancaById(2);
            banca.setStatus(status);
            banca.setDataAtualizacao(LocalDateTime.now());

            dao.save(banca);

            String destinatario = banca.getArtigoAvaliado().getEnviadoPor().getEmail();
            String destinatario2 = banca.getArtigoAvaliado().getOrientador().getEmail();
            // avisar os demais membros da banca
            String assunto = "Data para defesa confirmada - MSA";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dataFormatada = dataAvaliacao.format(formatter);
            String mensagem = "Prezado usuário,\n\n\tA data para avaliação da defesa do artigo \"" +
                    banca.getArtigoAvaliado().getTitulo() + "\" foi confirmada na plataforma para o dia " +
                    dataFormatada + " às " + dataAvaliacao.toLocalTime() + "\n\nObrigado.";
            try {
                emailService.enviarEmail(destinatario, assunto, mensagem);
                emailService.enviarEmail(destinatario2, assunto, mensagem);
                System.out.println("E-mail enviado com sucesso.");
            } catch (MessagingException e) {
                System.out.println("Erro ao enviar o e-mail: " + e.getMessage());
            }

            return "Horário de avaliação cadastrado com sucesso" + dataAvaliacao;

        } else {
            String destinatario = banca.getArtigoAvaliado().getEnviadoPor().getEmail();
            // String destinatario2 = banca.getArtigoAvaliado().getOrientador().getEmail();
            String assunto = "Aguardando confirmação da data - MSA";
            String mensagem = "Prezado usuário,\n\n\tNão foi possível marcar a data para defesa do artigo \"" +
                    banca.getArtigoAvaliado().getTitulo() +
                    "\" devido a indisponibilidade de horário dos envolvidos. Por favor, insira novas datas e horários na plataforma."
                    + "\n\nObrigado.";

            try {
                emailService.enviarEmail(destinatario, assunto, mensagem);
                // emailService.enviarEmail(destinatario2, assunto, mensagem);
                System.out.println("E-mail enviado com sucesso.");
            } catch (MessagingException e) {
                System.out.println("Erro ao enviar o e-mail: " + e.getMessage());
            }

            return "Problema para o cadastro de horário da avaliação, não foi possível achar um horario em comum.";
        }
    }*/
}
