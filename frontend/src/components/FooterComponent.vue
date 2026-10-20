<template>
  <footer class="footer text-center px-4 py-6 sm:p-3 gap-3 text-xs sm:text-sm border-top break-words" :class="footerBg">
    <p class="m-0 footer-text">
      문의/버그 제보:
      <a href="mailto:roqkfejrdl@gmail.com" class="footer-link">roqkfejrdl@gmail.com</a>
    </p>
    <p class="m-0 footer-text">
      영상 클릭은 독허브 자체 통계로 집계되며, 독케익 채팅 기록은 공개됩니다.
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
