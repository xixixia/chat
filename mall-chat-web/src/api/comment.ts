import { BASE_URL, request } from "./http";

export type CommentItem = {
    id: number;
    postId: number;
    parentId?: number | null;
    userId: number;
    content: string;
    createdAt: string;
};

export type MyCommentItem = {
    id: number;
    postId: number;
    postTitle: string;
    parentId?: number | null;
    userId: number;
    content: string;
    auditStatus?: number;
    createdAt: string;
};

export type CommentCreateRequest = {
    postId: number;
    parentId?: number | null;
    content: string;
};

export type ContentCreateResult = {
    id: number;
    auditStatus: number;
    message: string;
};

export function fetchComments(postId: number) {
    return request<CommentItem[]>(BASE_URL, `/comments?postId=${postId}`);
}

export function fetchMyComments(page = 1, size = 10) {
    const params = new URLSearchParams({ page: String(page), size: String(size) });
    return request<{ total: number; list: MyCommentItem[] }>(BASE_URL, `/comments/me?${params.toString()}`, { auth: true });
}

export function createComment(payload: CommentCreateRequest) {
    return request<ContentCreateResult>(BASE_URL, "/comments", { method: "POST", body: payload, auth: true });
}

export function deleteComment(id: number) {
    return request<boolean>(BASE_URL, `/comments/${id}`, { method: "DELETE", auth: true });
}
