package br.com.criandoapi.projeto.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.com.criandoapi.projeto.DAO.IVersao;
import br.com.criandoapi.projeto.config.DatabaseConfig;
import br.com.criandoapi.projeto.controller.VersaoController;
import br.com.criandoapi.projeto.model.Aluno;
import br.com.criandoapi.projeto.model.Artigo;
import br.com.criandoapi.projeto.model.Professor;
import br.com.criandoapi.projeto.model.StatusArtigo;
import br.com.criandoapi.projeto.model.Versao;

@Service
public class VersaoService {

    private final IVersao dao;
    private final DatabaseConfig databaseConfig;
    private final VersaoController versaoController;
    private final ArtigoService artigoService;

    @Autowired
    public VersaoService(IVersao dao, DatabaseConfig databaseConfig, VersaoController versaoController,
        ArtigoService artigoService) {
        this.dao = dao;
        this.databaseConfig = databaseConfig;
        this.versaoController = versaoController;
        this.artigoService = artigoService;
    }

    public Integer criaVersao(Artigo artigo) throws IOException {
        return this.versaoController.uploadVersao(artigo);
    }

    public String uploadFile(Artigo artigo) {

        // Copia arquivo atual
        byte[] arquivoBytes = artigo.getArquivo();

        Versao versao = new Versao();
        versao.setIdArtigo(artigo);
        versao.setTitulo(artigo.getTitulo());
        versao.setArquivo(arquivoBytes);
        versao.setDataInsercao(artigo.getAlteracao());
        //versao.setDataInsercao(LocalDateTime.now());
        
        dao.save(versao);
        return "Versão salva com sucesso";
    }

}
