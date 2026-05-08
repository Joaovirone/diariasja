<template>
  <div class="modal-overlay" @click="fechar">
    <div class="modal" @click.stop>
      <div class="modal-header">
        <h2>Avaliar Diária</h2>
        <button @click="fechar" class="close-btn">&times;</button>
      </div>

      <form @submit.prevent="handleAvaliar" class="modal-body">
        <div v-if="error" class="alert alert-error">
          {{ error }}
        </div>

        <div class="form-group">
          <label>Nota (1-5 estrelas)</label>
          <div class="stars-input">
            <button
              v-for="i in 5"
              :key="i"
              type="button"
              :class="['star', { active: i <= nota }]"
              @click="nota = i"
            >
              ★
            </button>
          </div>
        </div>

        <div class="modal-footer">
          <button type="button" @click="fechar" class="btn">
            Cancelar
          </button>
          <button type="submit" :disabled="isLoading || nota === 0" class="btn btn-primary">
            {{ isLoading ? 'Avaliando...' : 'Confirmar Avaliação' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  diariaId: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['fechar', 'avaliar'])

const nota = ref(0)
const isLoading = ref(false)
const error = ref(null)

const fechar = () => {
  emit('fechar')
}

const handleAvaliar = async () => {
  if (nota.value === 0) {
    error.value = 'Selecione uma nota'
    return
  }

  isLoading.value = true
  error.value = null

  try {
    emit('avaliar', nota.value)
  } catch (err) {
    error.value = 'Erro ao avaliar diária'
  } finally {
    isLoading.value = false
  }
}
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
  max-width: 400px;
  width: 100%;
  margin: 1rem;
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

.stars-input {
  display: flex;
  gap: 0.5rem;
  margin: 1rem 0;
}

.star {
  background: none;
  border: none;
  font-size: 2rem;
  cursor: pointer;
  color: #ddd;
  transition: color 0.2s;
  padding: 0;
  width: 2.5rem;
  height: 2.5rem;
}

.star:hover,
.star.active {
  color: #ffc107;
}
</style>
