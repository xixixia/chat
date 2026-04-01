import { BASE_URL, request } from "./http";

export type PageResult<T> = {
    total: number;
    list: T[];
};

export type PostListItem = {
    id: number;
    userId: number;
    title: string;
    content: string;
    createdAt: string;
    categoryId: number;
    auditStatus?: number;
    categoryName: string;
    tags: string[];
};

export type PostDetail = {
    id: number;
    userId: number;
    title: string;
    content: string;
    createdAt: string;
    categoryId: number;
    auditStatus?: number;
    categoryName: string;
    tags: string[];
};

export type PostCreateRequest = {
    categoryId: number;
    title: string;
    content: string;
};

export type ContentCreateResult = {
    id: number;
    auditStatus: number;
    message: string;
};

export function fetchPosts(page = 1, size = 10, title?: string, categoryId?: number | null) {
    const params = new URLSearchParams({ page: String(page), size: String(size) });
    if (title && title.trim()) {
        params.set("title", title.trim());
    }
    if (categoryId) {
        params.set("categoryId", String(categoryId));
    }
    return request<PageResult<PostListItem>>(BASE_URL, `/posts?${params.toString()}`);
}

export function fetchPost(id: number) {
    return request<PostDetail>(BASE_URL, `/posts/${id}`);
}

export function fetchMyPosts(page = 1, size = 10) {
    const params = new URLSearchParams({ page: String(page), size: String(size) });
    return request<PageResult<PostListItem>>(BASE_URL, `/posts/me?${params.toString()}`, { auth: true });
}

export function createPost(payload: PostCreateRequest) {
    return request<ContentCreateResult>(BASE_URL, "/posts", { method: "POST", body: payload, auth: true });
}

export function deletePost(id: number) {
    return request<boolean>(BASE_URL, `/posts/${id}`, { method: "DELETE", auth: true });
}
