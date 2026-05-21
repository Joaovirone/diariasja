<template>
  <main class="login-page">
    <aside class="side-note" aria-label="Resumo do produto">
      <span>Sergipe em movimento</span>
      <h2>Conecte quem precisa de ajuda com quem sabe resolver.</h2>
      <p>Prestadores de serviço, valores, agenda, notificações e chat em uma experiência simples para o dia a dia.</p>
    </aside>

    <section class="login-card" aria-labelledby="loginTitle">
      <div class="brand-area">
        <span class="brand-mark">DJ</span>
        <span class="brand-name">Diárias Já</span>
      </div>

      <div class="headline">
        <span class="eyebrow">Acesso rápido</span>
        <h1 id="loginTitle">Entre na sua conta</h1>
        <p>Encontre prestadores de serviço, organize diárias e acompanhe contratações com uma identidade inspirada em Sergipe.</p>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div v-if="error" class="alert alert-error">{{ error }}</div>

        <div class="form-group">
          <label for="email">E-mail</label>
          <input v-model="form.email" type="email" id="email" placeholder="joao@gmail.com" required />
        </div>

        <div class="form-group">
          <label for="senha">Senha</label>
          <input v-model="form.senha" type="password" id="senha" placeholder="123456" required />
        </div>

        <button type="submit" :disabled="isLoading" class="btn btn-primary btn-full">
          {{ isLoading ? 'Entrando...' : 'Entrar' }}
        </button>
      </form>

      <p class="auth-footer">
        Não tem conta?
        <router-link to="/register">Cadastre-se</router-link>
      </p>
    </section>
  </main>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

onMounted(() => {
  document.body.classList.add('auth-screen')
})

onUnmounted(() => {
  document.body.classList.remove('auth-screen')
})

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
.login-page {
  position: relative;
  width: 100%;
  min-height: 100vh;
  min-height: 100dvh;
  padding: clamp(1.25rem, 3vw, 3rem);
  /* 🌟 ALTERADO: Fundo azul escuro mais fechado com esferas sutis em verde e amarelo de Sergipe */
  background:
    radial-gradient(circle at 14% 18%, rgba(22, 138, 88, 0.22), transparent 22rem),
    radial-gradient(circle at 82% 16%, rgba(242, 183, 5, 0.14), transparent 24rem),
    linear-gradient(135deg, #051f2e 0%, #06364a 46%, #0b4f5f 100%);
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(380px, 480px);
  align-items: center;
  gap: clamp(2rem, 6vw, 7rem);
  overflow: hidden;
}

.login-card {
  position: relative;
  z-index: 1;
  width: 100%;
  padding: clamp(1.75rem, 3vw, 2.5rem);
  border: none;
  border-radius: 24px;
  background: #ffffff;
  box-shadow: none;
}

.brand-area {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: #111827;
  font-weight: 950;
  font-size: 1.05rem;
}

.brand-name {
  color: #06364a; /* 🌟 ALTERADO: Texto da marca com o Azul de Sergipe */
}

.brand-mark {
  width: 48px;
  height: 48px;
  border-radius: 15px;
  display: grid;
  place-items: center;
  /* 🌟 ALTERADO: Gradiente da logo usando o verde e o amarelo oficiais */
  background: linear-gradient(135deg, var(--sergipe-green), var(--sergipe-yellow));
  color: #ffffff;
  font-weight: 950;
}

.headline {
  margin: 2rem 0 1.4rem;
}

.eyebrow {
  display: inline-flex;
  margin-bottom: 0.85rem;
  padding: 0.42rem 0.7rem;
  border-radius: 999px;
  /* 🌟 ALTERADO: Tag de acesso rápido usando tons de verde suave */
  background: #e6f4f7;
  color: var(--info);
  font-weight: 950;
  text-transform: uppercase;
  font-size: 0.76rem;
}

.headline h1 {
  color: #06364a; /* 🌟 ALTERADO: Título principal com o Azul de Sergipe */
  font-size: clamp(2.2rem, 4vw, 3.15rem);
  margin-bottom: 0.7rem;
}

.headline p {
  color: #475467;
  font-weight: 500;
}

.login-form :deep(input) {
  background: #f8fafc;
  border-color: #d8dee8;
  color: #111827;
}

.login-form :deep(input::placeholder) {
  color: #7a8699;
}

/* 🌟 APLICADO: Botão "Entrar" agora usa o Verde de Sergipe com transição para o verde escuro */
.login-form :deep(.btn-primary) {
  background: var(--sergipe-green) !important;
  color: white !important;
  box-shadow: 0 10px 20px rgba(22, 138, 88, 0.25) !important;
}

.login-form :deep(.btn-primary:hover) {
  background: var(--sergipe-green-dark) !important;
}

.btn-full {
  width: 100%;
}

.auth-footer {
  margin-top: 1.15rem;
  color: #5f6f85;
  text-align: center;
}

.auth-footer a {
  color: var(--sergipe-green); /* 🌟 ALTERADO: Link de cadastro em verde */
  font-weight: 900;
  text-decoration: none;
}

.auth-footer a:hover {
  text-decoration: underline;
  color: var(--sergipe-green-dark);
}

.side-note {
  position: relative;
  z-index: 1;
  max-width: 760px;
  color: white;
}

.side-note span {
  display: inline-flex;
  margin-bottom: 1rem;
  padding: 0.5rem 0.75rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  color: #dff7e9;
  font-size: 0.78rem;
  font-weight: 900;
  text-transform: uppercase;
}

.side-note h2 {
  color: white;
  font-size: clamp(2.6rem, 5.4vw, 5.6rem);
  line-height: 0.96;
  text-shadow: 0 18px 48px rgba(0, 0, 0, 0.28);
}

.side-note p {
  max-width: 640px;
  margin-top: 1.2rem;
  color: #e3eeee;
  font-size: 1.12rem;
  font-weight: 500;
}

@media (max-width: 920px) {
  .login-page {
    grid-template-columns: 1fr;
    place-items: center;
  }

  .side-note {
    display: none;
  }

  .login-card {
    max-width: 480px;
  }
}

@media (max-width: 560px) {
  .login-page {
    padding: 1rem;
  }
}
</style>