<template>
  <t-card class="section-card" :bordered="false">
    <div class="notify-header">
      <div>
        <h2>消息中心</h2>
        <p class="sub">评论与回复提醒</p>
      </div>
      <t-space size="small">
        <t-button variant="outline" :disabled="!hasUnread" @click="markAllReadAction">全部已读</t-button>
        <t-button variant="text" @click="goHome">返回首页</t-button>
      </t-space>
    </div>
  </t-card>

  <t-card class="section-card" :bordered="false">
    <t-tabs v-model="statusTab">
      <t-tab-panel value="all" label="全部" />
      <t-tab-panel value="unread" label="未读" />
    </t-tabs>

    <p v-if="error" class="error">{{ error }}</p>
    <div v-if="items.length" class="notify-list">
      <t-card v-for="item in items" :key="item.id" class="notify-item" :bordered="false">
        <div class="notify-main">
          <div class="notify-title">
            <t-tag theme="primary" variant="light">#{{ item.id }}</t-tag>
            <span class="actor">{{ actorName(item) }}</span>
            <span class="action">{{ actionLabel(item.action) }}</span>
            <span class="dot">路</span>
            <span class="time">{{ formatDate(item.createdAt) }}</span>
            <t-tag v-if="item.isRead === 0" theme="warning" variant="light" class="unread-tag">未读</t-tag>
          </div>
          <div class="snippet">{{ item.contentSnippet || "（无内容）" }}</div>
        </div>
        <t-space size="small">
          <t-button variant="text" @click="goPost(item.postId)">查看帖子</t-button>
          <t-button
            v-if="item.isRead === 0"
            variant="outline"
            size="small"
            @click="markReadAction(item)"
          >
            标记已读
          </t-button>
        </t-space>
      </t-card>
    </div>
    <p v-else class="empty">{{ loading ? "加载中..." : "暂无消息" }}</p>

    <div class="pager">
      <t-button variant="outline" :disabled="page <= 1 || loading" @click="prevPage">Prev</t-button>
      <span>Page {{ page }} / {{ totalPages }}</span>
      <t-button variant="outline" :disabled="page >= totalPages || loading" @click="nextPage">Next</t-button>
    </div>
  </t-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import { useRouter } from "vue-router";
import {
  fetchNotifications,
  markAllNotificationsRead,
  markNotificationsRead,
  getToken,
  type NotificationItem
} from "../api";

const router = useRouter();

const statusTab = ref<"all" | "unread">("all");
const items = ref<NotificationItem[]>([]);
const loading = ref(false);
const error = ref("");
const page = ref(1);
const size = 10;
const total = ref(0);

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / size)));
const hasUnread = computed(() => items.value.some((item) => item.isRead === 0));

function ensureLogin() {
  if (!getToken()) {
    router.push("/auth");
    return false;
  }
  return true;
}

function formatDate(val: string) {
  const date = new Date(val);
  if (Number.isNaN(date.getTime())) {
    return val;
  }
  return date.toLocaleString();
}

function actionLabel(action: NotificationItem["action"]) {
  return action === "reply" ? "回复了你" : "评论了你的帖子";
}

function actorName(item: NotificationItem) {
  return item.actorNickname || `用户 ${item.actorId}`;
}

function goPost(postId: number) {
  router.push(`/posts/${postId}`);
}

function goHome() {
  router.push("/");
}

async function loadNotifications() {
  if (!ensureLogin()) {
    return;
  }
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchNotifications(statusTab.value, page.value, size);
    items.value = data.list;
    total.value = data.total;
    if (page.value > totalPages.value) {
      page.value = totalPages.value;
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : "加载失败";
  } finally {
    loading.value = false;
  }
}

async function markReadAction(item: NotificationItem) {
  if (!ensureLogin()) {
    return;
  }
  try {
    await markNotificationsRead([item.id]);
    if (statusTab.value === "unread") {
      items.value = items.value.filter((existing) => existing.id !== item.id);
    } else {
      item.isRead = 1;
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : "操作失败";
  }
}

async function markAllReadAction() {
  if (!ensureLogin()) {
    return;
  }
  try {
    await markAllNotificationsRead();
    if (statusTab.value === "unread") {
      items.value = [];
    } else {
      items.value = items.value.map((item) => ({ ...item, isRead: 1 }));
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : "操作失败";
  }
}

async function nextPage() {
  if (page.value >= totalPages.value) {
    return;
  }
  page.value += 1;
  await loadNotifications();
}

async function prevPage() {
  if (page.value <= 1) {
    return;
  }
  page.value -= 1;
  await loadNotifications();
}

watch(
  () => statusTab.value,
  async () => {
    page.value = 1;
    await loadNotifications();
  }
);

onMounted(async () => {
  await loadNotifications();
});
</script>

<style scoped>
.section-card {
  margin-bottom: 16px;
}

.notify-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.sub {
  margin: 4px 0 0;
  color: #64748b;
  font-size: 13px;
}

.notify-list {
  display: grid;
  gap: 12px;
  margin-top: 12px;
}

.notify-item :deep(.t-card__body) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  border-radius: 14px;
  background: #ffffff;
}

.notify-main {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.notify-title {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  font-size: 12px;
  color: #64748b;
}

.actor {
  font-weight: 600;
  color: #1f2937;
}

.action {
  color: #0f172a;
}

.dot {
  color: #c7d2fe;
}

.time {
  color: #64748b;
}

.snippet {
  color: #334155;
  font-size: 14px;
}

.unread-tag {
  margin-left: 4px;
}

.pager {
  margin-top: 14px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  font-size: 13px;
  padding: 8px 12px;
  border: 1px dashed #e2e8f0;
  border-radius: 999px;
  background: #ffffff;
}

.error {
  color: #b91c1c;
}

.empty {
  color: #64748b;
  margin-top: 12px;
}

@media (max-width: 700px) {
  .notify-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .notify-item :deep(.t-card__body) {
    flex-direction: column;
    align-items: flex-start;
  }

  .pager {
    justify-content: flex-start;
  }
}
</style>
