package org.study.ioc.property;

import java.util.Map;

public interface PropertySourceLoader {
    PropertySource loadProperties(String location);

}
