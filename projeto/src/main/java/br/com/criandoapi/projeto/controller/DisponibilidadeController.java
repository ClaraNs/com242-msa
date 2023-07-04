package br.com.criandoapi.projeto.controller;

import java.util.List;
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
import br.com.criandoapi.projeto.service.BancaService;
import br.com.criandoapi.projeto.service.ProfessorService;

@RestController
@CrossOrigin("*")
@RequestMapping
public class DisponibilidadeController {

    private IDisponibilidade dao;
    private final ProfessorService professorService;
    private final BancaService bancaService;

    @Autowired
    public DisponibilidadeController(IDisponibilidade dao, ProfessorService professorService,
        BancaService bancaService) {
        this.dao = dao;
        this.professorService = professorService;
        this.bancaService = bancaService;
    }

    @GetMapping("/disponibilidade")
    public List<Disponibilidade> listDisponibilidades() {
        return (List<Disponibilidade>) dao.findAll();
    }

    @PostMapping("disponibilidadebanca/{idBanca}")
    public ResponseEntity<String> cadastraDisponibilidade(@PathVariable Integer idBanca,
            @ModelAttribute("idProfessor") String idProfessor,
            @ModelAttribute("data") String dataString,
            @ModelAttribute("horaInicio") String horaInicioString) {
        // Obtenha o objeto Banca do banco de dados com base no ID
        Banca banca = bancaService.getBancaById(idBanca);

        if (banca != null) {
            Disponibilidade disponibilidade = new Disponibilidade();
            disponibilidade.setBanca(banca);

            Professor professor = professorService.FindProfessorById(idProfessor);
            disponibilidade.setProfessor(professor);

            LocalDate data_convertida = LocalDate.parse(dataString);
            LocalTime horaInicio = LocalTime.parse(horaInicioString);
            disponibilidade.setData(data_convertida.atTime(horaInicio));

            // Salve
            dao.save(disponibilidade);

            return ResponseEntity.ok("Disponibilidade cadastrada com sucesso.");
        } else {
            // Caso a banca não seja encontrada, retorne uma resposta de erro
            return ResponseEntity.notFound().build();
        }
    }

    //SELECT MIN(b.idBanca) from banca b JOIN artigo a ON a.idArtigo = b.artigoAvaliado
    @PostMapping("disponibilidadebancaorientador/{idArtigo}")
    public ResponseEntity<String> cadastraDisponibilidadeOrientador(@PathVariable Integer idArtigo,
            @RequestParam("idProfessor") String idProfessor,
            @RequestParam("data") String dataString,
            @RequestParam("horaInicio") String horaInicioString) {

        // O orientador fica disponível em relação ao menor ID das bancas relacionadas a aquele artigo
        Banca banca = bancaService.getBancaById(bancaService.getPrimeiraBancaByArtigoAvaliado(idArtigo));

        if (banca != null) {
            Disponibilidade disponibilidade = new Disponibilidade();
            disponibilidade.setBanca(banca);

            Professor professor = professorService.FindProfessorById(idProfessor);
            disponibilidade.setProfessor(professor);
            LocalDate data_convertida = LocalDate.parse(dataString);
            LocalTime horaInicio = LocalTime.parse(horaInicioString);

            disponibilidade.setData(data_convertida.atTime(horaInicio));

            // Salve
            dao.save(disponibilidade);

            return ResponseEntity.ok("Disponibilidade cadastrada com sucesso.");
        } else {
            // Caso a banca não seja encontrada, retorne uma resposta de erro
            return ResponseEntity.notFound().build();
        }
    }

}
