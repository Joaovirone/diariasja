<template>
  <div class="register-container">
    <div class="register-card">
      <h1>Criar Conta</h1>
      <p class="subtitle">Junte-se ao Diárias JÁ</p>

      <form @submit.prevent="handleRegister">
        <div v-if="error" class="alert alert-error">
          <span>{{ error }}</span>
        </div>

        <div v-if="success" class="alert alert-success">
          <span>Conta criada com sucesso! Redirecionando...</span>
        </div>

        <div class="form-group">
          <label for="nome">Nome Completo</label>
          <input
            v-model="form.nome"
            type="text"
            id="nome"
            placeholder="Seu nome completo"
            required
          />
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
          <label for="dataNascimento">Data de Nascimento</label>
          <input
            v-model="form.dataNascimento"
            type="date"
            id="dataNascimento"
            required
          />
        </div>

        <div class="form-group">
          <label for="tipo">Tipo de Usuário</label>
          <select v-model="form.tipo" id="tipo" required>
            <option value="">Selecione um tipo</option>
            <option value="CONTRATANTE">Contratante</option>
            <option value="CONTRATADO">Profissional</option>
          </select>
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

        <div class="form-group">
          <label for="confirmaSenha">Confirmar Senha</label>
          <input
            v-model="form.confirmaSenha"
            type="password"
            id="confirmaSenha"
            placeholder="••••••••"
            required
          />
        </div>

        <button
          type="submit"
          :disabled="isLoading"
          class="btn btn-primary btn-full"
        >
          {{ isLoading ? 'Criando conta...' : 'Criar Conta' }}
        </button>
      </form>

      <p class="register-footer">
        Já tem conta?
        <router-link to="/login">Faça login aqui</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUsuarioStore } from '../stores/usuarioStore'

const router = useRouter()
const usuarioStore = useUsuarioStore()

const form = reactive({
  nome: '',
  email: '',
  dataNascimento: '',
  tipo: '',
  senha: '',
  confirmaSenha: ''
})

const isLoading = ref(false)
const error = ref(null)
const success = ref(false)

const handleRegister = async () => {
  if (form.senha !== form.confirmaSenha) {
    error.value = 'As senhas não correspondem'
    return
  }

  isLoading.value = true
  error.value = null

  try {
    const dados = {
      nome: form.nome,
      email: form.email,
      dataNascimento: form.dataNascimento,
      tipo: form.tipo,
      senha: form.senha
    }

    await usuarioStore.criar(dados)
    success.value = true

    setTimeout(() => {
      router.push('/login')
    }, 2000)
  } catch (err) {
    error.value = usuarioStore.error || 'Erro ao criar conta'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 1rem;
}

.register-card {
  background: white;
  border-radius: 1rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  padding: 3rem 2rem;
  width: 100%;
  max-width: 450px;
}

h1 {
  text-align: center;
  color: #667eea;
  margin-bottom: 0.5rem;
  font-size: 1.75rem;
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

.register-footer {
  text-align: center;
  margin-top: 1.5rem;
  font-size: 0.9rem;
  color: #666;
}

.register-footer a {
  color: #667eea;
  font-weight: 600;
  text-decoration: none;
}

.register-footer a:hover {
  text-decoration: underline;
}

@media (max-width: 480px) {
  .register-card {
    padding: 2rem 1.5rem;
  }

  h1 {
    font-size: 1.5rem;
  }
}
</style>
