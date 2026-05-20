<template>
  <div class="container">
    <section class="dashboard-hero" :class="{ provider: !isContratante }">
      <div>
        <span class="pill">{{ isContratante ? 'Contratante' : 'Prestador de serviço' }}</span>
        <h1>{{ isContratante ? 'Encontre prestadores de serviço com clareza' : 'Gerencie seu trabalho em um só lugar' }}</h1>
        <p>
          {{ isContratante
            ? 'Pesquise por categoria, localização, disponibilidade e preço antes de iniciar uma contratação.'
            : 'Atualize seus serviços, contatos, disponibilidade e acompanhe oportunidades sem perder contexto.' }}
        </p>
      </div>
      <router-link v-if="isContratante" to="/diarias/criar" class="btn btn-primary">Pesquisar prestadores</router-link>
      <router-link v-else to="/perfil" class="btn btn-primary">Completar perfil</router-link>
    </section>

    <div v-if="isLoading" class="loading">
      <div class="spinner"></div>
    </div>

    <template v-else>
      <section v-if="isContratante" class="dashboard-section">
        <div class="metrics-grid">
          <div class="metric-card"><i></i><span>Pedidos abertos</span><strong>{{ pendentes }}</strong></div>
          <div class="metric-card"><i></i><span>Diárias confirmadas</span><strong>{{ confirmadas }}</strong></div>
          <div class="metric-card"><i></i><span>Prestadores ativos</span><strong>{{ prestadores.length }}</strong></div>
          <div class="metric-card money-card"><i></i><span>Valor contratado</span><strong>{{ formatCurrency(totalFinanceiro) }}</strong></div>
        </div>

        <section class="card search-panel mt-4">
          <div class="search-header">
            <div>
              <h2>Busca para contratação</h2>
              <p>Filtre por serviço, localização, disponibilidade e faixa de preço.</p>
            </div>
            <router-link to="/diarias/criar" class="btn btn-primary btn-small">Busca completa</router-link>
          </div>

          <div class="filters-row">
            <div class="form-group">
              <label for="buscaPrestador">Prestador ou serviço</label>
              <input v-model="filters.busca" id="buscaPrestador" type="search" placeholder="Nome, serviço ou bairro" />
            </div>
            <div class="form-group">
              <label for="categoria">Categoria</label>
              <select v-model="filters.categoria" id="categoria">
                <option value="">Todas</option>
                <option v-for="categoria in categoriasDisponiveis" :key="categoria" :value="categoria">{{ categoria }}</option>
              </select>
            </div>
            <div class="form-group">
              <label for="valorMaximo">Preço máximo</label>
              <input v-model.number="filters.valorMaximo" id="valorMaximo" type="number" min="0" step="10" placeholder="Ex.: 220" />
            </div>
          </div>

          <div class="provider-list">
            <article v-for="prestador in prestadoresFiltrados" :key="prestador.id" class="provider-card">
              <img :src="prestador.fotoUrl" :alt="`Foto de ${prestador.nome}`" @error="handleImageError" />
              <div>
                <h3>{{ prestador.nome }}</h3>
                <p>{{ prestador.categoriaPrincipal }} · {{ prestador.bairro || 'Sergipe' }}</p>
                <span>{{ prestador.descricao }}</span>
              </div>
              <div class="provider-actions">
                <strong>{{ formatCurrency(prestador.valorBase) }}</strong>
                <small>Disponível</small>
                <router-link to="/diarias/criar" class="btn btn-small">Detalhes</router-link>
              </div>
            </article>
          </div>
        </section>

        <div class="dashboard-grid mt-4">
          <section class="card primary-panel">
            <div class="card-header">
              <h2>Contratações recentes</h2>
              <router-link to="/diarias" class="btn btn-small">Ver todas</router-link>
            </div>
            <div class="work-list">
              <article v-for="diaria in diariasRecentes" :key="diaria.id" class="work-item">
                <div>
                  <h3>{{ diaria.nomeCategoria }}</h3>
                  <p>Prestador de serviço: {{ diaria.nomeContratado }}</p>
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
            <h2>Atalhos do contratante</h2>
            <p>Continue uma busca, revise contatos salvos ou acompanhe conversas abertas.</p>
            <div class="quick-actions">
              <router-link to="/diarias/criar" class="btn btn-primary">Pesquisar</router-link>
              <router-link to="/chat" class="btn">Conversas</router-link>
              <router-link to="/notificacoes" class="btn">Notificações</router-link>
            </div>
          </aside>
        </div>
      </section>

      <section v-else class="dashboard-section">
        <div class="metrics-grid">
          <div class="metric-card"><i></i><span>Oportunidades</span><strong>{{ pendentes }}</strong></div>
          <div class="metric-card"><i></i><span>Na agenda</span><strong>{{ confirmadas }}</strong></div>
          <div class="metric-card"><i></i><span>Concluídas</span><strong>{{ concluidas }}</strong></div>
          <div class="metric-card money-card"><i></i><span>A receber</span><strong>{{ formatCurrency(totalFinanceiro) }}</strong></div>
        </div>

        <div class="provider-grid mt-4">
          <section class="card profile-progress">
            <div class="card-header">
              <h2>Resumo do perfil</h2>
              <router-link to="/perfil" class="btn btn-primary btn-small">Editar</router-link>
            </div>
            <div class="profile-checklist">
              <span><strong>Serviço principal</strong> Faxina residencial</span>
              <span><strong>Área de atuação</strong> Aracaju e região</span>
              <span><strong>Disponibilidade</strong> Segunda a sábado</span>
              <span><strong>Contato</strong> Telefone e chat ativos</span>
            </div>
          </section>

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

          <aside class="card focus-card provider-focus">
            <h2>Gestão do trabalho</h2>
            <p>Mantenha descrição, horário, localização, preço e formas de contato sempre atualizados.</p>
            <div class="quick-actions">
              <router-link to="/perfil" class="btn btn-primary">Perfil de serviço</router-link>
              <router-link to="/chat" class="btn">Conversas</router-link>
              <router-link to="/notificacoes" class="btn">Oportunidades</router-link>
            </div>
          </aside>
        </div>
      </section>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useAuthStore } from '../stores/authStore'
import { useDiariaStore } from '../stores/diariaStore'
import { useUsuarioStore } from '../stores/usuarioStore'
import { format } from 'date-fns'
import { ptBR } from 'date-fns/locale'

const authStore = useAuthStore()
const diariaStore = useDiariaStore()
const usuarioStore = useUsuarioStore()
const isLoading = ref(false)
const filters = reactive({
  busca: '',
  categoria: '',
  valorMaximo: 0
})

const isContratante = computed(() => authStore.user?.tipo === 'CONTRATANTE')
const sourceDiarias = computed(() => isContratante.value ? diariaStore.diarias : diariaStore.diariasPendentes)
const diariasRecentes = computed(() => sourceDiarias.value.slice(0, 5))
const pendentes = computed(() => sourceDiarias.value.filter(d => d.status === 'PENDENTE').length)
const confirmadas = computed(() => sourceDiarias.value.filter(d => d.status === 'CONFIRMADA').length)
const concluidas = computed(() => sourceDiarias.value.filter(d => d.status === 'CONCLUIDA').length)
const totalFinanceiro = computed(() => sourceDiarias.value
  .filter(d => ['CONFIRMADA', 'CONCLUIDA'].includes(d.status))
  .reduce((total, diaria) => total + Number(diaria.valorServico || 0), 0))
const prestadores = computed(() => usuarioStore.prestadores)
const categoriasDisponiveis = computed(() => [...new Set(prestadores.value.map(item => item.categoriaPrincipal).filter(Boolean))])
const prestadoresFiltrados = computed(() => {
  const busca = filters.busca.trim().toLowerCase()

  return prestadores.value.filter((prestador) => {
    const searchable = [
      prestador.nome,
      prestador.bairro,
      prestador.descricao,
      prestador.categoriaPrincipal
    ].join(' ').toLowerCase()
    const matchesSearch = !busca || searchable.includes(busca)
    const matchesCategory = !filters.categoria || prestador.categoriaPrincipal === filters.categoria
    const matchesPrice = !filters.valorMaximo || Number(prestador.valorBase || 0) <= Number(filters.valorMaximo)

    return matchesSearch && matchesCategory && matchesPrice
  }).slice(0, 4)
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

const handleImageError = (event) => {
  event.target.src = 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=600&q=80'
}

onMounted(async () => {
  isLoading.value = true
  if (isContratante.value) {
    await Promise.all([
      diariaStore.listarPorContratante(authStore.user.id, 0, 10),
      usuarioStore.listarPrestadores(0, 100)
    ])
  } else {
    await diariaStore.listarPendentesPrestador(authStore.user.id, 0, 10)
  }
  isLoading.value = false
})
</script>

<style scoped>
.dashboard-hero {
  position: relative;
  overflow: hidden;
  min-height: 260px;
  padding: clamp(1.5rem, 4vw, 3rem);
  margin-bottom: 1.5rem;
  border-radius: 16px;
  background:
    radial-gradient(circle at 82% 18%, rgba(242, 183, 5, 0.24), transparent 15rem),
    linear-gradient(130deg, #075f7a 0%, #168a58 58%, #10202b 100%);
  color: white;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 1rem;
}

.dashboard-hero.provider {
  background:
    radial-gradient(circle at 82% 18%, rgba(242, 183, 5, 0.2), transparent 15rem),
    linear-gradient(130deg, #06364a 0%, #0b6f8f 45%, #168a58 100%);
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
  color: #e3eeee;
  font-size: 1.05rem;
}

.pill {
  display: inline-flex;
  margin-bottom: 0.7rem;
  padding: 0.35rem 0.7rem;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.18);
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
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  min-height: 126px;
  padding: 1.35rem;
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

.metric-card:nth-child(2) i { background: var(--sergipe-green); }
.metric-card:nth-child(3) i { background: var(--sergipe-yellow); }

.metric-card span {
  color: var(--muted);
  font-weight: 700;
}

.metric-card strong {
  display: block;
  margin-top: 0.5rem;
  font-size: 2.25rem;
  color: var(--dark);
}

.money-card {
  background: linear-gradient(145deg, #f3fbf5, #ffffff);
  border-color: #b7e4c8;
}

.money-card strong {
  color: var(--sergipe-green-dark);
  font-size: 1.8rem;
}

.search-header,
.dashboard-grid,
.provider-grid {
  display: grid;
  gap: 1rem;
}

.search-header {
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: start;
  margin-bottom: 1rem;
}

.search-header p,
.focus-card p,
.work-item p,
.provider-card p,
.provider-card span {
  color: var(--muted);
}

.filters-row {
  display: grid;
  grid-template-columns: minmax(260px, 1fr) 220px 180px;
  gap: 1rem;
}

.provider-list,
.work-list,
.profile-checklist,
.quick-actions {
  display: grid;
  gap: 0.75rem;
}

.provider-card,
.work-item {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  gap: 1rem;
  align-items: center;
  padding: 1rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: #fbfcfd;
}

.provider-card img {
  width: 76px;
  height: 76px;
  border-radius: 8px;
  object-fit: cover;
}

.provider-actions,
.work-meta {
  display: grid;
  justify-items: end;
  gap: 0.35rem;
  white-space: nowrap;
}

.provider-actions strong,
.work-meta strong {
  color: var(--primary);
}

.dashboard-grid {
  grid-template-columns: minmax(0, 1.55fr) minmax(320px, 0.65fr);
}

.provider-grid {
  grid-template-columns: minmax(320px, 0.8fr) minmax(0, 1.25fr) minmax(300px, 0.7fr);
}

.focus-card {
  background: linear-gradient(160deg, #fff8df, #ffffff);
  border-color: #f7d868;
}

.provider-focus {
  background: linear-gradient(160deg, #e8f7ef, #ffffff);
  border-color: #b7e4c8;
}

.profile-checklist span {
  padding: 0.85rem;
  border-radius: 8px;
  background: var(--soft);
  color: var(--muted);
}

.profile-checklist strong {
  display: block;
  color: var(--dark);
  margin-bottom: 0.2rem;
}

@media (max-width: 1100px) {
  .dashboard-grid,
  .provider-grid,
  .filters-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .dashboard-hero,
  .provider-card,
  .work-item,
  .search-header {
    align-items: stretch;
    grid-template-columns: 1fr;
  }

  .metrics-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .provider-actions,
  .work-meta {
    justify-items: start;
  }
}

@media (max-width: 620px) {
  .metrics-grid {
    grid-template-columns: 1fr;
  }
}
</style>
