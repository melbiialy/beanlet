package org.study.beanlet.env;

import java.util.Map;

public interface PropertySourceLoader {
    PropertySource loadProperties(String location);

}
