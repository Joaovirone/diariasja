<template>
  <div class="container">
    <div class="page-header">
      <div>
        <h1>Solicitar nova diária</h1>
        <p>Encontre um prestador de serviço, filtre por serviço e solicite direto pelo perfil.</p>
      </div>
    </div>

    <div v-if="!isContratante" class="card">
      <strong>Acesso exclusivo para contratantes</strong>
      <p class="text-muted mb-2">Prestadores de serviço acompanham oportunidades pela tela de serviços.</p>
      <router-link to="/diarias" class="btn btn-primary">Ver serviços</router-link>
    </div>

    <template v-else>
      <section class="filters-panel card">
        <div class="form-group">
          <label for="busca">Buscar prestador de serviço</label>
          <input v-model="filters.busca" id="busca" type="search" placeholder="Nome, serviço, bairro ou descrição" />
        </div>

        <div class="form-group">
          <label for="categoriaFiltro">Serviço</label>
          <select v-model="filters.categoria" id="categoriaFiltro">
            <option value="">Todos os serviços</option>
            <option v-for="cat in categorias" :key="cat.id" :value="cat.nome">{{ cat.nome }}</option>
          </select>
        </div>

        <div class="form-group">
          <label for="valorMaximo">Valor máximo</label>
          <input v-model.number="filters.valorMaximo" id="valorMaximo" type="number" min="0" step="10" />
        </div>

        <div class="form-group">
          <label for="disponibilidadeFiltro">Disponibilidade</label>
          <select v-model="filters.disponibilidade" id="disponibilidadeFiltro">
            <option value="">Qualquer dia</option>
            <option value="semana">Dias úteis</option>
            <option value="fim-de-semana">Fim de semana</option>
          </select>
        </div>
      </section>

      <section class="professionals-grid">
        <article
          v-for="prestador in prestadoresFiltrados"
          :key="prestador.id"
          class="professional-card"
          tabindex="0"
          role="button"
          @click="openProfile(prestador)"
          @keydown.enter="openProfile(prestador)"
        >
          <div class="photo-wrap">
            <img :src="prestador.fotoUrl" :alt="`Foto de ${prestador.nome}`" @error="handleImageError" />
            <span class="rating-badge">{{ prestador.avaliacao.toFixed(1) }}</span>
          </div>

          <div class="professional-top">
            <div>
              <h2>{{ prestador.nome }}</h2>
              <p>{{ prestador.categoriaPrincipal }} · {{ prestador.bairro }}</p>
            </div>
          </div>

          <p class="description">{{ prestador.descricao }}</p>

          <div class="professional-meta">
            <span>
              Atendimento
              <strong>{{ prestador.categoriaPrincipal }}</strong>
            </span>
            <span>
              Diária desde
              <strong>{{ formatCurrency(prestador.valorBase) }}</strong>
            </span>
          </div>

          <button type="button" class="btn btn-primary" @click.stop="openModal(prestador)">
            Ver perfil
          </button>
        </article>
      </section>
    </template>

    <div v-if="profileProfessional" class="modal-backdrop" @click.self="closeProfile">
      <section class="profile-modal" role="dialog" aria-modal="true" aria-labelledby="profileTitle">
        <button type="button" class="close-button profile-close" aria-label="Fechar" @click="closeProfile">x</button>
        <img class="profile-cover" :src="profileProfessional.fotoUrl" :alt="`Foto de ${profileProfessional.nome}`" @error="handleImageError" />
        <div class="profile-content">
          <div>
            <span class="eyebrow">{{ profileProfessional.categoriaPrincipal }}</span>
            <h2 id="profileTitle">{{ profileProfessional.nome }}</h2>
            <p>{{ profileProfessional.bairro }}</p>
          </div>

          <div class="profile-stats">
            <span>Avaliação <strong>{{ profileProfessional.avaliacao.toFixed(1) }}</strong></span>
            <span>Diária desde <strong>{{ formatCurrency(profileProfessional.valorBase) }}</strong></span>
            <span>Status <strong>Disponível</strong></span>
          </div>

          <p class="profile-description">{{ profileProfessional.descricao }}</p>

          <div class="profile-tags">
            <span>Atendimento residencial</span>
            <span>Confirma pelo chat</span>
            <span>Agenda flexível</span>
          </div>

          <div class="form-actions">
            <button type="button" class="btn" @click="closeProfile">Voltar</button>
            <button type="button" class="btn btn-primary" @click="openModalFromProfile">
              Solicitar diária
            </button>
          </div>
        </div>
      </section>
    </div>

    <div v-if="selectedProfessional" class="modal-backdrop" @click.self="closeModal">
      <section class="request-modal" role="dialog" aria-modal="true" aria-labelledby="modalTitle">
        <div class="modal-header">
          <div>
            <span class="eyebrow">Solicitando para</span>
            <h2 id="modalTitle">{{ selectedProfessional.nome }}</h2>
          </div>
          <button type="button" class="close-button" aria-label="Fechar" @click="closeModal">x</button>
        </div>

        <form @submit.prevent="handleSubmit">
          <div v-if="error" class="alert alert-error">{{ error }}</div>

          <div class="form-group">
            <label for="categoria">Categoria de serviço *</label>
            <select v-model="form.categoriaId" id="categoria" required>
              <option value="">Selecione uma categoria</option>
              <option v-for="cat in categorias" :key="cat.id" :value="cat.id">{{ cat.nome }}</option>
            </select>
          </div>

          <div class="form-group">
            <label for="dataServico">Data do serviço *</label>
            <input v-model="form.dataServico" type="date" id="dataServico" :min="hoje" required />
          </div>

          <div class="form-group">
            <label for="valorServico">Valor sugerido</label>
            <input v-model.number="form.valorServico" type="number" id="valorServico" min="0" step="10" />
          </div>

          <div class="form-actions">
            <button type="button" class="btn" @click="closeModal">Cancelar</button>
            <button type="submit" :disabled="isLoading" class="btn btn-primary">
              {{ isLoading ? 'Processando...' : 'Solicitar diária' }}
            </button>
          </div>
        </form>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'
import { useDiariaStore } from '../stores/diariaStore'
import { useCategoriaStore } from '../stores/categoriaStore'
import { useUsuarioStore } from '../stores/usuarioStore'

const router = useRouter()
const authStore = useAuthStore()
const diariaStore = useDiariaStore()
const categoriaStore = useCategoriaStore()
const usuarioStore = useUsuarioStore()

const form = reactive({
  categoriaId: '',
  dataServico: '',
  valorServico: 180
})

const filters = reactive({
  busca: '',
  categoria: '',
  valorMaximo: 0,
  disponibilidade: ''
})

const selectedProfessional = ref(null)
const profileProfessional = ref(null)
const isLoading = ref(false)
const error = ref(null)
const hoje = new Date().toISOString().slice(0, 10)

const categorias = computed(() => categoriaStore.categorias)
const prestadores = computed(() => usuarioStore.prestadores)
const isContratante = computed(() => authStore.user?.tipo === 'CONTRATANTE')

const prestadoresFiltrados = computed(() => {
  const busca = filters.busca.trim().toLowerCase()

  return prestadores.value.filter((prestador) => {
    const matchesSearch = !busca || [
      prestador.nome,
      prestador.bairro,
      prestador.descricao,
      prestador.categoriaPrincipal
    ].some(value => String(value || '').toLowerCase().includes(busca))
    const matchesCategory = !filters.categoria || prestador.categoriaPrincipal === filters.categoria
    const matchesPrice = !filters.valorMaximo || Number(prestador.valorBase || 0) <= Number(filters.valorMaximo)
    const matchesAvailability = !filters.disponibilidade || prestador.disponibilidade === filters.disponibilidade

    return matchesSearch && matchesCategory && matchesPrice && matchesAvailability
  })
})

const openModal = (prestador) => {
  selectedProfessional.value = prestador
  profileProfessional.value = null
  const categoria = categorias.value.find(item => item.nome === prestador.categoriaPrincipal)
  form.categoriaId = categoria?.id || ''
  form.valorServico = prestador.valorBase || 180
  form.dataServico = ''
  error.value = null
}

const openProfile = (prestador) => {
  profileProfessional.value = prestador
}

const closeProfile = () => {
  profileProfessional.value = null
}

const openModalFromProfile = () => {
  openModal(profileProfessional.value)
}

const closeModal = () => {
  selectedProfessional.value = null
  error.value = null
}

const handleImageError = (event) => {
  event.target.src = 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=600&q=80'
}

const formatCurrency = (value) => new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL'
}).format(Number(value || 0))

const handleSubmit = async () => {
  isLoading.value = true
  error.value = null

  try {
    if (!authStore.user?.id) {
      throw new Error('Perfil do usuário não carregado')
    }

    await diariaStore.solicitar({
      contratanteId: authStore.user.id,
      contratadoId: Number(selectedProfessional.value.id),
      categoriaId: Number(form.categoriaId),
      dataServico: form.dataServico,
      valorServico: Number(form.valorServico)
    })
    router.push('/diarias')
  } catch (err) {
    error.value = diariaStore.error || err.message || 'Erro ao solicitar diária'
  } finally {
    isLoading.value = false
  }
}

onMounted(async () => {
  await Promise.all([
    categoriaStore.listar(0, 100),
    usuarioStore.listarPrestadores(0, 100)
  ])
})
</script>

<style scoped>
.filters-panel {
  display: grid;
  grid-template-columns: minmax(320px, 1fr) 230px 180px 200px;
  gap: 1rem;
  margin-bottom: 1rem;
}

.filters-panel .form-group {
  margin: 0;
}

.professionals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 1.15rem;
}

.professional-card {
  min-height: 430px;
  overflow: hidden;
  border: 1px solid var(--border-color);
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: var(--shadow);
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}

.professional-card:hover,
.professional-card:focus {
  border-color: var(--primary);
  box-shadow: 0 28px 70px rgba(16, 24, 40, 0.14);
  transform: translateY(-2px);
  outline: none;
}

.photo-wrap {
  position: relative;
  aspect-ratio: 4 / 3;
  height: auto;
  background: var(--soft);
  overflow: hidden;
}

.photo-wrap img {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
  object-position: center top;
}

.rating-badge {
  position: absolute;
  right: 0.85rem;
  bottom: 0.85rem;
  min-width: 54px;
  min-height: 34px;
  display: grid;
  place-items: center;
  border-radius: 999px;
  background: white;
  color: var(--dark);
  font-weight: 900;
  box-shadow: 0 10px 26px rgba(16, 24, 40, 0.2);
}

.professional-top {
  display: flex;
  align-items: center;
  gap: 0.85rem;
  padding: 1.15rem 1.15rem 0;
}

.professional-top h2 {
  font-size: 1.25rem;
}

.professional-top p,
.description {
  color: var(--muted);
}

.description {
  flex: 1;
  padding: 0 1.15rem;
  margin-top: 0.75rem;
}

.professional-meta {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
  padding: 0 1.15rem;
  margin: 1rem 0;
}

.professional-meta span {
  padding: 0.75rem;
  border-radius: 16px;
  background: var(--soft);
  color: var(--muted);
  font-size: 0.86rem;
}

.professional-meta strong {
  display: block;
  color: var(--dark);
  margin-top: 0.2rem;
}

.professional-card > .btn {
  margin: 0 1.15rem 1.15rem;
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  z-index: 20;
  padding: 1rem;
  background: rgba(16, 24, 40, 0.48);
  display: grid;
  place-items: center;
}

.request-modal {
  width: min(760px, 100%);
  padding: 1.25rem;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.97);
  box-shadow: 0 30px 90px rgba(16, 24, 40, 0.28);
}

.profile-modal {
  position: relative;
  width: min(920px, 100%);
  max-height: calc(100vh - 2rem);
  overflow: hidden;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.98);
  box-shadow: 0 30px 90px rgba(16, 24, 40, 0.28);
  display: grid;
  grid-template-columns: minmax(300px, 0.9fr) minmax(0, 1fr);
}

.profile-close {
  position: absolute;
  top: 1rem;
  right: 1rem;
  z-index: 2;
  background: rgba(255, 255, 255, 0.92);
}

.profile-cover {
  width: 100%;
  height: 100%;
  min-height: 520px;
  display: block;
  object-fit: contain;
  object-position: center;
  background: #101828;
}

.profile-content {
  padding: 1.4rem;
  overflow-y: auto;
}

.profile-content h2 {
  font-size: 2rem;
}

.profile-content p {
  color: var(--muted);
}

.profile-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.75rem;
  margin: 1.25rem 0;
}

.profile-stats span {
  padding: 1rem;
  border-radius: 16px;
  background: var(--soft);
  color: var(--muted);
}

.profile-stats strong {
  display: block;
  margin-top: 0.25rem;
  color: var(--dark);
  font-size: 1.15rem;
}

.profile-description {
  font-size: 1.05rem;
}

.profile-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.55rem;
  margin-top: 1rem;
}

.profile-tags span {
  padding: 0.45rem 0.7rem;
  border-radius: 999px;
  background: #ccfbf1;
  color: #115e59;
  font-weight: 800;
  font-size: 0.84rem;
}

.modal-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1rem;
}

.eyebrow {
  color: var(--muted);
  font-size: 0.78rem;
  font-weight: 900;
  text-transform: uppercase;
}

.close-button {
  width: 38px;
  min-height: 38px;
  padding: 0;
  background: var(--soft);
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
}

@media (max-width: 1300px) {
  .professionals-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 980px) {
  .filters-panel,
  .professionals-grid {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }

  .profile-cover {
    height: auto;
    min-height: 280px;
  }

  .profile-stats {
    grid-template-columns: 1fr;
  }

  .profile-modal {
    display: block;
    overflow-y: auto;
  }
}
</style>
