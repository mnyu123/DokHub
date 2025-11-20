<template>
  <footer class="footer text-center p-3 border-top" :class="footerBg">
    <p class="m-0 footer-text">
      문의/버그 제보:
      <a href="mailto:roqkfejrdl@gmail.com" class="footer-link">roqkfejrdl@gmail.com</a>
    </p>
    <p class="m-0 footer-text">
      독허브는 개인정보와 IP를 무단으로 수집·판매하지 않습니다.
    </p>
    <p class="m-0 footer-text">
      이 서비스는 YouTube API Services를 사용합니다.&nbsp;
      <router-link to="/terms-of-use" class="footer-link">이용약관</router-link>
      <router-link to="/privacy-policy" class="footer-link">개인정보처리방침</router-link>
    </p>
    <p class="m-0 footer-text">
      YouTube 서비스 이용약관:&nbsp;
      <a href="https://www.youtube.com/t/terms" target="_blank" rel="noopener noreferrer"
        class="footer-link">https://www.youtube.com/t/terms</a>
    </p>
    <p class="m-0 footer-text">
      Google 개인정보처리방침:&nbsp;
      <a href="https://policies.google.com/privacy" target="_blank" rel="noopener noreferrer"
        class="footer-link">https://policies.google.com/privacy</a>
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