import { createApp } from 'vue'
import App from './App.vue'
import router from './router';

// 1.0 이후로 bootstap -> tailwindcss
import "./assets/tailwind.css";      // <— tailwind 전역
import 'swiper/css';                 // 기존 슬라이더 유지

const app = createApp(App);
app.use(router);
app.mount('#app');
