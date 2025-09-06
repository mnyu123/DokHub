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