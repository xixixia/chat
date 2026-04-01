package com.mallchat.register.service.impl;

import com.mallchat.exception.BusinessException;
import com.mallchat.register.service.SmsService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** TODO: update docs. */
@Service
public class TencentSmsService implements SmsService {
    @Value("${app.sms.secret-id}")
    private String secretId;

    @Value("${app.sms.secret-key}")
    private String secretKey;

    @Value("${app.sms.sdk-app-id}")
    private String sdkAppId;

    @Value("${app.sms.sign-name}")
    private String signName;

    @Value("${app.sms.template-id}")
    private String templateId;

    @Value("${app.sms.endpoint:sms.tencentcloudapi.com}")
    private String endpoint;

    @Value("${app.sms.region:ap-guangzhou}")
    private String region;

    /** TODO: update docs. */
    @Override
    public void sendCode(String phone, String code) {
        try {
            Credential credential = new Credential(secretId, secretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(endpoint);
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            SmsClient client = new SmsClient(credential, region, clientProfile);

            SendSmsRequest request = new SendSmsRequest();
            request.setPhoneNumberSet(new String[]{phone});
            request.setSmsSdkAppId(sdkAppId);
            request.setSignName(signName);
            request.setTemplateId(templateId);
            request.setTemplateParamSet(new String[]{code});

            client.SendSms(request);
        } catch (TencentCloudSDKException e) {
            throw new BusinessException("短信发送失败: " + e.getMessage());
        }
    }
}
