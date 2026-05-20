<template>
  <main class="register-page">
    <aside class="side-note" aria-label="Resumo do cadastro">
      <span>Cadastro simples</span>
      <h2>Escolha sua jornada no Diárias Já.</h2>
      <p>Contratantes encontram ajuda com clareza. Prestadores de serviço organizam perfil, agenda e contatos.</p>
    </aside>

    <section class="register-card" aria-labelledby="registerTitle">
      <div class="brand-area">
        <span class="brand-mark">DJ</span>
        <span>Diárias Já</span>
      </div>

      <div class="headline">
        <span class="eyebrow">Nova conta</span>
        <h1 id="registerTitle">Criar conta</h1>
        <p>Informe seus dados e selecione o tipo de acesso para abrir a área correta após o login.</p>
      </div>

      <form @submit.prevent="handleRegister" novalidate>
        <div v-if="error" class="alert alert-error">{{ error }}</div>
        <div v-if="success" class="alert alert-success">Conta criada com sucesso. Redirecionando para o login...</div>

        <div class="form-group">
          <label for="nome">Nome completo</label>
          <input v-model.trim="form.nome" type="text" id="nome" placeholder="Seu nome completo" required />
        </div>

        <div class="form-group">
          <label for="email">E-mail</label>
          <input v-model.trim="form.email" type="email" id="email" placeholder="seu@email.com" required />
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
              <option value="CONTRATADO">Prestador de serviço</option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="senha">Senha</label>
            <input v-model="form.senha" type="password" id="senha" placeholder="Mínimo 8 caracteres" required minlength="8" />
          </div>

          <div class="form-group">
            <label for="confirmaSenha">Confirmar senha</label>
            <input v-model="form.confirmaSenha" type="password" id="confirmaSenha" placeholder="Repita a senha" required minlength="8" />
          </div>
        </div>

        <button type="submit" :disabled="isLoading || success" class="btn btn-primary btn-full">
          {{ isLoading ? 'Criando conta...' : 'Criar conta' }}
        </button>
      </form>

      <p class="auth-footer">
        Já tem conta?
        <router-link to="/login">Entrar</router-link>
      </p>
    </section>
  </main>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUsuarioStore } from '../stores/usuarioStore'

const router = useRouter()
const usuarioStore = useUsuarioStore()

onMounted(() => {
  document.body.classList.add('auth-screen')
})

onUnmounted(() => {
  document.body.classList.remove('auth-screen')
})

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

const validateForm = () => {
  if (!form.nome || !form.email || !form.dataNascimento || !form.tipo || !form.senha || !form.confirmaSenha) {
    return 'Preencha todos os campos obrigatórios.'
  }

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    return 'Informe um e-mail válido.'
  }

  if (form.senha.length < 8) {
    return 'A senha deve conter no mínimo 8 caracteres.'
  }

  if (form.senha !== form.confirmaSenha) {
    return 'As senhas não correspondem.'
  }

  return null
}

const handleRegister = async () => {
  error.value = validateForm()
  if (error.value) return

  isLoading.value = true
  success.value = false

  try {
    await usuarioStore.criar({
      nome: form.nome,
      email: form.email,
      dataNascimento: form.dataNascimento,
      tipo: form.tipo,
      senha: form.senha
    })
    success.value = true

    window.setTimeout(() => {
      router.push('/login')
    }, 1200)
  } catch (err) {
    error.value = usuarioStore.error || 'Erro ao criar conta.'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.register-page {
  width: 100%;
  min-height: 100vh;
  min-height: 100dvh;
  padding: clamp(1.25rem, 3vw, 3rem);
  background:
    radial-gradient(circle at 14% 18%, rgba(22, 138, 88, 0.26), transparent 18rem),
    radial-gradient(circle at 82% 16%, rgba(242, 183, 5, 0.16), transparent 22rem),
    linear-gradient(135deg, #06364a 0%, #0b5967 46%, #10202b 100%);
  display: grid;
  grid-template-columns: minmax(0, 0.9fr) minmax(420px, 620px);
  align-items: center;
  gap: clamp(2rem, 5vw, 6rem);
}

.register-card {
  width: 100%;
  padding: clamp(1.5rem, 3vw, 2.25rem);
  border: 1px solid rgba(255, 255, 255, 0.92);
  border-radius: 24px;
  background: #ffffff;
  box-shadow: 0 34px 90px rgba(2, 8, 23, 0.34);
}

.brand-area {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: var(--dark);
  font-weight: 950;
  font-size: 1.05rem;
}

.brand-mark {
  width: 48px;
  height: 48px;
  border-radius: 15px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, var(--sergipe-green), var(--sergipe-yellow));
  color: #082f36;
  font-weight: 950;
}

.headline {
  margin: 1.55rem 0 1.25rem;
}

.eyebrow {
  display: inline-flex;
  margin-bottom: 0.7rem;
  padding: 0.42rem 0.7rem;
  border-radius: 999px;
  background: var(--primary-soft);
  color: var(--primary);
  font-weight: 950;
  text-transform: uppercase;
  font-size: 0.76rem;
}

.headline h1 {
  color: var(--dark);
  margin-bottom: 0.5rem;
}

.headline p,
.auth-footer {
  color: var(--muted);
}

.btn-full {
  width: 100%;
}

.auth-footer {
  margin-top: 1.15rem;
  text-align: center;
}

.auth-footer a {
  color: var(--primary);
  font-weight: 900;
  text-decoration: none;
}

.side-note {
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
  font-size: clamp(2.45rem, 5vw, 5rem);
  line-height: 0.98;
  text-shadow: 0 18px 48px rgba(0, 0, 0, 0.28);
}

.side-note p {
  max-width: 620px;
  margin-top: 1.2rem;
  color: #e3eeee;
  font-size: 1.08rem;
  font-weight: 500;
}

@media (max-width: 980px) {
  .register-page {
    grid-template-columns: 1fr;
    place-items: center;
  }

  .side-note {
    display: none;
  }

  .register-card {
    max-width: 620px;
  }
}

@media (max-width: 560px) {
  .register-page {
    padding: 1rem;
  }
}
</style>
