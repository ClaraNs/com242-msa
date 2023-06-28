package br.com.criandoapi.projeto.service;

import java.io.Console;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.criandoapi.projeto.DAO.IArtigo;
import br.com.criandoapi.projeto.config.DatabaseConfig;
import br.com.criandoapi.projeto.model.Aluno;
import br.com.criandoapi.projeto.model.Artigo;
import br.com.criandoapi.projeto.model.Professor;
import br.com.criandoapi.projeto.model.StatusArtigo;

@Service
public class ArtigoService {

    private final IArtigo dao;
    private final DatabaseConfig databaseConfig;

    @Autowired
    public ArtigoService(IArtigo dao, DatabaseConfig databaseConfig) {
        this.dao = dao;
        this.databaseConfig = databaseConfig;
    }

    public Artigo findArtigoByid(@RequestParam("id") Integer idartigo) {
        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM artigo WHERE idArtigo = ?")) {
            statement.setInt(1, idartigo);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Artigo artigo = new Artigo();
                artigo.setIdArtigo(resultSet.getInt("idArtigo"));
                artigo.setTitulo(resultSet.getString("titulo"));
                artigo.setUrl(resultSet.getString("url"));
                artigo.setResumo(resultSet.getString("resumo"));
                artigo.setDataEnvio(resultSet.getTimestamp("dataEnvio").toLocalDateTime());
                artigo.setAlteracao(resultSet.getTimestamp("alteracao").toLocalDateTime());

                // Corrigir a definição do campo "status" usando um objeto do tipo StatusArtigo
                StatusArtigo status = new StatusArtigo();
                status.setId(resultSet.getInt("status"));
                artigo.setStatus(status);

                artigo.setNotaFinal(resultSet.getFloat("notaFinal"));
                artigo.setConsideracoes(resultSet.getString("consideracoes"));

                // Corrigir a definição do campo "enviadoPor" usando um objeto do tipo Aluno
                Aluno aluno = new Aluno();
                aluno.setMatricula(resultSet.getString("enviadoPor"));
                artigo.setEnviadoPor(aluno);

                // Corrigir a definição do campo "matriculaOrientador" usando um objeto do tipo
                // Professor
                Professor professor = new Professor();
                professor.setMatricula(resultSet.getString("matriculaOrientador"));
                artigo.setOrientador(professor);

                return artigo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return null; // Retorna null se o artigo não for encontrado
    }
}
