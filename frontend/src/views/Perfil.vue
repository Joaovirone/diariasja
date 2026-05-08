<template>
  <div class="container">
    <div class="page-header">
      <h1>Meu Perfil</h1>
    </div>

    <div v-if="isLoading" class="loading">
      <div class="spinner"></div>
    </div>

    <div v-else class="profile-container">
      <div class="card">
        <div class="card-header">
          <h2>Informações Pessoais</h2>
        </div>

        <div v-if="error" class="alert alert-error">
          {{ error }}
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="nome">Nome</label>
            <input v-model="form.nome" type="text" id="nome" disabled />
          </div>

          <div class="form-group">
            <label for="email">E-mail</label>
            <input v-model="form.email" type="email" id="email" disabled />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="dataNascimento">Data de Nascimento</label>
            <input v-model="form.dataNascimento" type="date" id="dataNascimento" disabled />
          </div>

          <div class="form-group">
            <label for="tipo">Tipo de Usuário</label>
            <input :value="formatTipo(form.tipo)" type="text" id="tipo" disabled />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUsuarioStore } from '../stores/usuarioStore'

const usuarioStore = useUsuarioStore()

const form = reactive({
  nome: '',
  email: '',
  dataNascimento: '',
  tipo: ''
})

const isLoading = ref(false)
const error = ref(null)

const formatTipo = (tipo) => {
  if (tipo === 'CONTRATANTE') return 'Contratante'
  if (tipo === 'CONTRATADO') return 'Profissional'
  return tipo
}

onMounted(async () => {
  isLoading.value = true
  try {
    const usuario = await usuarioStore.obterPerfil()
    if (usuario) {
      form.nome = usuario.nome
      form.email = usuario.email
      form.dataNascimento = usuario.dataNascimento
      form.tipo = usuario.tipo
    }
  } catch (err) {
    error.value = usuarioStore.error || 'Erro ao carregar perfil'
  } finally {
    isLoading.value = false
  }
})
</script>

<style scoped>
.profile-container {
  max-width: 600px;
}
</style>
