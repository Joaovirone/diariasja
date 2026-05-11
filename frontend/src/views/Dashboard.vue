<template>
  <div class="container">
    <section class="dashboard-hero" :class="{ worker: !isContratante }">
      <div>
        <span class="pill">{{ isContratante ? 'Contratante' : 'Autônomo' }}</span>
        <h1>{{ isContratante ? 'Encontre o profissional certo para sua diária' : 'Organize seus serviços e aumente seus ganhos' }}</h1>
        <p>
          {{ isContratante
            ? 'Compare autônomos, acompanhe pedidos e centralize conversas antes do atendimento.'
            : 'Veja oportunidades, confirme sua agenda e acompanhe quanto você tem a receber.' }}
        </p>
      </div>
      <router-link v-if="isContratante" to="/diarias/criar" class="btn btn-primary">Buscar autônomos</router-link>
      <router-link v-else to="/diarias" class="btn btn-primary">Ver oportunidades</router-link>
    </section>

    <div v-if="isLoading" class="loading">
      <div class="spinner"></div>
    </div>

    <template v-else>
      <section v-if="isContratante" class="dashboard-section">
        <div class="metrics-grid">
          <div class="metric-card"><i></i><span>Pedidos abertos</span><strong>{{ pendentes }}</strong></div>
          <div class="metric-card"><i></i><span>Diárias confirmadas</span><strong>{{ confirmadas }}</strong></div>
          <div class="metric-card"><i></i><span>Profissionais ativos</span><strong>4</strong></div>
          <div class="metric-card money-card"><i></i><span>Valor contratado</span><strong>{{ formatCurrency(totalFinanceiro) }}</strong></div>
        </div>

        <div class="dashboard-grid mt-4">
          <section class="card primary-panel">
            <div class="card-header">
              <h2>Minhas solicitações</h2>
              <router-link to="/diarias" class="btn btn-small">Ver todas</router-link>
            </div>
            <div class="work-list">
              <article v-for="diaria in diariasRecentes" :key="diaria.id" class="work-item">
                <div>
                  <h3>{{ diaria.nomeCategoria }}</h3>
                  <p>Profissional: {{ diaria.nomeContratado }}</p>
                </div>
                <div class="work-meta">
                  <strong>{{ formatCurrency(diaria.valorServico) }}</strong>
                  <span>{{ formatDate(diaria.dataServico) }}</span>
                  <span :class="`badge badge-${getStatusClass(diaria.status)}`">{{ formatStatus(diaria.status) }}</span>
                </div>
              </article>
            </div>
          </section>

          <aside class="card focus-card">
            <h2>Contratação rápida</h2>
            <p>Filtre por categoria, avaliação e valor para encontrar autônomos disponíveis.</p>
            <div class="quick-actions">
              <router-link to="/diarias/criar" class="btn btn-primary">Solicitar diária</router-link>
              <router-link to="/chat" class="btn">Conversar</router-link>
              <router-link to="/notificacoes" class="btn">Notificações</router-link>
            </div>
          </aside>

          <section class="card notification-preview">
            <div class="card-header"><h2>Resumo do contratante</h2></div>
            <div class="insight-grid">
              <div><strong>Melhor categoria:</strong><span>Faxina residencial</span></div>
              <div><strong>Último contato:</strong><span>Pedro Henrique respondeu no chat</span></div>
              <div><strong>Próxima diária:</strong><span>{{ nextDateLabel }}</span></div>
            </div>
          </section>
        </div>
      </section>

      <section v-else class="dashboard-section">
        <div class="metrics-grid">
          <div class="metric-card"><i></i><span>Oportunidades</span><strong>{{ pendentes }}</strong></div>
          <div class="metric-card"><i></i><span>Na agenda</span><strong>{{ confirmadas }}</strong></div>
          <div class="metric-card"><i></i><span>Concluídas</span><strong>{{ concluidas }}</strong></div>
          <div class="metric-card money-card"><i></i><span>Dinheiro a receber</span><strong>{{ formatCurrency(totalFinanceiro) }}</strong></div>
        </div>

        <div class="dashboard-grid mt-4">
          <section class="card primary-panel">
            <div class="card-header">
              <h2>Agenda e oportunidades</h2>
              <router-link to="/diarias" class="btn btn-small">Ver serviços</router-link>
            </div>
            <div class="work-list">
              <article v-for="diaria in diariasRecentes" :key="diaria.id" class="work-item">
                <div>
                  <h3>{{ diaria.nomeCategoria }}</h3>
                  <p>Contratante: {{ diaria.nomeContratante }}</p>
                </div>
                <div class="work-meta">
                  <strong>{{ formatCurrency(diaria.valorServico) }}</strong>
                  <span>{{ formatDate(diaria.dataServico) }}</span>
                  <span :class="`badge badge-${getStatusClass(diaria.status)}`">{{ formatStatus(diaria.status) }}</span>
                </div>
              </article>
            </div>
          </section>

          <aside class="card focus-card worker-card">
            <h2>Minha operação</h2>
            <p>Atualize valor base, disponibilidade e mantenha o chat ativo para fechar mais serviços.</p>
            <div class="quick-actions">
              <router-link to="/perfil" class="btn btn-primary">Editar perfil</router-link>
              <router-link to="/chat" class="btn">Abrir chat</router-link>
              <router-link to="/notificacoes" class="btn">Oportunidades</router-link>
            </div>
          </aside>

          <section class="card notification-preview">
            <div class="card-header"><h2>Resumo do autônomo</h2></div>
            <div class="insight-grid">
              <div><strong>Valor base:</strong><span>R$ 180,00</span></div>
              <div><strong>Categoria forte:</strong><span>Faxina residencial</span></div>
              <div><strong>Próximo serviço:</strong><span>{{ nextDateLabel }}</span></div>
            </div>
          </section>
        </div>
      </section>
    </template>
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

const isContratante = computed(() => authStore.user?.tipo === 'CONTRATANTE')
const sourceDiarias = computed(() => isContratante.value ? diariaStore.diarias : diariaStore.diariasPendentes)
const diariasRecentes = computed(() => sourceDiarias.value.slice(0, 5))
const pendentes = computed(() => sourceDiarias.value.filter(d => d.status === 'PENDENTE').length)
const confirmadas = computed(() => sourceDiarias.value.filter(d => d.status === 'CONFIRMADA').length)
const concluidas = computed(() => sourceDiarias.value.filter(d => d.status === 'CONCLUIDA').length)
const totalFinanceiro = computed(() => sourceDiarias.value
  .filter(d => ['CONFIRMADA', 'CONCLUIDA'].includes(d.status))
  .reduce((total, diaria) => total + Number(diaria.valorServico || 0), 0))
const nextDateLabel = computed(() => diariasRecentes.value[0]?.dataServico ? formatDate(diariasRecentes.value[0].dataServico) : 'Sem data marcada')

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

onMounted(async () => {
  isLoading.value = true
  if (isContratante.value) {
    await diariaStore.listarPorContratante(authStore.user.id, 0, 10)
  } else {
    await diariaStore.listarPendentesProfissional(authStore.user.id, 0, 10)
  }
  isLoading.value = false
})
</script>

<style scoped>
.dashboard-hero {
  position: relative;
  overflow: hidden;
  min-height: 300px;
  padding: 3rem;
  margin-bottom: 1.5rem;
  border-radius: 24px;
  background:
    radial-gradient(circle at 82% 22%, rgba(255, 122, 61, 0.42), transparent 15rem),
    linear-gradient(130deg, #0e7c72 0%, #0b3f3b 42%, #101828 100%);
  color: white;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 1rem;
}

.dashboard-hero.worker {
  background:
    radial-gradient(circle at 82% 22%, rgba(37, 208, 189, 0.34), transparent 15rem),
    linear-gradient(130deg, #172554 0%, #0e7c72 48%, #101828 100%);
}

.dashboard-hero::after {
  content: "";
  position: absolute;
  right: -5rem;
  bottom: -7rem;
  width: 24rem;
  height: 24rem;
  border-radius: 999px;
  border: 42px solid rgba(255, 255, 255, 0.08);
}

.dashboard-hero > * {
  position: relative;
  z-index: 1;
}

.dashboard-hero h1,
.dashboard-hero p {
  color: white;
}

.dashboard-hero p {
  max-width: 820px;
  margin-top: 0.5rem;
  color: #d1faf4;
  font-size: 1.05rem;
}

.dashboard-hero h1 {
  max-width: 980px;
}

.pill {
  display: inline-flex;
  margin-bottom: 0.7rem;
  padding: 0.35rem 0.7rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(12px);
  font-weight: 800;
  text-transform: uppercase;
  font-size: 0.76rem;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 1rem;
}

.metric-card {
  position: relative;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid var(--border-color);
  border-radius: 18px;
  min-height: 126px;
  padding: 1.45rem;
  box-shadow: var(--shadow);
}

.metric-card i {
  width: 42px;
  height: 6px;
  display: block;
  border-radius: 999px;
  margin-bottom: 0.9rem;
  background: var(--primary);
}

.metric-card:nth-child(2) i { background: #2563eb; }
.metric-card:nth-child(3) i { background: #16a34a; }

.metric-card span {
  color: var(--muted);
  font-weight: 700;
}

.metric-card strong {
  display: block;
  margin-top: 0.5rem;
  font-size: 2.65rem;
  color: var(--dark);
}

.money-card {
  background: linear-gradient(145deg, #ecfdf3, #ffffff);
  border-color: #abefc6;
}

.money-card i { background: var(--accent); }

.money-card strong {
  color: #067647;
  font-size: 2.1rem;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.55fr) minmax(360px, 0.65fr);
  gap: 1rem;
}

.work-list {
  display: grid;
  gap: 0.75rem;
}

.work-item {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  padding: 1.15rem;
  border: 1px solid var(--border-color);
  border-radius: 16px;
  background: #fbfcfd;
}

.work-item p,
.focus-card p {
  color: var(--muted);
  margin-top: 0.25rem;
}

.work-meta {
  display: grid;
  justify-items: end;
  gap: 0.45rem;
  white-space: nowrap;
}

.work-meta strong {
  color: var(--primary);
}

.focus-card {
  background: linear-gradient(160deg, #fff7ed, #ffffff);
  border-color: #fed7aa;
}

.worker-card {
  background: linear-gradient(160deg, #ecfeff, #ffffff);
  border-color: #a5f3fc;
}

.notification-preview {
  grid-column: 1 / -1;
}

.insight-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 1rem;
}

.insight-grid div {
  padding: 1rem;
  border-radius: 16px;
  background: #fbfcfd;
  border: 1px solid var(--border-color);
}

.insight-grid strong {
  display: inline;
}

.insight-grid span {
  display: inline;
  color: var(--muted);
  margin-left: 0.25rem;
}

.quick-actions {
  display: grid;
  gap: 0.65rem;
  margin-top: 1.25rem;
}

@media (max-width: 900px) {
  .dashboard-hero,
  .work-item {
    align-items: stretch;
    flex-direction: column;
  }

  .dashboard-grid,
  .insight-grid {
    grid-template-columns: 1fr;
  }

  .metrics-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .work-meta {
    justify-items: start;
  }
}

@media (max-width: 620px) {
  .dashboard-hero {
    padding: 1.5rem;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
  }
}
</style>
