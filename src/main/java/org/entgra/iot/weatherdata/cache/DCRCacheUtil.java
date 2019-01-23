package org.entgra.iot.weatherdata.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.entgra.iot.weatherdata.dto.DCRInfo;
import org.entgra.iot.weatherdata.dto.DCRRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class DCRCacheUtil {
    private static final Log log = LogFactory.getLog(DCRCacheUtil.class);
    private static LoadingCache<String, DCRInfo> dcrCache;

    static {
        dcrCache = CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<String, DCRInfo>() {
                            @Override
                            public DCRInfo load(String key) throws Exception {
                                return getCredentials();
                            }
                        }
                );
    }

    public static LoadingCache<String, DCRInfo> getLoadingCache() {
        return dcrCache;
    }

    private static DCRInfo getCredentials() {
        DCRInfo dcrInfo = new DCRInfo();
        try {
            log.info("-------- Making calls to initiate cache! -------- $$ DCR APP");
            URL dcrUrl = new URL(Constants.DCR_EP);
            HttpClient httpClient = Util.getHttpClient(dcrUrl.getProtocol());

            HttpPost postMethod = new HttpPost(Constants.DCR_EP);

            DCRRequest dcrRequest = new DCRRequest();
            dcrRequest.setApplicationName("weather-app");
            dcrRequest.setTags(new String[]{"device_management"});

            String jsonString = new ObjectMapper().writer().writeValueAsString(dcrRequest);
            StringEntity requestEntity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
            postMethod.setEntity(requestEntity);
            String basicAuth = Util.getBase64Encode(Constants.ADMIN_USERNAME, Constants.ADMIN_PW);
            postMethod.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.MEDIA_TYPE_APPLICATION_JSON);
            postMethod.setHeader(HTTPConstants.HEADER_AUTHORIZATION, "Basic " + basicAuth);
            HttpResponse httpResponse = httpClient.execute(postMethod);

            if (httpResponse != null) {
                String response = Util.getResponseString(httpResponse);
                try {
                    if (response != null) {
                        JSONParser jsonParser = new JSONParser();
                        JSONObject jsonPayload = (JSONObject) jsonParser.parse(response);
                        dcrInfo.setClientID((String) jsonPayload.get(Constants.CLIENT_ID));
                        dcrInfo.setClientSecret((String) jsonPayload.get(Constants.CLIENT_SECRET));
                        log.info("Client ID : " + dcrInfo.getClientID());
                        log.info("Client Secret : " + dcrInfo.getClientSecret());
                        return dcrInfo;
                    }
                } catch (ParseException e) {
                    String msg = "error occurred while parsing generating token for the adapter";
                    log.error(msg, e);
                    return dcrInfo;
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dcrInfo;
    }
}
