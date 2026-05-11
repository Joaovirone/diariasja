<template>
  <div class="modal-overlay" @click="fechar">
    <div class="modal" @click.stop>
      <div class="modal-header">
        <h2>{{ categoria ? 'Editar categoria' : 'Nova categoria' }}</h2>
        <button @click="fechar" class="close-btn">&times;</button>
      </div>

      <form @submit.prevent="handleSalvar" class="modal-body">
        <div v-if="error" class="alert alert-error">
          {{ error }}
        </div>

        <div class="form-group">
          <label for="nome">Nome da categoria *</label>
          <input
            v-model="form.nome"
            type="text"
            id="nome"
            placeholder="Ex: Limpeza, Jardinagem..."
            required
          />
        </div>

        <div class="form-group">
          <label for="descricao">Descrição</label>
          <textarea
            v-model="form.descricao"
            id="descricao"
            placeholder="Descreva a categoria..."
            rows="3"
          ></textarea>
        </div>

        <div class="modal-footer">
          <button type="button" @click="fechar" class="btn">
            Cancelar
          </button>
          <button type="submit" :disabled="isLoading" class="btn btn-primary">
            {{ isLoading ? 'Salvando...' : 'Salvar' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'

const props = defineProps({
  categoria: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['fechar', 'salvar'])

const form = reactive({
  nome: '',
  descricao: ''
})

const isLoading = ref(false)
const error = ref(null)

const fechar = () => {
  emit('fechar')
}

const handleSalvar = async () => {
  if (!form.nome.trim()) {
    error.value = 'O nome da categoria é obrigatório'
    return
  }

  isLoading.value = true
  error.value = null

  try {
    emit('salvar', {
      nome: form.nome,
      descricao: form.descricao
    })
  } catch (err) {
    error.value = 'Erro ao salvar categoria'
  } finally {
    isLoading.value = false
  }
}

watch(
  () => props.categoria,
  (novaCategoria) => {
    if (novaCategoria) {
      form.nome = novaCategoria.nome
      form.descricao = novaCategoria.descricao || ''
    } else {
      form.nome = ''
      form.descricao = ''
    }
    error.value = null
  }
)
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 0.75rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  max-width: 500px;
  width: 100%;
  margin: 1rem;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid var(--border-color);
}

.modal-header h2 {
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 2rem;
  cursor: pointer;
  color: #666;
  padding: 0;
  width: 2rem;
  height: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 1.5rem;
}

.modal-footer {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  padding: 1.5rem;
  border-top: 1px solid var(--border-color);
}

textarea {
  resize: vertical;
  min-height: 80px;
}
</style>
