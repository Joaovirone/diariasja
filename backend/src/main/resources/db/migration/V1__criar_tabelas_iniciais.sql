-- 1. Criação da Tabela de Usuários (Contratantes e Prestadores de Serviço)
CREATE TABLE tb_usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    data_nascimento DATE NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    
    -- Campos de Auditoria (Spring Data JPA Auditing)
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Criação da Tabela de Categorias de Serviço (Vitrine)
CREATE TABLE tb_categoria_servico (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    
    -- Campos de Auditoria
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. Criação da Tabela de Diárias (O Coração do Sistema)
CREATE TABLE tb_diarias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contratante_id BIGINT NOT NULL,
    contratado_id BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    data_servico DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    nota INT,
    
    -- Campos de Auditoria
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Chaves Estrangeiras (Relacionamentos)
    CONSTRAINT fk_diaria_contratante FOREIGN KEY (contratante_id) REFERENCES tb_usuarios(id),
    CONSTRAINT fk_diaria_contratado FOREIGN KEY (contratado_id) REFERENCES tb_usuarios(id),
    CONSTRAINT fk_diaria_categoria FOREIGN KEY (categoria_id) REFERENCES tb_categoria_servico(id)
);
