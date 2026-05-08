# 🏛️ Arquitetura e Decisões de Design

## Visão Geral da Arquitetura

O projeto Diárias JÁ segue uma arquitetura moderna de três camadas:

```
Frontend (Vue.js) <-- HTTP/REST --> Backend (Spring Boot) <-- Database (MySQL)
                                                    ↓
                                            Cache (Redis)
```

## 📐 Frontend

### Stack Tecnológico

- **Vue.js 3**: Framework progressivo com composition API
- **Vite**: Build tool rápido e moderno
- **Pinia**: State management elegante e type-safe
- **Vue Router**: Roteamento declarativo
- **Axios**: HTTP client configurável
- **date-fns**: Manipulação de datas (i18n ready)

### Arquitetura de Pastas

```
src/
├── assets/          # Estilos globais e imagens
├── components/      # Componentes reutilizáveis (modals, cards, etc)
├── router/          # Configuração de rotas e guards
├── services/        # Serviços de API (camada de integração)
├── stores/          # Pinia stores (estado global)
├── views/           # Páginas/views da aplicação
├── App.vue          # Componente raiz
└── main.js          # Entrada da aplicação
```

### Padrões de Design

#### 1. Composition API

Todas as views e componentes usam a Composition API do Vue 3:

```javascript
import { ref, computed, onMounted } from 'vue'

const count = ref(0)
const doubled = computed(() => count.value * 2)

onMounted(() => {
  // Lógica de inicialização
})
```

**Vantagens:**
- Melhor reutilização de lógica
- Melhor tree-shaking
- Melhor suporte a TypeScript

#### 2. Stores com Pinia

Gerenciamento centralizado de estado usando Pinia:

```javascript
export const useAuthStore = defineStore('auth', () => {
  const token = ref(null)
  const isLoggedIn = computed(() => !!token.value)
  
  const login = async (email, senha) => {
    // ...
  }
  
  return { token, isLoggedIn, login }
})
```

**Decisão**: Pinia em vez de Vuex pela:
- API mais simples e intuitiva
- Melhor suporte a TypeScript
- Sem boilerplate excessivo

#### 3. Serviços de API

Camada de integração com o backend usando Axios:

```javascript
// src/services/diariaService.js
export const diariaService = {
  solicitar(dados) { return api.post('/diarias/solicitar', dados) },
  listar(page, size) { return api.get('/diarias', { params: { page, size } }) }
}
```

**Vantagens:**
- Lógica de API centralizada
- Fácil de mockar em testes
- Reutilizável entre componentes

#### 4. Autenticação JWT

Token armazenado no localStorage e enviado automaticamente:

```javascript
// src/services/api.js
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})
```

**Fluxo:**
1. Login → Backend retorna token
2. Token armazenado no localStorage
3. Interceptor adiciona token em todas as requisições
4. Se resposta 401 → logout automático

#### 5. Roteamento com Guards

Proteção de rotas com meta dados:

```javascript
// src/router/index.js
const routes = [
  {
    path: '/dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  }
]

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})
```

**Benefícios:**
- Proteção automática de rotas
- Redirecionamento para login se não autenticado
- Pode ser estendido para verificar roles/permissões

### Convenções de Código

1. **Nomeação de Componentes**: PascalCase (LoginForm.vue, UserCard.vue)
2. **Nomeação de Views**: PascalCase (Dashboard.vue, Profile.vue)
3. **Variáveis Reativas**: camelCase
4. **Constantes**: UPPER_SNAKE_CASE
5. **Props**: camelCase com type validation
6. **Emits**: kebab-case (ex: @fechar)

### Performance

1. **Code Splitting**: Lazy loading de views com `() => import(...)`
2. **Tree Shaking**: Imports nomeados em vez de default exports
3. **CSS Scoped**: Cada componente tem estilos scoped
4. **Computed Properties**: Reatividade eficiente

## 🔐 Segurança Frontend

1. **JWT Bearer Token**: Autenticação stateless
2. **HTTPS em Produção**: Redirecionamento automático
3. **CSP Headers**: Content Security Policy configurado
4. **XSS Prevention**: Vue sanitiza templates automaticamente
5. **CSRF Protection**: Integrado com backend

## 🎯 Funcionalidades Implementadas

### Autenticação
- ✅ Login com JWT
- ✅ Registro de novos usuários
- ✅ Logout com limpeza de token
- ✅ Guard de rotas
- ✅ Auto-logout em 401

### Gestão de Diárias
- ✅ Criar solicitude de diária
- ✅ Listar diárias com paginação
- ✅ Filtrar por status
- ✅ Aceitar diária (profissional)
- ✅ Avaliar diária (contratante)
- ✅ Cancelar diária
- ✅ Histórico de diárias

### Perfil e Dados
- ✅ Visualizar perfil
- ✅ Atualizar dados pessoais
- ✅ Ver reputação e avaliações
- ✅ Histórico de serviços

### Categorias
- ✅ Listar categorias
- ✅ Criar/Editar/Deletar (admin)
- ✅ Modal para gerenciamento

## 📊 Fluxo de Dados

```
View Component
    ↓
    ├─→ User Action (click, submit)
    ↓
Store (Pinia)
    ↓
    ├─→ Call Service
    ↓
Service (API)
    ↓
    ├─→ HTTP Request (Axios)
    ↓
Backend API
    ↓
    ├─→ Database/Cache
    ↓
Response JSON
    ↓
Service (Parse)
    ↓
Store (Update State)
    ↓
View Component (Reactive Update)
```

## 🔄 Ciclo de Vida de um Feature

### 1. Criar a Store (se necessário)
```javascript
// src/stores/featureStore.js
export const useFeatureStore = defineStore('feature', () => {
  const items = ref([])
  const listar = async () => { /* ... */ }
  return { items, listar }
})
```

### 2. Criar o Serviço
```javascript
// src/services/featureService.js
export const featureService = {
  listar() { return api.get('/features') }
}
```

### 3. Criar a View
```vue
<!-- src/views/Feature.vue -->
<template>...</template>
<script setup>
import { useFeatureStore } from '../stores/featureStore'
// ...
</script>
<style scoped>...</style>
```

### 4. Adicionar Rota
```javascript
// src/router/index.js
{
  path: '/features',
  component: () => import('../views/Feature.vue'),
  meta: { requiresAuth: true }
}
```

### 5. Adicionar Link de Navegação
```vue
<!-- src/App.vue -->
<router-link to="/features">Features</router-link>
```

## 🧪 Testing Strategy

### Unit Tests
- Services
- Stores
- Utilities

### Integration Tests
- Fluxo completo (UI → Store → API)
- Autenticação

### E2E Tests
- Cenários críticos
- User journeys

## 📱 Responsividade

- **Mobile First**: Começamos com mobile
- **Breakpoints**:
  - 480px: Mobile
  - 768px: Tablet
  - 1024px: Desktop

## 🌍 Internacionalização (i18n)

Pronto para i18n com:
- `date-fns/locale` para datas
- Strings em constantes
- Fácil migração para i18n library

## 🚀 Deployment

### Build Optimization
- Minificação automática
- Tree shaking
- Code splitting

### Environments
- Development: `http://localhost:5173`
- Production: Build estático servido via CDN

## 📚 Dependências Principais

| Dependência | Versão | Propósito |
|---|---|---|
| vue | ^3.5.13 | Framework |
| vue-router | ^4.4.5 | Roteamento |
| pinia | ^2.2.2 | State management |
| axios | ^1.7.7 | HTTP client |
| date-fns | ^3.6.0 | Manipulação de datas |
| vite | ^5.4.2 | Build tool |

## 🔮 Melhorias Futuras

1. **TypeScript**: Migrar para TypeScript full
2. **Testes**: Adicionar suite de testes com Vitest
3. **i18n**: Implementar i18n com vue-i18n
4. **Dark Mode**: Tema escuro
5. **PWA**: Service workers e offline support
6. **Storybook**: Documentação de componentes
7. **Analytics**: Integração com Google Analytics
8. **Notifications**: Sistema de notificações real-time com WebSocket

## 📖 Referências

- [Vue.js 3 Docs](https://vuejs.org/)
- [Pinia Docs](https://pinia.vuejs.org/)
- [Vite Docs](https://vitejs.dev/)
- [Axios Docs](https://axios-http.com/)
