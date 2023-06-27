package br.com.criandoapi.projeto.DAO;

import org.springframework.data.repository.CrudRepository;

import br.com.criandoapi.projeto.model.StatusBanca;

public interface IStatusBanca extends CrudRepository<StatusBanca, Integer>{
    
}
