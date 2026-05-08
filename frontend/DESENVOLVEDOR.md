# 👨‍💻 Guia para Desenvolvedores

## Setup do Ambiente

### Pré-requisitos

- Node.js 18+ 
- npm ou yarn
- Git
- Docker (opcional, para rodar localmente)
- Visual Studio Code (recomendado)

### Instalação Inicial

1. **Clone o repositório**
```bash
git clone <repo-url>
cd diariasja
```

2. **Instale as dependências do frontend**
```bash
cd frontend
npm install
```

3. **Configure variáveis de ambiente**
```bash
cp .env.example .env.local
```

4. **Inicie o servidor de desenvolvimento**
```bash
npm run dev
```

A aplicação estará em `http://localhost:5173`

## Estrutura de Desenvolvimento

### Arquivo & Folder Structure

```
frontend/
├── src/
│   ├── assets/
│   │   └── styles.css          # Estilos globais
│   ├── components/             # Componentes reutilizáveis
│   │   ├── ModalCategoria.vue
│   │   └── ModalAvaliacao.vue
│   ├── router/
│   │   └── index.js            # Definição de rotas
│   ├── services/               # Integração com API
│   │   ├── api.js
│   │   ├── authService.js
│   │   ├── diariaService.js
│   │   ├── categoriaService.js
│   │   └── usuarioService.js
│   ├── stores/                 # Pinia stores (estado)
│   │   ├── authStore.js
│   │   ├── diariaStore.js
│   │   ├── categoriaStore.js
│   │   └── usuarioStore.js
│   ├── views/                  # Páginas da aplicação
│   │   ├── Login.vue
│   │   ├── Register.vue
│   │   ├── Dashboard.vue
│   │   ├── Diarias.vue
│   │   ├── CriarDiaria.vue
│   │   ├── Categorias.vue
│   │   ├── Perfil.vue
│   │   └── MeusDados.vue
│   ├── App.vue                 # Componente raiz
│   └── main.js                 # Entrada da aplicação
├── public/                      # Arquivos estáticos
├── index.html
├── vite.config.js
├── package.json
├── .eslintrc.cjs
└── README.md
```

## Desenvolvimento de Features

### 1. Criar um Novo Serviço de API

**Arquivo**: `src/services/novoService.js`

```javascript
import api from './api'

export const novoService = {
  listar(page = 0, size = 10) {
    return api.get('/novo', { params: { page, size } })
  },

  obterPorId(id) {
    return api.get(`/novo/${id}`)
  },

  criar(dados) {
    return api.post('/novo', dados)
  },

  atualizar(id, dados) {
    return api.put(`/novo/${id}`, dados)
  },

  deletar(id) {
    return api.delete(`/novo/${id}`)
  }
}
```

### 2. Criar uma Store Pinia

**Arquivo**: `src/stores/novoStore.js`

```javascript
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { novoService } from '../services/novoService'

export const useNovoStore = defineStore('novo', () => {
  const items = ref([])
  const isLoading = ref(false)
  const error = ref(null)

  const listar = async () => {
    isLoading.value = true
    error.value = null
    try {
      const response = await novoService.listar()
      items.value = response.data
      return items.value
    } catch (err) {
      error.value = err.response?.data || 'Erro ao listar'
    } finally {
      isLoading.value = false
    }
  }

  return {
    items,
    isLoading,
    error,
    listar
  }
})
```

### 3. Criar uma View

**Arquivo**: `src/views/Novo.vue`

```vue
<template>
  <div class="container">
    <h1>Novo</h1>
    
    <div v-if="isLoading" class="loading">
      <div class="spinner"></div>
    </div>

    <div v-else>
      <div v-if="error" class="alert alert-error">{{ error }}</div>

      <div class="grid">
        <div v-for="item in items" :key="item.id" class="card">
          <!-- Renderizar item -->
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useNovoStore } from '../stores/novoStore'

const store = useNovoStore()

const items = computed(() => store.items)
const isLoading = computed(() => store.isLoading)
const error = computed(() => store.error)

onMounted(() => {
  store.listar()
})
</script>

<style scoped>
/* Estilos específicos */
</style>
```

### 4. Adicionar Rota

**Arquivo**: `src/router/index.js`

```javascript
const Novo = () => import('../views/Novo.vue')

const routes = [
  // ... outras rotas
  {
    path: '/novo',
    name: 'Novo',
    component: Novo,
    meta: { requiresAuth: true }
  }
]
```

### 5. Adicionar Link na Navegação

**Arquivo**: `src/App.vue`

```vue
<template>
  <nav class="navbar" v-if="isLoggedIn">
    <!-- ... -->
    <li><router-link to="/novo">Novo</router-link></li>
    <!-- ... -->
  </nav>
</template>
```

## Padrões de Código

### Composition API

```javascript
import { ref, computed, onMounted } from 'vue'

// Reatividade
const count = ref(0)
const doubled = computed(() => count.value * 2)

// Lifecycle
onMounted(() => {
  console.log('Componente montado')
})
```

### Props e Emits

```vue
<script setup>
const props = defineProps({
  titulo: String,
  ativo: Boolean
})

const emit = defineEmits(['atualizar', 'fechar'])

const handleClick = () => {
  emit('atualizar', newValue)
}
</script>
```

### Condicional e Loops

```vue
<template>
  <!-- Condicional -->
  <div v-if="isLoading">Carregando...</div>
  <div v-else-if="error">Erro: {{ error }}</div>
  <div v-else>Sucesso</div>

  <!-- Loop -->
  <div v-for="item in items" :key="item.id">
    {{ item.name }}
  </div>
</template>
```

## Commits e Git Flow

### Convenção de Commits

```
feat: adiciona nova funcionalidade
fix: corrige bug
docs: atualiza documentação
style: mudanças de formatação
refactor: refatora código existente
test: adiciona testes
chore: atualizações de dependências
```

### Exemplo

```bash
git commit -m "feat: adiciona modal de avaliação"
git commit -m "fix: corrige redirecionamento após login"
git commit -m "docs: atualiza guia de desenvolvimento"
```

## Debugging

### Vue DevTools

1. Instale a extensão no Chrome/Firefox
2. Abra as DevTools (F12)
3. Vá para a aba Vue
4. Inspecione componentes, stores e eventos

### Console Logging

```javascript
// Log simples
console.log('Valor:', value)

// Log com estilo
console.log('%cMeu Log', 'color: blue; font-weight: bold')

// Tabela
console.table(data)

// Rastrear
console.trace('Stack trace')
```

### Debugger no VSCode

Crie `.vscode/launch.json`:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "chrome",
      "request": "launch",
      "name": "Launch Chrome",
      "url": "http://localhost:5173",
      "webRoot": "${workspaceFolder}/frontend"
    }
  ]
}
```

## Testes

### Unit Tests (Vitest)

```javascript
import { describe, it, expect } from 'vitest'
import { useAuthStore } from '@/stores/authStore'

describe('AuthStore', () => {
  it('deve fazer login com sucesso', async () => {
    const store = useAuthStore()
    // Teste
  })
})
```

### Executar Testes

```bash
npm run test
npm run test:watch
npm run test:coverage
```

## Performance

### Lazy Loading de Routes

```javascript
const routes = [
  {
    path: '/dashboard',
    component: () => import('../views/Dashboard.vue')
  }
]
```

### Code Splitting

```javascript
// Bom ✓
import { ref } from 'vue'

// Evitar
import * as Vue from 'vue'
```

### Computed vs Methods

```javascript
// Use computed para dados derivados
const doubled = computed(() => count.value * 2)

// Use methods para ações
const handleClick = () => {
  // Ação
}
```

## ESLint e Formatting

### Lint Code

```bash
npm run lint
```

### Formatar Código

```bash
npm run format
```

## Environment Variables

### Desenvolvimento

```env
# .env.local
VITE_API_URL=http://localhost:8080
VITE_APP_NAME=Diárias JÁ
VITE_DEBUG=true
```

### Produção

```env
# .env.production
VITE_API_URL=https://api.diariasja.com
VITE_APP_NAME=Diárias JÁ
VITE_DEBUG=false
```

## Build para Produção

```bash
npm run build
```

Gera pasta `dist/` com arquivos otimizados.

## Deploy

### Vercel

```bash
vercel deploy
```

### Netlify

```bash
netlify deploy --prod --dir=dist
```

### Docker

```bash
docker build -t diarias-ja-frontend .
docker run -p 5173:5173 diarias-ja-frontend
```

## Troubleshooting

### Porta 5173 já em uso

```bash
# Windows
netstat -ano | findstr :5173

# Linux/Mac
lsof -i :5173

# Matar processo
kill -9 PID
```

### Erro de módulo não encontrado

```bash
# Limpar node_modules
rm -rf node_modules
npm install
```

### Cache do Vite

```bash
# Limpar cache
rm -rf node_modules/.vite
npm run dev
```

## Contribuindo

1. Crie uma branch: `git checkout -b feature/minha-feature`
2. Commit mudanças: `git commit -m "feat: descrição"`
3. Push: `git push origin feature/minha-feature`
4. Abra um Pull Request

## Recursos Úteis

- [Vue.js Documentation](https://vuejs.org/)
- [Pinia Documentation](https://pinia.vuejs.org/)
- [Vite Documentation](https://vitejs.dev/)
- [MDN Web Docs](https://developer.mozilla.org/)
- [JavaScript.info](https://javascript.info/)

## Suporte

- Documentação: `/frontend/README.md`
- Arquitetura: `/frontend/ARQUITETURA.md`
- Guia de Uso: `/GUIA-DE-USO.md`
