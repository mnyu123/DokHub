<!-- 우측 사이드바(움짤,업데이트 내역 박스) -->
<template>
    <div class="text-center mt-3 mt-md-0">
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