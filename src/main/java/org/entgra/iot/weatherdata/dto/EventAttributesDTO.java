package org.entgra.iot.weatherdata.dto;

import java.util.Map;

public class EventAttributesDTO {
    /*
    {
        "eventAttributes": {
            "attributes": [{
                "name": "ABC",
                "type": "STRING"
            }, {
                "name": "DDD",
                "type": "STRING"
            }, {
                "name": "FFF",
                "type": "STRING"
            }, {
                "name": "QQQ",
                "type": "STRING"
            }]
        },
        "transport": "HTTP"
    }
     */
    private Map<String, String> attibutes;
    private String transport;

    public String getAttibutes() {
        StringBuffer bf = new StringBuffer();
        for (Map.Entry<String, String> entry : attibutes.entrySet())
        {
            bf.append("{\"name\":\"" + entry.getKey() + "\",\"type\":\"" + entry.getValue() + "\"},");
        }
        if(bf.length() > 0) {
            return bf.toString().substring(0, bf.length() - 1);
        } else {
            return "";
        }
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public void setAttibutes(Map<String, String> attibutes) {
        this.attibutes = attibutes;
    }

    public String toJson(){
        String response = "{\"eventAttributes\":{\"attributes\":" +
                "["+ getAttibutes() +"]}," + "\"transport\":\""+getTransport()+"\"}";
        return response;
    }
}
