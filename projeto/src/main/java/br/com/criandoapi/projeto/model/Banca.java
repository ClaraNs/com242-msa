package br.com.criandoapi.projeto.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Banca")
public class Banca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idbanca")
    private Integer idBanca;

    @Column(name = "dataregistro", nullable = false)
    private LocalDateTime dataRegistro;

    @Column(name = "dataatualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @ManyToOne
    @JoinColumn(name = "professoravaliador", referencedColumnName = "matricula", foreignKey = @ForeignKey(name = "fk_banca_professor"))
    private Professor professorAvaliador;

    @Column(name = "dataavaliacao")
    private LocalDateTime dataAvaliacao;

    @ManyToOne
    @JoinColumn(name = "artigoavaliado", referencedColumnName = "idArtigo", foreignKey = @ForeignKey(name = "fk_banca_artigo"))
    private Artigo artigoAvaliado;

    @ManyToOne
    @JoinColumn(name = "status", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_banca_status"))
    private StatusBanca status;

    @Column(name = "nota")
    private Float nota;

    @Column(name = "consideracoes")
    private String consideracoes;

    // getters and setters

    public Integer getIdBanca() {
        return idBanca;
    }

    public void setIdBanca(Integer idBanca) {
        this.idBanca = idBanca;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Professor getProfessorAvaliador() {
        return professorAvaliador;
    }

    public void setProfessorAvaliador(Professor professorAvaliador) {
        this.professorAvaliador = professorAvaliador;
    }

    public LocalDateTime getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(LocalDateTime dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public Artigo getArtigoAvaliado() {
        return artigoAvaliado;
    }

    public void setArtigoAvaliado(Artigo artigoAvaliado) {
        this.artigoAvaliado = artigoAvaliado;
    }

    public StatusBanca getStatus() {
        return status;
    }

    public void setStatus(StatusBanca status) {
        this.status = status;
    }

    public Float getNota() {
        return nota;
    }

    public void setNota(Float nota) {
        this.nota = nota;
    }

    public String getConsideracoes() {
        return consideracoes;
    }

    public void setConsideracoes(String consideracoes) {
        this.consideracoes = consideracoes;
    }
}
