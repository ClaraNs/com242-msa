package br.com.criandoapi.projeto.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;  // Importe a classe java.sql.Date
import java.time.LocalDate;
import java.time.LocalTime;

//import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import br.com.criandoapi.projeto.DAO.IArtigo;
import br.com.criandoapi.projeto.DAO.IBanca;
import br.com.criandoapi.projeto.model.Artigo;
import br.com.criandoapi.projeto.model.Banca;
import br.com.criandoapi.projeto.model.Disponibilidade;
import br.com.criandoapi.projeto.model.Professor;
//import br.com.criandoapi.projeto.model.StatusArtigo;
import br.com.criandoapi.projeto.model.StatusBanca;
import br.com.criandoapi.projeto.service.ArtigoService;
import br.com.criandoapi.projeto.service.BancaService;
import br.com.criandoapi.projeto.service.DisponibilidadeService;
import br.com.criandoapi.projeto.service.ProfessorService;

@RestController
@CrossOrigin("*")
@RequestMapping
public class BancaController {

    private IBanca dao;
    private final ProfessorService professorService;
    private final ArtigoService artigoService;
    private final BancaService bancaService;
    private final DisponibilidadeService disponibilidadeService;

    @Autowired
    public BancaController(IBanca dao, ProfessorService professorService, ArtigoService artigoService,
            BancaService bancaService, DisponibilidadeService disponibilidadeService) {
        this.dao = dao;
        this.professorService = professorService;
        this.artigoService = artigoService;
        this.bancaService = bancaService;
        this.disponibilidadeService = disponibilidadeService;
    }

    @GetMapping("/banca")
    public List<Banca> listaBancas() {
        return (List<Banca>) dao.findAll();
    }

    @GetMapping("/bancaporprofessor/{matricula}")
    public List<Banca> listaBancasPorProfessor(@PathVariable String matricula) {
        return bancaService.getBancasPorProfessor(matricula);
    }

    @PostMapping("/bancacadastro")
    public String cadastrarBancas(@RequestParam("professor1") String nomeProfessor1,
            @RequestParam("professor2") String nomeProfessor2,
            @RequestParam("idartigo") Integer idartigo) {

        // Encontre os objetos Professor com base nos nomes informados
        Professor professor1 = professorService.findProfessorByNome(nomeProfessor1);
        Professor professor2 = professorService.findProfessorByNome(nomeProfessor2);

        if (professor1 != null && professor2 != null) {

            LocalDateTime dia = LocalDateTime.now();
            // Crie as novas bancas
            Banca banca1 = new Banca();
            Artigo artigo = artigoService.findArtigoByid(idartigo);
            StatusBanca status = new StatusBanca();
            status.setId(0); // Set the ID of the desired status

            banca1.setProfessorAvaliador(professor1);
            banca1.setDataRegistro(dia);
            banca1.setDataAtualizacao(dia);
            banca1.setArtigoAvaliado(artigo);
            banca1.setStatus(status);

            Banca banca2 = new Banca();
            banca2.setProfessorAvaliador(professor2);
            banca2.setDataRegistro(dia);
            banca2.setDataAtualizacao(dia);
            banca2.setArtigoAvaliado(artigo);
            banca2.setStatus(status);

            // Salve as bancas no banco de dados
            dao.save(banca1);
            dao.save(banca2);

            return "Bancas cadastradas com sucesso.";
        } else {
            return "Erro ao cadastrar as bancas. Verifique os nomes dos professores informados.";
        }
    }

    // Verificar se existe alguma data que coincide, se sim, marca a avaliacao
    @PostMapping("/bancacadastraavaliacao/{idArtigo}")
    public String cadastrarAvaliacao(@PathVariable Integer idArtigo) {

        List<Integer> bancas = bancaService.getBancasByArtigoAvaliado(idArtigo);

        // pegar professores da banca e o orientador, pegar a disponibilidade deles
        List<Disponibilidade> disponibilidadesProfessor1 = new ArrayList<>();
        List<Disponibilidade> disponibilidadesProfessor2 = new ArrayList<>();
        List<Disponibilidade> disponibilidadesProfessor3 = new ArrayList<>();
        int counter = 1; // Counter variable

        for (Integer bancaId : bancas) {
            Banca banca = bancaService.getBancaById(bancaId);
            Professor professor = new Professor();
            professor = banca.getProfessorAvaliador();
            String matricula = professor.getMatricula();

            //List<Disponibilidade> disponibilidades;

            if (counter == 1) {
                disponibilidadesProfessor1 = disponibilidadeService.getDisponibilidadesPorMatricula(matricula, bancaId);
            } else if (counter == 2) {
                disponibilidadesProfessor2 = disponibilidadeService.getDisponibilidadesPorMatricula(matricula, bancaId);
            }

            // Increment the counter
            counter = (counter % 2) + 1;

        }

        Artigo artigo = new Artigo();
        artigo = artigoService.findArtigoByid(idArtigo);
    
        Professor orientador = new Professor();
        orientador = artigo.getOrientador();

        // Como fica o orientador?? Está disponível em relação a qual banca? A banca de menor número
        disponibilidadesProfessor3 = disponibilidadeService.getDisponibilidadesPorMatricula(orientador.getMatricula(), bancaService.getPrimeiraBancaByArtigoAvaliado(idArtigo));

        // chamar encontrarDataHoraEmComum passando as 3 disponibilidades
        Date dataAvaliacao = disponibilidadeService.encontrarDataHoraEmComum(disponibilidadesProfessor1, disponibilidadesProfessor2, disponibilidadesProfessor3);

        if( dataAvaliacao != null){
            for (Integer bancaId : bancas) {
                Banca banca = bancaService.getBancaById(bancaId);
                LocalDateTime dataHora = LocalDateTime.ofInstant(dataAvaliacao.toInstant(), ZoneId.systemDefault());
                banca.setDataAvaliacao(dataHora);
                banca.setDataHora(dataHora);
            }

            return "Horário de avaliação cadastrado com sucesso";
        } else {
            return "Problema para o cadastro de horário da avaliação, não foi possível achar um horario em comum.";
        }
    }
}
