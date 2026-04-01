import { createRouter, createWebHistory } from "vue-router";
import PostListView from "./views/PostListView.vue";
import PostDetailView from "./views/PostDetailView.vue";
import AuthView from "./views/AuthView.vue";
import QqCallbackView from "./views/QqCallbackView.vue";
import ProfileView from "./views/ProfileView.vue";
import NotificationView from "./views/NotificationView.vue";
import { getToken } from "./api";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", component: PostListView },
    { path: "/posts/:id", component: PostDetailView, props: true },
    { path: "/auth", component: AuthView },
    { path: "/auth/qq-callback", component: QqCallbackView },
    { path: "/me", component: ProfileView },
    { path: "/notifications", component: NotificationView }
  ]
});

router.beforeEach((to) => {
  if ((to.path === "/me" || to.path === "/notifications") && !getToken()) {
    return "/auth";
  }
  return true;
});

export default router;
