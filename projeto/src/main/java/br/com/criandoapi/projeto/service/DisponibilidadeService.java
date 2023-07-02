package br.com.criandoapi.projeto.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
                Date dateInicio = resultSet.getTimestamp("horaInicio");
                Date dateFim = resultSet.getTimestamp("horaFim");
                LocalTime horaInicio = dateInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                LocalTime horaFim = dateFim.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

                LocalDate data = resultSet.getObject("data", LocalDate.class);
                LocalDateTime dataHoraInicio = LocalDateTime.of(data, horaInicio);
                LocalDateTime dataHoraFim = LocalDateTime.of(data, horaFim);

                disponibilidade.setData(dataHoraInicio);
                disponibilidade.setHoraInicio(horaInicio);
                disponibilidade.setHoraFim(horaFim);

                listaDisponibilidade.add(disponibilidade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return listaDisponibilidade;
    }

    // Comparar as disponibilidades dos professores da banca, e retornar a que eles
    // tem em comum
    public LocalDate encontrarDataEmComum(List<Disponibilidade> disponibilidadesProfessor1,
            List<Disponibilidade> disponibilidadesProfessor2, List<Disponibilidade> disponibilidadesProfessor3) {

        for (Disponibilidade disponibilidade1 : disponibilidadesProfessor1) {
            for (Disponibilidade disponibilidade2 : disponibilidadesProfessor2) {
                for (Disponibilidade disponibilidade3 : disponibilidadesProfessor3) {
                    if (disponibilidade1.getData().toLocalDate().isEqual(disponibilidade2.getData().toLocalDate())
                            && disponibilidade1.getData().toLocalDate()
                                    .isEqual(disponibilidade3.getData().toLocalDate())
                            && disponibilidade1.getHoraInicio().equals(disponibilidade2.getHoraInicio())
                            && disponibilidade1.getHoraInicio().equals(disponibilidade3.getHoraInicio())
                            && disponibilidade1.getHoraFim().equals(disponibilidade2.getHoraFim())
                            && disponibilidade1.getHoraFim().equals(disponibilidade3.getHoraFim())) {
                        // A data encontrada em comum
                        return disponibilidade1.getData().toLocalDate();
                    }
                }
            }
        }

        // Nenhuma data em comum foi encontrada
        return null;
    }

    public LocalTime encontrarHoraEmComum(List<Disponibilidade> disponibilidadesProfessor1,
            List<Disponibilidade> disponibilidadesProfessor2, List<Disponibilidade> disponibilidadesProfessor3,
            LocalDate dataComum) {
        for (Disponibilidade disponibilidade1 : disponibilidadesProfessor1) {
            for (Disponibilidade disponibilidade2 : disponibilidadesProfessor2) {
                for (Disponibilidade disponibilidade3 : disponibilidadesProfessor3) {
                    if (disponibilidade1.getData().toLocalDate().isEqual(dataComum) &&
                            disponibilidade2.getData().toLocalDate().isEqual(dataComum) &&
                            disponibilidade3.getData().toLocalDate().isEqual(dataComum) &&
                            disponibilidade1.getHoraInicio().isBefore(disponibilidade2.getHoraFim()) &&
                            disponibilidade1.getHoraInicio().isBefore(disponibilidade3.getHoraFim()) &&
                            disponibilidade2.getHoraInicio().isBefore(disponibilidade1.getHoraFim()) &&
                            disponibilidade2.getHoraInicio().isBefore(disponibilidade3.getHoraFim()) &&
                            disponibilidade3.getHoraInicio().isBefore(disponibilidade1.getHoraFim()) &&
                            disponibilidade3.getHoraInicio().isBefore(disponibilidade2.getHoraFim())) {
                        // Encontrada uma hora em comum dentro dos intervalos de início e fim
                        return disponibilidade1.getHoraInicio();
                    }
                }
            }
        }
        return null; // Se nenhuma hora em comum for encontrada
    }

    // Se não tiver, pedir pra preencher de novo
}
