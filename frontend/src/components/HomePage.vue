<template>
  <div class="container mx-auto px-6 lg:px-12 space-y-10 py-8">
    <TabNavigation :selectedTab="selectedTab" @update:tab="selectedTab=$event" />
    <LiveStatus class="my-6" />

    <ChannelSlider @loaded-video-ids="sliderVideoIds = $event" />

    <CustomLinksSlider />

    <ClickAnalyticsCard />

    <AiChannelSummaryCard
      v-if="selectedTab !== 'replay'"
      :category="selectedTab"
    />

    <ChannelList
      :selectedTab="selectedTab"
      :excludedVideoIds="selectedTab === 'clip' ? sliderVideoIds : []"
      :key="selectedTab"
    />
  </div>
</template>

<script setup>
import { ref }            from 'vue'
import TabNavigation      from './TabNavigation.vue'
import LiveStatus         from './LiveStatus.vue'
import ChannelSlider      from './ChannelSlider.vue'
import CustomLinksSlider  from './CustomLinksSlider.vue'
import ClickAnalyticsCard from './ClickAnalyticsCard.vue'
import ChannelList        from './ChannelList.vue'
import AiChannelSummaryCard from './AiChannelSummaryCard.vue'

const selectedTab = ref('clip')
const sliderVideoIds = ref([])
</script>
