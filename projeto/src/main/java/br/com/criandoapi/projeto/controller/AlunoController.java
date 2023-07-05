package br.com.criandoapi.projeto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestController;

import br.com.criandoapi.projeto.DAO.IAluno;
import br.com.criandoapi.projeto.DAO.IArtigo;
import br.com.criandoapi.projeto.model.Aluno;
//import br.com.criandoapi.projeto.model.Aluno;
import br.com.criandoapi.projeto.model.Banca;
import br.com.criandoapi.projeto.service.AlunoService;
import br.com.criandoapi.projeto.service.ArtigoService;
import br.com.criandoapi.projeto.service.EmailService;
import br.com.criandoapi.projeto.service.ProfessorService;
import br.com.criandoapi.projeto.service.StatusArtigoService;

//import java.util.List;
//import java.util.Optional;

@RestController
@CrossOrigin("*")
public class AlunoController {

    private IAluno dao;
    private final ProfessorService professorService;
    private final AlunoService alunoService;
    private final ArtigoService artigoService;
    private final StatusArtigoService statusArtigoService;
    private final EmailService emailService;

    @Autowired
    public AlunoController(IAluno dao, ProfessorService professorService, AlunoService alunoService,
            ArtigoService artigoService, StatusArtigoService statusArtigoService, EmailService emailService) {
        this.dao = dao;
        this.professorService = professorService;
        this.alunoService = alunoService;
        this.artigoService = artigoService;
        this.statusArtigoService = statusArtigoService;
        this.emailService = emailService;
    }

    @GetMapping("/alunos")
    public List<Aluno> listaAlunos() {
        return (List<Aluno>) dao.findAll();
    }

    @GetMapping("/alunos/{matricula}")
    public Aluno listaAlunosMatricula(@PathVariable String matricula) {
        return this.alunoService.findAlunoByMatricula(matricula);
    }
}