<template>
  <a
    href="https://www.youtube.com/"
    target="_blank"
    rel="noopener noreferrer"
    class="youtube-attribution inline-flex shrink-0 items-center gap-3 rounded py-2 text-sm focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-primary"
    aria-label="영상 정보 출처: YouTube (새 창)"
  >
    <span class="text-base-content/80">영상 출처</span>
    <img src="@/assets/youtube-logo-dark.png" alt="" class="youtube-logo--dark h-10 w-auto" />
    <img src="@/assets/youtube-logo-light.png" alt="" class="youtube-logo--light h-10 w-auto" />
  </a>
</template>

<style>
.youtube-attribution {
  font-family: inherit;
}

.youtube-attribution:hover span {
  text-decoration: underline;
  text-underline-offset: 3px;
}

.youtube-attribution .youtube-logo--light {
  display: none;
}

[data-theme='light'] .youtube-attribution .youtube-logo--dark {
  display: none;
}

[data-theme='light'] .youtube-attribution .youtube-logo--light {
  display: block;
}
</style>
