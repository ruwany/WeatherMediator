package org.entgra.iot.weatherdata.dto;

import java.util.HashMap;
import java.util.Map;

public class DynamicDeviceDTO {

    /*
    {
        "name": "NAME",
        "deviceIdentifier": "ID",
        "description": "Weather Station",
        "type": "weather3",
        "enrolmentInfo": {
            "status": "ACTIVE",
            "ownership": "BYOD"
        },
        "properties": [{
            "name": "action",
            "value": "ACTION"
        }, {
            "name": "softwaretype",
            "value": "SFTYPE"
        }, {
            "name": "version",
            "value": "VERSION"
        }]
    }
     */

    private String name;
    private String deviceIdentifier;
    private String description;
    private String type;

    private Map<String, String> propertyMap = new HashMap<String, String>();

    public String  getPropertyMap() {
        StringBuffer bf = new StringBuffer("");
        for (Map.Entry<String, String> entry : propertyMap.entrySet())
        {
            bf.append("{\"name\":\"" + entry.getKey() + "\",\"value\":\"" + entry.getValue() + "\"},");
        }
        bf.deleteCharAt(bf.length() -1);
        return bf.toString();
    }

    public void setPropertyMap(Map<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public void setDeviceIdentifier(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toJson() {
        String payload = "{\"name\":\"" + getName() + "\",\"deviceIdentifier\":\""+getDeviceIdentifier()+"\",\"description\":\"Weather Station\",\"type\":\""+getType()+"\",\"enrolmentInfo\":{\"status\":\"ACTIVE\",\"ownership\":\"BYOD\"}," +
                "\"properties\":[" + getPropertyMap() + "]}";
        return payload;
    }
}
