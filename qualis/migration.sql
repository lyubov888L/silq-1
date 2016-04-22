DROP MATERIALIZED VIEW IF EXISTS mv_areas_avaliacao_periodico;
DROP TABLE IF EXISTS tb_qualis_periodico_novo;

DROP SEQUENCE IF EXISTS sq_qualis_periodico_novo;
CREATE SEQUENCE sq_qualis_periodico_novo;

CREATE TABLE tb_qualis_periodico_novo (
   co_seq_periodico numeric(19) PRIMARY KEY NOT NULL DEFAULT NEXTVAL('sq_qualis_periodico_novo'),
   co_issn varchar(9),
   no_titulo varchar(255),
   no_estrato varchar(2),
   no_area_avaliacao varchar(50),
   nu_ano int
);

CREATE INDEX ON tb_qualis_periodico_novo (co_issn);
CREATE INDEX ON tb_qualis_periodico_novo (no_area_avaliacao);
CREATE INDEX on tb_qualis_periodico_novo (co_issn, no_area_avaliacao);

-- Inserindo valores das tabelas
\COPY tb_qualis_periodico_novo(co_issn, no_titulo, no_area_avaliacao, no_estrato) FROM 2010_75786_registros.xls DELIMITER E'\t' CSV HEADER ENCODING 'latin1';
UPDATE tb_qualis_periodico_novo SET nu_ano = 2010 WHERE nu_ano IS NULL;

\COPY tb_qualis_periodico_novo(co_issn, no_titulo, no_area_avaliacao, no_estrato) FROM 2011_66171_registros.xls DELIMITER E'\t' CSV HEADER ENCODING 'latin1';
UPDATE tb_qualis_periodico_novo SET nu_ano = 2011 WHERE nu_ano IS NULL;

\COPY tb_qualis_periodico_novo(co_issn, no_titulo, no_area_avaliacao, no_estrato) FROM 2012_108272_registros.xls DELIMITER E'\t' CSV HEADER ENCODING 'latin1';
UPDATE tb_qualis_periodico_novo SET nu_ano = 2012 WHERE nu_ano IS NULL;

\COPY tb_qualis_periodico_novo(co_issn, no_titulo, no_area_avaliacao, no_estrato) FROM 2013_44479_registros.xls DELIMITER E'\t' CSV HEADER ENCODING 'latin1';
UPDATE tb_qualis_periodico_novo SET nu_ano = 2013 WHERE nu_ano IS NULL;

\COPY tb_qualis_periodico_novo(co_issn, no_titulo, no_area_avaliacao, no_estrato) FROM 2014_44582_registros.xls DELIMITER E'\t' CSV HEADER ENCODING 'latin1';
UPDATE tb_qualis_periodico_novo SET nu_ano = 2014 WHERE nu_ano IS NULL;

-- Removendo espaços em branco das colunas:
-- Exemplo: "ADMINISTRAÇÃO ..         " -> "ADMINISTRAÇÃO .."
UPDATE tb_qualis_periodico_novo SET no_titulo = trim(no_titulo), no_estrato = trim(no_estrato), no_area_avaliacao = trim(no_area_avaliacao);

--
CREATE MATERIALIZED VIEW mv_areas_avaliacao_periodico AS
	SELECT DISTINCT no_area_avaliacao FROM tb_qualis_periodico_novo ORDER BY no_area_avaliacao;
