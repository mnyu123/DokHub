<template>
  <footer class="footer text-center p-3 border-top" :class="footerBg">
    <p class="m-0 footer-text">
      문의/버그 제보:
      <a href="mailto:roqkfejrdl@gmail.com" class="footer-link">roqkfejrdl@gmail.com</a>
    </p>
  </footer>
</template>

<script>
export default {
  name: 'FooterComponent',
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
  display: flex;
  align-items: center;
  justify-content: center;
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
