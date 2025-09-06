<template>
  <div class="container mx-auto px-6 lg:px-12 space-y-10 py-8">
    <TabNavigation :selectedTab="selectedTab" @update:tab="selectedTab=$event" />
    <LiveStatus class="my-6" />

    <ChannelSlider :selectedTab="selectedTab" />

    <CustomLinksSlider />

    <ChannelList :selectedTab="selectedTab" :key="selectedTab" />
  </div>
</template>

<script setup>
import { ref }            from 'vue'
import TabNavigation      from './TabNavigation.vue'
import LiveStatus         from './LiveStatus.vue'
import ChannelSlider      from './ChannelSlider.vue'
import CustomLinksSlider  from './CustomLinksSlider.vue'
import ChannelList        from './ChannelList.vue'

const selectedTab = ref('clip')
</script>