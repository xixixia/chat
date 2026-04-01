<template>
  <t-card class="section-card profile-hero" :bordered="false">
    <div class="profile-header">
      <div class="title-wrap">
        <t-tag theme="primary" variant="light" class="badge">My Space</t-tag>
        <h2>Profile Center</h2>
        <div class="sub">View your posts and replies</div>
      </div>
      <t-space size="small">
        <t-button variant="outline" @click="goHome">Back Home</t-button>
      </t-space>
    </div>
  </t-card>

  <t-card class="section-card profile-card" :bordered="false">
    <div class="profile-card-head">
      <h3>Profile</h3>
      <t-tag theme="primary" variant="light">Account: {{ profile.username || "-" }}</t-tag>
    </div>
    <t-form class="profile-form" :label-width="90">
      <t-form-item label="Nickname">
        <t-input v-model="profileForm.nickname" maxlength="32" placeholder="Enter nickname" />
        <span v-if="profileErrors.nickname" class="error">{{ profileErrors.nickname }}</span>
      </t-form-item>
      <t-form-item label="Avatar URL">
        <t-input v-model="profileForm.avatarUrl" placeholder="https://..." />
      </t-form-item>
      <t-form-item label="Upload Avatar">
        <input
          ref="avatarInput"
          type="file"
          accept="image/*"
          class="avatar-input"
          @change="handleAvatarChange"
        />
        <t-space size="small">
          <t-button variant="outline" :loading="avatarUploading" @click="triggerAvatarUpload">Choose Image</t-button>
          <span class="hint">{{ avatarHint }}</span>
        </t-space>
        <t-progress
          v-if="avatarUploading"
          class="avatar-progress"
          theme="line"
          :percentage="avatarProgress"
          :label="`${avatarProgress}%`"
        />
        <span v-if="avatarMessage" class="message" :class="avatarMessageType">{{ avatarMessage }}</span>
      </t-form-item>
    </t-form>
    <t-space size="small" class="profile-actions">
      <t-button theme="primary" :loading="profileSaving" @click="saveProfile">Save</t-button>
      <span v-if="profileMessage" class="message" :class="profileMessageType">{{ profileMessage }}</span>
    </t-space>
  </t-card>

  <t-card class="section-card" :bordered="false">
    <t-tabs v-model="activeTab">
      <t-tab-panel value="posts" label="My Posts">
        <div class="tab-body">
          <p v-if="postError" class="error">{{ postError }}</p>
          <div v-if="myPosts.length" class="grid">
            <t-card v-for="post in myPosts" :key="post.id" class="post-card" :bordered="false">
              <div class="post-head">
                <div class="post-title-wrap">
                  <h3 class="title">{{ post.title }}</h3>
                  <t-space size="small" class="meta">
                    <t-tag theme="primary" variant="light">#{{ post.id }}</t-tag>
                    <span>{{ formatDate(post.createdAt) }}</span>
                  </t-space>
                  <t-tag
                    v-if="post.auditStatus !== undefined && post.auditStatus !== 1"
                    theme="warning"
                    variant="light"
                    class="audit-tag"
                  >
                    {{ formatAuditStatus(post.auditStatus) }}
                  </t-tag>
                </div>
              </div>
              <p class="excerpt">{{ post.content }}</p>
              <div class="post-meta-row">
                <t-tag theme="primary" variant="light" class="category-chip">{{ post.categoryName }}</t-tag>
                <t-space size="small" v-if="post.tags && post.tags.length" class="tag-list">
                  <t-tag v-for="tag in post.tags" :key="tag" variant="outline" class="tag-chip">#{{ tag }}</t-tag>
                </t-space>
              </div>
              <t-space size="small" class="item-actions">
                <t-button variant="text" @click="goDetail(post.id)">View</t-button>
              </t-space>
            </t-card>
          </div>
          <p v-else class="empty">No posts</p>
          <div class="pager">
            <t-button variant="outline" :disabled="postPage <= 1 || loadingPosts" @click="prevPostPage">Prev</t-button>
            <span>Page {{ postPage }} / {{ postTotalPages }}</span>
            <t-button variant="outline" :disabled="postPage >= postTotalPages || loadingPosts" @click="nextPostPage">Next</t-button>
          </div>
        </div>
      </t-tab-panel>

      <t-tab-panel value="comments" label="My Replies">
        <div class="tab-body">
          <p v-if="commentError" class="error">{{ commentError }}</p>
          <div v-if="myComments.length" class="comment-list">
            <t-card v-for="c in myComments" :key="c.id" class="comment-item" :bordered="false">
              <div class="comment-main">
                <div class="comment-head">
                  <t-tag theme="primary" variant="light">#{{ c.id }}</t-tag>
                  <span class="post-title">Post: {{ c.postTitle }}</span>
                </div>
                <div class="comment-content">{{ c.content }}</div>
                <div class="comment-meta">
                  <span v-if="c.auditStatus !== undefined && c.auditStatus !== 1" class="audit-status">
                    {{ formatAuditStatus(c.auditStatus) }}
                  </span>
                  <span v-if="c.auditStatus !== undefined && c.auditStatus !== 1" class="dot">·</span>
                  <span v-if="c.parentId">Reply #{{ c.parentId }}</span>
                  <span class="dot">·</span>
                  <span>{{ formatDate(c.createdAt) }}</span>
                </div>
              </div>
              <t-space size="small">
                <t-button variant="text" @click="goDetail(c.postId)">View Post</t-button>
              </t-space>
            </t-card>
          </div>
          <p v-else class="empty">No replies</p>
          <div class="pager">
            <t-button variant="outline" :disabled="commentPage <= 1 || loadingComments" @click="prevCommentPage">Prev</t-button>
            <span>Page {{ commentPage }} / {{ commentTotalPages }}</span>
            <t-button variant="outline" :disabled="commentPage >= commentTotalPages || loadingComments" @click="nextCommentPage">Next</t-button>
          </div>
        </div>
      </t-tab-panel>
    </t-tabs>
  </t-card>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import {
  fetchMyComments,
  fetchMyPosts,
  fetchProfile,
  getToken,
  setAuth,
  updateProfile,
  type MyCommentItem,
  type PostListItem
} from "../api";
import { useCosUpload } from "../composables/useCosUpload";

const router = useRouter();

const activeTab = ref<"posts" | "comments">("posts");

const myPosts = ref<PostListItem[]>([]);
const myComments = ref<MyCommentItem[]>([]);

const profile = ref({
  userId: 0,
  username: "",
  nickname: "",
  avatarUrl: ""
});
const profileForm = ref({
  nickname: "",
  avatarUrl: ""
});
const profileSaving = ref(false);
const profileMessage = ref("");
const profileMessageType = ref<"success" | "error" | "">("");
const profileErrors = ref({
  nickname: ""
});
const avatarInput = ref<HTMLInputElement | null>(null);
const avatarHint = ref("Images only, max 5MB");
const {
  uploadFile: uploadAvatarFile,
  uploading: avatarUploading,
  progress: avatarProgress,
  message: avatarMessage,
  messageType: avatarMessageType,
  clearMessage: clearAvatarMessage
} = useCosUpload();

const loadingPosts = ref(false);
const loadingComments = ref(false);
const postError = ref("");
const commentError = ref("");

const postPage = ref(1);
const commentPage = ref(1);
const size = 10;
const postTotal = ref(0);
const commentTotal = ref(0);

const postTotalPages = computed(() => Math.max(1, Math.ceil(postTotal.value / size)));
const commentTotalPages = computed(() => Math.max(1, Math.ceil(commentTotal.value / size)));

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

function formatAuditStatus(status?: number) {
  if (status === 0) {
    return "待审核";
  }
  if (status === 2) {
    return "已拒绝";
  }
  return "已通过";
}

function goHome() {
  router.push("/");
}

function goDetail(id: number) {
  router.push(`/posts/${id}`);
}

function triggerAvatarUpload() {
  if (!ensureLogin()) {
    return;
  }
  avatarInput.value?.click();
}

async function handleAvatarChange(event: Event) {
  const input = event.target as HTMLInputElement | null;
  const file = input?.files?.[0];
  if (!file) {
    return;
  }
  clearAvatarMessage();
  profileMessage.value = "";
  profileMessageType.value = "";
  try {
    const sts = await uploadAvatarFile(file, { prefix: "avatars" });
    const nickname = profileForm.value.nickname.trim();
    const nicknameOk = /^[A-Za-z0-9_\u4e00-\u9fa5]+$/.test(nickname);
    if (nickname.length < 2 || nickname.length > 32 || !nicknameOk) {
      profileErrors.value.nickname = "Nickname must be 2-32 chars (CN/EN/0-9/_)";
      throw new Error("Invalid nickname");
    }
    const updated = await updateProfile({
      nickname,
      avatarUrl: sts.url
    });
    profileForm.value.avatarUrl = updated.avatarUrl || "";
    profile.value = {
      userId: updated.userId,
      username: updated.username,
      nickname: updated.nickname,
      avatarUrl: updated.avatarUrl || ""
    };
    const token = getToken();
    if (token) {
      setAuth(token, {
        userId: updated.userId,
        username: updated.username,
        nickname: updated.nickname,
        avatarUrl: updated.avatarUrl
      });
    }
  } catch (err) {
    if (!avatarMessage.value) {
      profileMessage.value = err instanceof Error ? err.message : "Upload failed";
      profileMessageType.value = "error";
    }
  } finally {
    if (input) {
      input.value = "";
    }
  }
}

async function loadProfile() {
  if (!ensureLogin()) {
    return;
  }
  profileMessage.value = "";
  profileMessageType.value = "";
  try {
    const data = await fetchProfile();
    profile.value = {
      userId: data.userId,
      username: data.username,
      nickname: data.nickname,
      avatarUrl: data.avatarUrl || ""
    };
    profileForm.value = {
      nickname: data.nickname || "",
      avatarUrl: data.avatarUrl || ""
    };
    const token = getToken();
    if (token) {
      setAuth(token, {
        userId: data.userId,
        username: data.username,
        nickname: data.nickname,
        avatarUrl: data.avatarUrl
      });
    }
  } catch (err) {
    profileMessage.value = err instanceof Error ? err.message : "Failed to load profile";
    profileMessageType.value = "error";
  }
}

async function saveProfile() {
  if (!ensureLogin()) {
    return;
  }
  const nickname = profileForm.value.nickname.trim();
  const nicknameOk = /^[A-Za-z0-9_\u4e00-\u9fa5]+$/.test(nickname);
  if (nickname.length < 2 || nickname.length > 32) {
    profileErrors.value.nickname = "Nickname must be 2-32 chars";
    profileMessage.value = "Please fix nickname";
    profileMessageType.value = "error";
    return;
  }
  if (!nicknameOk) {
    profileErrors.value.nickname = "Nickname supports CN/EN/0-9/_ only";
    profileMessage.value = "Please fix nickname";
    profileMessageType.value = "error";
    return;
  }
  profileErrors.value.nickname = "";
  profileSaving.value = true;
  profileMessage.value = "";
  profileMessageType.value = "";
  try {
    const payload = {
      nickname,
      avatarUrl: profileForm.value.avatarUrl.trim() || null
    };
    const data = await updateProfile(payload);
    profile.value = {
      userId: data.userId,
      username: data.username,
      nickname: data.nickname,
      avatarUrl: data.avatarUrl || ""
    };
    const token = getToken();
    if (token) {
      setAuth(token, {
        userId: data.userId,
        username: data.username,
        nickname: data.nickname,
        avatarUrl: data.avatarUrl
      });
    }
    profileMessage.value = "Saved";
    profileMessageType.value = "success";
  } catch (err) {
    profileMessage.value = err instanceof Error ? err.message : "Save failed";
    profileMessageType.value = "error";
  } finally {
    profileSaving.value = false;
  }
}

async function loadMyPosts() {
  if (!ensureLogin()) {
    return;
  }
  loadingPosts.value = true;
  postError.value = "";
  try {
    const data = await fetchMyPosts(postPage.value, size);
    myPosts.value = data.list;
    postTotal.value = data.total;
    if (postPage.value > postTotalPages.value) {
      postPage.value = postTotalPages.value;
    }
  } catch (err) {
    postError.value = err instanceof Error ? err.message : "Failed to load posts";
  } finally {
    loadingPosts.value = false;
  }
}

async function loadMyComments() {
  if (!ensureLogin()) {
    return;
  }
  loadingComments.value = true;
  commentError.value = "";
  try {
    const data = await fetchMyComments(commentPage.value, size);
    myComments.value = data.list;
    commentTotal.value = data.total;
    if (commentPage.value > commentTotalPages.value) {
      commentPage.value = commentTotalPages.value;
    }
  } catch (err) {
    commentError.value = err instanceof Error ? err.message : "Failed to load replies";
  } finally {
    loadingComments.value = false;
  }
}

async function nextPostPage() {
  if (postPage.value >= postTotalPages.value) {
    return;
  }
  postPage.value += 1;
  await loadMyPosts();
}

async function prevPostPage() {
  if (postPage.value <= 1) {
    return;
  }
  postPage.value -= 1;
  await loadMyPosts();
}

async function nextCommentPage() {
  if (commentPage.value >= commentTotalPages.value) {
    return;
  }
  commentPage.value += 1;
  await loadMyComments();
}

async function prevCommentPage() {
  if (commentPage.value <= 1) {
    return;
  }
  commentPage.value -= 1;
  await loadMyComments();
}

onMounted(async () => {
  await loadProfile();
  await loadMyPosts();
  await loadMyComments();
});
</script>

<style scoped>
.section-card {
  margin-bottom: 16px;
}

.profile-hero :deep(.t-card__body) {
  background: linear-gradient(135deg, #f8fafc 0%, #eef2ff 55%, #e2e8f0 100%);
  border: 1px solid #e2e8f0;
  border-radius: 18px;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
}

.profile-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.title-wrap {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.badge {
  width: fit-content;
}

.sub {
  color: #64748b;
  font-size: 13px;
}

.tab-body {
  margin-top: 12px;
}

.profile-card :deep(.t-card__body) {
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.9);
}

.profile-card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.profile-form {
  max-width: 520px;
}

.profile-actions {
  margin-top: 8px;
}

.avatar-input {
  display: none;
}

.profile-form .hint {
  font-size: 12px;
  color: #64748b;
}

.avatar-progress {
  margin-top: 8px;
  width: 240px;
}

.message.success {
  color: #15803d;
}

.message.error {
  color: #b91c1c;
}

.tab-body :deep(.t-tabs__nav-item) {
  font-weight: 600;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 18px;
}

.post-card {
  border: 1px solid #e2e8f0;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.post-card:hover {
  transform: translateY(-2px);
  border-color: #c7d2fe;
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.12);
}

.post-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}

.post-title-wrap {
  min-width: 0;
}

.title {
  font-weight: 600;
  font-size: 17px;
  margin: 0 0 4px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meta {
  font-size: 12px;
  color: #64748b;
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.excerpt {
  color: #334155;
  font-size: 14px;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 60px;
  line-height: 1.55;
}

.post-meta-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.category-chip {
  border-radius: 999px;
  font-weight: 600;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag-chip {
  border-radius: 999px;
  color: #334155;
  border-color: #e2e8f0;
}

.item-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.comment-list {
  display: grid;
  gap: 12px;
}

.comment-item :deep(.t-card__body) {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  border-radius: 14px;
  background: #ffffff;
}

.comment-main {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.comment-head {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.post-title {
  font-weight: 600;
  color: #1f2937;
}

.comment-content {
  color: #334155;
  line-height: 1.6;
}

.comment-meta {
  font-size: 12px;
  color: #64748b;
  display: flex;
  gap: 8px;
  align-items: center;
}

.audit-tag {
  margin-top: 6px;
}

.audit-status {
  color: #b45309;
  font-weight: 600;
}

.dot {
  color: #c7d2fe;
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
}

@media (max-width: 700px) {
  .profile-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .comment-item :deep(.t-card__body) {
    flex-direction: column;
    align-items: flex-start;
  }

  .pager {
    justify-content: flex-start;
  }
}
</style>
