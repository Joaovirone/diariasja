# Frontend - Diárias JÁ

Frontend moderno desenvolvido em Vue 3 com Vite para o sistema de gerenciamento de diárias.

## 🚀 Tecnologias

- **Vue 3** - Framework progressivo
- **Vite** - Build tool rápido
- **Pinia** - State management
- **Vue Router** - Roteamento
- **Axios** - HTTP Client
- **date-fns** - Manipulação de datas

## 📦 Instalação

### Pré-requisitos

- Node.js 18+
- npm ou yarn

### Passos

1. Instale as dependências:

```bash
npm install
```

2. Configure as variáveis de ambiente:

```bash
cp .env.example .env.local
```

3. Edite `.env.local` com a URL da sua API:

```env
VITE_API_URL=http://localhost:8080
VITE_APP_NAME=Diárias JÁ
```

## 🎯 Scripts

### Desenvolvimento

```bash
npm run dev
```

Inicia o servidor de desenvolvimento em `http://localhost:5173`

### Build para Produção

```bash
npm run build
```

### Preview da Build

```bash
npm run preview
```

## 📁 Estrutura do Projeto

```
frontend/
├── public/              # Arquivos estáticos
├── src/
│   ├── assets/         # Estilos e imagens
│   ├── components/     # Componentes reutilizáveis
│   ├── router/         # Configuração de rotas
│   ├── services/       # Serviços de API
│   ├── stores/         # Estado global (Pinia)
│   ├── views/          # Páginas/Views
│   ├── App.vue         # Componente raiz
│   └── main.js         # Entrada da aplicação
├── index.html
├── vite.config.js
├── package.json
└── Dockerfile
```

## 🔐 Autenticação

A aplicação usa autenticação JWT. O token é armazenado no localStorage e enviado automaticamente nas requisições via header `Authorization: Bearer {token}`.

### Fluxo de Autenticação

1. Usuário faz login em `/login`
2. Backend retorna o token JWT
3. Token é armazenado no localStorage
4. Requisições subsequentes incluem o token automaticamente

## 🛣️ Rotas

### Públicas

- `/login` - Página de login
- `/register` - Cadastro de novo usuário

### Protegidas

- `/dashboard` - Dashboard principal
- `/diarias` - Listagem de minhas diárias
- `/diarias/criar` - Criar nova diária
- `/categorias` - Gerenciar categorias
- `/perfil` - Meu perfil
- `/meus-dados` - Meus dados e estatísticas

## 🏗️ Componentes

### Views

- **Login.vue** - Autenticação
- **Register.vue** - Registro de usuário
- **Dashboard.vue** - Página inicial
- **Diarias.vue** - Listagem de diárias
- **CriarDiaria.vue** - Formulário de nova diária
- **Categorias.vue** - Gerenciamento de categorias
- **Perfil.vue** - Edição de perfil
- **MeusDados.vue** - Visualização de dados

### Componentes Reutilizáveis

- **ModalCategoria.vue** - Modal para criar/editar categorias
- **ModalAvaliacao.vue** - Modal para avaliar diárias

## 📡 Serviços de API

### authService

```javascript
- login(email, senha)
- logout()
- getToken()
- setToken(token)
- isLoggedIn()
```

### diariaService

```javascript
- solicitar(dados)
- listar(page, size)
- obterPorId(id)
- aceitar(id, idProfissional)
- avaliar(id, nota)
- cancelar(id)
- listarMinhas(page, size)
```

### categoriaService

```javascript
- listar(page, size)
- obterPorId(id)
- criar(dados)
- atualizar(id, dados)
- deletar(id)
```

### usuarioService

```javascript
- obterPerfil()
- atualizar(dados)
- criarConta(dados)
- obterPorId(id)
- listar(page, size)
```

## 🎨 Temas e Estilos

A aplicação usa um sistema de cores CSS customizável:

```css
--primary: #667eea
--primary-dark: #764ba2
--secondary: #f093fb
--success: #48bb78
--danger: #f56565
--warning: #ed8936
--info: #4299e1
```

## 🐳 Docker

### Build da Imagem

```bash
docker build -t diarias-ja-frontend .
```

### Executar Container

```bash
docker run -p 5173:5173 diarias-ja-frontend
```

## 🔗 Integração com Backend

A aplicação espera que o backend esteja rodando em `http://localhost:8080` por padrão.

Endpoints esperados:

- `POST /api/auth/login`
- `POST /api/usuarios`
- `GET /api/usuarios/perfil`
- `PUT /api/usuarios/perfil`
- `GET /api/diarias`
- `POST /api/diarias/solicitar`
- `PATCH /api/diarias/{id}/aceitar`
- `PATCH /api/diarias/{id}/avaliar`
- `PATCH /api/diarias/{id}/cancelar`
- `GET /api/categorias`
- `POST /api/categorias`
- `PUT /api/categorias/{id}`
- `DELETE /api/categorias/{id}`

## 📝 Notas Importantes

1. O frontend espera que o backend tenha CORS habilitado
2. A autenticação funciona com JWT (JWT Bearer Token)
3. Os tokens têm expiração configurada no backend
4. Ao fazer logout, o token é removido do localStorage

## 🤝 Contribuindo

1. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
2. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
3. Push para a branch (`git push origin feature/AmazingFeature`)
4. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT.

## 👨‍💻 Desenvolvedor

Frontend desenvolvido por um dev senior em Vue.js seguindo as melhores práticas modernas.
