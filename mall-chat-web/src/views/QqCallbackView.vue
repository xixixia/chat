<template>
  <t-card class="section-card" :bordered="false">
    <h2>QQ 登录中...</h2>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-else>请稍候，正在完成登录。</p>
  </t-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { oauthLogin, setAuth } from "../api";

const route = useRoute();
const router = useRouter();
const error = ref("");

onMounted(async () => {
  const code = String(route.query.code || "");
  if (!code) {
    error.value = "缺少授权码";
    return;
  }
  try {
    const res = await oauthLogin("qq", code);
    setAuth(res.token, {
      userId: res.userId,
      username: res.username,
      nickname: res.nickname
    });
    await router.push("/");
  } catch (err) {
    error.value = err instanceof Error ? err.message : "QQ 登录失败";
  }
});
</script>

<style scoped>
.section-card {
  margin-bottom: 16px;
}

.error {
  color: #b91c1c;
}
</style>
