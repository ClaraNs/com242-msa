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

    public Artigo findArtigoByid(Integer idartigo) {
        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM artigo WHERE idArtigo = ?")) {
            statement.setInt(1, idartigo);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Artigo artigo = new Artigo();
                artigo.setIdArtigo(resultSet.getInt("idArtigo"));
                artigo.setTitulo(resultSet.getString("titulo"));
                artigo.setArquivo(resultSet.getBytes("arquivo"));
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

    public List<Artigo> getArtigosPorMatriculaAluno(String matricula) {
        List<Artigo> artigos = new ArrayList<>();

        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT * FROM artigo WHERE enviadoPor = ?")) {
            statement.setString(1, matricula);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Artigo artigo = new Artigo();
                artigo.setIdArtigo(resultSet.getInt("idArtigo"));
                artigo.setTitulo(resultSet.getString("titulo"));
                artigo.setArquivo(resultSet.getBytes("arquivo"));
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

                artigos.add(artigo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return artigos;
    }

    public List<Artigo> getArtigosPorMatriculaOrientador(String matricula) {
        List<Artigo> artigos = new ArrayList<>();

        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT * FROM artigo WHERE matriculaOrientador = ?")) {
            statement.setString(1, matricula);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Artigo artigo = new Artigo();
                artigo.setIdArtigo(resultSet.getInt("idArtigo"));
                artigo.setTitulo(resultSet.getString("titulo"));
                artigo.setArquivo(resultSet.getBytes("arquivo"));
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

                artigos.add(artigo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return artigos;
    }

    public List<Artigo> getArtigoEsperandoAvaliacao(String matricula) {
        List<Artigo> artigos = new ArrayList<>();

        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement( "SELECT a.idArtigo, a.titulo, a.arquivo, a.url, a.resumo, " +
                     "a.dataEnvio, a.alteracao, a.status, a.notaFinal, a.consideracoes, a.enviadoPor, a.matriculaOrientador " +
                     "FROM Artigo a " +
                     "JOIN Banca b ON a.idArtigo = b.artigoAvaliado " +
                     "JOIN ComposicaoBanca c ON b.idBanca = c.idBanca " +
                     "WHERE a.status = 2 AND c.professorAvaliador = ?")) {
            statement.setString(1, matricula);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Artigo artigo = new Artigo();
                artigo.setIdArtigo(resultSet.getInt("idArtigo"));
                artigo.setTitulo(resultSet.getString("titulo"));
                artigo.setArquivo(resultSet.getBytes("arquivo"));
                artigo.setUrl(resultSet.getString("url"));
                artigo.setResumo(resultSet.getString("resumo"));
                artigo.setDataEnvio(resultSet.getTimestamp("dataEnvio").toLocalDateTime());
                artigo.setAlteracao(resultSet.getTimestamp("alteracao").toLocalDateTime());

                // Corrigir a definição do campo "status" usando um objeto do tipo StatusArtigo
                StatusArtigo status = new StatusArtigo();
                status.setId(resultSet.getInt("status"));
                artigo.setStatus(status);
                Float nota = resultSet.getFloat("notaFinal");
                if (nota != null)
                    artigo.setNotaFinal(resultSet.getFloat("notaFinal"));

                artigo.setConsideracoes(resultSet.getString("consideracoes"));

                Aluno aluno = new Aluno();
                aluno.setMatricula(resultSet.getString("enviadoPor"));
                artigo.setEnviadoPor(aluno);

                Professor professor = new Professor();
                professor.setMatricula(resultSet.getString("matriculaOrientador"));
                artigo.setOrientador(professor);

                artigos.add(artigo);
            }
        } catch (

        SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return artigos;
    }

    // Retorna os artigo que esperam liberação do orientador
    public List<Artigo> getArtigosPendentesPorMatriculaOrientador(String matricula) {
        List<Artigo> artigos = new ArrayList<>();

        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT * FROM artigo WHERE status = 0 OR status = 2 AND matriculaOrientador = ?")) {
            statement.setString(1, matricula);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Artigo artigo = new Artigo();
                artigo.setIdArtigo(resultSet.getInt("idArtigo"));
                artigo.setTitulo(resultSet.getString("titulo"));
                artigo.setArquivo(resultSet.getBytes("arquivo"));
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

                artigos.add(artigo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return artigos;
    }

    public void setarNota(Integer idArtigo, Float nota) {
        Artigo artigo = new Artigo();
        artigo = findArtigoByid(idArtigo);
        artigo.setNotaFinal(nota);
        dao.save(artigo);
    }

    
}
