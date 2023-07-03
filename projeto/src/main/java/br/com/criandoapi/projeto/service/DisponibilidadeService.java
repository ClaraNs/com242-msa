package br.com.criandoapi.projeto.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

    // Comparar as disponibilidades dos professores da banca, e retornar a que eles
    // tem em comum
    /*
     * public LocalDate encontrarDataEmComum(List<Disponibilidade>
     * disponibilidadesProfessor1,
     * List<Disponibilidade> disponibilidadesProfessor2, List<Disponibilidade>
     * disponibilidadesProfessor3) {
     * 
     * for (Disponibilidade disponibilidade1 : disponibilidadesProfessor1) {
     * for (Disponibilidade disponibilidade2 : disponibilidadesProfessor2) {
     * for (Disponibilidade disponibilidade3 : disponibilidadesProfessor3) {
     * if
     * (disponibilidade1.getData().toLocalDate().isEqual(disponibilidade2.getData().
     * toLocalDate())
     * && disponibilidade1.getData().toLocalDate()
     * .isEqual(disponibilidade3.getData().toLocalDate())
     * && disponibilidade1.getHoraInicio().equals(disponibilidade2.getHoraInicio())
     * && disponibilidade1.getHoraInicio().equals(disponibilidade3.getHoraInicio())
     * && disponibilidade1.getHoraFim().equals(disponibilidade2.getHoraFim())
     * && disponibilidade1.getHoraFim().equals(disponibilidade3.getHoraFim())) {
     * // A data encontrada em comum
     * return disponibilidade1.getData().toLocalDate();
     * }
     * }
     * }
     * }
     * 
     * // Nenhuma data em comum foi encontrada
     * return null;
     * }
     * 
     * public LocalTime encontrarHoraEmComum(List<Disponibilidade>
     * disponibilidadesProfessor1,
     * List<Disponibilidade> disponibilidadesProfessor2, List<Disponibilidade>
     * disponibilidadesProfessor3) {
     * LocalDate dataEmComum = encontrarDataEmComum(disponibilidadesProfessor1,
     * disponibilidadesProfessor2,
     * disponibilidadesProfessor3);
     * 
     * if (dataEmComum != null) {
     * for (Disponibilidade disponibilidade1 : disponibilidadesProfessor1) {
     * if (disponibilidade1.getData().toLocalDate().isEqual(dataEmComum)) {
     * for (Disponibilidade disponibilidade2 : disponibilidadesProfessor2) {
     * if (disponibilidade2.getData().toLocalDate().isEqual(dataEmComum)) {
     * for (Disponibilidade disponibilidade3 : disponibilidadesProfessor3) {
     * if (disponibilidade3.getData().toLocalDate().isEqual(dataEmComum)) {
     * LocalTime horaInicio1 = disponibilidade1.getHoraInicio();
     * LocalTime horaFim1 = disponibilidade1.getHoraFim();
     * LocalTime horaInicio2 = disponibilidade2.getHoraInicio();
     * LocalTime horaFim2 = disponibilidade2.getHoraFim();
     * LocalTime horaInicio3 = disponibilidade3.getHoraInicio();
     * LocalTime horaFim3 = disponibilidade3.getHoraFim();
     * 
     * // Verificar se há uma sobreposição de horários
     * if (horaInicio1.isBefore(horaFim2) && horaFim1.isAfter(horaInicio2) &&
     * horaInicio1.isBefore(horaFim3) && horaFim1.isAfter(horaInicio3) &&
     * horaInicio2.isBefore(horaFim1) && horaFim2.isAfter(horaInicio1) &&
     * horaInicio2.isBefore(horaFim3) && horaFim2.isAfter(horaInicio3) &&
     * horaInicio3.isBefore(horaFim1) && horaFim3.isAfter(horaInicio1) &&
     * horaInicio3.isBefore(horaFim2) && horaFim3.isAfter(horaInicio2)) {
     * // Encontrada uma hora em comum com sobreposição
     * return horaInicio1; // Podemos retornar a hora de início de qualquer
     * disponibilidade
     * }
     * }
     * }
     * }
     * }
     * }
     * }
     * }
     * 
     * return null; // Se nenhuma hora em comum com sobreposição for encontrada
     * }
     */

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
