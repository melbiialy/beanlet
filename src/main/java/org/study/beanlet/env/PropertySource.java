package org.study.beanlet.env;

import java.util.Map;

public class PropertySource {
    private String name;
    private Map<String ,String > properties;
    public PropertySource(String name,Map<String ,String > properties) {
        this.name = name;
        this.properties = properties;
    }
    public String getName() {
        return name;
    }
    public Map<String, String> getProperties() {
        return properties;
    }
    public String getProperty(String key){
        return properties.get(key);
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
    public void addProperty(String key,String value){
        properties.put(key,value);
    }
    public boolean containsProperty(String key){
        return properties.containsKey(key);
    }
    public void removeProperty(String key){
        properties.remove(key);
    }
    public void clear(){
        properties.clear();
    }
}
