<template>
  <div class="container">
    <div class="page-header">
      <div>
        <h1>{{ isContratante ? 'Minhas diárias' : 'Serviços disponíveis' }}</h1>
        <p>{{ isContratante ? 'Acompanhe seus pedidos e combine detalhes pelo chat.' : 'Aceite oportunidades e gerencie os próximos atendimentos.' }}</p>
      </div>
      <router-link v-if="isContratante" to="/diarias/criar" class="btn btn-primary">Solicitar diária</router-link>
    </div>

    <div v-if="isLoading" class="loading">
      <div class="spinner"></div>
    </div>

    <div v-else>
      <div v-if="error" class="alert alert-error">{{ error }}</div>

      <div class="filters">
        <select v-model="filtroStatus" class="filter-select">
          <option value="">Todos os status</option>
          <option value="PENDENTE">Pendente</option>
          <option value="CONFIRMADA">Confirmada</option>
          <option value="CONCLUIDA">Concluída</option>
          <option value="CANCELADA">Cancelada</option>
        </select>
      </div>

      <div v-if="diariasFiltradas.length === 0" class="card text-center">
        <strong>Nada encontrado</strong>
        <p class="text-muted">Quando houver diárias neste perfil, elas aparecem aqui.</p>
      </div>

      <div v-else class="diarias-grid">
        <article v-for="diaria in diariasFiltradas" :key="diaria.id" class="diaria-card">
          <div class="diaria-top">
            <div>
              <h3>{{ diaria.nomeCategoria }}</h3>
              <p>{{ isContratante ? diaria.nomeContratado : diaria.nomeContratante }}</p>
            </div>
            <span :class="`badge badge-${getStatusClass(diaria.status)}`">{{ formatStatus(diaria.status) }}</span>
          </div>

          <div class="diaria-info">
            <div>
              <span>Data</span>
              <strong>{{ formatDate(diaria.dataServico) }}</strong>
            </div>
            <div>
              <span>{{ isContratante ? 'Valor combinado' : 'Seu valor' }}</span>
              <strong v-if="isContratante">{{ formatCurrency(diaria.valorServico) }}</strong>
              <input
                v-else
                :value="getValorServico(diaria)"
                type="number"
                min="0"
                step="10"
                aria-label="Valor do serviço"
                @input="updateValorServico(diaria.id, $event.target.value)"
              />
            </div>
          </div>

          <div class="diaria-actions">
            <button
              v-if="!isContratante && diaria.status === 'PENDENTE'"
              class="btn btn-success btn-small"
              @click="aceitarDiaria(diaria.id)"
            >
              Aceitar
            </button>
            <button
              v-if="isContratante && diaria.status === 'CONCLUIDA'"
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
            <router-link to="/chat" class="btn btn-small">Chat</router-link>
          </div>
        </article>
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

const VALORES_KEY = 'diariasJaValoresServico'
const authStore = useAuthStore()
const diariaStore = useDiariaStore()
const filtroStatus = ref('')
const showModalAvaliacao = ref(false)
const diariaEmAvaliacao = ref(null)
const valoresServico = ref(loadValoresServico())

const isContratante = computed(() => authStore.user?.tipo === 'CONTRATANTE')
const isLoading = computed(() => diariaStore.isLoading)
const error = computed(() => diariaStore.error)
const sourceDiarias = computed(() => isContratante.value ? diariaStore.diarias : diariaStore.diariasPendentes)

const diariasFiltradas = computed(() => {
  if (!filtroStatus.value) return sourceDiarias.value
  return sourceDiarias.value.filter(d => d.status === filtroStatus.value)
})

const formatDate = (date) => format(new Date(date), 'dd MMM yyyy', { locale: ptBR })
const formatCurrency = (value) => new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL'
}).format(Number(value || 0))
const formatStatus = (status) => ({
  PENDENTE: 'Pendente',
  CONFIRMADA: 'Confirmada',
  CONCLUIDA: 'Concluída',
  CANCELADA: 'Cancelada'
}[status] || status)

const getStatusClass = (status) => ({
  PENDENTE: 'info',
  CONFIRMADA: 'success',
  CONCLUIDA: 'success',
  CANCELADA: 'danger'
}[status] || 'info')

const cancelarDiaria = async (id) => {
  if (confirm('Tem certeza que deseja cancelar esta diária?')) {
    await diariaStore.cancelar(id, authStore.user.id)
    await loadDiarias()
  }
}

const aceitarDiaria = async (id) => {
  await diariaStore.aceitar(id, authStore.user.id)
  await loadDiarias()
}

function loadValoresServico() {
  try {
    return JSON.parse(localStorage.getItem(VALORES_KEY)) || {}
  } catch {
    return {}
  }
}

const persistValoresServico = () => {
  localStorage.setItem(VALORES_KEY, JSON.stringify(valoresServico.value))
}

const getValorServico = (diaria) => valoresServico.value[diaria.id] ?? diaria.valorServico ?? 180

const updateValorServico = (diariaId, valor) => {
  valoresServico.value = {
    ...valoresServico.value,
    [diariaId]: Number(valor)
  }
  persistValoresServico()
}

const avaliarDiaria = (id) => {
  diariaEmAvaliacao.value = id
  showModalAvaliacao.value = true
}

const handleAvaliar = async (nota) => {
  await diariaStore.avaliar(diariaEmAvaliacao.value, nota)
  showModalAvaliacao.value = false
}

const loadDiarias = async () => {
  if (isContratante.value) {
    await diariaStore.listarPorContratante(authStore.user.id, 0, 20)
  } else {
    await diariaStore.listarPendentesPrestador(authStore.user.id, 0, 20)
  }
}

onMounted(loadDiarias)
</script>

<style scoped>
.filters {
  margin-bottom: 1rem;
}

.filter-select {
  max-width: 280px;
}

.diarias-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(360px, 1fr));
  gap: 1rem;
}

.diaria-card {
  min-height: 250px;
  background: var(--surface);
  border: 1px solid var(--border-color);
  border-radius: 18px;
  padding: 1.25rem;
  box-shadow: var(--shadow);
  display: flex;
  flex-direction: column;
}

.diaria-top {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
}

.diaria-top p {
  color: var(--muted);
  margin-top: 0.25rem;
}

.diaria-info {
  margin: 1.25rem 0;
  padding: 1rem;
  border-radius: 8px;
  background: var(--soft);
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
}

.diaria-info span {
  display: block;
  color: var(--muted);
  font-size: 0.85rem;
  font-weight: 700;
}

.diaria-info input {
  margin-top: 0.35rem;
  background: white;
  font-weight: 800;
}

.diaria-actions {
  margin-top: auto;
  display: flex;
  flex-wrap: wrap;
  gap: 0.55rem;
}

@media (min-width: 1280px) {
  .diarias-grid {
    grid-template-columns: repeat(auto-fit, minmax(380px, 1fr));
  }
}

@media (max-width: 620px) {
  .diaria-info {
    grid-template-columns: 1fr;
  }
}
</style>
