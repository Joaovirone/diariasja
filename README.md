# Diárias JÁ

![Status](https://img.shields.io/badge/status-Production%20Ready-brightgreen)
![Vue.js](https://img.shields.io/badge/Vue.js-3.x-4FC08D?logo=vue.js)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=spring)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker)

Uma plataforma moderna para conectar clientes a prestadores de serviços diários, com gerenciamento completo de agendamentos, avaliações e perfis.

## 📖 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
  - [Funcionalidades](#-funcionalidades)
  - [Stack Tecnológica](#-stack-tecnológica)
- [🏗️ Arquitetura](#️-arquitetura)
- [🚀 Começando](#-começando)
  - [Pré-requisitos](#-pré-requisitos)
  - [Instalação e Execução](#-instalação-e-execução)
- [🐳 Usando com Docker (Recomendado)](#-usando-com-docker-recomendado)
- [👨‍💻 Usando Manualmente](#-usando-manualmente)
- [🧪 Rodando Testes](#-rodando-testes)
- [🔗 Endpoints Importantes](#-endpoints-importantes)
- [📁 Estrutura do Projeto](#-estrutura-do-projeto)
- [🚢 Deploy](#-deploy)
- [🤝 Contribuindo](#-contribuindo)
- [📄 Licença](#-licença)

## ✨ Sobre o Projeto

O **Diárias JÁ** foi desenvolvido para ser uma solução completa e moderna para o agenciamento de serviços diários. A plataforma permite que **Contratantes** encontrem e contratem profissionais para serviços rápidos, enquanto **Profissionais** podem encontrar oportunidades de trabalho, gerenciar sua agenda e construir sua reputação.

O projeto é dividido em duas partes principais:
-   **Backend**: Uma API RESTful robusta construída com Spring Boot, responsável por toda a lógica de negócio, gerenciamento de dados e segurança.
-   **Frontend**: Uma Single Page Application (SPA) reativa e intuitiva, construída com Vue.js 3, que consome a API do backend para fornecer uma experiência de usuário fluida e moderna.

### ⭐ Funcionalidades

-   ✅ **Autenticação Segura**: Sistema de Login/Cadastro com JWT.
-   ✅ **Perfis de Usuário**: Distinção entre `Contratante` e `Profissional`.
-   ✅ **Gerenciamento de Diárias (CRUD)**: Solicitar, aceitar, cancelar e acompanhar o status das diárias.
-   ✅ **Sistema de Avaliações**: Contratantes podem avaliar os profissionais após a conclusão do serviço.
-   ✅ **Gerenciamento de Categorias**: CRUD completo para as categorias de serviços.
-   ✅ **Dashboard com Estatísticas**: Visão geral para acompanhamento rápido.
-   ✅ **Busca e Filtros**: Encontre diárias e profissionais com facilidade.
-   ✅ **Design Responsivo**: Experiência otimizada para desktop, tablets e celulares.

### 🛠️ Stack Tecnológica

| Área | Tecnologia | Propósito |
| :--- | :--- | :--- |
| **Frontend** | Vue.js 3 | Framework reativo para a UI |
| | Vite | Build tool e servidor de desenvolvimento |
| | Pinia | Gerenciamento de estado centralizado |
| | Vue Router | Roteamento de páginas (SPA) |
| | Axios | Cliente HTTP para consumir a API |
| **Backend** | Spring Boot 3 | Framework para a API REST |
| | Java 17 | Linguagem de programação |
| | Spring Security | Autenticação e autorização com JWT |
| | JPA / Hibernate | Persistência de dados (ORM) |
| | Flyway | Versionamento de schema do banco de dados |
| **Banco de Dados** | MySQL | Banco de dados relacional |
| | Redis | Cache e gerenciamento de sessão (opcional) |
| **DevOps** | Docker | Conteinerização da aplicação e serviços |
| | Maven | Gerenciamento de dependências do backend |

## 🏗️ Arquitetura

A aplicação segue uma arquitetura cliente-servidor desacoplada, onde o frontend (cliente) se comunica com o backend (servidor) através de uma API REST.

```
┌───────────────────────────┐      ┌──────────────────────────┐
│    Cliente (Navegador)    │      │     Servidor (Backend)   │
├───────────────────────────┤      ├──────────────────────────┤
│                           │      │                          │
│   Frontend (Vue.js 3)     │◀────▶│   API REST (Spring Boot) │
│   (localhost:5173)        │      │   (localhost:8080)       │
│                           │      │                          │
└───────────────────────────┘      └───────────┬──────────────┘
                                               │
                                               ▼
                               ┌──────────────────────────┐
                               │     Banco de Dados       │
                               │   (MySQL + Redis)        │
                               └──────────────────────────┘
```

-   **Frontend (Vue.js)**: Utiliza uma arquitetura de 3 camadas (View → Store → Service) para organizar a lógica de UI, estado e comunicação com a API.
-   **Backend (Spring Boot)**: Segue uma arquitetura em camadas (Controller → Service → Repository) para garantir a separação de responsabilidades.

## 🚀 Começando

Siga os passos abaixo para configurar e executar o projeto em seu ambiente de desenvolvimento.

### ✅ Pré-requisitos

-   Git
-   Node.js (versão 18 ou superior)
-   JDK (versão 17 ou superior)
-   Maven
-   Docker e Docker Compose

### ⚙️ Instalação e Execução

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/seu-usuario/diarias-ja.git
    cd diarias-ja
    ```

2.  **Escolha um método de execução:**
    -   Docker (Recomendado): A forma mais simples e rápida.
    -   Manual: Para ter mais controle sobre cada parte da aplicação.

##  Usando com Docker (Recomendado)

Este método utiliza Docker Compose para orquestrar todos os serviços necessários (Frontend, Backend, MySQL, Redis) com um único comando.

1.  **Execute os scripts de inicialização:**
    -   No Windows:
        ```bash
        start.bat
        ```
    -   No Linux ou macOS:
        ```bash
        chmod +x start.sh
        ./start.sh
        ```

2.  **Acesse a aplicação:**
    -   Frontend: **http://localhost:5173**
    -   Backend API: **http://localhost:8080**

Os scripts `start` são atalhos para o comando `docker-compose up -d --build`.

Para parar todos os serviços, execute:
```bash
docker-compose down
```

## 👨‍💻 Usando Manualmente

Este método requer que você execute o backend e o frontend em terminais separados.

### 1. Iniciar o Banco de Dados

Primeiro, suba as instâncias do MySQL e Redis com Docker Compose.

```bash
docker-compose up -d mysql redis
```
Isso irá iniciar o banco de dados na porta `3300` e o Redis na porta `6379`.

### 2. Configurar e Rodar o Backend (Spring Boot)

1.  **Navegue até a pasta do backend:**
    ```bash
    cd backend
    ```

2.  **Instale as dependências e execute a aplicação:**
    O Spring Boot usará as variáveis de ambiente definidas no `docker-compose.yaml` para se conectar ao banco de dados.
    ```bash
    ./mvnw spring-boot:run
    ```
    O backend estará disponível em `http://localhost:8080`.

### 3. Configurar e Rodar o Frontend (Vue.js)

1.  **Abra um novo terminal e navegue até a pasta do frontend:**
    ```bash
    cd frontend
    ```

2.  **Copie o arquivo de ambiente e instale as dependências:**
    ```bash
    cp .env.example .env.local
    npm install
    ```
    O arquivo `.env.local` já vem configurado para se conectar à API em `http://localhost:8080`.

3.  **Inicie o servidor de desenvolvimento:**
    ```bash
    npm run dev
    ```
    O frontend estará disponível em `http://localhost:5173`.

## 🧪 Rodando Testes

-   **Frontend (em breve, com Vitest):**
    ```bash
    cd frontend
    npm run test
    ```

-   **Backend (JUnit):**
    ```bash
    cd backend
    ./mvnw test
    ```

## 🔗 Endpoints Importantes

| Serviço | URL | Descrição |
| :--- | :--- | :--- |
| **Frontend** | `http://localhost:5173` | Interface principal da aplicação. |
| **Backend API** | `http://localhost:8080` | Base da API REST. |
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` | Documentação interativa da API. |
| **MySQL** | `localhost:3300` | Porta do banco de dados exposta no host. |
| **Redis** | `localhost:6379` | Porta do Redis exposta no host. |

## 📁 Estrutura do Projeto

```
.
├── backend/                # Código-fonte do Backend (Spring Boot)
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/               # Código-fonte do Frontend (Vue.js)
│   ├── src/
│   │   ├── components/     # Componentes reutilizáveis
│   │   ├── router/         # Configuração de rotas
│   │   ├── services/       # Serviços de API (Axios)
│   │   ├── stores/         # Gerenciamento de estado (Pinia)
│   │   └── views/          # Páginas da aplicação
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yaml     # Orquestração dos contêineres
├── start.sh                # Script de inicialização para Linux/macOS
├── start.bat               # Script de inicialização para Windows
└── README.md               # Este arquivo
```
Para uma documentação mais detalhada sobre a estrutura e decisões de arquitetura, consulte os arquivos `ARQUITETURA.md` e `DESENVOLVEDOR.md` dentro da pasta `frontend/`.

## 🚢 Deploy

O projeto está pronto para deploy em diversas plataformas.

### Frontend
O comando `npm run build` na pasta `frontend/` gera uma versão estática otimizada na pasta `dist/`.

-   **Vercel/Netlify**: Faça o deploy do repositório Git e configure o comando de build para `npm run build` e o diretório de publicação para `frontend/dist`.
-   **Docker**: A imagem Docker do frontend serve os arquivos estáticos com Nginx.
    ```bash
    # Construir a imagem
    docker build -t diarias-ja-frontend ./frontend

    # Executar o contêiner
    docker run -p 80:80 diarias-ja-frontend
    ```

### Backend
A imagem Docker do backend pode ser construída e executada em qualquer provedor de nuvem que suporte contêineres.

```bash
# Construir a imagem
docker build -t diarias-ja-backend ./backend

# Executar o contêiner (lembre-se de passar as variáveis de ambiente do banco de dados)
docker run -p 8080:8080 \
  -e DB_HOST=... \
  -e DB_USER=... \
  -e DB_PASSWORD=... \
  diarias-ja-backend
```

## 🤝 Contribuindo

Contribuições são o que tornam a comunidade de código aberto um lugar incrível para aprender, inspirar e criar. Qualquer contribuição que você fizer será **muito apreciada**.

1.  Faça um Fork do projeto
2.  Crie sua Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Faça o Commit de suas mudanças (`git commit -m 'feat: Add some AmazingFeature'`)
4.  Faça o Push para a Branch (`git push origin feature/AmazingFeature`)
5.  Abra um Pull Request

## 📄 Licença

Distribuído sob a licença MIT.

---
