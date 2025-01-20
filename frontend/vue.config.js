const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 3000, // Vue.js 개발 서버 포트 설정
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // Spring Boot 서버 주소
        changeOrigin: true, // 백엔드 서버와 다른 도메인 간의 요청 허용
        pathRewrite: { '^/api': '' }, // '/api'를 제거하고 요청 전달
      },
    },
  },
});
