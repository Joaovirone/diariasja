<template>
  <div class="container">
    <div class="page-header">
      <h1>Meus dados</h1>
    </div>

    <div class="grid grid-2">
      <div class="card">
        <div class="card-header">
          <h2>Dados da conta</h2>
        </div>
        <div class="data-display">
          <div class="data-item">
            <span class="label">Tipo de usuário:</span>
            <span class="value">{{ formatTipo(usuario?.tipo) }}</span>
          </div>
          <div class="data-item">
            <span class="label">Diárias carregadas:</span>
            <span class="value">{{ historicoRecente.length }}</span>
          </div>
          <div class="data-item">
            <span class="label">Concluídas:</span>
            <span class="value">{{ concluidas }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="card mt-4">
      <div class="card-header">
        <h2>Histórico de diárias</h2>
      </div>
      <table>
        <thead>
          <tr>
            <th>Categoria</th>
            <th>Data</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="diaria in historicoRecente" :key="diaria.id">
            <td>{{ diaria.nomeCategoria }}</td>
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
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/authStore'
import { useDiariaStore } from '../stores/diariaStore'
import { useUsuarioStore } from '../stores/usuarioStore'
import { format } from 'date-fns'
import { ptBR } from 'date-fns/locale'

const authStore = useAuthStore()
const diariaStore = useDiariaStore()
const usuarioStore = useUsuarioStore()

const usuario = ref(null)
const historicoRecente = computed(() => diariaStore.diarias)
const concluidas = computed(() => historicoRecente.value.filter(d => d.status === 'CONCLUIDA').length)

const formatDate = (date) => format(new Date(date), 'dd/MM/yyyy', { locale: ptBR })
const formatTipo = (tipo) => tipo === 'CONTRATANTE' ? 'Contratante' : 'Profissional'

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
  usuario.value = await usuarioStore.obterPerfil()

  if (usuario.value?.tipo === 'CONTRATANTE') {
    await diariaStore.listarPorContratante(authStore.user.id, 0, 5)
  } else {
    await diariaStore.listarPendentesProfissional(authStore.user.id, 0, 5)
    diariaStore.diarias = diariaStore.diariasPendentes
  }
})
</script>

<style scoped>
.page-header {
  margin-bottom: 2rem;
}

.data-display {
  padding: 1rem 0;
}

.data-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 0;
  border-bottom: 1px solid var(--border-color);
}

.data-item:last-child {
  border-bottom: none;
}

.label {
  font-weight: 600;
  color: #333;
}

.value {
  color: #667eea;
  font-weight: 500;
}

table {
  margin-top: 1rem;
}

td {
  padding: 1rem;
}
</style>
