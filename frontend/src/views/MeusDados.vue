<template>
  <div class="container">
    <div class="page-header">
      <h1>Meus Dados</h1>
    </div>

    <div class="grid grid-2">
      <div class="card">
        <div class="card-header">
          <h2>Dados Profissionais</h2>
        </div>
        <div class="data-display">
          <div class="data-item">
            <span class="label">Tipo de Usuário:</span>
            <span class="value">{{ usuario?.tipo === 'CONTRATANTE' ? 'Contratante' : 'Profissional' }}</span>
          </div>
          <div class="data-item">
            <span class="label">Diárias Solicitadas:</span>
            <span class="value">{{ stats.solicitadas }}</span>
          </div>
          <div class="data-item">
            <span class="label">Diárias Aceitas:</span>
            <span class="value">{{ stats.aceitas }}</span>
          </div>
          <div class="data-item">
            <span class="label">Diárias Concluídas:</span>
            <span class="value">{{ stats.concluidas }}</span>
          </div>
          <div class="data-item">
            <span class="label">Taxa de Conclusão:</span>
            <span class="value">{{ stats.taxaConclusao }}%</span>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <h2>Avaliações</h2>
        </div>
        <div class="data-display">
          <div class="data-item">
            <span class="label">Avaliação Média:</span>
            <span class="value">
              {{ stats.avaliacaoMedia }}/5.0
              <span class="stars">★★★★★</span>
            </span>
          </div>
          <div class="data-item">
            <span class="label">Total de Avaliações:</span>
            <span class="value">{{ stats.totalAvaliacoes }}</span>
          </div>
          <div class="data-item">
            <span class="label">Reputação:</span>
            <span class="badge" :class="`badge-${getReputacaoClass()}`">
              {{ getReputacao() }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <div class="card mt-4">
      <div class="card-header">
        <h2>Histórico de Diárias</h2>
      </div>
      <table>
        <thead>
          <tr>
            <th>Categoria</th>
            <th>Data</th>
            <th>Status</th>
            <th>Avaliação</th>
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
            <td>
              <span v-if="diaria.avaliacao" class="stars">
                {{ renderStars(diaria.avaliacao) }}
              </span>
              <span v-else class="text-muted">-</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useDiariaStore } from '../stores/diariaStore'
import { useUsuarioStore } from '../stores/usuarioStore'
import { format } from 'date-fns'
import { ptBR } from 'date-fns/locale'

const diariaStore = useDiariaStore()
const usuarioStore = useUsuarioStore()

const usuario = ref(null)
const historicoRecente = ref([])

const stats = reactive({
  solicitadas: 0,
  aceitas: 0,
  concluidas: 0,
  taxaConclusao: 0,
  avaliacaoMedia: 4.5,
  totalAvaliacoes: 0
})

const formatDate = (date) => {
  return format(new Date(date), 'dd/MM/yyyy', { locale: ptBR })
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

const getReputacao = () => {
  if (stats.avaliacaoMedia >= 4.5) return 'Excelente'
  if (stats.avaliacaoMedia >= 4.0) return 'Muito Bom'
  if (stats.avaliacaoMedia >= 3.5) return 'Bom'
  return 'Regular'
}

const getReputacaoClass = () => {
  if (stats.avaliacaoMedia >= 4.5) return 'success'
  if (stats.avaliacaoMedia >= 4.0) return 'success'
  if (stats.avaliacaoMedia >= 3.5) return 'warning'
  return 'danger'
}

const renderStars = (nota) => {
  return '★'.repeat(nota) + '☆'.repeat(5 - nota)
}

onMounted(async () => {
  try {
    usuario.value = await usuarioStore.obterPerfil()
    await diariaStore.listarMinhas(0, 5)

    historicoRecente.value = diariaStore.minhasDiarias

    // Calcular estatísticas
    const todasDiarias = diariaStore.minhasDiarias
    stats.solicitadas = todasDiarias.length
    stats.aceitas = todasDiarias.filter(d => d.status === 'ACEITA').length
    stats.concluidas = todasDiarias.filter(d => d.status === 'CONCLUIDA').length
    stats.taxaConclusao = stats.solicitadas > 0 ? Math.round((stats.concluidas / stats.solicitadas) * 100) : 0
  } catch (err) {
    console.error('Erro ao carregar dados:', err)
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

.stars {
  color: #ffc107;
  font-size: 1.2rem;
  margin-left: 0.5rem;
}

table {
  margin-top: 1rem;
}

td {
  padding: 1rem;
}

.badge {
  padding: 0.25rem 0.75rem;
  border-radius: 0.25rem;
  font-size: 0.85rem;
  font-weight: 600;
}
</style>
