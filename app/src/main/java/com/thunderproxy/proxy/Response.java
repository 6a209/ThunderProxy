package com.thunderproxy.proxy;

import java.nio.ByteBuffer;
import java.security.cert.CRL;
import java.util.Map;

/**
 * Created by 6a209 on 16/1/31.
 */
public class Response {

    private static final String CRLF = "\r\n";

    private String mHttpVersion;
    private String mStatusCode;
    private String mShortDesc;
    private Map<String, String> mHeader;
    private String mBody;

    public String getHttpVersion() {
        return mHttpVersion;
    }

    public void setHttpVersion(String mHttpVersion) {
        this.mHttpVersion = mHttpVersion;
    }

    public String getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(String mStatusCode) {
        this.mStatusCode = mStatusCode;
    }

    public String getShortDesc() {
        return mShortDesc;
    }

    public void setShortDesc(String mShortDesc) {
        this.mShortDesc = mShortDesc;
    }

    public Map<String, String> getHeader() {
        return mHeader;
    }

    public void setHeader(Map<String, String> mHeader) {
        this.mHeader = mHeader;
    }

    public void addHeader(String key, String value){
        mHeader.put(key, value);
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String mBody) {
        this.mBody = mBody;
    }

    public byte[] format2Byte(){
        StringBuilder sb = new StringBuilder();
        sb.append(mHttpVersion).append(" ").append(mStatusCode).append(" ").append(mShortDesc).append(CRLF);
        for(Map.Entry<String, String> entry : mHeader.entrySet()){
           sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(CRLF);
        }
        sb.append(CRLF);
        return sb.toString().getBytes();
    }
}
