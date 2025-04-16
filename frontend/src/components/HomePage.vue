<!-- 콘텐츠(좌측 사이드바, 중앙의 TabNavigation, LiveStatus, ChannelList, 우측 사이드바)  -->
<template>
    <div class="container">
      <div class="row">
        <!-- 좌측 사이드바 -->
        <div class="col-12 col-md-2">
          <LeftSidebar />
        </div>
        <!-- 중앙 콘텐츠 영역 -->
        <div class="col-12 col-md-8">
          <TabNavigation :selectedTab="selectedTab" @update:tab="selectedTab = $event" />
          <LiveStatus />
          <ChannelList :selectedTab="selectedTab" :key="selectedTab" />
        </div>
        <!-- 우측 사이드바 -->
        <div class="col-12 col-md-2">
          <RightSidebar />
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import LeftSidebar from "@/components/LeftSidebar.vue";
  import TabNavigation from "@/components/TabNavigation.vue";
  import LiveStatus from "@/components/LiveStatus.vue";
  import ChannelList from "@/components/ChannelList.vue";
  import RightSidebar from "@/components/RightSidebar.vue";
  
  export default {
    name: "HomePage",
    components: {
      LeftSidebar,
      TabNavigation,
      LiveStatus,
      ChannelList,
      RightSidebar,
    },
    data() {
      return {
        selectedTab: "clip",
      };
    },
  };
  </script>
  
  <style scoped>
  /* 필요한 스타일 추가 */
  </style>
  