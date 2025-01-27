<template>
  <div class="recent-reviews">
    <div class="reviews-title">{{ title }}</div>
    <div class="reviews-container">
      <transition-group
          name="slide"
          tag="div"
          class="review-wrapper"
          :style="{ minHeight: wrapperHeight }"
      >
        <div
            v-for="review in displayReview"
            :key="review.summonerReviewSeq"
            class="review-item"
        >
          <div class="review-left">
            <ThumbsUp
                class="thumb-icon"
                :class="{
                 'up': review.isUp,
                 'down': !review.isUp
               }"
            />
          </div>
          <div class="review-content-wrapper">
            <div class="review-main" :class="{ 'center-content': !review.tagDtoList?.length }">
              <div v-if="showProfile" class="review-profile">
                <img
                    :src="review.targetProfileIconUrl"
                    :alt="review.targetProfileIconUrl"
                    class="profile-icon"
                />
                <div class="summoner-info">
                  <span class="summoner-name">{{ review.targetGameName }}</span>
                  <span class="summoner-tag">#{{ review.targetTagLine }}</span>
                </div>
              </div>
              <div v-if="review.tagDtoList?.length" class="review-tags">
                <TagList :tags="review.tagDtoList" size="small" :is-show-count="false"/>
              </div>
              <div class="review-content">
                {{ review.comment }}
              </div>
            </div>
            <div class="review-date">
              {{ formatDate(review.regDt) }}
            </div>
          </div>
        </div>
      </transition-group>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, watch } from 'vue';
import { ThumbsUp } from 'lucide-vue-next';
import type { ReviewRequestDto } from "@/types/review.ts";
import TagList from "@/components/common/TagList.vue";

const props = defineProps<{
  reviews?: ReviewRequestDto[];
  title?: string;
  rollingInterval?: number;
  wrapperHeight?: string;
  showProfile?: boolean;
}>();

onMounted(() => {
  if (props.reviews && props.reviews.length > 0) {
    startRolling();
  }
});

const currentIndex = ref(0);
let timer: number | null = null;

const displayReview = computed(() => {
  if (!props.reviews || props.reviews.length === 0) {
    return [];
  }
  return [props.reviews[currentIndex.value % props.reviews.length]];
});

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}.${month}.${day}`;
};

const getProfileIconUrl = (iconId: string) => {
  return `https://ddragon.leagueoflegends.com/cdn/14.3.1/img/profileicon/${iconId}.png`;
};

const startRolling = () => {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
  if (props.reviews && props.reviews.length > 0) {
    timer = window.setInterval(() => {
      currentIndex.value++;
    }, props.rollingInterval || 3000);
  }
};

watch(
    () => props.reviews,
    (newReviews = []) => {
      if (newReviews.length > 0) {
        startRolling();
      }
    },
    {
      deep: true,
      immediate: true
    }
);

onUnmounted(() => {
  if (timer) clearInterval(timer);
});
</script>

<style scoped>
.recent-reviews {
  margin-top: 32px;
  background: #141414;
  border-radius: 8px;
}

.reviews-title {
  font-size: 15px;
  font-weight: 500;
  color: #fff;
  margin-bottom: 16px;
  text-align: left;
}

.reviews-container {
  position: relative;
  margin-bottom: 2px;
}

.review-wrapper {
  position: relative;
}

.review-item {
  position: absolute;
  width: 100%;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
  padding: 16px;
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 16px;
}

.review-left {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
}

.thumb-icon {
  width: 32px;
  height: 32px;
}

.thumb-icon.up {
  color: #4CAF50;
}

.thumb-icon.down {
  color: #FF5252;
  transform: rotate(180deg);
}

.review-content-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  min-width: 0;
  height: 100%;
}

.review-main {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.review-main.center-content {
  justify-content: center;
}

.review-profile {
  display: flex;
  align-items: center;
  gap: 8px;
  /*margin-bottom: 8px;*/
}

.profile-icon {
  width: 24px;
  height: 24px;
  border-radius: 12px;
}

.summoner-info {
  display: flex;
  align-items: center;
  gap: 4px;
}

.summoner-name {
  color: #ffffff;
  font-size: 14px;
  font-weight: 500;
}

.summoner-tag {
  color: #9e9e9e;
  font-size: 12px;
}

.review-tags {
  display: flex;
  align-items: center;
}

.review-date {
  align-self: start;
  margin-top: 4px;
  color: #9e9e9e;
  font-size: 13px;
  white-space: nowrap;
  flex-shrink: 0;
}

.review-content {
  color: #e0e0e0;
  font-size: 14px;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  text-align: left;
  word-break: break-word;
}

.slide-enter-active,
.slide-leave-active {
  transition: all 0.5s ease;
}

.slide-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.slide-leave-to {
  opacity: 0;
  transform: translateY(-30px);
}
</style>