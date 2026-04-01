import { BASE_URL, request } from "./http";

export type NotificationItem = {
  id: number;
  userId: number;
  actorId: number;
  actorNickname?: string | null;
  action: "comment" | "reply";
  targetType: "post" | "comment";
  targetId: number;
  postId: number;
  contentSnippet?: string | null;
  isRead: number;
  createdAt: string;
};

export function fetchNotifications(status: "unread" | "all" = "all", page = 1, size = 10) {
  const params = new URLSearchParams({
    status,
    page: String(page),
    size: String(size)
  });
  return request<{ total: number; list: NotificationItem[] }>(
    BASE_URL,
    `/notifications?${params.toString()}`,
    { auth: true }
  );
}

export function fetchUnreadCount() {
  return request<number>(BASE_URL, "/notifications/unread-count", { auth: true });
}

export function markNotificationsRead(ids: number[]) {
  return request<boolean>(BASE_URL, "/notifications/read", { method: "POST", body: { ids }, auth: true });
}

export function markAllNotificationsRead() {
  return request<boolean>(BASE_URL, "/notifications/read/all", { method: "POST", auth: true });
}
