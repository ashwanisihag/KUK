package com.track24x7.kuk.util;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

public class Headers {

    String authorization;
    public Headers(String authorization){
        this.authorization=authorization;
    }

    public GlideUrl getUrlWithHeaders(String url){
        return new GlideUrl(url, new LazyHeaders.Builder()
                .addHeader("Authorization", authorization)
                .build());
    }
}
