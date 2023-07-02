package br.com.criandoapi.projeto.controller;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import br.com.criandoapi.projeto.DAO.IDisponibilidade;
import br.com.criandoapi.projeto.model.Banca;
import br.com.criandoapi.projeto.model.Disponibilidade;
import br.com.criandoapi.projeto.model.Professor;
import br.com.criandoapi.projeto.service.ArtigoService;
import br.com.criandoapi.projeto.service.BancaService;
import br.com.criandoapi.projeto.service.ProfessorService;

@RestController
@CrossOrigin("*")
@RequestMapping
public class DisponibilidadeController {

    private IDisponibilidade dao;
    private final ProfessorService professorService;
    private final ArtigoService artigoService;
    private final BancaService bancaService;

    @Autowired
    public DisponibilidadeController(IDisponibilidade dao, ProfessorService professorService,
            ArtigoService artigoService, BancaService bancaService) {
        this.dao = dao;
        this.professorService = professorService;
        this.artigoService = artigoService;
        this.bancaService = bancaService;
    }

    @GetMapping("/disponibilidade")
    public List<Disponibilidade> listDisponibilidades() {
        return (List<Disponibilidade>) dao.findAll();
    }

    @PostMapping("disponibilidadebanca/{idBanca}")
    public ResponseEntity<String> cadastraDisponibilidade(@PathVariable Integer idBanca,
            @RequestParam("idProfessor") String idProfessor,
            @RequestParam("data") Date data,
            @RequestParam("horaInicio") Time horaInicio,
            @RequestParam("horaFim") Time horaFim){
        // Obtenha o objeto Banca do banco de dados com base no ID
        Banca banca = bancaService.getBancaById(idBanca);

        if (banca != null) {
            Disponibilidade disponibilidade = new Disponibilidade();
            disponibilidade.setBanca(banca);

            Professor professor = professorService.FindProfessorById(idProfessor);
            disponibilidade.setProfessor(professor);
            disponibilidade.setData(data);
            disponibilidade.setHoraInicio(horaInicio);
            disponibilidade.setHoraFim(horaFim);

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
            @RequestParam("data") Date data,
            @RequestParam("horaInicio") Time horaInicio,
            @RequestParam("horaFim") Time horaFim){
        
        // O orientador fica disponível em relação ao menor id das bancas relacionadas aquele artigo
        Banca banca = bancaService.getBancaById(bancaService.getPrimeiraBancaByArtigoAvaliado(idArtigo));

        if (banca != null) {
            Disponibilidade disponibilidade = new Disponibilidade();
            disponibilidade.setBanca(banca);

            Professor professor = professorService.FindProfessorById(idProfessor);
            disponibilidade.setProfessor(professor);
            disponibilidade.setData(data);
            disponibilidade.setHoraInicio(horaInicio);
            disponibilidade.setHoraFim(horaFim);

            // Salve
            dao.save(disponibilidade);

            return ResponseEntity.ok("Disponibilidade cadastrada com sucesso.");
        } else {
            // Caso a banca não seja encontrada, retorne uma resposta de erro
            return ResponseEntity.notFound().build();
        }
    }

}
