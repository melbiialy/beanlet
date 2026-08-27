package org.study.beanlet.support;

import java.util.*;

public class CreationTracker {
    private final ThreadLocal<LinkedHashSet<String>> beansUnderCreation;

    public CreationTracker() {
        this.beansUnderCreation = ThreadLocal.withInitial(LinkedHashSet::new);
    }


    public boolean isUnderCreated(String beanName) {
        return beansUnderCreation.get().contains(beanName);
    }

    public void markAsUnderCreated(String beanName) {
        beansUnderCreation.get().add(beanName);
    }

    public void unmarkAsUnderCreated(String beanName) {
        beansUnderCreation.get().remove(beanName);
    }


    public List<String> getNames() {
        return new ArrayList<>(beansUnderCreation.get());
    }
}
