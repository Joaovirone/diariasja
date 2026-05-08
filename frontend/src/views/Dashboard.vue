<template>
  <div class="container">
    <div class="dashboard-header">
      <h1>Dashboard</h1>
      <p>Bem-vindo ao Diárias JÁ</p>
    </div>

    <div v-if="isLoading" class="loading">
      <div class="spinner"></div>
    </div>

    <div v-else class="dashboard-content">
      <div class="grid grid-3">
        <div class="card stat-card">
          <h3>Diárias Ativas</h3>
          <p class="stat-number">{{ diariasAtivas }}</p>
        </div>
        <div class="card stat-card">
          <h3>Diárias Concluídas</h3>
          <p class="stat-number">{{ diariasConcluidas }}</p>
        </div>
        <div class="card stat-card">
          <h3>Taxa de Aprovação</h3>
          <p class="stat-number">{{ taxaAprovacao }}%</p>
        </div>
      </div>

      <div class="card mt-4">
        <div class="card-header">
          <h2>Diárias Recentes</h2>
          <router-link to="/diarias/criar" class="btn btn-primary btn-small">
            + Nova Diária
          </router-link>
        </div>
        <div class="card-body">
          <div v-if="diariasRecentes.length === 0" class="text-center text-muted">
            <p>Nenhuma diária disponível</p>
          </div>
          <table v-else>
            <thead>
              <tr>
                <th>Categoria</th>
                <th>Contratante</th>
                <th>Data</th>
                <th>Status</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="diaria in diariasRecentes" :key="diaria.id">
                <td>{{ diaria.nomeCategoria }}</td>
                <td>{{ diaria.nomeContratante }}</td>
                <td>{{ formatDate(diaria.dataServico) }}</td>
                <td>
                  <span :class="`badge badge-${getStatusClass(diaria.status)}`">
                    {{ formatStatus(diaria.status) }}
                  </span>
                </td>
                <td>
                  <router-link :to="`/diarias/${diaria.id}`" class="btn btn-small">
                    Ver Detalhes
                  </router-link>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useDiariaStore } from '../stores/diariaStore'
import { format } from 'date-fns'
import { ptBR } from 'date-fns/locale'

const diariaStore = useDiariaStore()
const isLoading = ref(false)

const diariasRecentes = computed(() => diariaStore.diarias.slice(0, 5))

const diariasAtivas = computed(() => {
  return diariaStore.diarias.filter(d => d.status === 'ATIVA' || d.status === 'ACEITA').length
})

const diariasConcluidas = computed(() => {
  return diariaStore.diarias.filter(d => d.status === 'CONCLUIDA').length
})

const taxaAprovacao = computed(() => {
  if (diariaStore.total === 0) return 0
  return Math.round((diariasConcluidas.value / diariaStore.total) * 100)
})

const formatDate = (date) => {
  return format(new Date(date), 'dd MMM yyyy', { locale: ptBR })
}

const formatStatus = (status) => {
  const statusMap = {
    'ATIVA': 'Ativa',
    'ACEITA': 'Aceita',
    'CONCLUIDA': 'Concluída',
    'CANCELADA': 'Cancelada'
  }
  return statusMap[status] || status
}

const getStatusClass = (status) => {
  const classMap = {
    'ATIVA': 'info',
    'ACEITA': 'success',
    'CONCLUIDA': 'success',
    'CANCELADA': 'danger'
  }
  return classMap[status] || 'info'
}

onMounted(async () => {
  isLoading.value = true
  await diariaStore.listar(0, 10)
  isLoading.value = false
})
</script>

<style scoped>
.dashboard-header {
  margin-bottom: 2rem;
}

.dashboard-header h1 {
  margin-bottom: 0.5rem;
}

.dashboard-header p {
  color: #666;
  font-size: 1.1rem;
  margin: 0;
}

.stat-card {
  text-align: center;
}

.stat-card h3 {
  font-size: 0.9rem;
  text-transform: uppercase;
  color: #666;
  margin-bottom: 1rem;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #667eea;
  margin: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
}

table {
  margin-top: 1rem;
}

.btn-small {
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
}
</style>
