# Beanlet

This is an educational project designed to explore the inner workings beneath Spring's magic.

By creating a lightweight dependency injection container from the ground up, this project reveals the mechanisms through which Spring Framework orchestrates bean management, dependency resolution, and application context handling.

## What is IoC (Inversion of Control)?

IoC is a fundamental design principle that shifts the responsibility of object creation and lifecycle management away from your application code to a container or framework. Rather than manually instantiating and wiring dependencies, the container assumes control over creating objects and establishing their relationships.

## What is Dependency Injection?

Dependency Injection is a design pattern that realizes the IoC principle in practice. It enables you to receive beans without manually creating them. Instead of using `new` to construct dependencies, the container automatically supplies the necessary dependencies to your objects. This approach fosters loose coupling and enhances code testability and maintainability.

## Component Scan Module

The Component Scan module is the entry point of the container. It's responsible for discovering and registering bean definitions from your codebase.

**How it works:**

1. **Classpath Scanning**: The scanner traverses the specified base packages on the classpath to locate all classes.

2. **Component Detection**: It identifies classes annotated with stereotype annotations like `@Component`, `@Configuration`, or other custom annotations.

3. **Bean Definition Extraction**: For each discovered component, the module extracts metadata (class type, scope, dependencies) and creates a `BeanDefinition` object.

4. **Registration**: All extracted bean definitions are registered in the `BeanDefinitionRegistry`, making them available for instantiation and dependency injection later.

This module mirrors Spring's `@ComponentScan` functionality, automatically discovering beans without requiring manual registration.
