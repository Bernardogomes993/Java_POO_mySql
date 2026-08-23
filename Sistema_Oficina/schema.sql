CREATE DATABASE IF NOT EXISTS oficina_db;
USE oficina_db;

CREATE TABLE IF NOT EXISTS servicos (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        tipo VARCHAR(30) NOT NULL,
    data_inicio DATETIME NOT NULL,
    data_fim DATETIME NULL,
    cliente VARCHAR(100) NOT NULL,
    funcionario VARCHAR(100) NOT NULL,
    equipamento VARCHAR(100) NOT NULL,
    preco_hora_ou_fixo DECIMAL(10, 2) NOT NULL,
    pago BOOLEAN DEFAULT FALSE,
    concluido BOOLEAN DEFAULT FALSE
    );

CREATE TABLE IF NOT EXISTS componentes (
                                           id INT AUTO_INCREMENT PRIMARY KEY,
                                           servico_id INT NOT NULL,
                                           nome VARCHAR(100) NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (servico_id) REFERENCES servicos(id) ON DELETE CASCADE
    );

CREATE OR REPLACE VIEW vw_servicos_ordenados_duracao AS
SELECT
    id,
    tipo,
    cliente,
    funcionario,
    equipamento,
    data_inicio,
    data_fim,
    preco_hora_ou_fixo,
    pago,
    concluido,
    TIMESTAMPDIFF(MINUTE, data_inicio, data_fim) AS duracao_minutos,
    ROUND(TIMESTAMPDIFF(MINUTE, data_inicio, data_fim) / 60.0, 2) AS duracao_horas
FROM servicos
WHERE concluido = TRUE AND data_fim IS NOT NULL
ORDER BY duracao_minutos ASC;