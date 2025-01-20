<template>
    <footer class="text-center p-3 border-top" :class="footerBg">
      <p class="m-0">
        문의/버그 제보: 
        <a href="mailto:roqkfejrdl@gmail.com">roqkfejrdl@gmail.com</a>
      </p>
    </footer>
  </template>
  
  <script>
  export default {
    name: 'FooterComponent',
    props: ['isDark'], // 다크 모드 여부를 prop으로 받아오고 싶다면
    computed: {
      footerBg() {
        return this.isDark ? 'bg-dark text-white' : 'bg-light'
      }
    }
  }
  </script>
  
  <style scoped>
  /* footer에 대한 추가 스타일 */
  footer {
    min-height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  </style>
  