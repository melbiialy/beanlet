package org.study.beanlet.support;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CreationTracker {
    private final Map<String,CreationPhase> creationTracker;
    public CreationTracker() {
        creationTracker = new ConcurrentHashMap<>();
    }
    public boolean allowEarlyRef(String beanName) {
        if (!creationTracker.containsKey(beanName)) {
            return true;
        }
        return creationTracker.get(beanName).getAllowEarlyRef();
    }
    public boolean isUnderCreationPhase(String beanName) {
        if (!creationTracker.containsKey(beanName)) {
            return false;
        }
        CreationPhase creationPhase = creationTracker.get(beanName);
        return creationPhase != CreationPhase.INITIALIZATION;
    }
    public List<String> getBeanNames() {
        return new ArrayList<>(creationTracker.keySet());
    }

    public void markAsUnderInstantiation(String beanName) {
        creationTracker.put(beanName,CreationPhase.INSTANTIATION);
    }

    public void finishInstantiation(String beanName) {
        creationTracker.put(beanName,CreationPhase.POPULATION);
    }

    public void finalizeCreationPhase(String beanName) {
        creationTracker.put(beanName,CreationPhase.INITIALIZATION);
    }


    public enum CreationPhase{
        INSTANTIATION(false),
        POPULATION(true),
        INITIALIZATION(true);
        final boolean allowEarlyRef;

        public  boolean getAllowEarlyRef() {
            return allowEarlyRef;
        }
        private CreationPhase(boolean allowEarlyRef) {
            this.allowEarlyRef = allowEarlyRef;
        }
    }
}

