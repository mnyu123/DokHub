<template>
  <!-- 햄버거 토글 아이콘 -->
  <label for="drawer-left" class="btn btn-ghost lg:hidden">
    <i class="fa-solid fa-bars"></i>
  </label>

  <!-- drawer -->
  <input id="drawer-left" type="checkbox" class="drawer-toggle" />
  <div class="drawer-side z-50">
    <label for="drawer-left" class="drawer-overlay"></label>
    <aside class="menu p-4 w-60 min-h-full bg-base-200 text-base-content">
      <h2 class="font-bold text-xl mb-4">독허브</h2>

      <ul>
        <li>
          <router-link to="/" class="flex items-center gap-2">
            <i class="fa-solid fa-house"></i> 홈
          </router-link>
        </li>
        <li>
          <router-link to="/chat/dokchat" class="flex items-center gap-2">
            <i class="fa-solid fa-message"></i> 독채팅
          </router-link>
        </li>
        <li>
          <!-- $emit 대신 emit 함수 사용 -->
          <button class="flex items-center gap-2" @click="emit('toggle-theme')">
            <i class="fa-solid" :class="isDark ? 'fa-sun' : 'fa-moon'"></i>
            {{ isDark ? '라이트' : '다크' }}
          </button>
        </li>
      </ul>

      <!-- 업데이트 박스 -->
      <UpdateBox class="mt-10"/>
    </aside>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import UpdateBox   from './UpdateBox.vue';
const props = defineProps({ theme:String });
const emit  = defineEmits(['toggle-theme']);
const isDark = computed(() => props.theme === 'dark');
</script>