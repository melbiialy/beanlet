package org.study.ioc.beans.factory.support.scope;

import java.util.Map;

public class ScopeRegistry {
    private final Map<String,Scope> scopes;

    public ScopeRegistry(Map<String, Scope> scopes) {
        this.scopes = scopes;
    }
    public Scope getScope(String scopeName){
        return scopes.get(scopeName);
    }
    public boolean containsScope(String scopeName){
        return scopes.containsKey(scopeName);
    }
    public Map<String, Scope> getScopes(){
        return scopes;
    }
    public void registerScope(String scopeName,Scope scope){
        scopes.put(scopeName,scope);
    }
}
