package br.com.criandoapi.projeto.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.criandoapi.projeto.DAO.IAluno;
import br.com.criandoapi.projeto.config.DatabaseConfig;
import br.com.criandoapi.projeto.model.Aluno;

@Service
public class AlunoService {

    private final IAluno dao;
    private final DatabaseConfig databaseConfig;

    @Autowired
    public AlunoService(IAluno dao, DatabaseConfig databaseConfig) {
        this.dao = dao;
        this.databaseConfig = databaseConfig;
    }

    @GetMapping("/aluno/{matricula}")
    public Aluno findAlunoByMatricula(@PathVariable String matricula) {
        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM aluno WHERE matricula = ?")) {
            statement.setString(1, matricula);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Aluno aluno = new Aluno();
                aluno.setMatricula(resultSet.getString("matricula"));
                aluno.setNome(resultSet.getString("nome"));
                aluno.setEmail(resultSet.getString("email"));

                return aluno;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return null; // Retorna null se o aluno não for encontrado
    }

    public String getEmailAlunoByArtigoId(Integer idArtigo) {
        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT a.email FROM aluno as a JOIN artigo as ar ON ar.enviadopor = a.matricula WHERE idArtigo = ?")) {
            statement.setInt(1, idArtigo);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                /*
                 * Aluno aluno = new Aluno();
                 * aluno.setMatricula(resultSet.getString("matricula"));
                 * aluno.setNome(resultSet.getString("nome"));
                 * aluno.setEmail(resultSet.getString("email"));
                 */

                return resultSet.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return null; // Retorna null se o aluno não for encontrado
    }

    public String getEmailOrientadorByArtigoId(Integer idArtigo) {
        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT p.email FROM professor as p JOIN artigo as a ON a.matriculaOrientador = p.matricula WHERE idArtigo = ?")) {
            statement.setInt(1, idArtigo);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                /*
                 * Aluno aluno = new Aluno();
                 * aluno.setMatricula(resultSet.getString("matricula"));
                 * aluno.setNome(resultSet.getString("nome"));
                 * aluno.setEmail(resultSet.getString("email"));
                 */

                return resultSet.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return null; // Retorna null se o aluno não for encontrado
    }

    public List<String> getEmailBancaByArtigoId(Integer idArtigo) {
        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT p.email FROM professor as p JOIN banca as b ON b.professorAvaliador = p.matricula WHERE artigoAvaliado = ?")) {
            statement.setInt(1, idArtigo);
            ResultSet resultSet = statement.executeQuery();
            List<String> emails = new ArrayList<>();

            while (resultSet.next()) {
                String email = resultSet.getString("email");
                emails.add(email);
            }

            return emails;
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return null; // Retorna null se o aluno não for encontrado
    }

    public String getEmailByMatricula(String matricula) {
        Aluno aluno = new Aluno();
        aluno = findAlunoByMatricula(matricula);

        if (aluno != null) {
            return aluno.getEmail();
        } else {
            return null;
        }
    }
}
