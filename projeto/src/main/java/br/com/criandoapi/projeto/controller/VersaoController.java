package br.com.criandoapi.projeto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import br.com.criandoapi.projeto.DAO.IVersao;
import br.com.criandoapi.projeto.model.Artigo;
import br.com.criandoapi.projeto.model.Versao;

import javax.mail.MessagingException;

@RestController
@CrossOrigin("*")
@RequestMapping
public class VersaoController {

    private final IVersao dao;

    @Autowired
    public VersaoController(IVersao dao) {
        this.dao = dao;
    }

    @GetMapping("/versao")
    public List<Versao> listaVersaoArtigo() {
        return (List<Versao>) dao.findAll();
    }
    
    public Integer uploadVersao(Artigo artigo) throws IOException {

        
        Versao versao = new Versao();

        versao.setTitulo(artigo.getTitulo());
        versao.setArquivo(artigo.getArquivo());
        versao.setDataInsercao(LocalDateTime.now());
        versao.setIdArtigo(artigo);

        // Salve o artigo com o caminho do arquivo e a URL de download no banco de dados
        Versao novaVersao = dao.save(versao);

        return 1;
    }

}
