<template>
  <div class="container">
    <div class="page-header">
      <h1>Gerenciar Categorias</h1>
      <button @click="showFormCreate = true" class="btn btn-primary">
        + Nova Categoria
      </button>
    </div>

    <div v-if="isLoading" class="loading">
      <div class="spinner"></div>
    </div>

    <div v-else>
      <div v-if="error" class="alert alert-error">
        {{ error }}
      </div>

      <div v-if="categorias.length === 0" class="card text-center">
        <p class="text-muted">Nenhuma categoria cadastrada</p>
      </div>

      <div v-else class="grid grid-2">
        <div
          v-for="categoria in categorias"
          :key="categoria.id"
          class="card categoria-card"
        >
          <h3>{{ categoria.nome }}</h3>
          <p class="text-muted">{{ categoria.descricao }}</p>
          <div class="categoria-footer">
            <button
              @click="editarCategoria(categoria)"
              class="btn btn-small"
            >
              Editar
            </button>
            <button
              @click="deletarCategoria(categoria.id)"
              class="btn btn-danger btn-small"
            >
              Deletar
            </button>
          </div>
        </div>
      </div>
    </div>

    <ModalCategoria
      v-if="showFormCreate || categoriaEmEdicao"
      :categoria="categoriaEmEdicao"
      @fechar="fecharModal"
      @salvar="handleSalvar"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useCategoriaStore } from '../stores/categoriaStore'
import ModalCategoria from '../components/ModalCategoria.vue'

const categoriaStore = useCategoriaStore()
const showFormCreate = ref(false)
const categoriaEmEdicao = ref(null)

const isLoading = computed(() => categoriaStore.isLoading)
const error = computed(() => categoriaStore.error)
const categorias = computed(() => categoriaStore.categorias)

const fecharModal = () => {
  showFormCreate.value = false
  categoriaEmEdicao.value = null
}

const editarCategoria = (categoria) => {
  categoriaEmEdicao.value = categoria
}

const deletarCategoria = async (id) => {
  if (confirm('Tem certeza que deseja deletar esta categoria?')) {
    try {
      await categoriaStore.deletar(id)
    } catch (err) {
      console.error('Erro ao deletar categoria:', err)
    }
  }
}

const handleSalvar = async (dados) => {
  try {
    if (categoriaEmEdicao.value) {
      await categoriaStore.atualizar(categoriaEmEdicao.value.id, dados)
    } else {
      await categoriaStore.criar(dados)
    }
    fecharModal()
  } catch (err) {
    console.error('Erro ao salvar categoria:', err)
  }
}

onMounted(async () => {
  await categoriaStore.listar(0, 50)
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.grid-2 {
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
}

.categoria-card h3 {
  margin-top: 0;
}

.categoria-footer {
  display: flex;
  gap: 0.5rem;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid var(--border-color);
}

.btn-small {
  flex: 1;
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }
}
</style>
