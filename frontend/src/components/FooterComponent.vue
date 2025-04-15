<template>
  <footer class="footer text-center p-3 border-top" :class="footerBg">
    <p class="m-0 footer-text">
      문의/버그 제보: 
      <a href="mailto:roqkfejrdl@gmail.com" class="footer-link">roqkfejrdl@gmail.com</a>
    </p>
    <p class="m-0 footer-text">
      독허브는 개인정보와 IP 무단수집을 하지않습니다.
    </p>
  </footer>
</template>

<script>
export default {
  name: "FooterComponent",
  props: ['isDark'],
  computed: {
    footerBg() {
      return this.isDark ? 'bg-dark text-white' : 'bg-light text-dark';
    }
  }
};
</script>

<style scoped>
.footer {
  min-height: 60px;
  display: block; /* 각 문단이 별도 블록 요소로 표시됨 */
  text-align: center;
  padding: 1rem;
  border-top: 1px solid #ccc;
  animation: fadeInUp 0.8s ease-out;
  transition: background-color 0.3s ease, color 0.3s ease;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.footer-text {
  font-size: 0.9rem;
  margin-bottom: 0.5rem; /* 문단 사이 약간의 간격 추가 */
}

.footer-link {
  color: inherit;
  text-decoration: underline;
  transition: color 0.3s ease;
}

.footer-link:hover {
  color: #0d6efd;
}
</style>
