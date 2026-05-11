<template>
  <div class="auth-page">
    <section class="auth-panel">
      <h1>Criar conta</h1>
      <p>Escolha seu perfil e complete os dados para entrar no Diárias Já.</p>

      <form @submit.prevent="handleRegister">
        <div v-if="error" class="alert alert-error">{{ error }}</div>
        <div v-if="success" class="alert alert-success">Conta criada com sucesso. Redirecionando...</div>

        <div class="form-group">
          <label for="nome">Nome completo</label>
          <input v-model="form.nome" type="text" id="nome" placeholder="Seu nome completo" required />
        </div>

        <div class="form-group">
          <label for="email">E-mail</label>
          <input v-model="form.email" type="email" id="email" placeholder="seu@email.com" required />
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="dataNascimento">Nascimento</label>
            <input v-model="form.dataNascimento" type="date" id="dataNascimento" required />
          </div>

          <div class="form-group">
            <label for="tipo">Perfil</label>
            <select v-model="form.tipo" id="tipo" required>
              <option value="">Selecione</option>
              <option value="CONTRATANTE">Contratante</option>
              <option value="CONTRATADO">Autônomo</option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="senha">Senha</label>
            <input v-model="form.senha" type="password" id="senha" placeholder="Mínimo 6 caracteres" required />
          </div>

          <div class="form-group">
            <label for="confirmaSenha">Confirmar senha</label>
            <input v-model="form.confirmaSenha" type="password" id="confirmaSenha" placeholder="Repita a senha" required />
          </div>
        </div>

        <button type="submit" :disabled="isLoading" class="btn btn-primary btn-full">
          {{ isLoading ? 'Criando conta...' : 'Criar conta' }}
        </button>
      </form>

      <p class="auth-footer">
        Já tem conta?
        <router-link to="/login">Entrar</router-link>
      </p>
    </section>
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
    await usuarioStore.criar({
      nome: form.nome,
      email: form.email,
      dataNascimento: form.dataNascimento,
      tipo: form.tipo,
      senha: form.senha
    })
    success.value = true

    setTimeout(() => {
      router.push('/login')
    }, 1600)
  } catch (err) {
    error.value = usuarioStore.error || 'Erro ao criar conta'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 0;
  background:
    radial-gradient(circle at 78% 18%, rgba(249, 115, 22, 0.18), transparent 28%),
    linear-gradient(135deg, #101828, #0f766e);
}

.auth-panel {
  width: min(720px, 100%);
  min-height: 100vh;
  padding: clamp(1.5rem, 4vw, 3rem);
  border-radius: 0;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 28px 80px rgba(16, 24, 40, 0.28);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.auth-panel p {
  color: var(--muted);
  margin-bottom: 1.25rem;
}

.btn-full {
  width: 100%;
}

.auth-footer {
  margin-top: 1.25rem;
  text-align: center;
}

.auth-footer a {
  color: var(--primary);
  font-weight: 800;
  text-decoration: none;
}
</style>
