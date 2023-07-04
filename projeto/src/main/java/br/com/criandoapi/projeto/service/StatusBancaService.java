package br.com.criandoapi.projeto.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.criandoapi.projeto.DAO.IStatusBanca;
import br.com.criandoapi.projeto.config.DatabaseConfig;
import br.com.criandoapi.projeto.model.StatusBanca;

@Service
public class StatusBancaService {
    private final IStatusBanca dao;
    private final DatabaseConfig databaseConfig;

    @Autowired
    public StatusBancaService(IStatusBanca dao, DatabaseConfig databaseConfig) {
        this.dao = dao;
        this.databaseConfig = databaseConfig;
    }

    public StatusBanca findStatusArtigoById(@RequestParam("id") Integer statusId) {
        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM statusbanca WHERE id = ?")) {
            statement.setInt(1, statusId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                StatusBanca status = new StatusBanca();
                status.setId(resultSet.getInt("id"));
                status.setDescricao(resultSet.getString("descricao"));

                return status;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return null; // Retorna null se o artigo não for encontrado
    }
}
