package br.com.criandoapi.projeto.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Table(name = "Versao")
public class Versao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idversao")
    private Integer idVersao;

    @Column(name = "titulo", length = 80, nullable = false)
    private String titulo;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "arquivo", nullable = false)
    private byte[] arquivo;

    @Column(name = "datainsercao", nullable = false)
    private LocalDateTime dataInsercao;

    @ManyToOne
    @JoinColumn(name = "idartigo", referencedColumnName = "idartigo", foreignKey = @ForeignKey(name = "fk_versao_artigo"))
    private Artigo idArtigo;


    // Construtores, getters e setters

    public Integer getIdVersao() {
        return idVersao;
    }

    public void setIdVersao(Integer idVersao) {
        this.idVersao = idVersao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public byte[] getArquivo() {
        return arquivo;
    }

    public void setArquivo(byte[] arquivo) {
        this.arquivo = arquivo;
    }


    public LocalDateTime getDataInsercao() {
        return dataInsercao;
    }

    public void setDataInsercao(LocalDateTime dataInsercao) {
        this.dataInsercao = dataInsercao;
    }


    public Artigo getIdArtigo() {
        return idArtigo;
    }

    public void setIdArtigo(Artigo artigo) {
        this.idArtigo = artigo;
    }


}
