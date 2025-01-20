const { defineConfig } = require('@vue/cli-service')
const webpack = require('webpack')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 3000, // Vue.js 개발 서버 포트 설정
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // Spring Boot 서버 주소
        changeOrigin: true, // 백엔드 서버와 다른 도메인 간의 요청 허용
      },
    },
  },
  configureWebpack: {
    plugins: [
      new webpack.DefinePlugin({ // 경고삭제
        __VUE_OPTIONS_API__: true,
        __VUE_PROD_DEVTOOLS__: false,
        __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: false
      })
    ]
  }
});
