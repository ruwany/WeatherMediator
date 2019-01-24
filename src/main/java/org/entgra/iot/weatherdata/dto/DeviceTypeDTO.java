package org.entgra.iot.weatherdata.dto;

public class DeviceTypeDTO {

    /*
    {
        "name": "firestarter",
        "deviceTypeMetaDefinition": {
            "description": "fire",
            "properties": ["ABC", "DCR"],
            "features": []
        }
    }

    * */

    private String name;
    private String description;
    private String[] properties;
    private String[] features;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProperties() {
        StringBuffer bf = new StringBuffer();
        for(String str : properties){
            bf.append("\"" + str + "\",");
        }
        if(bf.length() > 0) {
            return bf.toString().substring(0, bf.length() - 1);
        } else {
            return "";
        }
    }

    public void setProperties(String[] properties) {
        this.properties = properties;
    }

    public String getFeatures() {
        StringBuffer bf = new StringBuffer();
        for(String str : features){
            bf.append("\"" + str + "\",");
        }
        if(bf.length() > 0) {
            return bf.toString().substring(0, bf.length() - 1);
        } else {
            return "";
        }
    }

    public void setFeatures(String[] features) {
        this.features = features;
    }

    public String toJson(){
        String response = "{\"name\":\""+getName()+"\",\"deviceTypeMetaDefinition\":{\"description\":\""+getDescription()+"\"," +
                "\"properties\":[" + getProperties() + "],\"features\":[" + getFeatures() + "]}}";
        return response;
    }
}
