<template>
  <div class="container">
    <div class="page-header">
      <h1>Solicitar Nova Diária</h1>
    </div>

    <div class="card">
      <form @submit.prevent="handleSubmit">
        <div v-if="error" class="alert alert-error">
          {{ error }}
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="categoria">Categoria de Serviço *</label>
            <select v-model="form.categoriaId" id="categoria" required>
              <option value="">Selecione uma categoria</option>
              <option v-for="cat in categorias" :key="cat.id" :value="cat.id">
                {{ cat.nome }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="contratado">Profissional *</label>
            <select v-model="form.contratadoId" id="contratado" required>
              <option value="">Selecione um profissional</option>
              <option v-for="profissional in profissionais" :key="profissional.id" :value="profissional.id">
                {{ profissional.nome }}
              </option>
            </select>
          </div>
        </div>

        <div class="form-group">
          <label for="dataServico">Data do Serviço *</label>
          <input
            v-model="form.dataServico"
            type="date"
            id="dataServico"
            :min="hoje"
            required
          />
        </div>

        <div class="form-actions">
          <router-link to="/diarias" class="btn">
            Cancelar
          </router-link>
          <button
            type="submit"
            :disabled="isLoading"
            class="btn btn-primary"
          >
            {{ isLoading ? 'Processando...' : 'Solicitar Diária' }}
          </button>
        </div>
      </form>
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
  contratadoId: '',
  dataServico: ''
})

const isLoading = ref(false)
const error = ref(null)
const hoje = new Date().toISOString().slice(0, 10)

const categorias = computed(() => categoriaStore.categorias)
const profissionais = computed(() => usuarioStore.profissionais)

const handleSubmit = async () => {
  isLoading.value = true
  error.value = null

  try {
    if (!authStore.user?.id) {
      throw new Error('Perfil do usuário não carregado')
    }

    const dados = {
      contratanteId: authStore.user.id,
      contratadoId: Number(form.contratadoId),
      categoriaId: Number(form.categoriaId),
      dataServico: form.dataServico
    }

    await diariaStore.solicitar(dados)
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
    usuarioStore.listarProfissionais(0, 100)
  ])
})
</script>

<style scoped>
.page-header {
  margin-bottom: 2rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
  justify-content: flex-end;
}
</style>
