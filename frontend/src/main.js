import { createApp } from 'vue'
import App from './App.vue'
import router from './router';

// Vuetify 관련 패키지 임포트 (Vuetify 3)
import { createVuetify } from 'vuetify';
import 'vuetify/styles';
import * as components from 'vuetify/components';
import * as directives from 'vuetify/directives';

// Bootstrap CSS import
import 'bootstrap/dist/css/bootstrap.min.css'
// (필요하다면 JS도 import)
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

// 글로벌 CSS import (부트스트랩 이후에 불러오기)
import './assets/global.css';

// vuetify 인스턴스 생성
const vuetify = createVuetify({
  components,
  directives,
});

// createApp() 결과를 app 변수에 할당한 후, vuetify 플러그인을 등록하고 마운트합니다.
const app = createApp(App);
app.use(vuetify);
app.use(router);
app.mount('#app');
