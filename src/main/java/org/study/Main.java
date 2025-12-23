package org.study;

import org.study.ioc.property.PropertySourceLoader;
import org.study.ioc.property.YamlPropertySourceLoader;

import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        PropertySourceLoader loader = new YamlPropertySourceLoader();
        System.out.println(loader.loadProperties("application.yml").getProperties());


    }
}