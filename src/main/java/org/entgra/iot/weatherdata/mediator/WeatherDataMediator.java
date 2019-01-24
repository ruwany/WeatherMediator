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
import org.entgra.iot.weatherdata.dto.DeviceTypeDTO;
import org.entgra.iot.weatherdata.dto.DynamicDeviceDTO;
import org.entgra.iot.weatherdata.dto.EventAttributesDTO;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class WeatherDataMediator extends AbstractMediator {

    private static final Log log = LogFactory.getLog(WeatherDataMediator.class);
    String sensorType;
    List<String> propertiesList;
    private Map<String,String> eventAttributes;
    boolean delay;

    public boolean mediate(MessageContext messageContext) {
        try {
            AccessTokenInfo token = Util.getToken();
            OMElement xmlResponse = messageContext.getEnvelope().getBody().getFirstElement();
            xmlResponse.build();
            org.json.JSONObject jsonObject = new org.json.JSONObject(JsonUtil.toJsonString(xmlResponse).toString());

            sensorType = jsonObject.getString(Constants.SENSOR_TYPE);

            boolean deviceTypeExists = Util.getDeviceType(sensorType);

            if(!deviceTypeExists){
                delay = true;
                addDeviceType(token, jsonObject);
                addEventAttributes(token, jsonObject);
                Util.refreshDeviceTypeCache(sensorType);
            }

            String deviceID = jsonObject.getString(Constants.DEVICE_ID);

            boolean deviceExists = Util.getDevice(deviceID + ":" + sensorType);

            if (!deviceExists) {
                addDevice(token, jsonObject);
                Util.refreshDeviceCache(deviceID + ":" + sensorType);
            }
            pushDynamicEvents(token, jsonObject);
            //pushEvents(token, jsonObject);
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

    private boolean pushDynamicEvents(AccessTokenInfo tokenInfo, org.json.JSONObject payload) {
        try {
//            if(delay){
//                try {
//                    TimeUnit.SECONDS.sleep(5);
//                } catch (InterruptedException e) {
//                    log.error("error while giving time for analytics script deployment");
//                }
//            }
            String deviceID = payload.getString(Constants.DEVICE_ID);
            String deviceDataEP = Constants.DEVICE_EVENTS_EP + sensorType + "/" + deviceID;

            org.json.JSONArray dataArr = payload.getJSONArray("data");

            if(eventAttributes == null){
                populateEventAttributes(payload);
            }

            for (int i = 0; i < dataArr.length(); i++) {

                StringBuffer bf = new StringBuffer("{");

                for(Map.Entry<String, String> entry : eventAttributes.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    bf.append("\"");
                    bf.append(entry.getKey());
                    bf.append("\"");
                    bf.append(":");
                    if(value.equalsIgnoreCase("INT") || value.equalsIgnoreCase("BOOL") || value.equalsIgnoreCase("DOUBLE")){
                        bf.append(((JSONObject)dataArr.get(i)).get(key));
                    } else{
                        bf.append("\"");
                        bf.append(((JSONObject)dataArr.get(i)).get(key));
                        bf.append("\"");
                    }
                    bf.append(",");
                }
                bf.deleteCharAt(bf.length() - 1);
                bf.append("}");


                URL deviceEventsURL = new URL(deviceDataEP);
                HttpPost postMethod = new HttpPost(deviceDataEP);
                HttpClient httpClient = Util.getHttpClient(deviceEventsURL.getProtocol());
                StringEntity requestEntity = new StringEntity(bf.toString(), ContentType.APPLICATION_JSON);
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
        DynamicDeviceDTO device = new DynamicDeviceDTO();
        try {

            device.setDeviceIdentifier(payload.getString(Constants.DEVICE_ID));
            device.setName(payload.getString(Constants.DEVICE_ID));
            device.setType(sensorType);

            Map<String,String> propMap = new HashMap<String, String>();

            if(propertiesList == null){
                populateDeviceProperties(payload);
            }

            for(String str : propertiesList){
                propMap.put(str, payload.getString(str));
            }

            device.setPropertyMap(propMap);

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


    private boolean addDeviceType(AccessTokenInfo tokenInfo, org.json.JSONObject payload) {
        DeviceTypeDTO deviceType = new DeviceTypeDTO();
        try {

            populateDeviceProperties(payload);

            deviceType.setName(payload.getString(Constants.SENSOR_TYPE));
            deviceType.setFeatures(new String[0]);
            deviceType.setProperties(propertiesList.toArray(new String[0]));

            URL deviceRegURL = new URL(Constants.DEVICE_TYPE_REG_EP);
            HttpPost postMethod = new HttpPost(Constants.DEVICE_TYPE_REG_EP);
            HttpClient httpClient = Util.getHttpClient(deviceRegURL.getProtocol());
            StringEntity requestEntity = new StringEntity(deviceType.toJson(), ContentType.APPLICATION_JSON);
            postMethod.setEntity(requestEntity);

            postMethod.setHeader(HTTPConstants.HEADER_CONTENT_TYPE, HTTPConstants.MEDIA_TYPE_APPLICATION_JSON);
            postMethod.setHeader(HTTPConstants.HEADER_AUTHORIZATION, "Bearer " + tokenInfo.getAccessToken());
            HttpResponse httpResponse = httpClient.execute(postMethod);

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return true;
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

    private void populateDeviceProperties(JSONObject payload) {
        Iterator<String> keys = payload.keys();

        propertiesList = new ArrayList<String>();

        while(keys.hasNext()) {
            String key = keys.next();
            if(key.equalsIgnoreCase("TYPE") || key.equalsIgnoreCase("data")) {
                continue;
            }
            propertiesList.add(key);
        }
    }


    private boolean addEventAttributes(AccessTokenInfo tokenInfo, org.json.JSONObject payload) {
        EventAttributesDTO eventAttributes = new EventAttributesDTO();
        try {
            populateEventAttributes(payload);
            eventAttributes.setAttibutes(this.eventAttributes);
            eventAttributes.setTransport("HTTP");

            URL deviceRegURL = new URL(Constants.DEVICE_TYPE_EVENTS_EP + sensorType);
            HttpPost postMethod = new HttpPost(Constants.DEVICE_TYPE_EVENTS_EP + sensorType);
            HttpClient httpClient = Util.getHttpClient(deviceRegURL.getProtocol());
            StringEntity requestEntity = new StringEntity(eventAttributes.toJson(), ContentType.APPLICATION_JSON);
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

    private void populateEventAttributes(JSONObject payload) throws JSONException {

        this.eventAttributes = new HashMap<String, String>();
        org.json.JSONArray dataArr = payload.getJSONArray("data");
        JSONObject firstElement =  (JSONObject) dataArr.get(0);

        Iterator<String> keys = firstElement.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            if (firstElement.get(key) instanceof String) {
                this.eventAttributes.put(key, "STRING");
            }
            if (firstElement.get(key) instanceof Boolean) {
                this.eventAttributes.put(key, "BOOL");
            }
            if (firstElement.get(key) instanceof Double) {
                this.eventAttributes.put(key, "DOUBLE");
            }
            if (firstElement.get(key) instanceof Integer) {
                this.eventAttributes.put(key, "INT");
            }
        }
    }

}
