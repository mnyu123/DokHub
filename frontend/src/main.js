import { createApp } from 'vue'
import App from './App.vue'
import router from './router';

// 1.0 이후로 bootstap -> tailwindcss
import "./assets/tailwind.css";      // <— tailwind 전역
import 'swiper/css';                 // 기존 슬라이더 유지

// createApp() 결과를 app 변수에 할당한 후, vuetify 플러그인을 등록하고 마운트합니다.
const app = createApp(App);
app.use(router);
app.mount('#app');