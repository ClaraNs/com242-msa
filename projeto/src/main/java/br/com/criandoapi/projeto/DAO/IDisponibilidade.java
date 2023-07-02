package br.com.criandoapi.projeto.DAO;

import org.springframework.data.repository.CrudRepository;

import br.com.criandoapi.projeto.model.Disponibilidade;

public interface IDisponibilidade  extends CrudRepository <Disponibilidade, Integer>{
    
}
