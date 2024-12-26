# TimeChart.vue
<template>
  <div ref="chartContainer" class="chart"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'

const props = withDefaults(defineProps<{
  data: Array<{
    hour: number
    likeCount: number
    dislikeCount: number
  }>
}>(), {
  data: () => Array.from({ length: 24 }, (_, i) => ({
    hour: i,
    likeCount: 0,
    dislikeCount: 0
  }))
})

const chartContainer = ref<HTMLElement | null>(null)
let chart: echarts.ECharts | null = null

const initChart = () => {
  if (!chartContainer.value) return

  chart = echarts.init(chartContainer.value)

  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      backgroundColor: '#1a1a1a',
      borderColor: '#333',
      textStyle: {
        color: '#fff'
      }
    },
    legend: {
      data: ['좋아요', '싫어요'],
      textStyle: {
        color: '#fff'
      },
      top: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: props.data.map(item => `${item.hour}시`),
      axisLine: {
        lineStyle: {
          color: '#666'
        }
      },
      axisLabel: {
        color: '#fff'
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        lineStyle: {
          color: '#666'
        }
      },
      axisLabel: {
        color: '#fff'
      },
      splitLine: {
        lineStyle: {
          color: '#333'
        }
      }
    },
    series: [
      {
        name: '좋아요',
        type: 'bar',
        data: props.data.map(item => item.likeCount),
        itemStyle: {
          color: '#4CAF50'
        }
      },
      {
        name: '싫어요',
        type: 'bar',
        data: props.data.map(item => item.dislikeCount),
        itemStyle: {
          color: '#FF5252'
        }
      }
    ]
  }

  chart.setOption(option)
}

const handleResize = () => {
  chart?.resize()
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  chart?.dispose()
  window.removeEventListener('resize', handleResize)
})

watch(() => props.data, () => {
  initChart()
}, { deep: true })
</script>

<style scoped>
.chart {
  width: 100%;
  height: 100%;
}
</style>