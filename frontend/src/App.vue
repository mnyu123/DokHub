<template>
  <div id="app">
    <!-- 상단 독쌤 캐릭터 이미지 -->
    <header>
      <img src="@/assets/dokcake.png" alt="독쌤 캐릭터" style="height: 100px;" />
      <!-- 간단한 메뉴 -->
      <nav>
        <button @click="selectedTab = 'clip'">클립</button>
        <button @click="selectedTab = 'song'">노래</button>
        <button @click="selectedTab = 'main'">본채널</button>
      </nav>
    </header>
    
    <!-- 좌우 GIF -->
    <div style="display: flex; justify-content: space-between;">
      <img src="@/assets/left.gif" alt="좌측GIF" style="width: 100px;" />
      <div style="flex: 1; margin: 0 20px;">
        <!-- 채널 리스트 컴포넌트 -->
        <ChannelList :selectedTab="selectedTab" />
      </div>
      <img src="@/assets/right.gif" alt="우측GIF" style="width: 100px;" />
    </div>
  </div>
</template>

<script>
import ChannelList from './components/ChannelList.vue'

export default {
  name: 'App',
  components: {
    ChannelList
  },
  data() {
    return {
      selectedTab: 'clip'
    }
  }
}
</script>

<style>
/* 기본 스타일 */
body {
  margin: 0;
  font-family: Arial, sans-serif;
}
header {
  display: flex;
  align-items: center;
  padding: 10px;
  background: #f4f4f4;
}
nav {
  margin-left: 20px;
}
button {
  margin-right: 10px;
}
</style>
