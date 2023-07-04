package br.com.criandoapi.projeto.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import br.com.criandoapi.projeto.DAO.IBanca;
import br.com.criandoapi.projeto.config.DatabaseConfig;
import br.com.criandoapi.projeto.model.Artigo;
import br.com.criandoapi.projeto.model.Banca;
import br.com.criandoapi.projeto.model.Professor;
import br.com.criandoapi.projeto.model.StatusBanca;

@Service
public class BancaService {
    private final IBanca dao;
    private final DatabaseConfig databaseConfig;

    @Autowired
    public BancaService(IBanca dao, DatabaseConfig databaseConfig) {
        this.dao = dao;
        this.databaseConfig = databaseConfig;
    }

    public List<Banca> getBancasPorProfessor(String matricula) {
        List<Banca> bancas = new ArrayList<>();

        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT * FROM banca WHERE professorAvaliador = ?")) {
            statement.setString(1, matricula);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Banca banca = new Banca();

                Professor professor = new Professor();
                professor.setMatricula(resultSet.getString("professorAvaliador"));
                Artigo artigo = new Artigo();
                artigo.setIdArtigo(resultSet.getInt("artigoAvaliado"));
                StatusBanca status = new StatusBanca();
                status.setId(resultSet.getInt("status"));

                Timestamp dataAvaliacao = resultSet.getTimestamp("dataAvaliacao");
                if (dataAvaliacao != null) {
                    banca.setDataAvaliacao(dataAvaliacao.toLocalDateTime());
                }

                banca.setIdBanca(resultSet.getInt("idBanca"));
                banca.setDataRegistro(resultSet.getTimestamp("dataRegistro").toLocalDateTime());
                banca.setDataAtualizacao(resultSet.getTimestamp("dataAtualizacao").toLocalDateTime());
                banca.setProfessorAvaliador(professor);
                banca.setArtigoAvaliado(artigo);
                banca.setStatus(status);
                banca.setNota(resultSet.getFloat("nota"));

                bancas.add(banca);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return bancas;
    }

    public Banca getBancaById(Integer idBanca) {
        return dao.findById(idBanca).orElse(null);
    }

    //Retornar id das bancas que avaliam o mesmo artigo
    public List<Integer> getBancasByArtigoAvaliado(int artigoAvaliado) {
    List<Integer> bancasIds = new ArrayList<>();

    try (Connection connection = databaseConfig.getConnection();
            PreparedStatement statement = connection
                    .prepareStatement("SELECT idBanca FROM Banca WHERE artigoAvaliado = ?")) {
        statement.setInt(1, artigoAvaliado);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            int bancaId = resultSet.getInt("idBanca");
            bancasIds.add(bancaId);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        // Lidar com exceções, se necessário
    }

    return bancasIds;
    }

    //Retornar id da 1ª banca que avaliam o mesmo artigo
    public Integer getPrimeiraBancaByArtigoAvaliado(int artigoAvaliado) {
    Integer primeiraBanca = -1;

    try (Connection connection = databaseConfig.getConnection();
            PreparedStatement statement = connection
                    .prepareStatement("SELECT MIN(b.idBanca) from banca b JOIN artigo a ON a.idArtigo = b.artigoAvaliado WHERE artigoAvaliado = ?")) {
        statement.setInt(1, artigoAvaliado);
        ResultSet resultSet = statement.executeQuery();

        //primeiraBanca = resultSet.getInt("MIN(b.idBanca)");
        if (resultSet.next()) {
            primeiraBanca = resultSet.getInt(1);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        // Lidar com exceções, se necessário
    }

    return primeiraBanca;
    }
}
