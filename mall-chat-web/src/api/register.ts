import { BASE_URL, request } from "./http";

export type SmsSendRequest = {
    phone: string;
    captchaKey: string;
    captchaCode: string;
};

export type SmsRegisterRequest = {
    phone: string;
    code: string;
    password: string;
    nickname: string;
};

export type EmailSendRequest = {
    email: string;
    captchaKey: string;
    captchaCode: string;
};

export type EmailRegisterRequest = {
    email: string;
    code: string;
    password: string;
    nickname: string;
};

export function sendSmsCode(payload: SmsSendRequest) {
    return request<boolean>(BASE_URL, "/register/sms/send", { method: "POST", body: payload });
}

export function registerBySms(payload: SmsRegisterRequest) {
    return request<boolean>(BASE_URL, "/register/sms", { method: "POST", body: payload });
}

export function sendEmailCode(payload: EmailSendRequest) {
    return request<boolean>(BASE_URL, "/register/email/send", { method: "POST", body: payload });
}

export type CaptchaResponse = {
    key: string;
    image: string;
};

export function fetchCaptcha() {
    return request<CaptchaResponse>(BASE_URL, "/register/captcha", { method: "POST" });
}

export function registerByEmail(payload: EmailRegisterRequest) {
    return request<boolean>(BASE_URL, "/register/email", { method: "POST", body: payload });
}
