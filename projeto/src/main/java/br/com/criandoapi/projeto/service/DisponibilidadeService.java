package br.com.criandoapi.projeto.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.criandoapi.projeto.DAO.IDisponibilidade;
import br.com.criandoapi.projeto.config.DatabaseConfig;
import br.com.criandoapi.projeto.model.Banca;
import br.com.criandoapi.projeto.model.Disponibilidade;
import br.com.criandoapi.projeto.model.Professor;

@Service
public class DisponibilidadeService {

    private final IDisponibilidade dao;
    private final ProfessorService professorService;
    private final BancaService bancaService;
    private final DatabaseConfig databaseConfig;

    @Autowired
    public DisponibilidadeService(IDisponibilidade dao, ProfessorService professorService, BancaService bancaService,
            DatabaseConfig databaseConfig) {
        this.dao = dao;
        this.professorService = professorService;
        this.bancaService = bancaService;
        this.databaseConfig = databaseConfig;
    }

    // Recuperar as disponibilidades por professor (retornar uma lista)
    public List<Disponibilidade> getDisponibilidadesPorMatricula(String matricula, Integer idBanca) {
        List<Disponibilidade> listaDisponibilidade = new ArrayList<>();

        try (Connection connection = databaseConfig.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT * FROM disponibilidade WHERE professor = ? and idBanca = ?")) {
            statement.setString(1, matricula);
            statement.setInt(2, idBanca);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Disponibilidade disponibilidade = new Disponibilidade();
                disponibilidade.setIdDisponibilidade(resultSet.getInt("idDisponibilidade"));

                Professor professor = new Professor();
                professor = professorService.FindProfessorById(matricula);
                disponibilidade.setProfessor(professor);

                Banca banca = new Banca();
                banca = bancaService.getBancaById(idBanca);
                disponibilidade.setBanca(banca);

                // Converter os valores de Date para LocalTime
                Timestamp timestamp = resultSet.getTimestamp("data");
                LocalDateTime dataHoraInicio = timestamp.toLocalDateTime();
                disponibilidade.setData(dataHoraInicio);

                listaDisponibilidade.add(disponibilidade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return listaDisponibilidade;
    }

    public LocalDateTime encontrarDataEmComum(List<Disponibilidade> disponibilidadesProfessor1,
            List<Disponibilidade> disponibilidadesProfessor2,
            List<Disponibilidade> disponibilidadesProfessor3) {

        for (Disponibilidade disponibilidade1 : disponibilidadesProfessor1) {
            for (Disponibilidade disponibilidade2 : disponibilidadesProfessor2) {
                for (Disponibilidade disponibilidade3 : disponibilidadesProfessor3) {
                    LocalDateTime dataHora1 = disponibilidade1.getData();
                    LocalDateTime dataHora2 = disponibilidade2.getData();
                    LocalDateTime dataHora3 = disponibilidade3.getData();

                    if (dataHora1.equals(dataHora2) && dataHora1.equals(dataHora3)) {
                        // A data encontrada em comum
                        return dataHora1;
                    }
                }
            }
        }

        // Nenhuma data em comum foi encontrada
        return null;
    }

    // Se não tiver, pedir pra preencher de novo
}
