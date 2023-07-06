package br.com.criandoapi.projeto.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.criandoapi.projeto.DAO.IVersao;
import br.com.criandoapi.projeto.config.DatabaseConfig;
import br.com.criandoapi.projeto.controller.VersaoController;
import br.com.criandoapi.projeto.model.Artigo;
import br.com.criandoapi.projeto.model.Versao;

@Service
public class VersaoService {

    private final IVersao dao;
    private final DatabaseConfig databaseConfig;
    private final VersaoController versaoController;

    @Autowired
    public VersaoService(IVersao dao, DatabaseConfig databaseConfig, VersaoController versaoController) {
        this.dao = dao;
        this.databaseConfig = databaseConfig;
        this.versaoController = versaoController;
    }

    public Integer criaVersao(Artigo artigo) throws IOException {
        return this.versaoController.uploadVersao(artigo);
    }

}
