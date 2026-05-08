<template>
  <div class="login-container">
    <div class="login-card">
      <h1>Diárias JÁ</h1>
      <p class="subtitle">Sistema de Gerenciamento de Diárias</p>

      <form @submit.prevent="handleLogin">
        <div v-if="error" class="alert alert-error">
          <span>{{ error }}</span>
        </div>

        <div class="form-group">
          <label for="email">E-mail</label>
          <input
            v-model="form.email"
            type="email"
            id="email"
            placeholder="seu@email.com"
            required
          />
        </div>

        <div class="form-group">
          <label for="senha">Senha</label>
          <input
            v-model="form.senha"
            type="password"
            id="senha"
            placeholder="••••••••"
            required
          />
        </div>

        <button
          type="submit"
          :disabled="isLoading"
          class="btn btn-primary btn-full"
        >
          {{ isLoading ? 'Entrando...' : 'Entrar' }}
        </button>
      </form>

      <p class="login-footer">
        Não tem conta?
        <router-link to="/register">Cadastre-se aqui</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

const form = reactive({
  email: '',
  senha: ''
})

const isLoading = ref(false)
const error = ref(null)

const handleLogin = async () => {
  isLoading.value = true
  error.value = null

  const success = await authStore.login(form.email, form.senha)

  if (success) {
    router.push('/dashboard')
  } else {
    error.value = authStore.error
  }

  isLoading.value = false
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 1rem;
}

.login-card {
  background: white;
  border-radius: 1rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  padding: 3rem 2rem;
  width: 100%;
  max-width: 400px;
}

h1 {
  text-align: center;
  color: #667eea;
  margin-bottom: 0.5rem;
  font-size: 2rem;
}

.subtitle {
  text-align: center;
  color: #666;
  margin-bottom: 2rem;
  font-size: 0.9rem;
}

.btn-full {
  width: 100%;
}

.login-footer {
  text-align: center;
  margin-top: 1.5rem;
  font-size: 0.9rem;
  color: #666;
}

.login-footer a {
  color: #667eea;
  font-weight: 600;
  text-decoration: none;
}

.login-footer a:hover {
  text-decoration: underline;
}

@media (max-width: 480px) {
  .login-card {
    padding: 2rem 1.5rem;
  }

  h1 {
    font-size: 1.5rem;
  }
}
</style>
