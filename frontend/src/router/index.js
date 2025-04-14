import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/components/HomePage.vue';
import ChatPage from '@/components/ChatPage.vue';

const routes = [
  {
    path: '/',
    name: 'HomePage',
    component: HomePage,
  },
  {
    // 프론트엔드 전용 독채팅 페이지 경로
    path: '/chat/dokchat',
    name: 'ChatPage',
    component: ChatPage,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
