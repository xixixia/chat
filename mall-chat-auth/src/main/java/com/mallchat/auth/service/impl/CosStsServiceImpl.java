package com.mallchat.auth.service.impl;

import com.mallchat.auth.exception.BusinessException;
import com.mallchat.auth.service.CosStsService;
import com.mallchat.auth.vo.CosStsResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sts.v20180813.StsClient;
import com.tencentcloudapi.sts.v20180813.models.Credentials;
import com.tencentcloudapi.sts.v20180813.models.GetFederationTokenRequest;
import com.tencentcloudapi.sts.v20180813.models.GetFederationTokenResponse;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * COS STS service implementation.
 */
@Service
public class CosStsServiceImpl implements CosStsService {
    @Value("${app.cos.secret-id}")
    private String secretId;

    @Value("${app.cos.secret-key}")
    private String secretKey;

    @Value("${app.cos.region}")
    private String region;

    @Value("${app.cos.bucket}")
    private String bucket;

    @Value("${app.cos.domain:}")
    private String domain;

    @Value("${app.cos.prefix:avatars/}")
    private String prefix;

    @Value("${app.cos.sts-duration-seconds:1800}")
    private int durationSeconds;

    @Value("${app.cos.app-id:}")
    private String appId;

    @Override
    public CosStsResponse issueUpload(String filename, String prefix) {
        String key = buildKey(filename, prefix);
        String resolvedAppId = resolveAppId();
        String policy = buildPolicy(resolvedAppId, key);
        try {
            Credential credential = new Credential(secretId, secretKey);
            HttpProfile httpProfile = new HttpProfile();
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            StsClient client = new StsClient(credential, region, clientProfile);

            GetFederationTokenRequest request = new GetFederationTokenRequest();
            request.setName("mallchat-upload");
            request.setDurationSeconds((long) durationSeconds);
            request.setPolicy(policy);

            GetFederationTokenResponse resp = client.GetFederationToken(request);
            Credentials creds = resp.getCredentials();

            CosStsResponse response = new CosStsResponse();
            response.setTmpSecretId(creds.getTmpSecretId());
            response.setTmpSecretKey(creds.getTmpSecretKey());
            response.setSessionToken(creds.getToken());
            response.setExpiredTime(resp.getExpiredTime());
            response.setBucket(bucket);
            response.setRegion(region);
            response.setKey(key);
            response.setUrl(buildUrl(key));
            return response;
        } catch (Exception e) {
            throw new BusinessException("获取临时密钥失败: " + e.getMessage());
        }
    }

    private String buildKey(String originalName, String requestPrefix) {
        String date = LocalDate.now().toString().replace("-", "");
        String safe = originalName == null ? "avatar" : originalName.replaceAll("[^A-Za-z0-9._-]", "_");
        String normalizedPrefix = normalizePrefix(requestPrefix);
        return normalizedPrefix + date + "/" + UUID.randomUUID().toString().replace("-", "") + "_" + safe;
    }

    private String resolveAppId() {
        if (appId != null && !appId.trim().isEmpty()) {
            return appId.trim();
        }
        int idx = bucket.lastIndexOf("-");
        if (idx > 0 && idx < bucket.length() - 1) {
            return bucket.substring(idx + 1);
        }
        throw new BusinessException("未配置 app.cos.app-id");
    }

    private String buildPolicy(String resolvedAppId, String key) {
        String resource = String.format("qcs::cos:%s:uid/%s:%s/%s", region, resolvedAppId, bucket, key);
        return "{"
                + "\"version\":\"2.0\","
                + "\"statement\":["
                + "{"
                + "\"action\":[\"name/cos:PutObject\"],"
                + "\"effect\":\"allow\","
                + "\"resource\":[\"" + resource + "\"]"
                + "}"
                + "]"
                + "}";
    }

    private String buildUrl(String key) {
        if (domain != null && !domain.trim().isEmpty()) {
            String base = domain.trim();
            if (!base.endsWith("/")) {
                base += "/";
            }
            return base + key;
        }
        return String.format("https://%s.cos.%s.myqcloud.com/%s", bucket, region, key);
    }

    private String normalizePrefix(String requestPrefix) {
        String base = requestPrefix;
        if (base == null || base.trim().isEmpty()) {
            base = prefix;
        }
        if (base == null) {
            return "";
        }
        String normalized = base.trim().replace("\\", "/");
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (normalized.contains("..") || normalized.contains(":")) {
            throw new BusinessException("Invalid upload prefix.");
        }
        if (!normalized.isEmpty() && !normalized.matches("[A-Za-z0-9/_-]+")) {
            throw new BusinessException("Invalid upload prefix.");
        }
        if (!normalized.isEmpty() && !normalized.endsWith("/")) {
            normalized += "/";
        }
        return normalized;
    }
}
