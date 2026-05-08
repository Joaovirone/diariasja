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

        <form @submit.prevent="handleSalvar">
          <div v-if="error" class="alert alert-error">
            {{ error }}
          </div>

          <div v-if="success" class="alert alert-success">
            Perfil atualizado com sucesso!
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="nome">Nome *</label>
              <input
                v-model="form.nome"
                type="text"
                id="nome"
                required
              />
            </div>

            <div class="form-group">
              <label for="email">E-mail *</label>
              <input
                v-model="form.email"
                type="email"
                id="email"
                disabled
              />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="dataNascimento">Data de Nascimento *</label>
              <input
                v-model="form.dataNascimento"
                type="date"
                id="dataNascimento"
                required
              />
            </div>

            <div class="form-group">
              <label for="tipo">Tipo de Usuário</label>
              <input
                :value="form.tipo"
                type="text"
                id="tipo"
                disabled
              />
            </div>
          </div>

          <div class="form-group">
            <label for="telefone">Telefone</label>
            <input
              v-model="form.telefone"
              type="tel"
              id="telefone"
              placeholder="(11) 9xxxx-xxxx"
            />
          </div>

          <div class="form-group">
            <label for="endereco">Endereço</label>
            <input
              v-model="form.endereco"
              type="text"
              id="endereco"
              placeholder="Rua, nº, bairro, cidade"
            />
          </div>

          <div class="form-actions">
            <button
              type="submit"
              :disabled="isSaving"
              class="btn btn-primary"
            >
              {{ isSaving ? 'Salvando...' : 'Salvar Alterações' }}
            </button>
          </div>
        </form>
      </div>

      <div class="card mt-4">
        <div class="card-header">
          <h2>Estatísticas</h2>
        </div>
        <div class="grid grid-3">
          <div class="stat">
            <p class="stat-label">Diárias Solicitadas</p>
            <p class="stat-value">{{ stats.diariasSolicitadas }}</p>
          </div>
          <div class="stat">
            <p class="stat-label">Diárias Concluídas</p>
            <p class="stat-value">{{ stats.diariasConcluidas }}</p>
          </div>
          <div class="stat">
            <p class="stat-label">Avaliação Média</p>
            <p class="stat-value">{{ stats.avaliacaoMedia }}</p>
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
  tipo: '',
  telefone: '',
  endereco: ''
})

const stats = reactive({
  diariasSolicitadas: 0,
  diariasConcluidas: 0,
  avaliacaoMedia: 0
})

const isLoading = ref(false)
const isSaving = ref(false)
const error = ref(null)
const success = ref(false)

const handleSalvar = async () => {
  isSaving.value = true
  error.value = null
  success.value = false

  try {
    const dados = {
      nome: form.nome,
      dataNascimento: form.dataNascimento,
      telefone: form.telefone,
      endereco: form.endereco
    }

    await usuarioStore.atualizar(dados)
    success.value = true
    setTimeout(() => {
      success.value = false
    }, 3000)
  } catch (err) {
    error.value = usuarioStore.error || 'Erro ao atualizar perfil'
  } finally {
    isSaving.value = false
  }
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
      form.telefone = usuario.telefone || ''
      form.endereco = usuario.endereco || ''

      // Simulando estatísticas (substituir com dados reais da API)
      stats.diariasSolicitadas = 12
      stats.diariasConcluidas = 10
      stats.avaliacaoMedia = '4.8/5'
    }
  } catch (err) {
    error.value = 'Erro ao carregar perfil'
  } finally {
    isLoading.value = false
  }
})
</script>

<style scoped>
.profile-container {
  max-width: 600px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 2rem;
  border-top: 1px solid var(--border-color);
  padding-top: 2rem;
}

.stat {
  text-align: center;
  padding: 1rem;
}

.stat-label {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
  text-transform: uppercase;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: bold;
  color: #667eea;
  margin: 0;
}
</style>
