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
            <select v-model="form.categoriaServicoId" id="categoria" required>
              <option value="">Selecione uma categoria</option>
              <option v-for="cat in categorias" :key="cat.id" :value="cat.id">
                {{ cat.nome }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="dataServico">Data do Serviço *</label>
            <input
              v-model="form.dataServico"
              type="date"
              id="dataServico"
              required
            />
          </div>
        </div>

        <div class="form-group">
          <label for="descricao">Descrição do Serviço *</label>
          <textarea
            v-model="form.descricao"
            id="descricao"
            placeholder="Descreva o serviço desejado..."
            rows="5"
            required
          ></textarea>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="endereco">Endereço *</label>
            <input
              v-model="form.endereco"
              type="text"
              id="endereco"
              placeholder="Rua, nº, bairro"
              required
            />
          </div>

          <div class="form-group">
            <label for="valor">Valor da Diária (R$) *</label>
            <input
              v-model="form.valor"
              type="number"
              id="valor"
              step="0.01"
              min="0"
              placeholder="0.00"
              required
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="horaInicio">Hora Início *</label>
            <input
              v-model="form.horaInicio"
              type="time"
              id="horaInicio"
              required
            />
          </div>

          <div class="form-group">
            <label for="horaFim">Hora Fim *</label>
            <input
              v-model="form.horaFim"
              type="time"
              id="horaFim"
              required
            />
          </div>
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useDiariaStore } from '../stores/diariaStore'
import { useCategoriaStore } from '../stores/categoriaStore'

const router = useRouter()
const diariaStore = useDiariaStore()
const categoriaStore = useCategoriaStore()

const form = reactive({
  categoriaServicoId: '',
  dataServico: '',
  descricao: '',
  endereco: '',
  valor: '',
  horaInicio: '',
  horaFim: ''
})

const isLoading = ref(false)
const error = ref(null)
const categorias = ref([])

const handleSubmit = async () => {
  isLoading.value = true
  error.value = null

  try {
    const dados = {
      ...form,
      categoriaServicoId: parseInt(form.categoriaServicoId),
      valor: parseFloat(form.valor)
    }

    await diariaStore.solicitar(dados)
    router.push('/diarias')
  } catch (err) {
    error.value = err.response?.data?.message || 'Erro ao solicitar diária'
  } finally {
    isLoading.value = false
  }
}

onMounted(async () => {
  await categoriaStore.listar(0, 100)
  categorias.value = categoriaStore.categorias
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

textarea {
  resize: vertical;
}
</style>
