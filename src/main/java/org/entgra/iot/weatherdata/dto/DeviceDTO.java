package org.entgra.iot.weatherdata.dto;

import java.util.HashMap;
import java.util.Map;

public class DeviceDTO {

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

    public void setAction(String action) {
        this.propertyMap.put("action", action);

    }

    public void setSoftwareType(String softwareType) {
        this.propertyMap.put("softwaretype", softwareType);
    }

    public void setVersion(String action) {
        this.propertyMap.put("version", action);
    }

    public String getAction() {
        return this.propertyMap.get("action");
    }

    public String getoftwareType() {
        return this.propertyMap.get("softwaretype");
    }

    public String getVersion() {
        return this.propertyMap.get("version");
    }

    public String toJson() {
        String payload = "{\"name\":\"" + getName() + "\",\"deviceIdentifier\":\""+getDeviceIdentifier()+"\",\"description\":\"Weather Station\",\"type\":\""+getType()+"\",\"enrolmentInfo\":{\"status\":\"ACTIVE\",\"ownership\":\"BYOD\"},\"properties\":[{\"name\":\"action\",\"value\":\""+getAction()+"\"},{\"name\":\"softwaretype\",\"value\":\""+getoftwareType()+"\"},{\"name\":\"version\",\"value\":\""+getVersion()+"\"}]}";
        return payload;
    }
}
