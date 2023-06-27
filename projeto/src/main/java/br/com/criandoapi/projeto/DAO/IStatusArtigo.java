package br.com.criandoapi.projeto.DAO;

import org.springframework.data.repository.CrudRepository;

import br.com.criandoapi.projeto.model.StatusArtigo;

public interface IStatusArtigo extends CrudRepository<StatusArtigo, Integer>{
    
}
