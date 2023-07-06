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
import br.com.criandoapi.projeto.model.StatusArtigo;
import br.com.criandoapi.projeto.model.StatusBanca;

@Service
public class BancaService {
    private final IBanca dao;
    private final ArtigoService artigoService;
    private final StatusBancaService statusBancaService;
    private final DatabaseConfig databaseConfig;

    @Autowired
    public BancaService(IBanca dao, ArtigoService artigoService, StatusBancaService statusBancaService,
            DatabaseConfig databaseConfig) {
        this.dao = dao;
        this.artigoService = artigoService;
        this.statusBancaService = statusBancaService;
        this.databaseConfig = databaseConfig;
    }

    public List<Banca> getBancasPorStatus(Integer status) {
        List<Banca> bancas = new ArrayList<>();

        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT * FROM banca WHERE status = ?")) {
            statement.setInt(1, status);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Banca banca = new Banca();

                // Artigo artigo = new Artigo();
                // artigo.setIdArtigo(resultSet.getInt("artigoAvaliado"));
                // StatusBanca statusBanca = new StatusBanca();
                // statusBanca.setId(resultSet.getInt("status"));

                Timestamp dataAvaliacao = resultSet.getTimestamp("dataAvaliacao");
                StatusBanca statusBanca = new StatusBanca();
                statusBanca = statusBancaService.findStatusBancaById(status);

                if (dataAvaliacao != null) {
                    banca.setDataAvaliacao(dataAvaliacao.toLocalDateTime());
                }

                banca.setIdBanca(resultSet.getInt("idBanca"));
                banca.setDataRegistro(resultSet.getTimestamp("dataRegistro").toLocalDateTime());
                banca.setDataAtualizacao(resultSet.getTimestamp("dataAtualizacao").toLocalDateTime());
                banca.setArtigoAvaliado(artigoService.findArtigoByid(resultSet.getInt("artigoAvaliado")));
                banca.setStatus(statusBanca);

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

    public String AtualizaStatusBanca(Banca banca, StatusBanca novoStatus) {
        banca.setStatus(novoStatus);

        dao.save(banca);
        return "Status atualizado com sucesso";
    }

    public void setarCorrecaoBanca(Integer idBanca) {
        Banca banca = getBancaById(idBanca);
        banca.setCorrecao(false);
        dao.save(banca);
    }

    public void mudarStatusBanca(Integer idBanca, Integer idStatus) {
        Banca banca = getBancaById(idBanca);
        StatusBanca status = statusBancaService.findStatusBancaById(idStatus);

        banca.setStatus(status);
        dao.save(banca);
    }

    public List<Banca> BancasSemDataPorOrientador(String matricula) {
        List<Banca> bancas = new ArrayList<>();

        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM banca b " +
                                "JOIN artigo a ON b.artigoAvaliado = a.idArtigo " +
                                "WHERE b.status = ? AND a.matriculaOrientador = ?")) {
            statement.setInt(1, 7);
            statement.setString(2, matricula);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Banca banca = new Banca();
                banca.setIdBanca(resultSet.getInt("idBanca"));
                banca.setDataRegistro(resultSet.getTimestamp("dataRegistro").toLocalDateTime());
                banca.setDataAtualizacao(resultSet.getTimestamp("dataAtualizacao").toLocalDateTime());
                if(resultSet.getTimestamp("dataAvaliacao") != null)
                    banca.setDataAvaliacao(resultSet.getTimestamp("dataAvaliacao").toLocalDateTime());

                bancas.add(banca);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return bancas;
    }
    /*
     * public List<Banca> getBancasPorProfessor(String matricula) {
     * List<Banca> bancas = new ArrayList<>();
     * 
     * try (Connection connection = databaseConfig.getConnection();
     * PreparedStatement statement = connection
     * .prepareStatement("SELECT *\r\n" + //
     * "FROM Banca AS B\r\n" + //
     * "JOIN ComposicaoBanca AS CB ON B.idBanca = CB.idBanca\r\n" + //
     * "WHERE CB.professorAvaliador = ?")) {
     * statement.setString(1, matricula);
     * ResultSet resultSet = statement.executeQuery();
     * 
     * while (resultSet.next()) {
     * Banca banca = new Banca();
     * 
     * Professor professor = new Professor();
     * professor.setMatricula(resultSet.getString("professorAvaliador"));
     * Artigo artigo = new Artigo();
     * artigo.setIdArtigo(resultSet.getInt("artigoAvaliado"));
     * StatusBanca status = new StatusBanca();
     * status.setId(resultSet.getInt("status"));
     * 
     * Timestamp dataAvaliacao = resultSet.getTimestamp("dataAvaliacao");
     * if (dataAvaliacao != null) {
     * banca.setDataAvaliacao(dataAvaliacao.toLocalDateTime());
     * }
     * 
     * banca.setIdBanca(resultSet.getInt("idBanca"));
     * banca.setDataRegistro(resultSet.getTimestamp("dataRegistro").toLocalDateTime(
     * ));
     * banca.setDataAtualizacao(resultSet.getTimestamp("dataAtualizacao").
     * toLocalDateTime());
     * banca.setArtigoAvaliado(artigo);
     * banca.setStatus(status);
     * 
     * bancas.add(banca);
     * }
     * } catch (SQLException e) {
     * e.printStackTrace();
     * // Lidar com exceções, se necessário
     * }
     * 
     * return bancas;
     * }
     */
}
