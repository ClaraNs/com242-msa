package br.com.criandoapi.projeto.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

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
@Table(name = "disponibilidade")
public class Disponibilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddisponibilidade")
    private Integer idDisponibilidade;
    
    @ManyToOne
    @JoinColumn(name = "professor", referencedColumnName = "matricula", foreignKey = @ForeignKey(name = "fk_disponibilidade_professor"), nullable = false)
    private Professor professor;

    @ManyToOne
    @JoinColumn(name = "idbanca", referencedColumnName = "idBanca", foreignKey = @ForeignKey(name = "fk_disponibilidade_banca"), nullable = false)
    private Banca banca;

    @Column(name = "data", nullable = false)
    private LocalDateTime data;

    // getters and setters

    public Integer getIdDisponibilidade() {
        return idDisponibilidade;
    }

    public void setIdDisponibilidade(Integer idDisponibilidade) {
        this.idDisponibilidade = idDisponibilidade;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Banca getBanca() {
        return banca;
    }

    public void setBanca(Banca banca) {
        this.banca = banca;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
}
