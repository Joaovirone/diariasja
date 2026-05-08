<template>
  <div class="container">
    <div class="page-header">
      <h1>Minhas Diárias</h1>
      <router-link to="/diarias/criar" class="btn btn-primary">
        + Solicitar Nova Diária
      </router-link>
    </div>

    <div v-if="isLoading" class="loading">
      <div class="spinner"></div>
    </div>

    <div v-else>
      <div v-if="error" class="alert alert-error">
        {{ error }}
      </div>

      <div class="filters">
        <select v-model="filtroStatus" class="filter-select">
          <option value="">Todos os Status</option>
          <option value="PENDENTE">Pendente</option>
          <option value="CONFIRMADA">Confirmada</option>
          <option value="CONCLUIDA">Concluída</option>
          <option value="CANCELADA">Cancelada</option>
        </select>
      </div>

      <div v-if="diariasFiltradas.length === 0" class="card text-center">
        <p class="text-muted">Nenhuma diária encontrada</p>
      </div>

      <div v-else class="grid grid-1">
        <div
          v-for="diaria in diariasFiltradas"
          :key="diaria.id"
          class="card diaria-card"
        >
          <div class="diaria-header">
            <div>
              <h3>{{ diaria.nomeCategoria }}</h3>
              <p class="text-muted">{{ diaria.nomeContratante }}</p>
            </div>
            <span :class="`badge badge-${getStatusClass(diaria.status)}`">
              {{ formatStatus(diaria.status) }}
            </span>
          </div>

          <div class="diaria-body">
            <div class="info-item">
              <span class="label">Data:</span>
              <span>{{ formatDate(diaria.dataServico) }}</span>
            </div>
            <div class="info-item">
              <span class="label">Contratado:</span>
              <span>{{ diaria.nomeContratado }}</span>
            </div>
          </div>

          <div class="diaria-footer">
            <button
              v-if="diaria.status === 'CONCLUIDA'"
              class="btn btn-success btn-small"
              @click="avaliarDiaria(diaria.id)"
            >
              Avaliar
            </button>
            <button
              v-if="diaria.status !== 'CONCLUIDA' && diaria.status !== 'CANCELADA'"
              class="btn btn-danger btn-small"
              @click="cancelarDiaria(diaria.id)"
            >
              Cancelar
            </button>
          </div>
        </div>
      </div>
    </div>

    <ModalAvaliacao
      v-if="showModalAvaliacao"
      :diaria-id="diariaEmAvaliacao"
      @fechar="showModalAvaliacao = false"
      @avaliar="handleAvaliar"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/authStore'
import { useDiariaStore } from '../stores/diariaStore'
import { format } from 'date-fns'
import { ptBR } from 'date-fns/locale'
import ModalAvaliacao from '../components/ModalAvaliacao.vue'

const authStore = useAuthStore()
const diariaStore = useDiariaStore()
const filtroStatus = ref('')
const showModalAvaliacao = ref(false)
const diariaEmAvaliacao = ref(null)

const isLoading = computed(() => diariaStore.isLoading)
const error = computed(() => diariaStore.error)

const diariasFiltradas = computed(() => {
  if (!filtroStatus.value) return diariaStore.diarias
  return diariaStore.diarias.filter(d => d.status === filtroStatus.value)
})

const formatDate = (date) => format(new Date(date), 'dd MMM yyyy', { locale: ptBR })

const formatStatus = (status) => {
  const statusMap = {
    PENDENTE: 'Pendente',
    CONFIRMADA: 'Confirmada',
    CONCLUIDA: 'Concluída',
    CANCELADA: 'Cancelada'
  }
  return statusMap[status] || status
}

const getStatusClass = (status) => {
  const classMap = {
    PENDENTE: 'info',
    CONFIRMADA: 'success',
    CONCLUIDA: 'success',
    CANCELADA: 'danger'
  }
  return classMap[status] || 'info'
}

const cancelarDiaria = async (id) => {
  if (confirm('Tem certeza que deseja cancelar esta diária?')) {
    await diariaStore.cancelar(id, authStore.user.id)
  }
}

const avaliarDiaria = (id) => {
  diariaEmAvaliacao.value = id
  showModalAvaliacao.value = true
}

const handleAvaliar = async (nota) => {
  await diariaStore.avaliar(diariaEmAvaliacao.value, nota)
  showModalAvaliacao.value = false
}

onMounted(async () => {
  await diariaStore.listarPorContratante(authStore.user.id, 0, 20)
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.filters {
  margin-bottom: 2rem;
}

.filter-select {
  padding: 0.75rem;
  border: 1px solid var(--border-color);
  border-radius: 0.5rem;
  font-size: 1rem;
  max-width: 300px;
}

.grid-1 {
  grid-template-columns: 1fr;
}

.diaria-card {
  border-left: 4px solid #667eea;
}

.diaria-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--border-color);
}

.diaria-header h3 {
  margin: 0 0 0.25rem 0;
}

.diaria-header .text-muted {
  margin: 0;
}

.diaria-body {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
  margin-bottom: 1rem;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.label {
  font-weight: 600;
  color: #666;
  font-size: 0.9rem;
}

.diaria-footer {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  padding-top: 1rem;
  border-top: 1px solid var(--border-color);
}

.btn-small {
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }

  .diaria-body {
    grid-template-columns: 1fr;
  }
}
</style>
