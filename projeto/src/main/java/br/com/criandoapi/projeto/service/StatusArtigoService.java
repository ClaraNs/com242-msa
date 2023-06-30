package br.com.criandoapi.projeto.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.criandoapi.projeto.model.Aluno;
import br.com.criandoapi.projeto.model.Artigo;
import br.com.criandoapi.projeto.model.Professor;
import br.com.criandoapi.projeto.model.StatusArtigo;
import br.com.criandoapi.projeto.DAO.IArtigo;
import br.com.criandoapi.projeto.DAO.IStatusArtigo;
import br.com.criandoapi.projeto.config.DatabaseConfig;

@Service
public class StatusArtigoService {

    private final IStatusArtigo dao;
    private final DatabaseConfig databaseConfig;

    @Autowired
    public StatusArtigoService(IStatusArtigo dao, DatabaseConfig databaseConfig) {
        this.dao = dao;
        this.databaseConfig = databaseConfig;
    }
    
    public StatusArtigo findStatusArtigoById(@RequestParam("id") Integer statusId) {
        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM statusartigo WHERE id = ?")) {
            statement.setInt(1, statusId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                StatusArtigo status = new StatusArtigo();
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
