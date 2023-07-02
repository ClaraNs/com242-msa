package br.com.criandoapi.projeto.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public DisponibilidadeService(IDisponibilidade dao, ProfessorService professorService, BancaService bancaService, DatabaseConfig databaseConfig) {
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
                disponibilidade.setData(resultSet.getDate("data"));
                disponibilidade.setHoraInicio(resultSet.getTime("horaInicio"));
                disponibilidade.setHoraFim(resultSet.getTime("horaFim"));
    

                listaDisponibilidade.add(disponibilidade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Lidar com exceções, se necessário
        }

        return listaDisponibilidade;
    }
    
    //Comparar as disponibilidades dos professores da banca, e retornar a que eles tem em comum
    public Date encontrarDataHoraEmComum(List<Disponibilidade> disponibilidadesProfessor1, List<Disponibilidade> disponibilidadesProfessor2, List<Disponibilidade> disponibilidadesProfessor3) {
    for (Disponibilidade disponibilidade1 : disponibilidadesProfessor1) {
        for (Disponibilidade disponibilidade2 : disponibilidadesProfessor2) {
            for (Disponibilidade disponibilidade3 : disponibilidadesProfessor3) {
                if (disponibilidade1.getData().equals(disponibilidade2.getData()) &&
                    disponibilidade1.getData().equals(disponibilidade3.getData()) &&
                    disponibilidade1.getHoraInicio().equals(disponibilidade2.getHoraInicio()) &&
                    disponibilidade1.getHoraInicio().equals(disponibilidade3.getHoraInicio()) &&
                    disponibilidade1.getHoraFim().equals(disponibilidade2.getHoraFim()) &&
                    disponibilidade1.getHoraFim().equals(disponibilidade3.getHoraFim())) {
                    // A data e horário em comum foram encontrados
                    return disponibilidade1.getData();
                }
            }
        }
    }

    // Nenhum horário em comum foi encontrado
    return null;
}

    //Se não tiver, pedir pra preencher de novo
}
