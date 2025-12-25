package org.study.ioc.beans.factory;

import java.util.HashSet;
import java.util.Set;

public class CreationTracker {
    private final Set<String> beansUnderCreation;

    public CreationTracker() {
        this.beansUnderCreation = new HashSet<>();
    }


    public boolean isUnderCreated(String beanName) {
        return beansUnderCreation.contains(beanName);
    }

    public void trackCreation(String beanName) {
        beansUnderCreation.add(beanName);
    }

    public void stopTracking(String beanName) {
        beansUnderCreation.remove(beanName);
    }
}
