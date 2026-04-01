<template>
  <section class="auth">
    <div class="brand">
      <t-card class="brand-card" :bordered="false">
        <t-tag theme="primary" variant="light" class="badge">Mall Chat</t-tag>
        <h2>欢迎回来</h2>
        <p>登录后即可发布帖子、参与评论与管理内容。</p>
        <t-space direction="vertical" size="small" class="feature-list">
          <div>轻量社区协作</div>
          <div>多服务架构</div>
          <div>安全的 JWT 登录</div>
        </t-space>
      </t-card>
    </div>

    <t-card class="panel" :bordered="false">
      <t-tabs v-model="mode">
        <t-tab-panel value="login" label="登录">
          <t-tabs v-model="loginMode" size="small">
            <t-tab-panel value="password" label="密码登录">
              <t-form class="form" :label-width="80">
                <t-form-item label="用户名">
                  <t-input v-model="loginForm.username" type="text" maxlength="32" placeholder="输入用户名" />
                </t-form-item>
                <t-form-item label="密码">
                  <t-input v-model="loginForm.password" type="password" maxlength="64" placeholder="输入密码" />
                </t-form-item>
              </t-form>
              <t-space size="small" class="actions">
                <t-button theme="primary" :loading="loading" @click="submitLogin">登录</t-button>
                <t-button variant="outline" :loading="loading" @click="loginByQq">QQ 登录</t-button>
                <span v-if="message" class="message" :class="messageType">{{ message }}</span>
              </t-space>
            </t-tab-panel>

            <t-tab-panel value="sms" label="短信登录">
              <t-form class="form" :label-width="80">
                <t-form-item label="手机号">
                  <t-input v-model="smsLoginForm.phone" type="text" placeholder="如 +8613812345678" />
                </t-form-item>
                <t-form-item label="图形码">
                  <t-space size="small">
                    <t-input v-model="smsLoginCaptcha" type="text" placeholder="请输入图形验证码" />
                    <div class="captcha" @click="loadLoginCaptcha">
                      <img v-if="loginCaptcha.image" :src="loginCaptcha.image" alt="captcha" />
                      <span v-else>加载中</span>
                    </div>
                  </t-space>
                </t-form-item>
                <t-form-item label="验证码">
                  <t-space size="small">
                    <t-input v-model="smsLoginForm.code" type="text" placeholder="6 位验证码" />
                    <t-button
                      variant="outline"
                      :disabled="loading || countdownLoginSms > 0"
                      @click="sendLoginSms"
                    >
                      {{ countdownLoginSms > 0 ? `${countdownLoginSms}s` : "发送验证码" }}
                    </t-button>
                  </t-space>
                </t-form-item>
              </t-form>
              <t-space size="small" class="actions">
                <t-button theme="primary" :loading="loading" @click="submitSmsLogin">登录</t-button>
                <t-button variant="outline" :loading="loading" @click="loginByQq">QQ 登录</t-button>
                <span v-if="message" class="message" :class="messageType">{{ message }}</span>
              </t-space>
            </t-tab-panel>
          </t-tabs>
        </t-tab-panel>

        <t-tab-panel value="register" label="注册">
          <t-tabs v-model="regMode" size="small">
            <t-tab-panel value="email" label="邮箱注册">
              <t-form class="form" :label-width="80">
                <t-form-item label="邮箱">
                  <t-input v-model="emailForm.email" type="email" placeholder="请输入邮箱" />
                </t-form-item>
                <t-form-item label="图形码">
                  <t-space size="small">
                    <t-input v-model="emailCaptcha" type="text" placeholder="请输入图形验证码" />
                    <div class="captcha" @click="loadEmailCaptcha">
                      <img v-if="emailCaptchaState.image" :src="emailCaptchaState.image" alt="captcha" />
                      <span v-else>加载中</span>
                    </div>
                  </t-space>
                </t-form-item>
                <t-form-item label="验证码">
                  <t-space size="small">
                    <t-input v-model="emailForm.code" type="text" placeholder="6 位验证码" />
                    <t-button variant="outline" :disabled="loading || countdownEmail > 0" @click="sendEmail">
                      {{ countdownEmail > 0 ? `${countdownEmail}s` : "发送验证码" }}
                    </t-button>
                  </t-space>
                </t-form-item>
                <t-form-item label="密码">
                  <t-input v-model="emailForm.password" type="password" placeholder="至少 6 位" />
                </t-form-item>
                <t-form-item label="昵称">
                  <t-input v-model="emailForm.nickname" type="text" placeholder="展示名称" />
                </t-form-item>
              </t-form>
              <t-space size="small" class="actions">
                <t-button theme="primary" :loading="loading" @click="submitEmailRegister">注册</t-button>
                <span v-if="message" class="message" :class="messageType">{{ message }}</span>
              </t-space>
            </t-tab-panel>

            <t-tab-panel value="sms" label="短信注册">
              <t-form class="form" :label-width="80">
                <t-form-item label="手机号">
                  <t-input v-model="smsForm.phone" type="text" placeholder="如 +8613812345678" />
                </t-form-item>
                <t-form-item label="图形码">
                  <t-space size="small">
                    <t-input v-model="smsCaptcha" type="text" placeholder="请输入图形验证码" />
                    <div class="captcha" @click="loadSmsCaptcha">
                      <img v-if="smsCaptchaState.image" :src="smsCaptchaState.image" alt="captcha" />
                      <span v-else>加载中</span>
                    </div>
                  </t-space>
                </t-form-item>
                <t-form-item label="验证码">
                  <t-space size="small">
                    <t-input v-model="smsForm.code" type="text" placeholder="6 位验证码" />
                    <t-button variant="outline" :disabled="loading || countdownSms > 0" @click="sendSms">
                      {{ countdownSms > 0 ? `${countdownSms}s` : "发送验证码" }}
                    </t-button>
                  </t-space>
                </t-form-item>
                <t-form-item label="密码">
                  <t-input v-model="smsForm.password" type="password" placeholder="至少 6 位" />
                </t-form-item>
                <t-form-item label="昵称">
                  <t-input v-model="smsForm.nickname" type="text" placeholder="展示名称" />
                </t-form-item>
              </t-form>
              <t-space size="small" class="actions">
                <t-button theme="primary" :loading="loading" @click="submitSmsRegister">注册</t-button>
                <span v-if="message" class="message" :class="messageType">{{ message }}</span>
              </t-space>
            </t-tab-panel>
          </t-tabs>
        </t-tab-panel>
      </t-tabs>

      <div class="foot">
        <span>默认使用本地认证服务：`mall-chat-auth`</span>
      </div>
    </t-card>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import {
  getOAuthAuthorizeUrl,
  login,
  loginBySms,
  registerByEmail,
  registerBySms,
  fetchCaptcha,
  sendEmailCode,
  sendSmsCode,
  setAuth
} from "../api";

const mode = ref<"login" | "register">("login");
const loginMode = ref<"password" | "sms">("password");
const regMode = ref<"email" | "sms">("email");
const loading = ref(false);
const message = ref("");
const messageType = ref<"" | "success" | "error">("");
const countdownSms = ref(0);
const countdownEmail = ref(0);
const countdownLoginSms = ref(0);
const loginCaptcha = reactive({ key: "", image: "" });
const smsCaptchaState = reactive({ key: "", image: "" });
const emailCaptchaState = reactive({ key: "", image: "" });
const smsLoginCaptcha = ref("");
const smsCaptcha = ref("");
const emailCaptcha = ref("");

const loginForm = reactive({
  username: "",
  password: ""
});

const smsLoginForm = reactive({
  phone: "",
  code: ""
});

const smsForm = reactive({
  phone: "",
  code: "",
  password: "",
  nickname: ""
});

const emailForm = reactive({
  email: "",
  code: "",
  password: "",
  nickname: ""
});

function resetMessage() {
  message.value = "";
  messageType.value = "";
}

function setMessage(text: string, type: "success" | "error") {
  message.value = text;
  messageType.value = type;
}

function switchMode(next: "login" | "register") {
  mode.value = next;
  resetMessage();
}

function startCountdown(type: "sms" | "email") {
  const target = type === "sms" ? countdownSms : countdownEmail;
  target.value = 60;
  const timer = setInterval(() => {
    target.value -= 1;
    if (target.value <= 0) {
      clearInterval(timer);
    }
  }, 1000);
}

function startLoginSmsCountdown() {
  countdownLoginSms.value = 60;
  const timer = setInterval(() => {
    countdownLoginSms.value -= 1;
    if (countdownLoginSms.value <= 0) {
      clearInterval(timer);
    }
  }, 1000);
}

async function loadLoginCaptcha() {
  try {
    const res = await fetchCaptcha();
    loginCaptcha.key = res.key;
    loginCaptcha.image = res.image;
  } catch {
    loginCaptcha.key = "";
    loginCaptcha.image = "";
  }
}

async function loadSmsCaptcha() {
  try {
    const res = await fetchCaptcha();
    smsCaptchaState.key = res.key;
    smsCaptchaState.image = res.image;
  } catch {
    smsCaptchaState.key = "";
    smsCaptchaState.image = "";
  }
}

async function loadEmailCaptcha() {
  try {
    const res = await fetchCaptcha();
    emailCaptchaState.key = res.key;
    emailCaptchaState.image = res.image;
  } catch {
    emailCaptchaState.key = "";
    emailCaptchaState.image = "";
  }
}

async function sendLoginSms() {
  resetMessage();
  loading.value = true;
  try {
    await sendSmsCode({
      phone: smsLoginForm.phone.trim(),
      captchaKey: loginCaptcha.key,
      captchaCode: smsLoginCaptcha.value.trim()
    });
    setMessage("验证码已发送", "success");
    startLoginSmsCountdown();
    await loadLoginCaptcha();
    smsLoginCaptcha.value = "";
  } catch (err) {
    setMessage(err instanceof Error ? err.message : "发送失败", "error");
    await loadLoginCaptcha();
  } finally {
    loading.value = false;
  }
}

async function sendSms() {
  resetMessage();
  loading.value = true;
  try {
    await sendSmsCode({
      phone: smsForm.phone.trim(),
      captchaKey: smsCaptchaState.key,
      captchaCode: smsCaptcha.value.trim()
    });
    setMessage("验证码已发送", "success");
    startCountdown("sms");
    await loadSmsCaptcha();
    smsCaptcha.value = "";
  } catch (err) {
    setMessage(err instanceof Error ? err.message : "发送失败", "error");
    await loadSmsCaptcha();
  } finally {
    loading.value = false;
  }
}

async function sendEmail() {
  resetMessage();
  loading.value = true;
  try {
    await sendEmailCode({
      email: emailForm.email.trim(),
      captchaKey: emailCaptchaState.key,
      captchaCode: emailCaptcha.value.trim()
    });
    setMessage("验证码已发送", "success");
    startCountdown("email");
    await loadEmailCaptcha();
    emailCaptcha.value = "";
  } catch (err) {
    setMessage(err instanceof Error ? err.message : "发送失败", "error");
    await loadEmailCaptcha();
  } finally {
    loading.value = false;
  }
}

async function submitSmsRegister() {
  resetMessage();
  loading.value = true;
  try {
    await registerBySms({
      phone: smsForm.phone.trim(),
      code: smsForm.code.trim(),
      password: smsForm.password,
      nickname: smsForm.nickname.trim()
    });
    setMessage("注册成功，请登录", "success");
    switchMode("login");
  } catch (err) {
    setMessage(err instanceof Error ? err.message : "注册失败", "error");
  } finally {
    loading.value = false;
  }
}

async function submitEmailRegister() {
  resetMessage();
  loading.value = true;
  try {
    await registerByEmail({
      email: emailForm.email.trim(),
      code: emailForm.code.trim(),
      password: emailForm.password,
      nickname: emailForm.nickname.trim()
    });
    setMessage("注册成功，请登录", "success");
    switchMode("login");
  } catch (err) {
    setMessage(err instanceof Error ? err.message : "注册失败", "error");
  } finally {
    loading.value = false;
  }
}

async function submitLogin() {
  resetMessage();
  loading.value = true;
  try {
    const res = await login({
      username: loginForm.username.trim(),
      password: loginForm.password
    });
    setAuth(res.token, {
      userId: res.userId,
      username: res.username,
      nickname: res.nickname
    });
    setMessage("登录成功", "success");
  } catch (err) {
    setMessage(err instanceof Error ? err.message : "登录失败", "error");
  } finally {
    loading.value = false;
  }
}

async function submitSmsLogin() {
  resetMessage();
  loading.value = true;
  try {
    const res = await loginBySms({
      phone: smsLoginForm.phone.trim(),
      code: smsLoginForm.code.trim()
    });
    setAuth(res.token, {
      userId: res.userId,
      username: res.username,
      nickname: res.nickname
    });
    setMessage("登录成功", "success");
  } catch (err) {
    setMessage(err instanceof Error ? err.message : "登录失败", "error");
  } finally {
    loading.value = false;
  }
}

async function loginByQq() {
  resetMessage();
  loading.value = true;
  try {
    const url = await getOAuthAuthorizeUrl("qq");
    window.location.href = url;
  } catch (err) {
    setMessage(err instanceof Error ? err.message : "QQ 登录失败", "error");
  } finally {
    loading.value = false;
  }
}

onMounted(async () => {
  await loadLoginCaptcha();
  await loadSmsCaptcha();
  await loadEmailCaptcha();
});
</script>

<style scoped>
.auth {
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  gap: 20px;
}

.brand {
  display: flex;
}

.brand-card :deep(.t-card__body) {
  background: linear-gradient(140deg, #0f172a, #1e3a8a 60%, #2563eb);
  color: #fff;
  border-radius: 18px;
  padding: 28px;
  box-shadow: 0 20px 50px rgba(15, 23, 42, 0.35);
}

.badge {
  margin-bottom: 12px;
}

.feature-list {
  margin-top: 12px;
}

.panel :deep(.t-card__body) {
  padding: 22px;
}

.captcha {
  width: 110px;
  height: 40px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
  cursor: pointer;
  overflow: hidden;
}

.captcha img {
  width: 110px;
  height: 40px;
  display: block;
}

.form {
  margin-top: 12px;
}

.actions {
  margin-top: 12px;
}

.message.success {
  color: #15803d;
}

.message.error {
  color: #b91c1c;
}

.foot {
  margin-top: 16px;
  color: #64748b;
  font-size: 12px;
}

@media (max-width: 900px) {
  .auth {
    grid-template-columns: 1fr;
  }
}
</style>
