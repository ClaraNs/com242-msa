package br.com.criandoapi.projeto.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Professor")
public class Professor {

    @Id
    @Column(name = "matricula", length = 10)
    private String matricula;

    @Column(name = "nome", length = 50, nullable = false)
    private String nome;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "coordenador", nullable = false, columnDefinition = "boolean default false")
    private boolean coordenador;

    // Construtores, getters e setters

    public Professor() {
        // Construtor vazio (necessário para JPA)
    }

    public Professor(String matricula, String nome, String email, boolean coordenador) {
        this.matricula = matricula;
        this.nome = nome;
        this.email = email;
        this.coordenador = coordenador;
    }

    // Getters e setters

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isCoordenador() {
        return coordenador;
    }

    public void setCoordenador(boolean coordenador) {
        this.coordenador = coordenador;
    }
}

