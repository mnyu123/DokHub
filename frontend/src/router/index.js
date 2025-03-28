// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/components/HomePage.vue';  // 위에서 생성한 HomePage
import ChatPage from '@/components/ChatPage.vue';  // 독채팅 페이지, 필요에 따라 생성

const routes = [
    {
      path: '/',
      name: 'HomePage',
      component: HomePage,
    },
    {
      path: '/api/chat/dokchat',
      name: 'ChatPage',
      component: ChatPage,
    },
  ];
  
  const router = createRouter({
    history: createWebHistory(),
    routes,
  });
  
  export default router;