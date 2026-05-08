# 📚 Índice de Documentação - Diárias JÁ

## 📖 Documentação Geral

### Para Começar
1. **[README.md](README.md)** - Visão geral do projeto
2. **[GUIA-DE-USO.md](GUIA-DE-USO.md)** - Como usar a aplicação
3. **[RESUMO-IMPLEMENTACAO.md](RESUMO-IMPLEMENTACAO.md)** - O que foi implementado
4. **[CHECKLIST-VALIDACAO.md](CHECKLIST-VALIDACAO.md)** - Validação e testes

### Desenvolvimento
1. **[frontend/ARQUITETURA.md](frontend/ARQUITETURA.md)** - Decisões de design
2. **[frontend/DESENVOLVEDOR.md](frontend/DESENVOLVEDOR.md)** - Guia para devs
3. **[frontend/README.md](frontend/README.md)** - Frontend específico

### Backend (Existente)
1. **[IMPLEMENTACAO-PASSO-A-PASSO.md](IMPLEMENTACAO-PASSO-A-PASSO.md)** - Estrutura backend
2. **[backend/README.md](backend/README.md)** - Backend específico

## 🗺️ Estrutura do Projeto

```
diariasja/
├── frontend/                    # Vue.js 3 (NOVO!)
│   ├── src/
│   │   ├── components/         # Componentes reutilizáveis
│   │   ├── router/             # Roteamento
│   │   ├── services/           # Integração com API
│   │   ├── stores/             # Pinia stores
│   │   ├── views/              # Páginas da aplicação
│   │   ├── assets/             # Estilos e imagens
│   │   ├── App.vue             # Componente raiz
│   │   └── main.js             # Entrada
│   ├── Dockerfile              # Container Docker
│   ├── package.json            # Dependências npm
│   ├── vite.config.js          # Configuração Vite
│   ├── README.md               # Instruções
│   ├── ARQUITETURA.md          # Design decisions
│   └── DESENVOLVEDOR.md        # Dev guide
│
├── backend/                     # Spring Boot
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── docker-compose.yaml          # Orquestração (ATUALIZADO!)
├── start.sh                     # Script iniciar Linux/Mac
├── start.bat                    # Script iniciar Windows
├── GUIA-DE-USO.md              # Manual do usuário
├── RESUMO-IMPLEMENTACAO.md     # O que foi feito
└── CHECKLIST-VALIDACAO.md      # Validação

```

## 🚀 Quick Start

### Mais Rápido Possível
```bash
# Windows
start.bat

# Linux/Mac  
./start.sh
```

### Mais Controle
```bash
# Backend
cd backend && mvn spring-boot:run

# Frontend (outro terminal)
cd frontend && npm install && npm run dev
```

### Desenvolvimento Local
```bash
cd frontend
npm install
npm run dev
# Abre http://localhost:5173
```

## 🎯 Navegação por Caso de Uso

### "Quero começar a desenvolver"
1. Leia [frontend/DESENVOLVEDOR.md](frontend/DESENVOLVEDOR.md)
2. Setup: `npm install` na pasta frontend
3. Execute: `npm run dev`
4. Comece em [frontend/src](frontend/src)

### "Preciso entender a arquitetura"
1. Leia [frontend/ARQUITETURA.md](frontend/ARQUITETURA.md)
2. Veja [frontend/README.md](frontend/README.md) - Sessão "Estrutura"
3. Estude a pasta [frontend/src](frontend/src)

### "Quero fazer deploy"
1. Consulte [GUIA-DE-USO.md](GUIA-DE-USO.md) - Seção Deployment
2. Build: `npm run build`
3. Docker: `docker build -t app .`

### "Encontrei um bug"
1. Consulte [CHECKLIST-VALIDACAO.md](CHECKLIST-VALIDACAO.md)
2. Verifique [frontend/DESENVOLVEDOR.md](frontend/DESENVOLVEDOR.md) - Debugging
3. Use Vue DevTools para inspecionar

### "Preciso adicionar uma feature"
1. Leia [frontend/DESENVOLVEDOR.md](frontend/DESENVOLVEDOR.md) - Feature Creation
2. Siga o padrão: Service → Store → View → Route
3. Valide com [CHECKLIST-VALIDACAO.md](CHECKLIST-VALIDACAO.md)

### "Quero integrar com API externa"
1. Criar novo arquivo em [frontend/src/services](frontend/src/services)
2. Usar padrão dos existentes como exemplo
3. Adicionar store em [frontend/src/stores](frontend/src/stores)
4. Criar view em [frontend/src/views](frontend/src/views)

## 📚 Documentação por Tópico

### Autenticação
- Implementação: [frontend/src/stores/authStore.js](frontend/src/stores/authStore.js)
- Guia: [frontend/DESENVOLVEDOR.md](frontend/DESENVOLVEDOR.md) - Debugging
- Fluxo: [frontend/ARQUITETURA.md](frontend/ARQUITETURA.md) - JWT Bearer Token

### Roteamento
- Configuração: [frontend/src/router/index.js](frontend/src/router/index.js)
- Guards: [frontend/src/router/index.js](frontend/src/router/index.js)
- Explicação: [frontend/ARQUITETURA.md](frontend/ARQUITETURA.md) - Roteamento com Guards

### Estado (Stores)
- authStore: [frontend/src/stores/authStore.js](frontend/src/stores/authStore.js)
- diariaStore: [frontend/src/stores/diariaStore.js](frontend/src/stores/diariaStore.js)
- categoriaStore: [frontend/src/stores/categoriaStore.js](frontend/src/stores/categoriaStore.js)
- usuarioStore: [frontend/src/stores/usuarioStore.js](frontend/src/stores/usuarioStore.js)
- Guia: [frontend/DESENVOLVEDOR.md](frontend/DESENVOLVEDOR.md) - Criar Store Pinia

### API Integration
- Axios config: [frontend/src/services/api.js](frontend/src/services/api.js)
- authService: [frontend/src/services/authService.js](frontend/src/services/authService.js)
- diariaService: [frontend/src/services/diariaService.js](frontend/src/services/diariaService.js)
- categoriaService: [frontend/src/services/categoriaService.js](frontend/src/services/categoriaService.js)
- usuarioService: [frontend/src/services/usuarioService.js](frontend/src/services/usuarioService.js)

### UI Components
- Login: [frontend/src/views/Login.vue](frontend/src/views/Login.vue)
- Register: [frontend/src/views/Register.vue](frontend/src/views/Register.vue)
- Dashboard: [frontend/src/views/Dashboard.vue](frontend/src/views/Dashboard.vue)
- Diarias: [frontend/src/views/Diarias.vue](frontend/src/views/Diarias.vue)
- ModalCategoria: [frontend/src/components/ModalCategoria.vue](frontend/src/components/ModalCategoria.vue)
- ModalAvaliacao: [frontend/src/components/ModalAvaliacao.vue](frontend/src/components/ModalAvaliacao.vue)

### Estilos
- Global CSS: [frontend/src/assets/styles.css](frontend/src/assets/styles.css)
- Cores e variáveis: [frontend/src/assets/styles.css](frontend/src/assets/styles.css)
- Responsividade: [frontend/src/assets/styles.css](frontend/src/assets/styles.css)

## 🔧 Configurações

### Environment Variables
```bash
# Development
VITE_API_URL=http://localhost:8080
VITE_APP_NAME=Diárias JÁ

# Production
VITE_API_URL=https://api.prod.com
VITE_APP_NAME=Diárias JÁ
```

### Docker
- Build Frontend: `docker build -t diarias-ja-frontend ./frontend`
- Run: `docker run -p 5173:5173 diarias-ja-frontend`
- Compose: `docker-compose up`

### Package.json Scripts
```bash
npm run dev       # Development
npm run build     # Production build
npm run preview   # Preview build
npm run lint      # ESLint check
```

## 🎯 Endpoints Backend Consumidos

### Auth
```
POST /api/auth/login
```

### Usuários
```
POST /api/usuarios
GET /api/usuarios/perfil
PUT /api/usuarios/perfil
GET /api/usuarios/{id}
GET /api/usuarios
```

### Diárias
```
POST /api/diarias/solicitar
GET /api/diarias
GET /api/diarias/minhas
GET /api/diarias/{id}
PATCH /api/diarias/{id}/aceitar
PATCH /api/diarias/{id}/avaliar
PATCH /api/diarias/{id}/cancelar
```

### Categorias
```
POST /api/categorias
GET /api/categorias
GET /api/categorias/{id}
PUT /api/categorias/{id}
DELETE /api/categorias/{id}
```

## 📊 Stack Tecnológico

### Frontend
- Vue.js 3.5.13
- Vite 5.4.2
- Pinia 2.2.2
- Vue Router 4.4.5
- Axios 1.7.7
- date-fns 3.6.0
- ESLint 9.11.1

### Backend
- Spring Boot 3.5.14
- MySQL 8.0
- Redis (Alpine)
- Java 17+

## 🤝 Contribuindo

1. Branch: `git checkout -b feature/xyz`
2. Commit: `git commit -m "feat: descrição"`
3. Push: `git push origin feature/xyz`
4. PR: Abra um Pull Request

## 📞 Suporte

### Documentação
- Frontend: [frontend/README.md](frontend/README.md)
- Arquitetura: [frontend/ARQUITETURA.md](frontend/ARQUITETURA.md)
- Dev: [frontend/DESENVOLVEDOR.md](frontend/DESENVOLVEDOR.md)
- Uso: [GUIA-DE-USO.md](GUIA-DE-USO.md)

### Troubleshooting
- Veja [CHECKLIST-VALIDACAO.md](CHECKLIST-VALIDACAO.md) - Problemas Comuns
- Consulte [frontend/DESENVOLVEDOR.md](frontend/DESENVOLVEDOR.md) - Debugging

### Referências
- [Vue.js 3 Docs](https://vuejs.org/)
- [Pinia Docs](https://pinia.vuejs.org/)
- [Vite Docs](https://vitejs.dev/)
- [Spring Boot Docs](https://spring.io/)

## ✨ Checklist de Validação

Antes de fazer deploy, verifique:
- [ ] Frontend compila sem erros
- [ ] Backend roda sem erros
- [ ] Login funciona
- [ ] Página inicial carrega
- [ ] Todas as rotas acessíveis
- [ ] Responsividade OK
- [ ] Sem console errors
- [ ] Testes passando (se houver)

## 📋 Últimas Atualizações

**Data**: 8 de Maio de 2026
**Versão**: 1.0.0
**Status**: ✅ Production Ready

### Changelog
- ✅ Frontend Vue.js 3 implementado
- ✅ Todas as funcionalidades implementadas
- ✅ Documentação completa
- ✅ Docker configurado
- ✅ Pronto para deploy

---

**Desenvolvido com ❤️ por Dev Frontend Senior**  
Vue.js 3 | Vite | Pinia | Modern Frontend
