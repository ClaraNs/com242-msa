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
import br.com.criandoapi.projeto.service.RequisicaoService;

@RestController
@CrossOrigin("*")
@RequestMapping
public class DisponibilidadeController {

    private IDisponibilidade dao;
    private final ProfessorService professorService;
    private final EmailService emailService;
    private final BancaService bancaService;
    private final DisponibilidadeService disponibilidadeService;
    private final RequisicaoService requisicaoService;

    @Autowired
    public DisponibilidadeController(IDisponibilidade dao, ProfessorService professorService,
           EmailService emailService, BancaService bancaService, DisponibilidadeService disponibilidadeService,
           RequisicaoService requisicaoService) {
        this.dao = dao;
        this.professorService = professorService;
        this.emailService = emailService;
        this.bancaService = bancaService;
        this.disponibilidadeService = disponibilidadeService;
        this.requisicaoService = requisicaoService;
    }

    @GetMapping("/disponibilidade")
    public List<Disponibilidade> listDisponibilidades() {
        return (List<Disponibilidade>) dao.findAll();
    }

    // Lista as disponibilidades válidas da banca informada
    @GetMapping("/disponibilidade/banca/{idBanca}")
    public List<Disponibilidade> listDisponibilidadesPorBanca(@PathVariable Integer idBanca) {
        return disponibilidadeService.getDisponibilidadesValidasPorBanca(idBanca);
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

                // Verificar se não era a última data válida
                if(disponibilidadeService.verificarDisponibilidadesFalsePorBanca(idBanca)){
                    bancaService.mudarStatusBanca(idBanca, 7);
                    String email = bancaService.getBancaById(idBanca).getArtigoAvaliado().getOrientador().getEmail();
                    String assunto = "Necessária Inclusão de NOvas Datas - MSA";
                    String mensagem = "Prezado orientador,\n\n\tTodas as datas cadastradas para possível defesa do artigo \"" + bancaService.getBancaById(idBanca).getArtigoAvaliado().getTitulo() + "\" foram dispensadas. É necessário a inclusão de novas datas.\n\nObrigado.";

                    requisicaoService.realizaRequisicao(email, assunto, mensagem);
                }

                String email = bancaService.getBancaById(idBanca).getArtigoAvaliado().getOrientador().getEmail();
                String assunto = "Data Excluída da Disponibilidade - MSA";
                String mensagem = "Prezado orientador,\n\n\tA data" + disponibilidade.getData().toLocalDate() + "cadastrada para possível defesa do artigo \"" + bancaService.getBancaById(idBanca).getArtigoAvaliado().getTitulo() + "\" foi dispensada por um dos membros da banca. Caso seja necessário, adicione novas opções.\n\nObrigado.";

                requisicaoService.realizaRequisicao(email, assunto, mensagem);
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
