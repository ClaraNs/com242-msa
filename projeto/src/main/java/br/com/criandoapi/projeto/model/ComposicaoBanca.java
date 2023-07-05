package br.com.criandoapi.projeto.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Composicaobanca")
public class ComposicaoBanca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcomposicao", nullable = false)
    private Integer idComposicao;

    @ManyToOne
    @JoinColumn(name = "professoravaliador", nullable = false)
    private Professor professorAvaliador;

    @ManyToOne
    @JoinColumn(name = "idbanca", nullable = false)
    private Banca banca;

    @Column(name = "nota", nullable = true)
    private float nota;

    @Column(name = "consideracoes", columnDefinition = "TEXT")
    private String consideracoes;

    // Construtores, getters e setters

    public Integer getIdComposicao() {
        return idComposicao;
    }

    public void setIdComposicao(Integer idComposicao) {
        this.idComposicao = idComposicao;
    }

    public Professor getProfessorAvaliador() {
        return professorAvaliador;
    }

    public void setProfessorAvaliador(Professor professorAvaliador) {
        this.professorAvaliador = professorAvaliador;
    }

    public Banca getBanca() {
        return banca;
    }

    public void setBanca(Banca banca) {
        this.banca = banca;
    }

    public float getNota() {
        return nota;
    }

    public void setNota(float nota) {
        this.nota = nota;
    }

    public String getConsideracoes() {
        return consideracoes;
    }

    public void setConsideracoes(String consideracoes) {
        this.consideracoes = consideracoes;
    }
}
