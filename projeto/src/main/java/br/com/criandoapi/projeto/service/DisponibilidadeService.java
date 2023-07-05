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

    public LocalDateTime encontrarDataEmComum(List<List<Disponibilidade>> todasDisponibilidades) {
        if (todasDisponibilidades.isEmpty()) {
            // Caso não haja disponibilidades, retorne null ou faça o tratamento apropriado
            return null;
        }

        // Obtenha as disponibilidades do primeiro professor da lista
        List<Disponibilidade> disponibilidadesProfessor1 = todasDisponibilidades.get(0);

        // Percorra as disponibilidades do primeiro professor
        for (Disponibilidade disponibilidade1 : disponibilidadesProfessor1) {
            LocalDateTime dataHora1 = disponibilidade1.getData();
            boolean isDataEmComum = true;

            // Verifique se a data é comum a todos os outros professores
            for (int i = 1; i < todasDisponibilidades.size(); i++) {
                List<Disponibilidade> disponibilidadesProfessor = todasDisponibilidades.get(i);
                boolean found = false;

                // Verifique se a data está presente nas disponibilidades do professor atual
                for (Disponibilidade disponibilidade : disponibilidadesProfessor) {
                    LocalDateTime dataHora = disponibilidade.getData();

                    if (dataHora.equals(dataHora1)) {
                        found = true;
                        break;
                    }
                }

                // Se a data não foi encontrada nas disponibilidades do professor atual, marque
                // como não sendo uma data em comum
                if (!found) {
                    isDataEmComum = false;
                    break;
                }
            }

            // Se a data é comum a todos os professores, retorne-a
            if (isDataEmComum) {
                return dataHora1;
            }
        }

        // Nenhuma data em comum foi encontrada
        return null;
    }

    // Se não tiver, pedir pra preencher de novo
}
