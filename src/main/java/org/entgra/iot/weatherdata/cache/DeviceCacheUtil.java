package org.entgra.iot.weatherdata.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.entgra.iot.weatherdata.Constants;
import org.entgra.iot.weatherdata.Util;
import org.entgra.iot.weatherdata.dto.AccessTokenInfo;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class DeviceCacheUtil {
    private static final Log log = LogFactory.getLog(DeviceCacheUtil.class);
    private static LoadingCache<String, Boolean> deviceCache;

    static {
        deviceCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<String, Boolean>() {
                            @Override
                            public Boolean load(String deviceID) throws Exception {
                                return getDevice(deviceID);
                            }
                        }
                );
    }

    public static LoadingCache<String, Boolean> getLoadingCache() {
        return deviceCache;
    }


    private static Boolean getDevice(String deviceID) {
        try {
            AccessTokenInfo token = Util.getToken();
            String deviceDataEP = Constants.DEVICE_DATA_EP + Constants.DEVICE_TYPE + "/" + deviceID;
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(deviceDataEP);

            // add request header
            request.addHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.MEDIA_TYPE_APPLICATION_JSON);
            request.addHeader(HTTPConstants.HEADER_AUTHORIZATION, "Bearer " + token.getAccessToken());
            HttpResponse response = client.execute(request);

            if (response != null && (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)) {
                String deviceData = Util.getResponseString(response);
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonPayload = (JSONObject) jsonParser.parse(deviceData);
                if (!jsonPayload.isEmpty()) {
                    log.info("#### A Device Already Exists with given ID!");
                    return true;
                }
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }


}
