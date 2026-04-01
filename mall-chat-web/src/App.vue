<template>
  <div class="page">
    <t-card class="hero-card" :bordered="false">
      <div class="hero">
        <div class="brand">
          <h1>Mall Chat</h1>
          <p class="sub">Forum MVP</p>
        </div>
        <t-space size="small" class="nav-actions">
          <t-button variant="text" class="notify-btn" @click="goTo('/notifications')">
            消息
            <span v-if="unreadCount > 0" class="badge">{{ badgeText }}</span>
          </t-button>
          <t-button variant="text" @click="goTo('/')">帖子</t-button>
          <t-button variant="text" @click="goTo('/me')">个人中心</t-button>
          <t-dropdown
            trigger="click"
            :options="userMenu"
            @click="handleUserMenu"
          >
            <t-button variant="text" class="avatar-btn">
              <t-avatar size="small" :image="currentUser?.avatarUrl || undefined">{{ avatarText }}</t-avatar>
              <span class="avatar-name">{{ nicknameText }}</span>
            </t-button>
          </t-dropdown>
        </t-space>
      </div>
    </t-card>

    <main class="content">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from "vue";
import { RouterView, useRouter } from "vue-router";
import { clearAuth, fetchUnreadCount, getCurrentUser, getToken } from "./api";

const router = useRouter();
const currentUser = ref(getCurrentUser());
const unreadCount = ref(0);
let unreadTimer: number | null = null;

const isAuthed = computed(() => !!getToken());
const nicknameText = computed(() => currentUser.value?.nickname || "访客");
const avatarText = computed(() => nicknameText.value.slice(0, 1).toUpperCase());
const badgeText = computed(() => (unreadCount.value > 99 ? "99+" : String(unreadCount.value)));

const userMenu = computed(() => {
  if (isAuthed.value) {
    return [
      {
        content: `昵称：${currentUser.value?.nickname || "未设置"}`,
        value: "info",
        disabled: true
      },
      {
        content: `账号：${currentUser.value?.username || "-"}`,
        value: "account",
        disabled: true
      },
      { content: "个人中心", value: "me" },
      { content: "退出登录", value: "logout", theme: "error" }
    ];
  }
  return [{ content: "登录/注册", value: "auth" }];
});

function goTo(path: string) {
  if ((path === "/me" || path === "/notifications") && !getToken()) {
    router.push("/auth");
    return;
  }
  router.push(path);
}

function handleUserMenu(value: string | number) {
  if (value === "auth") {
    router.push("/auth");
    return;
  }
  if (value === "me") {
    goTo("/me");
    return;
  }
  if (value === "logout") {
    clearAuth();
    currentUser.value = null;
    router.push("/");
  }
}

function refreshUser() {
  currentUser.value = getCurrentUser();
}

async function refreshUnread() {
  if (!getToken()) {
    unreadCount.value = 0;
    return;
  }
  try {
    unreadCount.value = await fetchUnreadCount();
  } catch {
    // ignore unread count errors
  }
}

onMounted(() => {
  window.addEventListener("mallchat-auth", refreshUser);
  refreshUnread();
  unreadTimer = window.setInterval(refreshUnread, 30000);
});

onBeforeUnmount(() => {
  window.removeEventListener("mallchat-auth", refreshUser);
  if (unreadTimer) {
    window.clearInterval(unreadTimer);
  }
});

watch(
  () => router.currentRoute.value.fullPath,
  () => {
    refreshUser();
    refreshUnread();
  }
);
</script>

<style scoped>
.hero-card {
  padding: 4px;
}

.hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.nav-actions :deep(.t-button) {
  border-radius: 999px;
}

.avatar-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 10px;
  border-radius: 999px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
}

.avatar-name {
  font-size: 13px;
  color: #334155;
}

.notify-btn {
  position: relative;
  padding-right: 18px;
}

.badge {
  position: absolute;
  top: -4px;
  right: 2px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  border-radius: 999px;
  background: #ef4444;
  color: #ffffff;
  font-size: 10px;
  line-height: 16px;
  text-align: center;
}

.sub {
  margin: 4px 0 0;
  color: #64748b;
}

@media (max-width: 700px) {
  .hero {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
