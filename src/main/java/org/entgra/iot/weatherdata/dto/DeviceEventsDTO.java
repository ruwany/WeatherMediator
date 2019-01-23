package org.entgra.iot.weatherdata.dto;

public class DeviceEventsDTO {

    private String dateutc = "";
    private Double rainin = 0.0;
    private Double rainMM = 0.0;
    private Double windspdmph_avg2m = 0.0;
    private Double winddir_avg2m = 0.0;
    private Double windgustmph_10m = 0.0;
    private Double windgustmph = 0.0;
    private Double windgustdir = 0.0;
    private Double windgustdir_10m = 0.0;
    private Double dailyrainin = 0.0;

    private Double tempf = 0.0;
    private Double tempc = 0.0;
    private Double winddir = 0.0;
    private Double windspeedmph = 0.0;
    private Double dailyrainMM = 0.0;
    private Double baromin = 0.0;
    private String dateist = "";
    private String rain = "";

    private Double healthBAT = 0.0;
    private Double healthRSSI = 0.0;

    private Double healthB = 0.0;
    private Double healthSV = 0.0;
    private Double healthEV = 0.0;
    private Double healthZ = 0.0;
    private Double healthSI = 0.0;
    private Double healthF = 0.0;

    private Double humidity = 0.0;
    private Double dewptf = 0.0;
    private Double dewptc = 0.0;
    private Double baromMM = 0.0;
    private Double solarradiation = 0.0;
    private Double UV = 0.0;
    private Double waterlevelm = 0.0;

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getDewptf() {
        return dewptf;
    }

    public void setDewptf(Double dewptf) {
        this.dewptf = dewptf;
    }

    public Double getDewptc() {
        return dewptc;
    }

    public void setDewptc(Double dewptc) {
        this.dewptc = dewptc;
    }

    public Double getBaromMM() {
        return baromMM;
    }

    public void setBaromMM(Double baromMM) {
        this.baromMM = baromMM;
    }

    public Double getSolarradiation() {
        return solarradiation;
    }

    public void setSolarradiation(Double solarradiation) {
        this.solarradiation = solarradiation;
    }

    public Double getUV() {
        return UV;
    }

    public void setUV(Double UV) {
        this.UV = UV;
    }

    public Double getWaterlevelm() {
        return waterlevelm;
    }

    public void setWaterlevelm(Double waterlevelm) {
        this.waterlevelm = waterlevelm;
    }

    public String getDateutc() {
        return dateutc;
    }

    public void setDateutc(String dateutc) {
        this.dateutc = dateutc;
    }

    public Double getRainin() {
        return rainin;
    }

    public void setRainin(Double rainin) {
        this.rainin = rainin;
    }

    public Double getRainMM() {
        return rainMM;
    }

    public void setRainMM(Double rainMM) {
        this.rainMM = rainMM;
    }

    public Double getWindspdmph_avg2m() {
        return windspdmph_avg2m;
    }

    public void setWindspdmph_avg2m(Double windspdmph_avg2m) {
        this.windspdmph_avg2m = windspdmph_avg2m;
    }

    public Double getWinddir_avg2m() {
        return winddir_avg2m;
    }

    public void setWinddir_avg2m(Double winddir_avg2m) {
        this.winddir_avg2m = winddir_avg2m;
    }

    public Double getWindgustmph_10m() {
        return windgustmph_10m;
    }

    public void setWindgustmph_10m(Double windgustmph_10m) {
        this.windgustmph_10m = windgustmph_10m;
    }

    public Double getWindgustmph() {
        return windgustmph;
    }

    public void setWindgustmph(Double windgustmph) {
        this.windgustmph = windgustmph;
    }

    public Double getWindgustdir() {
        return windgustdir;
    }

    public void setWindgustdir(Double windgustdir) {
        this.windgustdir = windgustdir;
    }

    public Double getWindgustdir_10m() {
        return windgustdir_10m;
    }

    public void setWindgustdir_10m(Double windgustdir_10m) {
        this.windgustdir_10m = windgustdir_10m;
    }

    public Double getDailyrainin() {
        return dailyrainin;
    }

    public void setDailyrainin(Double dailyrainin) {
        this.dailyrainin = dailyrainin;
    }

    public Double getHealthBAT() {
        return healthBAT;
    }

    public void setHealthBAT(Double healthBAT) {
        this.healthBAT = healthBAT;
    }

    public Double getHealthRSSI() {
        return healthRSSI;
    }

    public void setHealthRSSI(Double healthRSSI) {
        this.healthRSSI = healthRSSI;
    }

    public Double getBaromin() {
        return baromin;
    }

    public void setBaromin(Double baromin) {
        this.baromin = baromin;
    }

    public Double getHealthB() {
        return healthB;
    }

    public void setHealthB(Double healthB) {
        this.healthB = healthB;
    }

    public Double getHealthSV() {
        return healthSV;
    }

    public void setHealthSV(Double healthSV) {
        this.healthSV = healthSV;
    }

    public Double getHealthEV() {
        return healthEV;
    }

    public void setHealthEV(Double healthEV) {
        this.healthEV = healthEV;
    }

    public Double getHealthZ() {
        return healthZ;
    }

    public void setHealthZ(Double healthZ) {
        this.healthZ = healthZ;
    }

    public Double getHealthSI() {
        return healthSI;
    }

    public void setHealthSI(Double healthSI) {
        this.healthSI = healthSI;
    }

    public Double getHealthF() {
        return healthF;
    }

    public void setHealthF(Double healthF) {
        this.healthF = healthF;
    }

    public String getDateist() {
        return dateist;
    }

    public void setDateist(String dateist) {
        this.dateist = dateist;
    }

    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public Double getTempf() {
        return tempf;
    }

    public void setTempf(Double tempf) {
        this.tempf = tempf;
    }

    public Double getTempc() {
        return tempc;
    }

    public void setTempc(Double tempc) {
        this.tempc = tempc;
    }

    public Double getWinddir() {
        return winddir;
    }

    public void setWinddir(Double winddir) {
        this.winddir = winddir;
    }

    public Double getWindspeedmph() {
        return windspeedmph;
    }

    public void setWindspeedmph(Double windspeedmph) {
        this.windspeedmph = windspeedmph;
    }

    public Double getDailyrainMM() {
        return dailyrainMM;
    }

    public void setDailyrainMM(Double dailyrainMM) {
        this.dailyrainMM = dailyrainMM;
    }

    public String toJson() {
        String payload = "{\"dateist\":\"" + getDateist() + "\",\"rain\":\"" + getRain() +
                "\",\"tempf\":" + getTempf() + ",\"tempc\":" + getTempc() + ",\"winddir\":" + getWinddir() +
                ",\"windspeedmph\":" + getWindspeedmph() + ",\"healthB\":" + getHealthB() +
                ",\"healthSV\":" + getHealthSV() + ",\"healthEV\":" + getHealthEV() + ",\"healthZ\":" + getHealthZ() +
                ",\"healthSI\":" + getHealthSI() + ",\"healthF\":" + getHealthF() + ",\"baromin\":" + getBaromin() +
                ",\"dateutc\":\"" + getDateutc() + "\",\"rainin\":" + getRainin() + ",\"rainMM\":" + getRainMM() +
                ",\"windspdmph_avg2m\":" + getWindspdmph_avg2m() + ",\"winddir_avg2m\":" + getWinddir_avg2m() +
                ",\"windgustmph_10m\":" + getWindgustmph_10m() + ",\"windgustmph\":" + getWindgustmph() +
                ",\"windgustdir\":" + getWindgustdir() + ",\"windgustdir_10m\":" + getWindgustdir_10m() +
                ",\"dailyrainin\":" + getDailyrainin() + ",\"healthBAT\":" + getHealthBAT() +
                ",\"healthRSSI\":" + getHealthRSSI() + ",\"humidity\":" + getHumidity() + ",\"dewptf\":" + getDewptf() +
                ",\"dewptc\":" + getDewptc() + ",\"baromMM\":" + getBaromMM() + ",\"solarradiation\":" + getSolarradiation() +
                ",\"UV\":" + getUV() + ",\"waterlevelm\":" + getWaterlevelm() + ",\"dailyrainMM\":" + getDailyrainMM() + "}";
        return payload;
    }
}
