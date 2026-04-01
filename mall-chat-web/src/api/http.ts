export type Result<T> = {
    code: number;
    message: string;
    data: T;
};

export const BASE_URL = "http://localhost:8080";
export const AUTH_URL = "http://localhost:8081";

export function getToken(): string | null {
    return localStorage.getItem("mallchat_token");
}

export type CurrentUser = {
    userId: number;
    username: string;
    nickname: string;
    avatarUrl?: string | null;
};

export function getCurrentUser(): CurrentUser | null {
    const raw = localStorage.getItem("mallchat_user");
    if (!raw) {
        return null;
    }
    try {
        const data = JSON.parse(raw) as CurrentUser;
        if (!data || typeof data.userId !== "number") {
            return null;
        }
        return data;
    } catch {
        return null;
    }
}

export function setAuth(token: string, user: CurrentUser) {
    localStorage.setItem("mallchat_token", token);
    localStorage.setItem("mallchat_user", JSON.stringify(user));
    if (typeof window !== "undefined") {
        window.dispatchEvent(new Event("mallchat-auth"));
    }
}

export function clearAuth() {
    localStorage.removeItem("mallchat_token");
    localStorage.removeItem("mallchat_user");
    if (typeof window !== "undefined") {
        window.dispatchEvent(new Event("mallchat-auth"));
    }
}

function authHeaders() {
    const token = getToken();
    return token ? { Authorization: `Bearer ${token}` } : {};
}

type RequestOptions = {
    method?: string;
    body?: unknown;
    auth?: boolean;
};

export async function request<T>(baseUrl: string, path: string, options: RequestOptions = {}): Promise<T> {
    const headers: Record<string, string> = {
        "Content-Type": "application/json"
    };
    if (options.auth) {
        Object.assign(headers, authHeaders());
    }
    const res = await fetch(`${baseUrl}${path}`, {
        method: options.method ?? "GET",
        headers,
        body: options.body ? JSON.stringify(options.body) : undefined
    });
    let payload: Result<T> | null = null;
    const text = await res.text();
    if (text) {
        try {
            payload = JSON.parse(text) as Result<T>;
        } catch {
            payload = null;
        }
    }
    if (res.status === 401) {
        clearAuth();
        if (window.location.pathname !== "/auth") {
            window.location.href = "/auth";
        }
    }
    if (!res.ok) {
        const message = payload?.message || `HTTP ${res.status}`;
        throw new Error(message);
    }
    if (!payload) {
        throw new Error("响应为空");
    }
    if (payload.code !== 0) {
        throw new Error(payload.message || "请求失败");
    }
    return payload.data;
}
