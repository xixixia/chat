<template>
  <section class="home-shell">
    <div class="home-grid">
      <t-card class="feed-card" :bordered="false">
        <div class="feed-head">
          <div class="feed-title">
            <h3>Latest Posts</h3>
            <span class="feed-count">Showing {{ posts.length }} items</span>
          </div>
          <t-space size="small" class="pager">
            <t-button variant="outline" :disabled="page <= 1 || loading" @click="prevPage">Prev</t-button>
            <span>{{ page }} / {{ totalPages }}</span>
            <t-button variant="outline" :disabled="page >= totalPages || loading" @click="nextPage">Next</t-button>
          </t-space>
        </div>

        <div class="filters">
          <t-input v-model="searchTitle" placeholder="Search title" clearable @enter="applySearch" />
          <t-select
            v-model="filterCategoryId"
            :options="categoryOptions"
            clearable
            placeholder="Category"
            class="category-filter"
          />
          <t-button theme="primary" variant="outline" :disabled="loading" @click="applySearch">Search</t-button>
          <t-button variant="text" :disabled="loading" @click="resetSearch">Reset</t-button>
          <t-space size="small" class="jump">
            <span>Jump</span>
            <t-input-number v-model="jumpPage" :min="1" :max="totalPages" />
            <t-button variant="outline" :disabled="loading" @click="goToPage">Go</t-button>
          </t-space>
        </div>

        <p v-if="error" class="error">{{ error }}</p>

        <div v-if="posts.length" class="grid">
          <t-card v-for="post in posts" :key="post.id" class="post-card" :bordered="false">
            <div class="post-head">
              <div class="post-title-wrap">
                <h3 class="title">{{ post.title }}</h3>
                <t-space size="small" class="meta">
                  <t-avatar size="small">U</t-avatar>
                  <span>User {{ post.userId }}</span>
                  <span class="dot">·</span>
                  <span>{{ formatDate(post.createdAt) }}</span>
                </t-space>
              </div>
              <t-tag theme="primary" variant="light" class="post-id">#{{ post.id }}</t-tag>
            </div>
            <p class="excerpt">{{ post.content }}</p>
            <div class="post-meta-row">
              <t-tag theme="primary" variant="light" class="category-chip">{{ post.categoryName }}</t-tag>
              <t-space size="small" v-if="post.tags && post.tags.length" class="tag-list">
                <t-tag v-for="tag in post.tags" :key="tag" variant="outline" class="tag-chip">#{{ tag }}</t-tag>
              </t-space>
            </div>
            <t-space size="small" class="item-actions">
              <t-button variant="text" @click="goDetail(post.id)">Details</t-button>
              <t-button v-if="canDelete(post.userId)" theme="danger" variant="outline" @click="removePost(post.id)">
                Delete
              </t-button>
            </t-space>
          </t-card>
        </div>

        <p v-else class="empty">No posts yet</p>
      </t-card>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import { useRouter } from "vue-router";
import {
  deletePost,
  fetchCategories,
  fetchPosts,
  getCurrentUser,
  getToken,
  type CategoryItem,
  type PostListItem
} from "../api";

const router = useRouter();

const posts = ref<PostListItem[]>([]);
const categories = ref<CategoryItem[]>([]);
const total = ref(0);
const loading = ref(false);
const error = ref("");
const page = ref(1);
const size = 10;
const jumpPage = ref(1);
const searchTitle = ref("");
const filterCategoryId = ref<number | null>(null);

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / size)));

const currentUser = ref(getCurrentUser());

type CategoryOption = {
  label: string;
  value: number;
};

const categoryOptions = computed<CategoryOption[]>(() => buildCategoryOptions(categories.value));

function buildCategoryOptions(list: CategoryItem[]): CategoryOption[] {
  const map = new Map<number, CategoryItem[]>();
  for (const item of list) {
    const key = item.parentId ?? 0;
    if (!map.has(key)) {
      map.set(key, []);
    }
    map.get(key)!.push(item);
  }
  for (const items of map.values()) {
    items.sort((a, b) => (b.sort ?? 0) - (a.sort ?? 0));
  }
  const options: CategoryOption[] = [];
  const visit = (parentId: number, prefix: string) => {
    const items = map.get(parentId) || [];
    for (const item of items) {
      options.push({ label: `${prefix}${item.name}`, value: item.id });
      if ((item.level ?? 1) < 3) {
        visit(item.id, `${prefix}- `);
      }
    }
  };
  visit(0, "");
  return options;
}

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

function formatDate(val: string) {
  const date = new Date(val);
  if (Number.isNaN(date.getTime())) {
    return val;
  }
  return date.toLocaleString();
}

function goDetail(id: number) {
  router.push(`/posts/${id}`);
}

watch(
  () => filterCategoryId.value,
  async () => {
    page.value = 1;
    await loadPosts();
  }
);

async function loadPosts() {
  loading.value = true;
  error.value = "";
  refreshUser();
  try {
    const data = await fetchPosts(page.value, size, searchTitle.value, filterCategoryId.value);
    posts.value = data.list;
    total.value = data.total;
    if (page.value > totalPages.value) {
      page.value = totalPages.value;
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : "Failed to load posts";
  } finally {
    loading.value = false;
  }
}

async function loadCategories() {
  try {
    categories.value = await fetchCategories();
  } catch {
    // ignore category load errors
  }
}

async function removePost(id: number) {
  if (!ensureLogin()) {
    return;
  }
  refreshUser();
  if (!confirm("Delete this post?")) {
    return;
  }
  try {
    await deletePost(id);
    await loadPosts();
  } catch (err) {
    error.value = err instanceof Error ? err.message : "Delete failed";
  }
}

async function nextPage() {
  if (page.value >= totalPages.value) {
    return;
  }
  page.value += 1;
  await loadPosts();
}

async function prevPage() {
  if (page.value <= 1) {
    return;
  }
  page.value -= 1;
  await loadPosts();
}

async function goToPage() {
  const target = Math.max(1, Math.min(jumpPage.value || 1, totalPages.value));
  page.value = target;
  await loadPosts();
}

async function applySearch() {
  page.value = 1;
  await loadPosts();
}

async function resetSearch() {
  searchTitle.value = "";
  filterCategoryId.value = null;
  page.value = 1;
  await loadPosts();
}

onMounted(async () => {
  await loadCategories();
  await loadPosts();
});
</script>

<style scoped>
.home-shell {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.home-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 16px;
  align-items: start;
}

.feed-card :deep(.t-card__body) {
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.9);
}

.feed-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.feed-title h3 {
  margin: 0;
}

.feed-count {
  font-size: 12px;
  color: #64748b;
}

.pager {
  font-size: 13px;
  color: #475569;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  padding: 6px 12px;
  border-radius: 999px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.pager span {
  font-weight: 600;
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  padding: 10px;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  background: #f8fafc;
  margin-bottom: 12px;
}

.filters :deep(.t-input) {
  min-width: 200px;
  flex: 1;
}

.category-filter {
  min-width: 180px;
}

.jump {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-left: 8px;
  margin-left: auto;
  border-left: 1px dashed #e2e8f0;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 18px;
  margin-top: 12px;
}

.post-card {
  position: relative;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.post-card::before {
  content: "";
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top right, rgba(59, 130, 246, 0.12), transparent 55%);
  opacity: 0;
  transition: opacity 0.2s ease;
}

.post-card:hover {
  transform: translateY(-2px);
  border-color: #c7d2fe;
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.12);
}

.post-card:hover::before {
  opacity: 1;
}

.post-card :deep(.t-card__body) {
  display: flex;
  flex-direction: column;
  gap: 10px;
  position: relative;
  z-index: 1;
}

.post-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
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

.dot {
  color: #c7d2fe;
}

.post-id {
  font-weight: 600;
  letter-spacing: 0.2px;
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
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 8px;
  border-top: 1px dashed #e2e8f0;
}

.error {
  color: #b91c1c;
}

.empty {
  color: #64748b;
}

@media (max-width: 1024px) {
  .home-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 700px) {
  .feed-head {
    flex-direction: column;
    align-items: flex-start;
  }

  .filters {
    flex-direction: column;
    align-items: stretch;
  }

  .jump {
    margin-left: 0;
    border-left: none;
    padding-left: 0;
  }
}
</style>
