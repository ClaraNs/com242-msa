package br.com.criandoapi.projeto.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

@Entity
@Table(name = "StatusArtigo")
public class StatusArtigo {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "descricao", length = 50, nullable = false)
    private String descricao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
