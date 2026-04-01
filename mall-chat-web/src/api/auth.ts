import { AUTH_URL, request } from "./http";

export type RegisterRequest = {
    username: string;
    password: string;
    nickname: string;
};

export type LoginRequest = {
    username: string;
    password: string;
};

export type SmsLoginRequest = {
    phone: string;
    code: string;
};

export type LoginResponse = {
    userId: number;
    username: string;
    nickname: string;
    token: string;
};

export type ProfileResponse = {
    userId: number;
    username: string;
    nickname: string;
    avatarUrl?: string | null;
};

export type UpdateProfileRequest = {
    nickname: string;
    avatarUrl?: string | null;
};

export type CosStsResponse = {
    tmpSecretId: string;
    tmpSecretKey: string;
    sessionToken: string;
    expiredTime: number;
    bucket: string;
    region: string;
    key: string;
    url: string;
};

export type CosUploadConfigResponse = {
    allowedTypes: string[];
    maxSizeMb: number;
};

export function register(payload: RegisterRequest) {
    return request<boolean>(AUTH_URL, "/auth/register", { method: "POST", body: payload });
}

export function login(payload: LoginRequest) {
    return request<LoginResponse>(AUTH_URL, "/auth/login", { method: "POST", body: payload });
}

export function loginBySms(payload: SmsLoginRequest) {
    return request<LoginResponse>(AUTH_URL, "/auth/login/sms", { method: "POST", body: payload });
}

export function fetchProfile() {
    return request<ProfileResponse>(AUTH_URL, "/auth/me", { auth: true });
}

export function updateProfile(payload: UpdateProfileRequest) {
    return request<ProfileResponse>(AUTH_URL, "/auth/profile", { method: "PUT", body: payload, auth: true });
}

export function fetchCosSts(filename: string, prefix?: string) {
    return request<CosStsResponse>(AUTH_URL, "/auth/cos/sts", { method: "POST", body: { filename, prefix }, auth: true });
}

export function fetchCosConfig() {
    return request<CosUploadConfigResponse>(AUTH_URL, "/auth/cos/config", { auth: true });
}

export async function issueCosUpload(file: File, prefix?: string) {
    const sts = await fetchCosSts(file.name, prefix);
    return sts;
}
