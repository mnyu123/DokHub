<!-- 우측 사이드바(움짤,업데이트 내역 박스) -->
<template>
    <div class="text-center mt-3 mt-md-0">
      <img src="@/assets/bigcake.gif" alt="우측GIF" class="img-fluid right-gif" />
      <UpdateBox />
    </div>
  </template>
  
  <script>
  import UpdateBox from "@/components/UpdateBox.vue";
  export default {
    name: "RightSidebar",
    components: {
      UpdateBox,
    },
  };
  </script>
  
  <style scoped>
  .right-gif {
    transition: transform 0.3s ease;
  }
  .right-gif:hover {
    transform: scale(1.05);
  }
  </style>
  