package br.com.criandoapi.projeto.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

//import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import br.com.criandoapi.projeto.DAO.IArtigo;
import br.com.criandoapi.projeto.DAO.IBanca;
import br.com.criandoapi.projeto.model.Artigo;
import br.com.criandoapi.projeto.model.Banca;
import br.com.criandoapi.projeto.model.Professor;
import br.com.criandoapi.projeto.model.StatusArtigo;
import br.com.criandoapi.projeto.model.StatusBanca;
import br.com.criandoapi.projeto.service.ArtigoService;
import br.com.criandoapi.projeto.service.ProfessorService;

@RestController
@CrossOrigin("*")
@RequestMapping("/banca")
public class BancaController {

    private IBanca dao;
    private final ProfessorService professorService;
    private final ArtigoService artigoService;

    @Autowired
    public BancaController(IBanca dao, ProfessorService professorService, ArtigoService artigoService) {
        this.dao = dao;
        this.professorService = professorService;
        this.artigoService = artigoService;
    }

    @GetMapping
    public List<Banca> listaBancas() {
        return (List<Banca>) dao.findAll();
    }

    @PostMapping("/cadastrar")
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
            status.setId(0); // Set the ID of the desired status

            banca1.setProfessorAvaliador(professor1);
            banca1.setDataRegistro(dia);
            banca1.setDataAtualizacao(dia);
            banca1.setArtigoAvaliado(artigo);
            banca1.setStatus(status);

            Banca banca2 = new Banca();
            banca2.setProfessorAvaliador(professor2);
            banca2.setProfessorAvaliador(professor1);
            banca2.setDataRegistro(dia);
            banca2.setDataAtualizacao(dia);
            banca2.setArtigoAvaliado(artigo);
            banca2.setStatus(status);

            // Salve as bancas no banco de dados
            dao.save(banca1);
            dao.save(banca2);

            return "Bancas cadastradas com sucesso.";
        } else {
            return "Erro ao cadastrar as bancas. Verifique os nomes dos professores informados.";
        }
    }

}
