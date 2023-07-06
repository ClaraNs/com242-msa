CREATE TABLE Aluno (
	matricula VARCHAR ( 10 ) PRIMARY KEY,
	nome VARCHAR ( 50 ) NOT NULL,
	email VARCHAR ( 50 ) NOT NULL
);

CREATE TABLE Professor (
	matricula VARCHAR ( 10 ) PRIMARY KEY,
	nome VARCHAR ( 50 ) NOT NULL,
	email VARCHAR ( 50 ) NOT NULL,
	coordenador BOOLEAN DEFAULT FALSE
);

CREATE TABLE StatusArtigo (
	id int PRIMARY KEY,
	descricao VARCHAR(50) NOT NULL
);

CREATE TABLE Artigo (
	idArtigo serial PRIMARY KEY,
	titulo VARCHAR(80) NOT NULL,
	arquivo BYTEA NOT NULL,
	url TEXT,
	resumo TEXT NOT NULL,
	dataEnvio TIMESTAMP NOT NULL,
	alteracao TIMESTAMP NOT NULL,
	status INT NOT NULL DEFAULT 0,
	notaFinal FLOAT,
	consideracoes TEXT,
	enviadoPor VARCHAR(10) NOT NULL,
	matriculaOrientador VARCHAR(10) NOT NULL,
	FOREIGN KEY (status) 
		REFERENCES StatusArtigo (id),
	FOREIGN KEY (enviadoPor) 
		REFERENCES Aluno (matricula),
	FOREIGN KEY (matriculaOrientador) 
		REFERENCES Professor (matricula)
);

CREATE TABLE StatusBanca (
	id int PRIMARY KEY,
	descricao VARCHAR(50) NOT NULL
);

CREATE TABLE Banca (
	idBanca serial PRIMARY KEY,
	dataRegistro TIMESTAMP NOT NULL,
	dataAtualizacao TIMESTAMP NOT NULL,
	dataAvaliacao TIMESTAMP,
	artigoAvaliado INT NOT NULL,
	status int NOT NULL DEFAULT 0,
	FOREIGN KEY (artigoAvaliado)
		REFERENCES Artigo (idArtigo),
	FOREIGN KEY (status)
		REFERENCES StatusBanca (id)
);

CREATE TABLE ComposicaoBanca (
	idComposicao SERIAL PRIMARY KEY,
	professorAvaliador VARCHAR ( 10 ) NOT NULL,
	idBanca INT NOT NULL,
	nota FLOAT DEFAULT null,
	consideracoes TEXT,
	UNIQUE(professorAvaliador, idBanca),
	FOREIGN KEY (professorAvaliador)
		REFERENCES Professor (matricula),
	FOREIGN KEY (idBanca)
		REFERENCES Banca (idBanca)
);

CREATE TABLE Disponibilidade (
    idDisponibilidade SERIAL PRIMARY KEY,
	idBanca INT NOT NULL,
	data TIMESTAMP NOT NULL,
	aprovacao BOOLEAN DEFAULT true NOT NULL,
    FOREIGN KEY (idBanca)
    	REFERENCES Banca (idBanca)
); -- adicioanr Booleano que servirá para todos

-- Trigger
CREATE OR REPLACE FUNCTION verificarNotas()
  RETURNS TRIGGER AS
$$
DECLARE
  totalProfessores INT;
  notasPositivas INT;
  artigoId INT;
  novaNota FLOAT;
BEGIN
  -- Obtém o total de professores da banca
  SELECT COUNT(idComposicao) INTO totalProfessores FROM ComposicaoBanca WHERE idBanca = NEW.idBanca;

  -- Obtém o total de notas válidas lançadas na banca
  SELECT COUNT(*) INTO notasPositivas FROM ComposicaoBanca WHERE idBanca = NEW.idBanca AND nota > 0;

  -- Verifica se o número de professores é igual ao número de notas positivas
  IF totalProfessores = notasPositivas THEN
    -- Obtém o ID do artigo avaliado pela banca
    SELECT artigoAvaliado INTO artigoId FROM banca JOIN ComposicaoBanca ON banca.idBanca = ComposicaoBanca.idBanca WHERE banca.idBanca = NEW.idBanca LIMIT 1;

    -- Calcula a média das notas
    SELECT AVG(nota) INTO novaNota FROM ComposicaoBanca WHERE idBanca = NEW.idBanca;

    -- Atualiza o valor da coluna notaFinal na tabela Artigo
    UPDATE Artigo SET notaFinal = novaNota WHERE idArtigo = artigoId;
  END IF;

  RETURN NEW;
END;
$$
LANGUAGE plpgsql;

-- Cria a trigger para inserção na tabela ComposicaoBanca
CREATE TRIGGER verificarNotasTrigger
AFTER INSERT OR UPDATE OF nota ON ComposicaoBanca
FOR EACH ROW
EXECUTE FUNCTION verificarNotas();
--

INSERT INTO StatusBanca VALUES(0, 'Aguardando Aprovação');
INSERT INTO StatusBanca VALUES(1, 'Banca Liberada - Aguardando confirmação de data');
INSERT INTO StatusBanca VALUES(2, 'Banca Marcada - Data para defesa confirmada');
INSERT INTO StatusBanca VALUES(3, 'TFG aprovado - Necessita correção');
INSERT INTO StatusBanca VALUES(4, 'TFG reprovado - Possibilidade de correção');
INSERT INTO StatusBanca VALUES(5, 'TFG Finalizado - aprovado');
INSERT INTO StatusBanca VALUES(6, 'TFG Finalizado - reprovado');

INSERT INTO StatusArtigo VALUES(0, 'Aguardando Aprovação')
INSERT INTO StatusArtigo VALUES(1, 'Aguardando Realização da Correção proposta');
INSERT INTO StatusArtigo VALUES(2, 'Aguardando Aprovação arquivo corrigido');
INSERT INTO StatusArtigo VALUES(3, 'Pronto para avaliação da banca');
INSERT INTO StatusArtigo VALUES(4, 'Aguardando Realização da Correção proposta Banca');
INSERT INTO StatusArtigo VALUES(5, 'Aguardando Aprovação das Correções (Banca)');
INSERT INTO StatusArtigo VALUES(6, 'Artigo Corrigido e aprovado.'); -- aprovado de 1ª
INSERT INTO StatusArtigo VALUES(7, 'Artigo Reprovado com Possibilidade de Correção');
INSERT INTO StatusArtigo VALUES(8, 'Artigo Reprovado Corrigido - Aguardando Aprovação');
INSERT INTO StatusArtigo VALUES(9, 'Artigo Resubmetido para Banca');
INSERT INTO StatusArtigo VALUES(10, 'Artigo Reprovado.');
INSERT INTO Aluno (matricula, nome, email)
VALUES('2020123456', 'Clara', 'anaclarans@live.com');	
INSERT INTO Aluno (matricula, nome, email)
VALUES('2020010680', 'Ana', 'anaclarans@unifei.edu.br');

INSERT INTO Professor(matricula, nome, email)
VALUES('6789011111', 'Anna', 'Anna@gmail.com');
INSERT INTO Professor(matricula, nome, email)
VALUES('6789022222', 'Luiz', 'Luiz@gmail.com');
INSERT INTO Professor(matricula, nome, email)
VALUES('6789033333', 'João', 'anaclarans@live.com');



SELECT COUNT(idComposicao) FROM ComposicaoBanca
SELECT COUNT(*) FROM ComposicaoBanca WHERE nota > 0;
 
SELECT a.status, a.notafinal, b.status FROM ARTIGO a JOIN banca b ON b.artigoavaliado = a.idartigo
SELECT * FROM artigo
SELECT * FROM BANCA
SELECT * FROM composicaoBanca
UPDATE BANCA SET STATUS = 4
UPDATE artigo SET STATUS = 7
UPDATE artigo SET notafinal = -1
UPDATE composicaobanca SET nota = -1
/*DELETE FROM disponibilidade
SELECT * FROM artigo where idartigo = 2
SELECT * FROM statusartigo
SELECT * FROM statusbanca

DELETE FROM VERSAO
DELETE FROM disponibilidade
DELETE FROM composicaobanca
DELETE FROM banca
DELETE FROM artigo
DELETE FROM professor
DELETE FROM Aluno
DELETE FROM statusartigo
DELETE FROM statusbanca
SELECT * FROM artigo WHERE status IN (0, 2, 5, 8) AND matriculaOrientador = '6789033333'
SELECT * FROM artigo
UPDATE artigo SET status = 7*/