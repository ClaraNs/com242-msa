package br.com.criandoapi.projeto.DAO;

import org.springframework.data.repository.CrudRepository;

import br.com.criandoapi.projeto.model.ComposicaoBanca;

public interface IComposicaoBanca extends CrudRepository <ComposicaoBanca, Integer>{
    
}
