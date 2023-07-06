package br.com.criandoapi.projeto.DAO;

import org.springframework.data.repository.CrudRepository;

import br.com.criandoapi.projeto.model.Versao;

public interface IVersao extends CrudRepository<Versao, Integer>{
    
}
