package br.com.criandoapi.projeto.controller;

import java.util.List;

import javax.mail.MessagingException;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.criandoapi.projeto.DAO.IDisponibilidade;
import br.com.criandoapi.projeto.model.Banca;
import br.com.criandoapi.projeto.model.Disponibilidade;
import br.com.criandoapi.projeto.model.Professor;
import br.com.criandoapi.projeto.model.StatusBanca;
import br.com.criandoapi.projeto.service.BancaService;
import br.com.criandoapi.projeto.service.DisponibilidadeService;
import br.com.criandoapi.projeto.service.EmailService;
import br.com.criandoapi.projeto.service.ProfessorService;

@RestController
@CrossOrigin("*")
@RequestMapping
public class DisponibilidadeController {

    private IDisponibilidade dao;
    private final ProfessorService professorService;
    private final EmailService emailService;
    private final BancaService bancaService;
    private final DisponibilidadeService disponibilidadeService;

    @Autowired
    public DisponibilidadeController(IDisponibilidade dao, ProfessorService professorService,
           EmailService emailService, BancaService bancaService, DisponibilidadeService disponibilidadeService) {
        this.dao = dao;
        this.professorService = professorService;
        this.emailService = emailService;
        this.bancaService = bancaService;
        this.disponibilidadeService = disponibilidadeService;
    }

    @GetMapping("/disponibilidade")
    public List<Disponibilidade> listDisponibilidades() {
        return (List<Disponibilidade>) dao.findAll();
    }

    // Lista as disponibilidades da banca informada
    @GetMapping("/disponibilidade/banca/{idBanca}")
    public List<Disponibilidade> listDisponibilidadesPorBanca(@PathVariable Integer idBanca) {
        return disponibilidadeService.getDisponibilidadesPorBanca(idBanca);
    }

    // Orientador cadastra disponibilidades
    @PostMapping("disponibilidade/banca/{idBanca}")
    public ResponseEntity<String> cadastraDisponibilidade(@PathVariable Integer idBanca,
            @ModelAttribute("data") String dataString,
            @ModelAttribute("horaInicio") String horaInicioString) {

        // Obtenha o objeto Banca do banco de dados com base no ID
        Banca banca = bancaService.getBancaById(idBanca);

        // Banca liberada para agendar defesa
        if (banca.getStatus().getId() == 1) {
            Disponibilidade disponibilidade = new Disponibilidade();
            disponibilidade.setBanca(banca);

            LocalDate data_convertida = LocalDate.parse(dataString);
            LocalTime horaInicio = LocalTime.parse(horaInicioString);
            disponibilidade.setData(data_convertida.atTime(horaInicio));
            disponibilidade.setAprovacao(true);

            // Salve
            dao.save(disponibilidade);

            return ResponseEntity.ok("Disponibilidade cadastrada com sucesso.");
        } else {
            // Caso a banca não esteja autorizada.
            return ResponseEntity.notFound().build();
        }
    }

    // Post mudança de status pelo coordenador
    @PostMapping("disponibilidade/banca/{idBanca}/{idDisponibilidade}")
    public String aprovarBanca(@PathVariable Integer idBanca,
            @PathVariable Integer idDisponibilidade,
            @RequestParam("aprovacao") Boolean aprovacao) {
        Disponibilidade disponibilidade = new Disponibilidade();
        disponibilidade = disponibilidadeService.getDisponibilidadePorId(idDisponibilidade);

        if (disponibilidade != null) {
            // Se um membro da banca não pode, aquela data deve ficar indisponível para os
            // outros;
            if (!aprovacao) {
                disponibilidade.setAprovacao(aprovacao);
                dao.save(disponibilidade);

                String destinatario = bancaService.getBancaById(idBanca).getArtigoAvaliado().getOrientador().getEmail();
                String assunto = "Data Excluída da Disponibilidade - MSA";
                String mensagem = "Prezado orientador,\n\n\tA data" + disponibilidade.getData().toLocalDate() + "cadastrada para possível defesa do artigo \"" + bancaService.getBancaById(idBanca).getArtigoAvaliado().getTitulo() + "\" foi dispensada por um dos membros da banca. Caso seja necessário, adicione novas opções.\n\nObrigado.";

                try {
                    emailService.enviarEmail(destinatario, assunto, mensagem);
                    System.out.println("E-mail enviado com sucesso.");
                } catch (MessagingException e) {
                    System.out.println("Erro ao enviar o e-mail: " + e.getMessage());
                }
                return "Essa data não será mais selecionável para os demais participantes da banca";
            } else {
                return "Data continua disponível";
            }
        } else
            return "Nenhuma disponibilidade encontrada";
    }

    /*@PostMapping("disponibilidade/banca/{idBanca}/{idDisponibilidade}")
    public String verificaDisponibilidade(@PathVariable Integer idBanca,
            @PathVariable("idDisponibilidade") Integer idDisponibilidade,
            @ModelAttribute("verificacao") boolean verificacao) {
       
    }*/

}
