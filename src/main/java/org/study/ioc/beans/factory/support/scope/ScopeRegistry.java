package org.study.ioc.beans.factory.support.scope;

import org.study.ioc.beans.defintion.BeanScope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScopeRegistry {
    private final Map<BeanScope,Scope> scopes;

    public ScopeRegistry() {
        scopes = new ConcurrentHashMap<>();
        scopes.put(BeanScope.SINGLETON,new SingletonScope(new SingletonBeanRegistry()));
        scopes.put(BeanScope.PROTOTYPE,new PrototypeScope());
    }
    public Scope getScope(BeanScope scopeName){
        return scopes.get(scopeName);
    }
    public boolean containsScope(BeanScope scopeName){
        return scopes.containsKey(scopeName);
    }
    public Map<BeanScope, Scope> getScopes(){
        return scopes;
    }
    public void registerScope(BeanScope scopeName,Scope scope){
        scopes.put(scopeName,scope);
    }
}
