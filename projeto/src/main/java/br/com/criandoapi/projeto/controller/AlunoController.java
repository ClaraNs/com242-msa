package br.com.criandoapi.projeto.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;

import br.com.criandoapi.projeto.DAO.IAluno;
//import br.com.criandoapi.projeto.model.Aluno;

//import java.util.List;
//import java.util.Optional;

@CrossOrigin("*")
@RequestMapping("/alunos")
public class AlunoController {
    
    @Autowired
    private IAluno dao;

    @GetMapping
    public String texto () {
        return "Acessando a api";
    }
}
