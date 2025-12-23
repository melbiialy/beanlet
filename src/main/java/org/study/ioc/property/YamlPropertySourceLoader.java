package org.study.ioc.property;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlPropertySourceLoader implements PropertySourceLoader{
    @Override
    public PropertySource loadProperties(String location) {
        PropertySource propertySource = new PropertySource(location,null);
        Yaml yaml = new Yaml();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(location);
         Map<String,Object> yamlData = yaml.load(inputStream);
        System.out.println(yamlData.get("spring"));

         Map<String ,String> properties = flattenYaml("",yamlData);
         propertySource.setProperties(properties);
         return propertySource;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> flattenYaml(String prefix,Map<String, Object> yamlData) {
       Map<String, String > result = new HashMap<>();
       for (Map.Entry<String, Object> entry : yamlData.entrySet()) {
           String key;
           if (!prefix.isEmpty()) {
                key = prefix + "." + entry.getKey();
           }
           else {
               key = entry.getKey();
           }
           Object value = entry.getValue();
           if(value instanceof Map){
               result.putAll(flattenYaml(key,(Map<String, Object>) value));
           }else if (value instanceof List<?> list){
               for (int i = 0; i < list.size(); i++) {
                   Object item = list.get(i);
                   if (item instanceof Map) {
                       result.putAll(flattenYaml(key , (Map<String, Object>) item));
                   } else {
                       result.put(key + "[" + i + "]", String.valueOf(item));
                   }
               }
           }
           else{
               result.put(key,value.toString());
           }
       }
       return result;
    }
}
