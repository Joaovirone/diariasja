# 🎯 Guia Completo de Uso - Diárias JÁ

## 📋 Visão Geral

Este documento descreve como usar a aplicação Diárias JÁ após a implementação do frontend em Vue.js.

## 🏗️ Arquitetura da Solução

```
┌─────────────────────┐
│  Frontend (Vue.js)  │ ← localhost:5173
├─────────────────────┤
│  HTTP/REST          │
├─────────────────────┤
│  Backend (Spring)   │ ← localhost:8080
├─────────────────────┤
│  MySQL + Redis      │
└─────────────────────┘
```

## 🚀 Iniciando a Aplicação

### Opção 1: Docker Compose (Recomendado)

```bash
# Windows
start.bat

# Linux/Mac
chmod +x start.sh
./start.sh
```

### Opção 2: Manualmente

#### Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

#### Frontend

```bash
cd frontend
npm install
npm run dev
```

## 🔐 Login e Cadastro

### Primeiro Acesso

1. Acesse `http://localhost:5173`
2. Clique em "Cadastre-se aqui"
3. Preencha os dados:
   - Nome completo
   - E-mail
   - Data de nascimento
   - Tipo: "Contratante" ou "Profissional"
   - Senha (mínimo 8 caracteres)

### Login

1. Use o e-mail e senha cadastrados
2. Será redirecionado para o Dashboard

## 📊 Fluxo de Uso por Tipo de Usuário

### 👔 Contratante

1. **Dashboard**: Visualiza resumo das diárias e estatísticas
2. **Solicitar Diária**: 
   - Seleciona a categoria do serviço
   - Preenche data, hora, local e valor
   - Escreve descrição do serviço
3. **Minhas Diárias**: 
   - Acompanha solicitações
   - Visualiza quem aceitou
   - Avalia o profissional após conclusão

### 👨‍💼 Profissional

1. **Dashboard**: Visualiza diárias disponíveis
2. **Minhas Diárias**: 
   - Lista de diárias que aceitou
   - Acompanhamento de cada uma
3. **Aceitar Diária**: 
   - Busca por diárias disponíveis
   - Aceita uma oportunidade
4. **Concluir e Ser Avaliado**: 
   - Após conclusão, recebe avaliação

## 📱 Funcionalidades Principais

### Dashboard
- Visualização rápida de estatísticas
- Resumo das últimas diárias
- Links rápidos para ações principais

### Gerenciamento de Diárias
- ✅ Criar nova solicitação
- ✅ Listar minhas diárias
- ✅ Filtrar por status (Ativa, Aceita, Concluída, Cancelada)
- ✅ Aceitar uma diária (profissional)
- ✅ Cancelar uma diária (contratante)
- ✅ Avaliar serviço (contratante)
- ✅ Visualizar detalhes

### Categorias de Serviço
- ✅ Listar categorias
- ✅ Criar nova categoria (admin)
- ✅ Editar categoria (admin)
- ✅ Deletar categoria (admin)

### Perfil do Usuário
- ✅ Visualizar dados pessoais
- ✅ Atualizar informações
- ✅ Ver avaliações e reputação
- ✅ Visualizar histórico de diárias

## 🌐 Endpoints da API

### Autenticação
```
POST /api/auth/login
```

### Usuários
```
POST /api/usuarios (criar)
GET /api/usuarios/perfil (meu perfil)
PUT /api/usuarios/perfil (atualizar)
GET /api/usuarios/{id}
GET /api/usuarios (listar com paginação)
```

### Diárias
```
POST /api/diarias/solicitar (criar)
GET /api/diarias (listar)
GET /api/diarias/minhas (minhas diárias)
GET /api/diarias/{id} (detalhes)
PATCH /api/diarias/{id}/aceitar (aceitar)
PATCH /api/diarias/{id}/avaliar (avaliar)
PATCH /api/diarias/{id}/cancelar (cancelar)
```

### Categorias
```
POST /api/categorias (criar)
GET /api/categorias (listar)
GET /api/categorias/{id} (detalhes)
PUT /api/categorias/{id} (atualizar)
DELETE /api/categorias/{id} (deletar)
```

## 🎨 Personalização

### Cores Principais

Edite `src/assets/styles.css`:

```css
:root {
  --primary: #667eea;
  --primary-dark: #764ba2;
  --secondary: #f093fb;
  --success: #48bb78;
  --danger: #f56565;
  --warning: #ed8936;
}
```

### Adicionar Nova Página

1. Crie um arquivo em `src/views/NovaPage.vue`
2. Adicione a rota em `src/router/index.js`
3. Adicione link na navegação em `src/App.vue`

### Adicionar Novo Componente

1. Crie em `src/components/NovoComponente.vue`
2. Importe e use em qualquer view

## 🔧 Variáveis de Ambiente

### Frontend (.env.local)

```env
VITE_API_URL=http://localhost:8080
VITE_APP_NAME=Diárias JÁ
```

### Backend (application.yaml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydatabase
    username: myuser
    password: secret
  jpa:
    hibernate:
      ddl-auto: update
  data:
    redis:
      host: localhost
      port: 6379
```

## 📦 Deployment

### Frontend

#### Vercel
```bash
npm run build
vercel deploy
```

#### Netlify
```bash
npm run build
netlify deploy --prod --dir=dist
```

#### Docker
```bash
docker build -t diarias-ja-frontend .
docker run -p 5173:5173 diarias-ja-frontend
```

### Backend

#### Docker
```bash
cd backend
docker build -t diarias-ja-backend .
docker run -p 8080:8080 diarias-ja-backend
```

## 🧪 Testes

### Frontend
```bash
npm run test
```

### Backend
```bash
mvn test
```

## 📊 Monitora

### Logs Docker

```bash
# Frontend
docker logs -f diarias-ja-frontend

# Backend
docker logs -f diarias-ja-api

# Todos
docker-compose logs -f
```

### Swagger UI (Backend)

Acesse: `http://localhost:8080/swagger-ui.html`

## 🚨 Troubleshooting

### Erro de Conexão com Backend

**Problema**: Frontend não consegue se conectar ao backend
**Solução**: Verifique se o backend está rodando na porta 8080 e se CORS está habilitado

### Erro ao fazer Login

**Problema**: E-mail ou senha incorretos
**Solução**: Verifique se o usuário foi criado corretamente e se a senha está correta

### Diárias não aparecem

**Problema**: Lista de diárias vazia
**Solução**: Crie uma nova diária ou verifique se existem categorias cadastradas

### Erro 401 Unauthorized

**Problema**: Token expirado ou inválido
**Solução**: Faça logout e login novamente

## 📚 Documentação Técnica

- [Vue.js 3](https://vuejs.org/)
- [Vite](https://vitejs.dev/)
- [Pinia](https://pinia.vuejs.org/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MySQL](https://www.mysql.com/)
- [Redis](https://redis.io/)

## 🤝 Suporte

Para dúvidas ou problemas:

1. Verifique a documentação do projeto
2. Consulte os logs da aplicação
3. Entre em contato com o time de desenvolvimento

## 📝 Checklist para Novo Desenvolvedor

- [ ] Node.js 18+ instalado
- [ ] Docker instalado
- [ ] Repositório clonado
- [ ] Dependências instaladas (`npm install`)
- [ ] Backend rodando (`mvn spring-boot:run`)
- [ ] Frontend rodando (`npm run dev`)
- [ ] Banco de dados criado e migrações executadas
- [ ] Redis rodando
- [ ] Teste de login realizado com sucesso
- [ ] Crie uma diária de teste
- [ ] Teste a aceitação da diária

## ✨ Desenvolvido com ❤️

Frontend desenvolvido em Vue.js 3 seguindo as melhores práticas de desenvolvimento moderno.
