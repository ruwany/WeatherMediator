package org.entgra.iot.weatherdata.mediator;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.mediators.AbstractMediator;
import org.entgra.iot.weatherdata.Constants;
import org.entgra.iot.weatherdata.Util;
import org.entgra.iot.weatherdata.dto.AccessTokenInfo;
import org.entgra.iot.weatherdata.dto.DeviceDTO;
import org.entgra.iot.weatherdata.dto.DeviceEventsDTO;
import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class WeatherDataMediator extends AbstractMediator {

    private static final Log log = LogFactory.getLog(WeatherDataMediator.class);

    public boolean mediate(MessageContext messageContext) {
        try {
            AccessTokenInfo token = Util.getToken();
            OMElement xmlResponse = messageContext.getEnvelope().getBody().getFirstElement();
            xmlResponse.build();
            org.json.JSONObject jsonObject = new org.json.JSONObject(JsonUtil.toJsonString(xmlResponse).toString());
            String deviceID = jsonObject.getString(Constants.DEVICE_ID);

            boolean deviceExists = Util.getDevice(deviceID);

            if (!deviceExists) {
                addDevice(token, jsonObject);
            }
            pushEvents(token, jsonObject);
            log.info("##### Token Info : " + token.getAccessToken());

        } catch (ExecutionException e) {
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (AxisFault axisFault) {
            axisFault.printStackTrace();
        }
        return true;
    }

    private boolean pushEvents(AccessTokenInfo tokenInfo, org.json.JSONObject payload) {
        try {
            String deviceID = payload.getString(Constants.DEVICE_ID);
            String deviceDataEP = Constants.DEVICE_EVENTS_EP + Constants.DEVICE_TYPE + "/" + deviceID;

            org.json.JSONArray dataArr = payload.getJSONArray("data");
            org.json.JSONObject health = payload.getJSONObject("health");
            Boolean eventSuccess = false;


            for (int i = 0; i < dataArr.length(); i++) {
                DeviceEventsDTO event = new DeviceEventsDTO();

                //Retrieving inputs from health section
                if(health.has("B")){
                    event.setHealthB(health.getDouble("B"));
                }
                if(health.has("SV")){
                    event.setHealthSV(health.getDouble("SV"));
                }
                if(health.has("EV")){
                    event.setHealthEV(health.getDouble("EV"));
                }
                if(health.has("Z")){
                    event.setHealthZ(health.getDouble("Z"));
                }
                if(health.has("SI")){
                    event.setHealthSI(health.getDouble("SI"));
                }
                if(health.has("F")){
                    event.setHealthF(health.getDouble("F"));
                }
                if(health.has("RSSI")){
                    event.setHealthRSSI(health.getDouble("RSSI"));
                }
                if(health.has("BAT")){
                    event.setHealthBAT(health.getDouble("BAT"));
                }

                //Retrieving inputs from data
                if(dataArr.getJSONObject(i).has("dailyrainin")){
                    event.setDailyrainin(dataArr.getJSONObject(i).getDouble("dailyrainin"));
                }
                if(dataArr.getJSONObject(i).has("dailyrainMM")){
                    event.setDailyrainMM(dataArr.getJSONObject(i).getDouble("dailyrainMM"));
                }
                if(dataArr.getJSONObject(i).has("dateist")){
                    event.setDateist(dataArr.getJSONObject(i).getString("dateist"));
                }
                if(dataArr.getJSONObject(i).has("dateutc")){
                    event.setDateutc(dataArr.getJSONObject(i).getString("dateutc"));
                }
                if(dataArr.getJSONObject(i).has("baromin")){
                    event.setBaromin(dataArr.getJSONObject(i).getDouble("baromin"));
                }
                if(dataArr.getJSONObject(i).has("tempf")){
                    event.setTempc(dataArr.getJSONObject(i).getDouble("tempf"));
                }
                if(dataArr.getJSONObject(i).has("tempc")){
                    event.setTempf(dataArr.getJSONObject(i).getDouble("tempc"));
                }
                if(dataArr.getJSONObject(i).has("winddir")){
                    event.setWinddir(dataArr.getJSONObject(i).getDouble("winddir"));
                }
                if(dataArr.getJSONObject(i).has("windspeedmph")){
                    event.setWindspeedmph(dataArr.getJSONObject(i).getDouble("windspeedmph"));
                }
                if(dataArr.getJSONObject(i).has("rainin")){
                    event.setRainin(dataArr.getJSONObject(i).getDouble("rainin"));
                }
                if(dataArr.getJSONObject(i).has("rainMM")){
                    event.setRainMM(dataArr.getJSONObject(i).getDouble("rainMM"));
                }
                if(dataArr.getJSONObject(i).has("windgustmph")){
                    event.setWindgustmph(dataArr.getJSONObject(i).getDouble("windgustmph"));
                }
                if(dataArr.getJSONObject(i).has("windgustdir")){
                    event.setWindgustdir(dataArr.getJSONObject(i).getDouble("windgustdir"));
                }
                if(dataArr.getJSONObject(i).has("windspdmph_avg2m")){
                    event.setWindspdmph_avg2m(dataArr.getJSONObject(i).getDouble("windspdmph_avg2m"));
                }
                if(dataArr.getJSONObject(i).has("winddir_avg2m")){
                    event.setWinddir_avg2m(dataArr.getJSONObject(i).getDouble("winddir_avg2m"));
                }
                if(dataArr.getJSONObject(i).has("windgustdir_10m")){
                    event.setWinddir_avg2m(dataArr.getJSONObject(i).getDouble("winddir_avg2m"));
                }
                if(dataArr.getJSONObject(i).has("windgustmph_10m")){
                    event.setWinddir_avg2m(dataArr.getJSONObject(i).getDouble("windgustmph_10m"));
                }

                if(dataArr.getJSONObject(i).has("humidity")){
                    event.setHumidity(dataArr.getJSONObject(i).getDouble("humidity"));
                }
                if(dataArr.getJSONObject(i).has("dewptc")){
                    event.setDewptc(dataArr.getJSONObject(i).getDouble("dewptc"));
                }
                if(dataArr.getJSONObject(i).has("dewptf")){
                    event.setDewptf(dataArr.getJSONObject(i).getDouble("dewptf"));
                }
                if(dataArr.getJSONObject(i).has("baromin")){
                    event.setBaromin(dataArr.getJSONObject(i).getDouble("baromin"));
                }
                if(dataArr.getJSONObject(i).has("baromMM")){
                    event.setBaromMM(dataArr.getJSONObject(i).getDouble("baromMM"));
                }
                if(dataArr.getJSONObject(i).has("solarradiation")){
                    event.setSolarradiation(dataArr.getJSONObject(i).getDouble("solarradiation"));
                }
                if(dataArr.getJSONObject(i).has("UV")){
                    event.setUV(dataArr.getJSONObject(i).getDouble("UV"));
                }
                if(dataArr.getJSONObject(i).has("waterlevelm")){
                    event.setWaterlevelm(dataArr.getJSONObject(i).getDouble("waterlevelm"));
                }

                if(dataArr.getJSONObject(i).has("rain") && (dataArr.getJSONObject(i).getJSONArray("rain").length() > 0)) {
                    String rainEvents = "";
                    for (int g = 0; g < dataArr.getJSONObject(i).getJSONArray("rain").length(); g++) {
                        rainEvents = dataArr.getJSONObject(i).getJSONArray("rain").get(g) + ",";
                    }
                    if (rainEvents.charAt(rainEvents.length() - 1) == ',') {
                        rainEvents = rainEvents.replace(rainEvents.substring(rainEvents.length() - 1), "");
                    }
                    event.setRain(rainEvents);
                }

                URL deviceEventsURL = new URL(deviceDataEP);
                HttpPost postMethod = new HttpPost(deviceDataEP);
                HttpClient httpClient = Util.getHttpClient(deviceEventsURL.getProtocol());
                StringEntity requestEntity = new StringEntity(event.toJson(), ContentType.APPLICATION_JSON);
                postMethod.setEntity(requestEntity);

                postMethod.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.MEDIA_TYPE_APPLICATION_JSON);
                postMethod.setHeader(HTTPConstants.HEADER_AUTHORIZATION, "Bearer " + tokenInfo.getAccessToken());
                HttpResponse httpResponse = httpClient.execute(postMethod);

                if (httpResponse == null || (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)) {
                    throw new IOException("Error while pushing events!");
                }
            }
            return true;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean addDevice(AccessTokenInfo tokenInfo, org.json.JSONObject payload) {
        DeviceDTO device = new DeviceDTO();
        try {

            device.setDeviceIdentifier(payload.getString(Constants.DEVICE_ID));
            device.setName(payload.getString(Constants.DEVICE_ID));
            device.setType(Constants.DEVICE_TYPE);
            device.setSoftwareType(payload.getString(Constants.DEVICE_SWTYPE));
            device.setVersion(payload.getString(Constants.DEVICE_VERSION));
            device.setAction(payload.getString(Constants.DEVICE_ACTION));

            URL deviceRegURL = new URL(Constants.DEVICE_REG_EP);
            HttpPost postMethod = new HttpPost(Constants.DEVICE_REG_EP);
            HttpClient httpClient = Util.getHttpClient(deviceRegURL.getProtocol());
            StringEntity requestEntity = new StringEntity(device.toJson(), ContentType.APPLICATION_JSON);
            postMethod.setEntity(requestEntity);

            postMethod.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.MEDIA_TYPE_APPLICATION_JSON);
            postMethod.setHeader(HTTPConstants.HEADER_AUTHORIZATION, "Bearer " + tokenInfo.getAccessToken());
            HttpResponse httpResponse = httpClient.execute(postMethod);

            if (httpResponse != null) {
                String response = Util.getResponseString(httpResponse);
                if (Boolean.getBoolean(response)) {
                    return true;
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

}
