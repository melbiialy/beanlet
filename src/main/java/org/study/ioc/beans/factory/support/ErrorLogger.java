package org.study.ioc.beans.factory.support;

import java.util.List;
import java.util.Set;

public class ErrorLogger {
    public static void reportError(List<String> creationStack, String beanName) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n***************************\n")
                .append("APPLICATION FAILED TO START\n")
                .append("***************************\n\n")
                .append("Description:\n\n")
                .append("The dependencies of some of the beans in the application context form a cycle:\n\n");

        // Build the “box” diagram
        int start = creationStack.indexOf(beanName);
        if (start == -1) start = 0;

        sb.append("┌─────┐\n");
        for (int i = start; i < creationStack.size(); i++) {
            String name = creationStack.get(i);
            sb.append("|  ").append(name).append("  \n");
            if (i < creationStack.size() - 1) {
                sb.append("↑     ↓\n");
            }
        }
        sb.append("└─────┘\n");


        System.out.println(sb.toString());

        System.exit(1);
        // Throw exception like Spring Boot does
    }
}
