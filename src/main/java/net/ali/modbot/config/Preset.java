package net.ali.modbot.config;

public class Preset {

    private String key;
    private String value;

    public Preset(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey(){
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
