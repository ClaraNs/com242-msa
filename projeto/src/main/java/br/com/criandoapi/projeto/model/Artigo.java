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
@Table(name = "Artigo")
public class Artigo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idartigo")
    private Integer idArtigo;

    @Column(name = "titulo", length = 80, nullable = false)
    private String titulo;

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "arquivo", nullable = false)
    private byte[] arquivo;

    @Column(name = "url", columnDefinition = "TEXT")
    private String url;

    @Column(name = "resumo", columnDefinition = "TEXT", nullable = false)
    private String resumo;

    @Column(name = "dataenvio", nullable = false)
    private LocalDateTime dataEnvio;

    @Column(name = "alteracao", nullable = false)
    private LocalDateTime alteracao;

    @ManyToOne
    @JoinColumn(name = "status", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_artigo_status"))
    private StatusArtigo status;

    @Column(name = "notafinal")
    private Float notaFinal;

    @Column(name = "consideracoes", columnDefinition = "TEXT")
    private String consideracoes;

    @ManyToOne
    @JoinColumn(name = "enviadopor", referencedColumnName = "matricula", foreignKey = @ForeignKey(name = "fk_artigo_aluno"))
    private Aluno enviadoPor;

    @ManyToOne
    @JoinColumn(name = "matriculaorientador", referencedColumnName = "matricula", foreignKey = @ForeignKey(name = "fk_artigo_orientador"))
    private Professor matriculaOrientador;

    // Construtores, getters e setters

    public Integer getIdArtigo() {
        return idArtigo;
    }

    public void setIdArtigo(Integer idArtigo) {
        this.idArtigo = idArtigo;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public LocalDateTime getAlteracao() {
        return alteracao;
    }

    public void setAlteracao(LocalDateTime alteracao) {
        this.alteracao = alteracao;
    }

    public StatusArtigo getStatus() {
        return status;
    }

    public void setStatus(StatusArtigo status) {
        this.status = status;
    }

    public Float getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(Float notaFinal) {
        this.notaFinal = notaFinal;
    }

    public String getConsideracoes() {
        return consideracoes;
    }

    public void setConsideracoes(String consideracoes) {
        this.consideracoes = consideracoes;
    }

    public Aluno getEnviadoPor() {
        return enviadoPor;
    }

    public void setEnviadoPor(Aluno enviadoPor) {
        this.enviadoPor = enviadoPor;
    }

    public Professor getOrientador() {
        return matriculaOrientador;
    }

    public void setOrientador(Professor orientador) {
        this.matriculaOrientador = orientador;
    }

}
