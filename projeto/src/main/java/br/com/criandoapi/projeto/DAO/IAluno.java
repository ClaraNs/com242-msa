package br.com.criandoapi.projeto.DAO;

import org.springframework.data.repository.CrudRepository;

import br.com.criandoapi.projeto.model.Aluno;

public interface IAluno extends CrudRepository <Aluno, String>{
    
}
