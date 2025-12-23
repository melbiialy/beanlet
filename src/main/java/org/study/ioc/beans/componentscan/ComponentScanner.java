package org.study.ioc.beans.componentscan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComponentScanner {
    private List<String > packages;
    private String basePackage;

    public ComponentScanner() {
        packages = new ArrayList<>();
        basePackage = "";
    }
    public void scan(){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("")).getPath());
        scanDirectory(file,basePackage);
    }

    private void scanDirectory(File file, String basePackage) {
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                scanDirectory(f,basePackage+"."+f.getName());
                continue;
            }
            System.out.println(f.getName().replace(".class","").replace(f.getName().charAt(0),Character.toLowerCase(f.getName().charAt(0))));
        }
    }
}
