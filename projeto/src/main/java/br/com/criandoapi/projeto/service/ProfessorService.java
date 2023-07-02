package br.com.criandoapi.projeto.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.criandoapi.projeto.DAO.IProfessor;
import br.com.criandoapi.projeto.config.DatabaseConfig;
import br.com.criandoapi.projeto.model.Banca;
import br.com.criandoapi.projeto.model.Professor;

@Service
public class ProfessorService {

    private final IProfessor dao;
    private final DatabaseConfig databaseConfig;

    @Autowired
    public ProfessorService(IProfessor dao, DatabaseConfig databaseConfig) {
        this.dao = dao;
        this.databaseConfig = databaseConfig;
    }

    @GetMapping("/professor/nome")
    public Professor findProfessorByNome(@RequestParam("nome") String nomeProfessor) {
        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT * FROM professor WHERE nome LIKE ?")) {
            statement.setString(1, "%" + nomeProfessor + "%");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Professor professor = new Professor();
                professor.setMatricula(resultSet.getString("matricula"));
                professor.setNome(resultSet.getString("nome"));
                professor.setEmail(resultSet.getString("email"));

                return professor;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return null; // Retorna null se o professor não for encontrado
    }

    public Professor FindProfessorById(String idProfessor) {
        return dao.findById(idProfessor).orElse(null);
    }

}
