package org.study;

import org.study.ioc.beans.componentscan.ComponentScanner;

public class Main {
    public static void main(String[] args) {
        ComponentScanner scanner = new ComponentScanner();
        scanner.scan();


    }
}