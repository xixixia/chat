import { ref } from "vue";
import COS from "cos-js-sdk-v5";
import { fetchCosConfig, issueCosUpload, type CosStsResponse } from "../api";

type CosConfig = {
    allowedTypes: string[];
    maxSizeMb: number;
};

type UploadOptions = {
    prefix?: string;
    allowedTypes?: string[];
    maxSizeMb?: number;
    successMessage?: string;
};

const COS_CONFIG_KEY = "mallchat_cos_config";
const COS_CONFIG_TTL = 12 * 60 * 60 * 1000;

function getErrorMessage(err: unknown, fallback: string) {
    if (err instanceof Error && err.message) {
        return err.message;
    }
    return fallback;
}

export function useCosUpload() {
    const uploading = ref(false);
    const progress = ref(0);
    const message = ref("");
    const messageType = ref<"success" | "error" | "">("");

    function setMessage(text: string, type: "success" | "error") {
        message.value = text;
        messageType.value = type;
    }

    function clearMessage() {
        message.value = "";
        messageType.value = "";
    }

    async function getCosConfigCached(): Promise<CosConfig> {
        const cached = localStorage.getItem(COS_CONFIG_KEY);
        if (cached) {
            try {
                const parsed = JSON.parse(cached) as { ts: number; data: CosConfig };
                if (parsed.ts && Date.now() - parsed.ts < COS_CONFIG_TTL) {
                    return parsed.data;
                }
            } catch {
                // ignore cache parse
            }
        }
        const data = await fetchCosConfig();
        localStorage.setItem(COS_CONFIG_KEY, JSON.stringify({ ts: Date.now(), data }));
        return data;
    }

    function validateFile(file: File, allowedTypes: string[], maxSizeMb: number) {
        if (allowedTypes.length && !allowedTypes.includes(file.type)) {
            throw new Error("不支持的图片类型");
        }
        const maxBytes = Math.max(1, maxSizeMb) * 1024 * 1024;
        if (file.size > maxBytes) {
            throw new Error(`图片过大（最大 ${maxSizeMb}MB）`);
        }
    }

    function uploadToCos(sts: CosStsResponse, file: File) {
        const cos = new COS({
            getAuthorization: (_options, callback) => {
                callback({
                    TmpSecretId: sts.tmpSecretId,
                    TmpSecretKey: sts.tmpSecretKey,
                    SecurityToken: sts.sessionToken,
                    StartTime: Math.floor(Date.now() / 1000) - 10,
                    ExpiredTime: sts.expiredTime
                });
            }
        });
        return new Promise<void>((resolve, reject) => {
            cos.putObject(
                {
                    Bucket: sts.bucket,
                    Region: sts.region,
                    Key: sts.key,
                    Body: file,
                    onProgress: (progressData) => {
                        progress.value = Math.round((progressData?.percent || 0) * 100);
                    }
                },
                (err) => {
                    if (err) {
                        reject(new Error(err.message || "上传失败"));
                        return;
                    }
                    resolve();
                }
            );
        });
    }

    async function uploadFile(file: File, options: UploadOptions = {}) {
        clearMessage();
        progress.value = 0;
        try {
            const config = await getCosConfigCached();
            const allowedTypes = options.allowedTypes ?? config.allowedTypes ?? [];
            const maxSizeMb = options.maxSizeMb ?? config.maxSizeMb ?? 5;
            validateFile(file, allowedTypes, maxSizeMb);

            uploading.value = true;
            const sts = await issueCosUpload(file, options.prefix);
            await uploadToCos(sts, file);
            setMessage(options.successMessage ?? "上传成功", "success");
            return sts;
        } catch (err) {
            setMessage(getErrorMessage(err, "上传失败"), "error");
            throw err;
        } finally {
            uploading.value = false;
            if (messageType.value === "error") {
                progress.value = 0;
            }
        }
    }

    return {
        uploading,
        progress,
        message,
        messageType,
        clearMessage,
        uploadFile
    };
}
