<template>
  <t-card class="section-card" :bordered="false">
    <div class="detail-header">
      <t-button variant="text" @click="goBack">返回</t-button>
      <t-button
        v-if="post"
        variant="outline"
        :loading="likingPost"
        @click="toggleLike"
      >
        {{ post.likedByMe ? "Unlike" : "Like" }} ({{ post.likeCount || 0 }})
      </t-button>
      <t-button
        v-if="post && canDelete(post.userId)"
        theme="danger"
        variant="outline"
        :loading="deletingPost"
        @click="removePost"
      >
        删除帖子
      </t-button>
    </div>

    <div v-if="loading">加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="post">
      <h2 class="title">{{ post.title }}</h2>
      <t-space size="small" class="meta">
        <t-tag theme="primary" variant="light">#{{ post.id }}</t-tag>
        <span>用户 {{ post.userId }}</span>
        <span class="dot">·</span>
        <span>{{ post.createdAt }}</span>
      </t-space>
      <div class="detail-tags">
        <t-tag theme="primary" variant="light" class="category-chip">{{ post.categoryName }}</t-tag>
        <t-space size="small" v-if="post.tags && post.tags.length" class="tag-list">
          <t-tag v-for="tag in post.tags" :key="tag" variant="outline" class="tag-chip">#{{ tag }}</t-tag>
        </t-space>
      </div>
      <ToastViewer :content="post.content" />
    </div>
  </t-card>

  <t-card class="section-card" :bordered="false">
    <t-collapse v-model="activePanels">
      <t-collapse-panel value="comments">
        <template #header>
          <div class="comments-header">
            <h3>评论</h3>
            <t-button variant="outline" :loading="commentsLoading" @click.stop="loadComments">刷新</t-button>
          </div>
        </template>

        <p v-if="commentError" class="error">{{ commentError }}</p>
        <div v-if="comments.length" class="comment-list">
          <t-card v-for="c in comments" :key="c.id" class="comment-item" :bordered="false">
            <div class="comment-left">
              <t-space size="small" class="comment-meta">
                <t-avatar size="small">U</t-avatar>
                <span>用户 {{ c.userId }}</span>
                <span v-if="c.parentId" class="reply-meta">回复 #{{ c.parentId }}</span>
                <span class="dot">·</span>
                <span>{{ c.createdAt }}</span>
              </t-space>
              <div class="comment-body" :class="{ reply: !!c.parentId }">{{ c.content }}</div>
            </div>
            <t-space size="small">
              <t-button variant="text" size="small" @click="beginReply(c)">回复</t-button>
              <t-button
                v-if="canDelete(c.userId)"
                theme="danger"
                variant="outline"
                size="small"
                @click="removeComment(c.id)"
              >
                删除
              </t-button>
            </t-space>
          </t-card>
        </div>
        <p v-else class="empty">暂无评论</p>

        <t-divider />

        <t-form class="comment-form" :label-width="80">
          <t-form-item label="评论内容">
            <div v-if="replyTo" class="reply-banner">
              <span>回复 #{{ replyTo.id }}（用户 {{ replyTo.userId }}）</span>
              <t-button variant="text" size="small" @click="clearReply">取消</t-button>
            </div>
            <ToastEditor v-model="commentContent" height="200px" previewStyle="tab" />
            <div class="row-meta">
              <span class="hint">2-500 个字符</span>
              <span class="counter">{{ commentContent.length }}/500</span>
            </div>
            <span v-if="commentErrors.content" class="error">{{ commentErrors.content }}</span>
          </t-form-item>
        </t-form>

        <t-space size="small" class="actions">
          <t-button theme="primary" :loading="commentSubmitting" :disabled="!isCommentValid" @click="submitComment">
            发送
          </t-button>
          <span v-if="commentNotice" class="notice" :class="commentNoticeType">{{ commentNotice }}</span>
        </t-space>
      </t-collapse-panel>
    </t-collapse>
  </t-card>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import ToastEditor from "../components/ToastEditor.vue";
import ToastViewer from "../components/ToastViewer.vue";
import {
  createComment,
  deleteComment,
  deletePost,
  fetchComments,
  fetchPost,
  getCurrentUser,
  getToken,
  likePost,
  type CommentItem,
  type PostDetail,
  unlikePost
} from "../api";

const route = useRoute();
const router = useRouter();
const postId = Number(route.params.id);

const post = ref<PostDetail | null>(null);
const loading = ref(false);
const error = ref("");
const deletingPost = ref(false);
const likingPost = ref(false);

const comments = ref<CommentItem[]>([]);
const commentsLoading = ref(false);
const commentError = ref("");
const commentSubmitting = ref(false);
const commentContent = ref("");
const replyTo = ref<CommentItem | null>(null);
const commentNotice = ref("");
const commentNoticeType = ref<"success" | "warning" | "">("");

const currentUser = ref(getCurrentUser());

const commentErrors = reactive({
  content: ""
});

const activePanels = ref<string[]>([]);

function ensureLogin() {
  if (!getToken()) {
    router.push("/auth");
    return false;
  }
  return true;
}

function refreshUser() {
  currentUser.value = getCurrentUser();
}

function canDelete(authorId: number) {
  return currentUser.value?.userId === authorId;
}

function goBack() {
  router.push("/");
}

function beginReply(target: CommentItem) {
  replyTo.value = target;
  activePanels.value = ["comments"];
}

function clearReply() {
  replyTo.value = null;
}

function validateComment() {
  const content = commentContent.value.trim();
  if (content.length < 2 || content.length > 500) {
    commentErrors.content = "评论长度需为 2-500 个字符";
  } else {
    commentErrors.content = "";
  }
}

const isCommentValid = computed(() => !commentErrors.content);

watch(() => [commentContent.value], validateComment, { immediate: true });

async function loadDetail() {
  loading.value = true;
  error.value = "";
  refreshUser();
  try {
    post.value = await fetchPost(postId);
  } catch (err) {
    error.value = err instanceof Error ? err.message : "加载失败";
  } finally {
    loading.value = false;
  }
}

async function loadComments() {
  commentError.value = "";
  commentsLoading.value = true;
  refreshUser();
  try {
    comments.value = await fetchComments(postId);
  } catch (err) {
    commentError.value = err instanceof Error ? err.message : "加载失败";
  } finally {
    commentsLoading.value = false;
  }
}

async function submitComment() {
  if (!ensureLogin()) {
    return;
  }
  refreshUser();
  validateComment();
  if (!isCommentValid.value) {
    commentError.value = "请先修正校验错误";
    return;
  }
  const content = commentContent.value.trim();
  commentSubmitting.value = true;
  commentNotice.value = "";
  commentNoticeType.value = "";
  try {
    const result = await createComment({
      postId,
      parentId: replyTo.value?.id ?? null,
      content
    });
    commentContent.value = "";
    replyTo.value = null;
    commentNotice.value = result.message || "提交成功";
    commentNoticeType.value = result.auditStatus === 1 ? "success" : "warning";
    if (result.auditStatus === 1) {
      await loadComments();
    }
  } catch (err) {
    commentError.value = err instanceof Error ? err.message : "提交失败";
  } finally {
    commentSubmitting.value = false;
  }
}

async function removeComment(id: number) {
  if (!ensureLogin()) {
    return;
  }
  refreshUser();
  if (!confirm("确定删除该评论？")) {
    return;
  }
  try {
    await deleteComment(id);
    await loadComments();
  } catch (err) {
    commentError.value = err instanceof Error ? err.message : "删除失败";
  }
}

async function removePost() {
  if (!ensureLogin()) {
    return;
  }
  refreshUser();
  if (!post.value) {
    return;
  }
  if (!confirm("确定删除该帖子？")) {
    return;
  }
  deletingPost.value = true;
  try {
    await deletePost(post.value.id);
    await router.push("/");
  } catch (err) {
    error.value = err instanceof Error ? err.message : "删除失败";
  } finally {
    deletingPost.value = false;
  }
}

async function toggleLike() {
  if (!post.value) {
    return;
  }
  if (!ensureLogin()) {
    return;
  }
  if (likingPost.value) {
    return;
  }
  refreshUser();
  likingPost.value = true;
  try {
    const result = post.value.likedByMe ? await unlikePost(post.value.id) : await likePost(post.value.id);
    post.value.likedByMe = result.likedByMe;
    post.value.likeCount = result.likeCount;
  } catch (err) {
    error.value = err instanceof Error ? err.message : "Like failed";
  } finally {
    likingPost.value = false;
  }
}

onMounted(async () => {
  await loadDetail();
  await loadComments();
});
</script>

<style scoped>
.section-card {
  margin-bottom: 16px;
}

.detail-header {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.title {
  margin: 0 0 6px;
}

.meta {
  font-size: 12px;
  color: #64748b;
  margin: 6px 0 12px;
}

.dot {
  color: #c7d2fe;
}

.detail-tags {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
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

.comments-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.comment-list {
  display: grid;
  gap: 10px;
  margin: 12px 0;
}

.comment-item :deep(.t-card__body) {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.comment-meta {
  font-size: 12px;
  color: #64748b;
  flex-wrap: wrap;
}

.reply-meta {
  color: #1f3a8a;
  background: #e9edff;
  padding: 2px 6px;
  border-radius: 999px;
}

.comment-body {
  color: #334155;
  margin-top: 4px;
  line-height: 1.55;
}

.comment-body.reply {
  border-left: 2px solid #cbd5f5;
  padding-left: 8px;
}

.comment-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.comment-form {
  margin-top: 8px;
}

.reply-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #f1f5ff;
  border: 1px solid #dbe3ff;
  border-radius: 10px;
  padding: 6px 10px;
  margin-bottom: 8px;
  font-size: 12px;
  color: #1f3a8a;
}

.row-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.hint {
  font-size: 12px;
  color: #94a3b8;
}

.counter {
  font-size: 12px;
  color: #94a3b8;
}

.actions {
  margin-top: 8px;
}

.notice {
  font-size: 12px;
  color: #15803d;
}

.notice.warning {
  color: #b45309;
}

.error {
  color: #b91c1c;
}

.empty {
  color: #64748b;
}

@media (max-width: 700px) {
  .detail-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .comment-item :deep(.t-card__body) {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
