package org.study.beanlet.env;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PropertySourceTest {

    @Test
    void loadsAndFlattensNestedYamlProperties() {
        PropertySource properties = new YamlPropertySourceLoader().loadProperties("test-properties.yml");

        assertEquals("localhost", properties.getProperty("${server.host}"));
        assertEquals("8080", properties.getProperty("${server.ports[0]}"));
        assertEquals("true", properties.getProperty("${features.enabled}"));
    }

    @Test
    void exposesPropertyMutationsAndValidatesPlaceholderSyntax() {
        PropertySource properties = new PropertySource("test", new HashMap<>(Map.of("name", "beanlet")));

        assertEquals("beanlet", properties.getProperty("${name}"));
        properties.addProperty("version", "1");
        assertEquals("1", properties.getProperty("${version}"));
        properties.removeProperty("name");
        assertNull(properties.getProperty("${name}"));
        assertThrows(IllegalArgumentException.class, () -> properties.getProperty("name"));
    }
}
