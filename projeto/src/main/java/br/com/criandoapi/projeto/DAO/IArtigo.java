package br.com.criandoapi.projeto.DAO;

import org.springframework.data.repository.CrudRepository;

import br.com.criandoapi.projeto.model.Artigo;

public interface IArtigo extends CrudRepository <Artigo, Integer>{
    
}



