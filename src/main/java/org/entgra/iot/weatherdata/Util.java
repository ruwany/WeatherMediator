package org.entgra.iot.weatherdata;

import com.google.common.cache.LoadingCache;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.entgra.iot.weatherdata.cache.DCRCacheUtil;
import org.entgra.iot.weatherdata.cache.DeviceCacheUtil;
import org.entgra.iot.weatherdata.cache.TokenCacheUtil;
import org.entgra.iot.weatherdata.dto.AccessTokenInfo;
import org.entgra.iot.weatherdata.dto.DCRInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class Util {
    private static final Log log = LogFactory.getLog(Util.class);

    public static HttpClient getHttpClient(String protocol)
            throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpClient httpclient;
        if (Constants.HTTPS_PROTOCOL.equals(protocol)) {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
            httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } else {
            httpclient = HttpClients.createDefault();
        }
        return httpclient;
    }

    public static String getBase64Encode(String key, String value) {
        return new String(Base64.encodeBase64((key + ":" + value).getBytes()));
    }


    public static String getResponseString(HttpResponse httpResponse) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
            String readLine;
            String response = "";
            while (((readLine = br.readLine()) != null)) {
                response += readLine;
            }
            return response;
        } finally {
            EntityUtils.consumeQuietly(httpResponse.getEntity());
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.warn("Error while closing the connection! " + e.getMessage());
                }
            }
        }
    }

    public static DCRInfo getDCRCredentals() throws ExecutionException {
        LoadingCache<String, DCRInfo> dcrCache = DCRCacheUtil.getLoadingCache();
        System.out.println("Cache Size:" + dcrCache.size());
        return dcrCache.get("DCR_APP");
    }

    public static AccessTokenInfo getToken() throws ExecutionException {
        DCRInfo appInfo = Util.getDCRCredentals();
        LoadingCache<DCRInfo, AccessTokenInfo> tokenCache = TokenCacheUtil.getLoadingCache();
        System.out.println("Cache Size:" + tokenCache.size());
        return tokenCache.get(appInfo);
    }

    public static Boolean getDevice(String deviceID) throws ExecutionException {
        LoadingCache<String, Boolean> deviceCache = DeviceCacheUtil.getLoadingCache();
        System.out.println("Cache Size:" + deviceCache.size());
        return deviceCache.get(deviceID);
    }
}
