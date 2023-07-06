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
@Table(name = "disponibilidade")
public class Disponibilidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddisponibilidade")
    private Integer idDisponibilidade;

    @ManyToOne
    @JoinColumn(name = "idbanca", referencedColumnName = "idBanca", foreignKey = @ForeignKey(name = "fk_disponibilidade_banca"), nullable = false)
    private Banca banca;

    @Column(name = "data", nullable = false)
    private LocalDateTime data;

    @Column(name = "aprovacao", nullable = false, columnDefinition = "boolean default true")
    private Boolean aprovacao;
    
    // getters and setters

    public Integer getIdDisponibilidade() {
        return idDisponibilidade;
    }

    public void setIdDisponibilidade(Integer idDisponibilidade) {
        this.idDisponibilidade = idDisponibilidade;
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

    public Boolean getAprovacao() {
        return aprovacao;
    }

    public void setAprovacao(Boolean aprovacao) {
        this.aprovacao = aprovacao;
    }
}
