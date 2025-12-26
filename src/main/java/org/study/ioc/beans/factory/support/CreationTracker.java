package org.study.ioc.beans.factory.support;

import java.util.*;

public class CreationTracker {
    private final Set<String> beansUnderCreation;

    public CreationTracker() {
        this.beansUnderCreation = new LinkedHashSet<>();
    }


    public boolean isUnderCreated(String beanName) {
        return beansUnderCreation.contains(beanName);
    }

    public void markAsUnderCreated(String beanName) {
        beansUnderCreation.add(beanName);
    }

    public void unmarkAsUnderCreated(String beanName) {
        beansUnderCreation.remove(beanName);
    }


    public List<String> getNames() {
        return new ArrayList<>(beansUnderCreation);
    }
}
