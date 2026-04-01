package com.mallchat.auth.service;

import com.mallchat.auth.vo.CosStsResponse;

/**
 * COS STS service.
 */
public interface CosStsService {
    CosStsResponse issueUpload(String filename, String prefix);
}
