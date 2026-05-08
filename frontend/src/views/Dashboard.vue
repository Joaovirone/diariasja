<template>
  <div class="container">
    <div class="dashboard-header">
      <h1>Dashboard</h1>
      <p>Bem-vindo ao Diárias Já</p>
    </div>

    <div v-if="isLoading" class="loading">
      <div class="spinner"></div>
    </div>

    <div v-else class="dashboard-content">
      <div class="grid grid-3">
        <div class="card stat-card">
          <h3>Pendentes</h3>
          <p class="stat-number">{{ pendentes }}</p>
        </div>
        <div class="card stat-card">
          <h3>Confirmadas</h3>
          <p class="stat-number">{{ confirmadas }}</p>
        </div>
        <div class="card stat-card">
          <h3>Concluídas</h3>
          <p class="stat-number">{{ concluidas }}</p>
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
                <th>Profissional</th>
                <th>Data</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="diaria in diariasRecentes" :key="diaria.id">
                <td>{{ diaria.nomeCategoria }}</td>
                <td>{{ diaria.nomeContratado }}</td>
                <td>{{ formatDate(diaria.dataServico) }}</td>
                <td>
                  <span :class="`badge badge-${getStatusClass(diaria.status)}`">
                    {{ formatStatus(diaria.status) }}
                  </span>
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
import { useAuthStore } from '../stores/authStore'
import { useDiariaStore } from '../stores/diariaStore'
import { format } from 'date-fns'
import { ptBR } from 'date-fns/locale'

const authStore = useAuthStore()
const diariaStore = useDiariaStore()
const isLoading = ref(false)

const diariasRecentes = computed(() => diariaStore.diarias.slice(0, 5))
const pendentes = computed(() => diariaStore.diarias.filter(d => d.status === 'PENDENTE').length)
const confirmadas = computed(() => diariaStore.diarias.filter(d => d.status === 'CONFIRMADA').length)
const concluidas = computed(() => diariaStore.diarias.filter(d => d.status === 'CONCLUIDA').length)

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

onMounted(async () => {
  isLoading.value = true
  await diariaStore.listarPorContratante(authStore.user.id, 0, 10)
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
