package org.study;

import org.study.ioc.beans.defintion.BeanDefinitionRegistry;
import org.study.ioc.componentscan.ComponentScanner;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
        ComponentScanner scanner = new ComponentScanner();
        scanner.scan(new BeanDefinitionRegistry());


    }
}