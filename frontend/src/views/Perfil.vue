<template>
  <div class="container">
    <div class="page-header">
      <div>
        <h1>Meu perfil</h1>
        <p>Dados da conta, preferências e informações de serviço.</p>
      </div>
    </div>

    <div v-if="isLoading" class="loading">
      <div class="spinner"></div>
    </div>

    <div v-else class="profile-grid">
      <aside class="profile-summary">
        <span class="profile-avatar">{{ initials }}</span>
        <h2>{{ form.nome }}</h2>
        <p>{{ formatTipo(form.tipo) }}</p>
        <span class="badge badge-success">Ativo</span>
      </aside>

      <section class="card">
        <div class="card-header">
          <h2>Informações pessoais</h2>
        </div>

        <div v-if="error" class="alert alert-error">{{ error }}</div>

        <div class="form-row">
          <div class="form-group">
            <label for="nome">Nome</label>
            <input v-model="form.nome" type="text" id="nome" disabled />
          </div>

          <div class="form-group">
            <label for="email">E-mail</label>
            <input v-model="form.email" type="email" id="email" disabled />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="dataNascimento">Data de nascimento</label>
            <input v-model="form.dataNascimento" type="date" id="dataNascimento" disabled />
          </div>

          <div class="form-group">
            <label for="tipo">Tipo de usuário</label>
            <input :value="formatTipo(form.tipo)" type="text" id="tipo" disabled />
          </div>
        </div>
      </section>

      <section v-if="isPrestador" class="card service-card">
        <div class="card-header">
          <h2>Perfil de prestador de serviço</h2>
          <button type="button" class="btn btn-primary btn-small" @click="saveServiceProfile">Salvar</button>
        </div>

        <div v-if="saved" class="alert alert-success">Perfil de serviço salvo.</div>

        <div class="form-row">
          <div class="form-group">
            <label for="servicoPrincipal">O que você faz</label>
            <input v-model="serviceProfile.servicoPrincipal" type="text" id="servicoPrincipal" placeholder="Ex.: faxina residencial" />
          </div>

          <div class="form-group">
            <label for="categoriaPrincipal">Especialidade</label>
            <select v-model="serviceProfile.categoriaPrincipal" id="categoriaPrincipal">
              <option>Faxina</option>
              <option>Jardinagem</option>
              <option>Manutenção residencial</option>
              <option>Cuidados gerais</option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="areaAtuacao">Área de atuação</label>
            <input v-model="serviceProfile.areaAtuacao" type="text" id="areaAtuacao" placeholder="Ex.: Aracaju, São Cristóvão e região" />
          </div>

          <div class="form-group">
            <label for="localizacao">Localização</label>
            <input v-model="serviceProfile.localizacao" type="text" id="localizacao" placeholder="Bairro ou cidade" />
          </div>
        </div>

        <div class="form-group">
          <label for="descricao">Descrição do atendimento</label>
          <textarea
            v-model="serviceProfile.descricao"
            id="descricao"
            rows="4"
            placeholder="Ex.: levo materiais básicos, atendo apartamentos e casas pequenas."
          ></textarea>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="horarioAtendimento">Horário de atendimento</label>
            <input v-model="serviceProfile.horarioAtendimento" type="text" id="horarioAtendimento" placeholder="Ex.: seg a sáb, 8h às 17h" />
          </div>

          <div class="form-group">
            <label for="telefone">Telefone</label>
            <input v-model="serviceProfile.telefone" type="tel" id="telefone" placeholder="(79) 99999-9999" />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="valorDiaria">Preço ou faixa de preço</label>
            <input v-model="serviceProfile.faixaPreco" type="text" id="valorDiaria" placeholder="Ex.: R$ 180 a R$ 260 por diária" />
          </div>

          <div class="form-group">
            <label for="formasContato">Formas de contato</label>
            <input v-model="serviceProfile.formasContato" type="text" id="formasContato" placeholder="Chat, telefone e WhatsApp" />
          </div>
        </div>

        <div class="availability">
          <label>
            <input v-model="serviceProfile.disponivelFds" type="checkbox" />
            Atendo aos fins de semana
          </label>
          <label>
            <input v-model="serviceProfile.levaMateriais" type="checkbox" />
            Levo materiais básicos
          </label>
          <label>
            <input v-model="serviceProfile.aceitaUrgencia" type="checkbox" />
            Aceito demandas urgentes
          </label>
          <label>
            <input v-model="serviceProfile.atendeForaCidade" type="checkbox" />
            Atendo fora da minha cidade
          </label>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref, onMounted } from 'vue'
import { useUsuarioStore } from '../stores/usuarioStore'

const SERVICE_PROFILE_KEY = 'diariasJaServiceProfile'
const usuarioStore = useUsuarioStore()

const form = reactive({
  nome: '',
  email: '',
  dataNascimento: '',
  tipo: ''
})

const serviceProfile = reactive(loadServiceProfile())
const isLoading = ref(false)
const error = ref(null)
const saved = ref(false)

const isPrestador = computed(() => form.tipo === 'CONTRATADO')
const initials = computed(() => form.nome.split(' ').map(part => part[0]).join('').slice(0, 2).toUpperCase())

function loadServiceProfile() {
  try {
    return JSON.parse(localStorage.getItem(SERVICE_PROFILE_KEY)) || defaultServiceProfile()
  } catch {
    return defaultServiceProfile()
  }
}

function defaultServiceProfile() {
  return {
    valorDiaria: 180,
    faixaPreco: 'R$ 180 a R$ 260 por diária',
    servicoPrincipal: 'Faxina residencial',
    categoriaPrincipal: 'Faxina',
    areaAtuacao: 'Aracaju e região',
    localizacao: 'Centro, Aracaju',
    horarioAtendimento: 'Segunda a sábado, 8h às 17h',
    telefone: '(79) 99999-9999',
    formasContato: 'Chat, telefone e WhatsApp',
    descricao: 'Atendimento residencial com combinados confirmados pelo chat.',
    disponivelFds: true,
    levaMateriais: true,
    aceitaUrgencia: false,
    atendeForaCidade: false
  }
}

const saveServiceProfile = () => {
  localStorage.setItem(SERVICE_PROFILE_KEY, JSON.stringify(serviceProfile))
  saved.value = true
  window.setTimeout(() => {
    saved.value = false
  }, 1800)
}

const formatTipo = (tipo) => {
  if (tipo === 'CONTRATANTE') return 'Contratante'
  if (tipo === 'CONTRATADO') return 'Prestador de serviço'
  return tipo
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
    }
  } catch (err) {
    error.value = usuarioStore.error || 'Erro ao carregar perfil'
  } finally {
    isLoading.value = false
  }
})
</script>

<style scoped>
.profile-grid {
  display: grid;
  grid-template-columns: minmax(320px, 0.32fr) minmax(0, 1fr);
  gap: 1rem;
}

.profile-summary {
  min-height: 100%;
  padding: 1.5rem;
  border-radius: 22px;
  background: linear-gradient(150deg, #06364a, #0f6f47);
  color: white;
  box-shadow: var(--shadow);
}

.profile-summary h2 {
  color: white;
  margin-top: 1rem;
}

.profile-summary p {
  color: #d0d5dd;
  margin: 0.25rem 0 1rem;
}

.profile-avatar {
  width: 74px;
  height: 74px;
  display: grid;
  place-items: center;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--sergipe-green), var(--sergipe-yellow));
  color: #082f36;
  font-size: 1.5rem;
  font-weight: 900;
}

.service-card {
  grid-column: 2;
}

.availability {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.75rem;
}

.availability label {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.85rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: var(--soft);
}

.availability input {
  width: 18px;
  min-height: 18px;
}

@media (max-width: 900px) {
  .profile-grid,
  .service-card {
    display: block;
  }

  .profile-summary {
    margin-bottom: 1rem;
  }

  .availability {
    grid-template-columns: 1fr;
  }
}
</style>
