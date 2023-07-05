package br.com.criandoapi.projeto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.com.criandoapi.projeto.DAO.IAluno;
import br.com.criandoapi.projeto.DAO.IProfessor;
import br.com.criandoapi.projeto.model.Aluno;
import br.com.criandoapi.projeto.model.Professor;
import br.com.criandoapi.projeto.service.AlunoService;
import br.com.criandoapi.projeto.service.ArtigoService;
import br.com.criandoapi.projeto.service.EmailService;
import br.com.criandoapi.projeto.service.ProfessorService;
import br.com.criandoapi.projeto.service.StatusArtigoService;

//import java.util.List;
//import java.util.Optional;

@RestController
@CrossOrigin("*")
public class ProfessorController {

    private IProfessor dao;
    private final ProfessorService professorService;
    private final AlunoService alunoService;
    private final ArtigoService artigoService;
    private final StatusArtigoService statusArtigoService;
    private final EmailService emailService;

    @Autowired
    public ProfessorController(IProfessor dao, ProfessorService professorService, AlunoService alunoService,
            ArtigoService artigoService, StatusArtigoService statusArtigoService, EmailService emailService) {
        this.dao = dao;
        this.professorService = professorService;
        this.alunoService = alunoService;
        this.artigoService = artigoService;
        this.statusArtigoService = statusArtigoService;
        this.emailService = emailService;
    }

    @GetMapping("/profs")
    public List<Professor> listaProfs() {
        return (List<Professor>) dao.findAll();
    }

    @GetMapping("/profs/{matricula}")
    public Professor listaProfsMatricula(@PathVariable String matricula) {
        return this.professorService.findProfessorByMatricula(matricula);
    }
}