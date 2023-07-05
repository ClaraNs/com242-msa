package br.com.criandoapi.projeto.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.criandoapi.projeto.DAO.IComposicaoBanca;
import br.com.criandoapi.projeto.config.DatabaseConfig;
import br.com.criandoapi.projeto.model.Banca;
import br.com.criandoapi.projeto.model.ComposicaoBanca;
import br.com.criandoapi.projeto.model.Professor;

@Service
public class ComposicaoBancaService {

    private final IComposicaoBanca dao;
    private final ArtigoService artigoService;
    private final ProfessorService professorService;
    private final StatusBancaService statusBancaService;
    private final BancaService bancaService;
    private final DatabaseConfig databaseConfig;

    @Autowired
    public ComposicaoBancaService(IComposicaoBanca dao, ArtigoService artigoService, ProfessorService professorService,
            StatusBancaService statusBancaService, BancaService bancaService, DatabaseConfig databaseConfig) {
        this.dao = dao;
        this.artigoService = artigoService;
        this.professorService = professorService;
        this.statusBancaService = statusBancaService;
        this.bancaService = bancaService;
        this.databaseConfig = databaseConfig;
    }

    // retornar lista de professores que compõe a mesma banca (idBanca =)
    public List<Professor> professoresByBancaId(Integer bancaId) {
        List<Professor> listaProfessores = new ArrayList<>();

        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement(
                                "SELECT matricula, nome, email FROM professor as p JOIN composicaobanca as c ON p.matricula = c.professorAvaliador WHERE c.idBanca = ?")) {
            statement.setInt(1, bancaId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Professor professor = new Professor();

                professor.setMatricula(resultSet.getString("matricula"));
                professor.setEmail(resultSet.getString("email"));
                professor.setNome(resultSet.getString("nome"));

                listaProfessores.add(professor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return listaProfessores;
    }

    public ComposicaoBanca getComposicaoByBancaId(Integer bancaId, String matricula) {
        ComposicaoBanca composicaoBanca = null;

        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT idComposicao, professorAvaliador, idBanca, nota, consideracoes " +
                                "FROM ComposicaoBanca " +
                                "WHERE idBanca = ? AND professorAvaliador = ?")) {

            statement.setInt(1, bancaId);
            statement.setString(2, matricula);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    composicaoBanca = new ComposicaoBanca();
                    composicaoBanca.setIdComposicao(resultSet.getInt("idComposicao"));

                    Professor professor = professorService.FindProfessorById(matricula);
                    composicaoBanca.setProfessorAvaliador(professor);

                    Banca banca = bancaService.getBancaById(bancaId);
                    composicaoBanca.setBanca(banca);

                    Float nota = resultSet.getFloat("nota");
                    String consideracoes = resultSet.getString("consideracoes");

                    if (nota != null)
                        composicaoBanca.setNota(nota);

                    if (consideracoes != null)
                        composicaoBanca.setConsideracoes(resultSet.getString("consideracoes"));
                }
            }

        } catch (SQLException e) {
            // Trate a exceção ou registre-a adequadamente
        }

        return composicaoBanca;
    }

    public List<ComposicaoBanca> getComposicoesByBancaId(Integer bancaId) {
        List<ComposicaoBanca> composicoes = new ArrayList<>();

        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT idComposicao, professorAvaliador, idBanca, nota, consideracoes " +
                                "FROM ComposicaoBanca " +
                                "WHERE idBanca = ?")) {

            statement.setInt(1, bancaId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ComposicaoBanca composicaoBanca = new ComposicaoBanca();
                    composicaoBanca.setIdComposicao(resultSet.getInt("idComposicao"));

                    Professor professor = professorService.FindProfessorById(resultSet.getString("professorAvaliador"));
                    composicaoBanca.setProfessorAvaliador(professor);

                    Banca banca = bancaService.getBancaById(resultSet.getInt("idBanca"));
                    composicaoBanca.setBanca(banca);

                    Float nota = resultSet.getFloat("nota");
                    String consideracoes = resultSet.getString("consideracoes");

                    if (nota != null)
                        composicaoBanca.setNota(nota);

                    if (consideracoes != null)
                        composicaoBanca.setConsideracoes(consideracoes);

                    composicoes.add(composicaoBanca);
                }
            }

        } catch (SQLException e) {
            // Trate a exceção ou registre-a adequadamente
        }

        return composicoes;
    }
    
    public Float getNotasByBanca(List<ComposicaoBanca> composicoes) {
    boolean todasNotasNaoNulas = true;
    float somaNotas = 0;
    int contadorNotas = 0;

    for (ComposicaoBanca composicao : composicoes) {
        Float nota = composicao.getNota();
        if (nota == null || nota == 0) {
            todasNotasNaoNulas = false;
            break;
        } else {
            somaNotas += nota;
            contadorNotas++;
        }
    }

    if (todasNotasNaoNulas && contadorNotas > 0) {
        return somaNotas / contadorNotas;
    } else {
        return null;
    }
}


}
