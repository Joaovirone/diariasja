import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/authStore'

// Lazy load dos views
const Login = () => import('../views/Login.vue')
const Register = () => import('../views/Register.vue')
const Dashboard = () => import('../views/Dashboard.vue')
const MeusDados = () => import('../views/MeusDados.vue')
const Diarias = () => import('../views/Diarias.vue')
const CriarDiaria = () => import('../views/CriarDiaria.vue')
const Categorias = () => import('../views/Categorias.vue')
const Perfil = () => import('../views/Perfil.vue')
const Chat = () => import('../views/Chat.vue')
const Notificacoes = () => import('../views/Notificacoes.vue')

const routes = [
  {
    path: '/',
    redirect: () => {
      const authStore = useAuthStore()
      return authStore.isLoggedIn ? '/dashboard' : '/login'
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { requiresAuth: false }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/diarias',
    name: 'Diarias',
    component: Diarias,
    meta: { requiresAuth: true }
  },
  {
    path: '/diarias/criar',
    name: 'CriarDiaria',
    component: CriarDiaria,
    meta: { requiresAuth: true }
  },
  {
    path: '/meus-dados',
    name: 'MeusDados',
    component: MeusDados,
    meta: { requiresAuth: true }
  },
  {
    path: '/categorias',
    name: 'Categorias',
    component: Categorias,
    meta: { requiresAuth: true }
  },
  {
    path: '/perfil',
    name: 'Perfil',
    component: Perfil,
    meta: { requiresAuth: true }
  },
  {
    path: '/chat',
    name: 'Chat',
    component: Chat,
    meta: { requiresAuth: true }
  },
  {
    path: '/notificacoes',
    name: 'Notificacoes',
    component: Notificacoes,
    meta: { requiresAuth: true }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const requiresAuth = to.meta.requiresAuth

  if (requiresAuth && !authStore.isLoggedIn) {
    next('/login')
  } else if (!requiresAuth && authStore.isLoggedIn && (to.path === '/login' || to.path === '/register')) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
