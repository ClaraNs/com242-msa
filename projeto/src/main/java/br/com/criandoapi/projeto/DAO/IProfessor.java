package br.com.criandoapi.projeto.DAO;

import org.springframework.data.repository.CrudRepository;

import br.com.criandoapi.projeto.model.Professor;

public interface IProfessor extends CrudRepository<Professor, String>{
    
}
