package br.com.criandoapi.projeto.DAO;

import org.springframework.data.repository.CrudRepository;

import br.com.criandoapi.projeto.model.Banca;

public interface IBanca extends CrudRepository<Banca, Integer>{
    
}
