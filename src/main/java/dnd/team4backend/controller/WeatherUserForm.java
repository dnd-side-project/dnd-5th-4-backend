package dnd.team4backend.controller;

public class WeatherUserForm {
    private String userId;
    private Float temperatureHigh;
    private Float temperatureLow;
    private Float humidity;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Float getTemperatureHigh() {
        return temperatureHigh;
    }

    public void setTemperatureHigh(Float temperatureHigh) {
        this.temperatureHigh = temperatureHigh;
    }

    public Float getTemperatureLow() {
        return temperatureLow;
    }

    public void setTemperatureLow(Float temperatureLow) {
        this.temperatureLow = temperatureLow;
    }

    public Float getHumidity() {
        return humidity;
    }

    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }
}
