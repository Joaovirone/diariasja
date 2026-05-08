import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/authStore'
import './assets/styles.css'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
useAuthStore().loadToken()
app.use(router)

app.mount('#app')
