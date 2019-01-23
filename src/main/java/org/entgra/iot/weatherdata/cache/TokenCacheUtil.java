package org.entgra.iot.weatherdata.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.entgra.iot.weatherdata.Constants;
import org.entgra.iot.weatherdata.Util;
import org.entgra.iot.weatherdata.dto.AccessTokenInfo;
import org.entgra.iot.weatherdata.dto.DCRInfo;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class TokenCacheUtil {
    private static final Log log = LogFactory.getLog(TokenCacheUtil.class);
    private static LoadingCache<DCRInfo, AccessTokenInfo> tokenCache;

    static {
        tokenCache = CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<DCRInfo, AccessTokenInfo>() {
                            @Override
                            public AccessTokenInfo load(DCRInfo dcrInfo) throws Exception {
                                return getAccessToken(dcrInfo);
                            }
                        }
                );
    }

    public static LoadingCache<DCRInfo, AccessTokenInfo> getLoadingCache() {
        return tokenCache;
    }

    private static AccessTokenInfo getAccessToken(DCRInfo dcrInfo) {
        AccessTokenInfo accessToken = new AccessTokenInfo();
        try {
            log.info("-------- Making calls to initiate cache! -------- $$ Access Token");
            URL tokenURL = new URL(Constants.TOKEN_EP);
            HttpPost postMethod = new HttpPost(Constants.TOKEN_EP);
            HttpClient httpClient = Util.getHttpClient(tokenURL.getProtocol());
            String payload = "grant_type=password&username=" + Constants.ADMIN_USERNAME + "&password=" + Constants.ADMIN_PW + "&scope=" + Constants.TOKEN_SCOPES;
            StringEntity requestEntity = new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);
            postMethod.setEntity(requestEntity);

            String basicAuth = Util.getBase64Encode(dcrInfo.getClientID(), dcrInfo.getClientSecret());
            postMethod.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.MEDIA_TYPE_X_WWW_FORM);
            postMethod.setHeader(HTTPConstants.HEADER_AUTHORIZATION, "Basic " + basicAuth);
            HttpResponse httpResponse = httpClient.execute(postMethod);

            if (httpResponse != null) {
                String response = Util.getResponseString(httpResponse);
                try {
                    if (response != null) {
                        JSONParser jsonParser = new JSONParser();
                        JSONObject jsonPayload = (JSONObject) jsonParser.parse(response);
                        accessToken.setAccessToken((String) jsonPayload.get(Constants.ACCESS_TOKEN));
                        accessToken.setRefreshToken((String) jsonPayload.get(Constants.REFRESH_TOKEN));
                    }
                } catch (ParseException e) {
                    String msg = "error occurred while parsing generating token for the adapter";
                    log.error(msg, e);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return accessToken;
    }


}
