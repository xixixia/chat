import { AUTH_URL, request } from "./http";

export type QqAuthorizeResponse = {
    url: string;
    state: string;
};

export function getQqAuthorizeUrl() {
    return request<QqAuthorizeResponse>(AUTH_URL, "/oauth/qq/authorize");
}

export function qqLogin(code: string) {
    return request<import("./auth").LoginResponse>(AUTH_URL, `/oauth/qq/login?code=${encodeURIComponent(code)}`);
}

export function getOAuthAuthorizeUrl(provider: string) {
    return request<string>(AUTH_URL, `/oauth/${encodeURIComponent(provider)}/authorize`);
}

export function oauthLogin(provider: string, code: string) {
    return request<import("./auth").LoginResponse>(
        AUTH_URL,
        `/oauth/${encodeURIComponent(provider)}/login?code=${encodeURIComponent(code)}`
    );
}
