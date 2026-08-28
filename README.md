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

1. **Classpath Scanning**: The `FileSystemClassPathScanner` traverses the specified base packages to locate all classes.

2. **Component Detection**: It identifies classes annotated with stereotype annotations like `@Component` or `@Configuration`.

3. **Bean Definition Extraction**: For each discovered component, the module extracts metadata (class type, scope, dependencies) and creates a `BeanDefinition` object via registered `BeanDefinitionExtractor` implementations (`ComponentExtractor`, `ConfigurationExtractor`).

4. **Registration**: All extracted bean definitions are registered in the `BeanDefinitionRegistry`, making them available for instantiation and dependency injection later.

5. **Processor Discovery**: A separate `ProcessorScanner` scans the classpath for `BeanPostProcessor` and `BeanFactoryPostProcessor` implementations and registers them for later use.

This module mirrors Spring's `@ComponentScan` functionality, automatically discovering beans without requiring manual registration.

## Bean Definition

A `BeanDefinition` is a metadata blueprint that describes how a bean should be created and managed. It captures all the essential information the container needs to instantiate and configure a bean.

**What metadata does it capture?**

- **Bean Class**: The actual Java class to instantiate
- **Scope**: Whether the bean is a singleton (one instance shared) or prototype (new instance per request)
- **Lazy Loading**: Should the bean be created on startup or only when first requested?
- **Primary**: Marks a bean as the default candidate when multiple beans of the same type exist
- **Qualified Name**: A unique identifier for the bean within the container
- **Lifecycle Callbacks**:
  - `initMethod`: Method annotated with `@PostConstruct` to execute after dependency injection
  - `destroyMethod`: Method annotated with `@PreDestroy` to execute before container shutdown
- **Factory Method**: For beans defined using `@Bean` methods in `@Configuration` classes

The `BeanDefinitionBuilder` provides a fluent API to construct these definitions, ensuring all required metadata is properly configured before the bean is instantiated.

## Bean Factory

The `BeanFactory` is the heart of the container. It transforms bean definitions into actual living objects ready to be used. This is where the magic happens — metadata becomes reality.

**Bean Creation Lifecycle:**

1. **Lookup**: When `getBean(beanName)` is called, the factory first checks if the bean already exists in cache based on its scope.

2. **Instantiation**: If not found, the factory creates the bean using the resolved constructor or factory method (`FactoryMethodInstantiator`).

3. **Early Reference Registration**: The partially created bean is registered immediately to handle circular dependencies.

4. **Population**: `BeanPostProcessor` implementations scan for `@Autowired` fields and methods, recursively resolving and injecting dependencies.

5. **Initialization**: After all dependencies are injected, lifecycle callbacks (`@PostConstruct`) are invoked.

6. **Caching**: The fully initialized bean is stored in the appropriate scope registry (singleton or prototype).

**Three-Level Cache for Circular Dependencies:**

The container solves circular dependencies using a three-level caching strategy, similar to Spring's approach:

1. **Level 1 - Singleton Beans Cache (`singletonBeans`)**:
   - Stores fully initialized and ready-to-use beans
   - This is the final destination for all singleton beans
   - Retrieved first when requesting a bean

2. **Level 2 - Early Singleton Objects Cache (`earlySingletonObjects`)**:
   - Holds beans that have been instantiated but not yet fully initialized (dependencies not injected)
   - Acts as a bridge between raw objects and fully initialized beans
   - Allows circular dependencies to be resolved by providing partially created beans

3. **Level 3 - Singleton Factories Cache (`singletonFactories`)**:
   - Contains factory lambdas that can produce early references to beans
   - Created immediately after bean instantiation, before dependency injection
   - When a circular dependency is detected, the factory is invoked to create an early reference, which is then promoted to Level 2

**How it resolves circular dependencies:**

If Bean A depends on Bean B, and Bean B depends on Bean A:
- Bean A is instantiated and its factory is stored in Level 3
- During A's population phase, it needs Bean B
- Bean B is instantiated and its factory is stored in Level 3
- During B's population phase, it needs Bean A
- The container retrieves A's factory from Level 3, invokes it to get an early reference, and moves it to Level 2
- Bean B receives the early (not fully initialized) reference to A and completes its initialization
- Bean B is moved to Level 1 as fully initialized
- Bean A now receives the fully initialized Bean B and completes its own initialization
- Bean A is moved to Level 1 as fully initialized

## Post-Processors

Post-processors are extension points that allow custom logic to be plugged into the bean creation pipeline. They are discovered automatically by `ProcessorScanner` at startup — only concrete, non-abstract classes that implement the relevant interface are instantiated.

### BeanPostProcessor

Invoked during bean creation to intercept or modify bean instances before and after initialization.

```java
public interface BeanPostProcessor {
    default Object postProcessBeforeInitialization(Class<?> beanClass, String beanName) { return null; }
    default boolean postProcessAfterInitialization(Object bean, String beanName) { return false; }
}
```

- **`postProcessBeforeInitialization`**: Called before the bean is instantiated. Returning a non-null object short-circuits normal instantiation.
- **`postProcessAfterInitialization`**: Called after instantiation. Returning `true` signals the bean is fully configured and further processing should stop.

### InstantiationAwareBeanPostProcessor

Extends `BeanPostProcessor` to also participate in dependency injection. `AutowiredAnnotationBeanPostProcessor` uses this to inject `@Autowired` fields and methods.

### SmartInstantiationAwareBeanPostProcessor

Further extends the above to influence **constructor selection** (`determineCandidateConstructor`) and provide **early bean references** for circular dependency resolution (`getEarlyBeanReference`).

### BeanFactoryPostProcessor

Invoked **after** all bean definitions are registered but **before** any beans are created. Allows modifying the `BeanDefinitionRegistry` (e.g., overriding scopes, adding new definitions).

```java
public interface BeanFactoryPostProcessor {
    void postProcessorBeanFactory(BeanDefinitionRegistry registry);
}
```

## Dependency Resolution & Injection

After a bean is instantiated, the container identifies and injects its dependencies. The `AutowiredAnnotationBeanPostProcessor` handles this phase.

**Injection Types:**

1. **Field Injection**: The injector scans all fields annotated with `@Autowired` and injects the appropriate bean instances.

2. **Method Injection**: Methods annotated with `@Autowired` (typically setters) are invoked with resolved dependencies as parameters.

**Resolution Strategy:**

- **Concrete Classes**: For concrete class dependencies, the container looks up the bean by its fully qualified class name.

- **Interfaces**: When the dependency is an interface, the resolution becomes more complex:
  - The container searches for all beans that implement the interface
  - If multiple implementations exist, `@Qualifier` specifies which implementation to inject
  - If no qualifier is provided and multiple beans exist, the container looks for a `@Primary` bean

**Recursive Resolution:**

Dependency injection is recursive. When injecting Bean A's dependencies, if Bean B is required but not yet created, the factory pauses A's population, creates and fully initializes B (including its own dependencies), then resumes A's population with the resolved B instance.

## Constructor Injection

The container supports constructor-based dependency injection, automatically resolving which constructor to use for bean instantiation.

**Constructor Selection Strategy:**

1. **Explicit Selection**: If a constructor is annotated with `@Autowired`, that constructor is chosen.

2. **Single Constructor**: If the class has only one constructor (including the default no-arg constructor), it's automatically selected.

3. **Multiple Constructors**: If multiple constructors exist without `@Autowired`, the container throws an exception requiring explicit annotation.

**Dependency Resolution:**

Once a constructor is selected, the `DependencyResolver` analyzes its parameters and recursively resolves each dependency from the container before invoking the constructor.

## Bean Scopes

Bean scope determines the lifecycle and visibility of a bean instance within the container.

**SINGLETON (Default):**
- Only one instance is created per container
- The instance is cached in Level 1 cache and reused for all requests
- Created eagerly at startup (unless marked `@Lazy`)
- Ideal for stateless beans and shared resources

**PROTOTYPE:**
- A new instance is created every time the bean is requested
- Not cached — each `getBean()` call triggers a new instantiation
- Dependencies are injected each time a new instance is created
- Suitable for stateful beans or beans with per-request data

The scope is specified using the `@Scope` annotation on the bean class or `@Bean` method.

## Bean Lifecycle Management

The container manages the complete lifecycle of beans, from creation to destruction, with hooks for custom initialization and cleanup logic.

**Lifecycle Phases:**

1. **Instantiation**: Bean object is created via constructor or factory method
2. **Population**: Dependencies are injected into fields and setter methods
3. **Initialization**: `@PostConstruct` methods are invoked after all dependencies are satisfied
4. **Ready**: Bean is fully initialized and cached for use
5. **Destruction**: `@PreDestroy` methods are called on shutdown; beans implementing `AutoCloseable` / `Closeable` have their `close()` method invoked automatically

**@PostConstruct:**
- Annotated methods execute after dependency injection completes
- Useful for validation, resource initialization, or setup logic that requires dependencies

**@PreDestroy:**
- Methods marked for execution during container shutdown
- Intended for cleanup operations like closing connections or releasing resources
- Additionally, any singleton bean implementing `AutoCloseable` / `Closeable` has its `close()` method called automatically during `BeanFactory.close()`

## Configuration Classes

`@Configuration` classes provide a Java-based approach to bean definition as an alternative to component scanning.

**How it works:**

1. **@Configuration Annotation**: Mark a class with `@Configuration` to designate it as a bean definition source.

2. **@Bean Methods**: Methods annotated with `@Bean` within a configuration class define beans. The method's return type becomes the bean type.

3. **Factory Method Pattern**: The container invokes `@Bean` methods to create bean instances, treating the method as a factory.

4. **Method Parameters**: `@Bean` methods can declare parameters, which the container automatically resolves and injects before invocation.

**Example:**

```java
@Configuration
public class AppConfig {
    @Bean
    public DataSource dataSource() {
        return new DataSource();
    }
    
    @Bean
    public UserService userService(DataSource dataSource) {
        return new UserService(dataSource);
    }
}
```

The `ConfigurationExtractor` scans for `@Configuration` classes and extracts bean definitions from their `@Bean` methods during component scanning.

## Lazy Initialization

By default, singleton beans are created eagerly when the container starts. The `@Lazy` annotation defers bean creation until first access.

**How it works:**

- **Eager Loading (Default)**: During `refresh()`, the container pre-initializes all singleton beans by calling `getBean()` for each registered bean.
- **Lazy Loading**: When `@Lazy` is present, the bean definition is registered but instantiation is skipped during startup.
- **First Access**: The bean is created only when first requested via `getBean()` or when injected as a dependency into another bean.

**Benefits:**
- Faster application startup time
- Reduced memory footprint if bean is never used

**Trade-off:**
- Configuration errors are discovered at runtime instead of startup
- First request experiences initialization overhead

## Property Management

The container loads external configuration from YAML files and makes properties available throughout the application.

**PropertySource:**

The `PropertySource` encapsulates configuration loaded from `application.yml`. The `YamlPropertySourceLoader` parses YAML and flattens nested structures into dot-notation keys (e.g., `logging.level`, `beanlet.scan.base-package`).

**YAML Loading:**

The loader recursively flattens nested maps and lists:
- Nested objects: `server.port` → `server.port`
- Lists: `items[0]`, `items[1]`, etc.

**@Value Annotation:**

Injects property values into fields or parameters using placeholder syntax (e.g., `@Value("${server.port}")`). The `ValueResolver` resolves placeholders against the loaded `PropertySource`.

**Configuration Usage:**

Properties influence container behavior:
- `beanlet.scan.base-package`: Defines the root package for component scanning
- `logging.level`: Sets the logging verbosity (`TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`)

## Application Events

Beanlet includes a lightweight publish/subscribe event system for decoupled in-process communication.

**Core Components:**

- **`Event`**: Wraps an event type (`Class<?>`) and a consumer handler that is invoked when the event is published.
- **`EventRegistry`**: Maintains a map of event type → list of registered `Event` handlers. Backed by a `ConcurrentHashMap` and `CopyOnWriteArrayList` for thread safety.
- **`ApplicationEventPublisher`**: Publishes events to all registered handlers asynchronously via a managed `ThreadPoolExecutor`. Implements `Closeable` — the executor is shut down gracefully when the context closes.

**How it works:**

```java
// Register a listener for String events
EventRegistry registry = (EventRegistry) context.getBean("EventRegistry");
registry.register(new Event(String.class, payload -> System.out.println("Got: " + payload)));

// Publish an event
ApplicationEventPublisher publisher = (ApplicationEventPublisher) context.getBean("ApplicationEventPublisher");
publisher.publish("hello world");
```

**Threading model:**

Events are dispatched on a `ThreadPoolExecutor` (up to 100 threads, 60s keep-alive). The publisher's `close()` method triggers a graceful shutdown — waiting up to 30 seconds for in-flight handlers before forcing termination. Because `ApplicationEventPublisher` implements `AutoCloseable`, the container calls `close()` on it automatically at shutdown.

## Application Context

The `ApplicationContext` is the top-level container interface that manages the entire bean lifecycle and orchestrates all subsystems.

**Responsibilities:**

1. **Property Loading**: Loads configuration from `application.yml` via `YamlPropertySourceLoader`
2. **Logging Configuration**: Sets up logging levels based on loaded properties
3. **Processor Discovery**: `ProcessorScanner` discovers `BeanPostProcessor` and `BeanFactoryPostProcessor` implementations
4. **Component Scanning**: Discovers and registers bean definitions from the classpath
5. **Bean Factory Post-Processing**: Applies all `BeanFactoryPostProcessor` implementations against the registry before bean creation
6. **Bean Factory Initialization**: Creates the `DefaultBeanFactory` with all necessary registries and post-processors
7. **Pre-Initialization**: Eagerly instantiates all non-lazy singleton beans during startup
8. **Bean Access**: Delegates `getBean()` calls to the underlying `BeanFactory`, with fallback to the singleton registry
9. **Shutdown**: Closes the `BeanFactory`, invoking `@PreDestroy` methods and `close()` on any `AutoCloseable` singleton beans

**The `refresh()` Method:**

1. Scans classpath for components and configurations
2. Registers all discovered bean definitions
3. Applies `BeanFactoryPostProcessor` implementations
4. Initializes the bean factory with scope registries and post-processors
5. Pre-instantiates singleton beans (except lazy ones)
6. Registers a JVM shutdown hook
7. Logs startup metrics and bean counts

## Supported Annotations

### Stereotype Annotations
- **`@Component`**: Marks a class as a bean candidate for component scanning
- **`@Configuration`**: Designates a class as a source of bean definitions via `@Bean` methods

### Bean Definition Annotations
- **`@Bean`**: Declares a method as a bean factory method within `@Configuration` classes
- **`@Scope`**: Specifies the bean scope (`SINGLETON` or `PROTOTYPE`)
- **`@Lazy`**: Defers bean instantiation until first access
- **`@Primary`**: Marks a bean as the primary candidate when multiple beans of the same type exist

### Dependency Injection Annotations
- **`@Autowired`**: Marks constructors, fields, or methods for automatic dependency injection
- **`@Qualifier`**: Specifies which bean to inject when multiple candidates exist
- **`@Value`**: Injects property values from configuration files (e.g., `@Value("${server.port}")`)
- **`@Required`**: Marks a dependency as mandatory (metadata only)

### Lifecycle Annotations
- **`@PostConstruct`**: Marks methods to execute after dependency injection completes
- **`@PreDestroy`**: Marks methods to execute before container shutdown

### Container Configuration Annotations
- **`@ComponentScan`**: Specifies base packages to scan for components

## Getting Started

**Requirements:** Java 21+, Maven 3.x

**Build:**

```bash
mvn package
```

**Run:**

```bash
java -jar target/beanlet-1.0-SNAPSHOT.jar
```

**Basic Usage:**

```java
// Use try-with-resources to ensure the context is closed cleanly on exit
try (DefaultApplicationContext context = new DefaultApplicationContext()) {
    context.refresh();
    MyService service = (MyService) context.getBean("MyService");
    service.doSomething();
}
```

**Configuration (`application.yml`):**

```yaml
beanlet:
  scan:
    base-package: "org.example"

logging:
  level: INFO
```

**Example Bean:**

```java
@Component
public class UserService {
    
    @Autowired
    private UserRepository repository;
    
    @PostConstruct
    public void init() {
        System.out.println("UserService initialized!");
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("UserService shutting down!");
    }
    
    public void createUser(String name) {
        repository.save(name);
    }
}
```

**Example Configuration:**

```java
@Configuration
public class DatabaseConfig {
    
    @Bean
    public DataSource dataSource() {
        return new DataSource("localhost", 5432);
    }
    
    @Bean
    public UserRepository userRepository(DataSource dataSource) {
        return new UserRepository(dataSource);
    }
}
```

## Project Structure

```
beanlet/
├── src/main/java/
│   ├── demo/                          # Demo app (Main, sample beans & configs)
│   └── org/study/beanlet/
│       ├── annotation/                # All container annotations (@Component, @Autowired, etc.)
│       ├── applicationevents/         # Event system (Event, EventRegistry, ApplicationEventPublisher)
│       ├── bean/                      # BeanDefinition, BeanDefinitionBuilder, BeanScope, BeanWrapper
│       ├── context/                   # ApplicationContext interface & DefaultApplicationContext
│       ├── env/                       # PropertySource, YamlPropertySourceLoader, ValueResolver
│       ├── exception/                 # Custom exceptions (BeanNotFoundException, etc.)
│       ├── factory/                   # BeanFactory interface & DefaultBeanFactory
│       ├── instantiation/             # BeanCreationStrategy, FactoryMethodInstantiator
│       ├── logging/                   # LoggerConfig, CircularDependencyReporter
│       ├── processor/                 # BeanPostProcessor, BeanFactoryPostProcessor, AutowiredAnnotationBeanPostProcessor
│       ├── registry/                  # BeanDefinitionRegistry, SingletonBeanRegistry, BeanCacheManager, BeanScopeRegistry
│       ├── scanner/                   # ClassPathScanner, BeanScanner, BeanDefinitionReader, ProcessorScanner
│       ├── support/                   # CreationTracker and internal support utilities
│       └── util/                      # Shared reflection and resolution utilities
└── src/main/resources/
    └── application.yml                # External configuration
```

## What You'll Learn

By exploring this project, you'll gain deep insights into:

- How dependency injection containers discover and register beans
- The strategy pattern for handling different bean creation methods
- Multi-level caching strategies for circular dependency resolution
- Reflection-based metadata extraction and runtime bean instantiation
- Post-processor extension points (`BeanPostProcessor`, `BeanFactoryPostProcessor`)
- Property externalization and configuration management
- Bean lifecycle management with initialization and destruction hooks
- Scope management and the difference between singleton and prototype beans
- Async event publishing with a thread pool and graceful shutdown
- Why non-daemon threads keep the JVM alive — and how to properly shut them down

This project demystifies the "magic" behind Spring Framework, showing that it's built on solid design patterns, clever caching strategies, and systematic reflection-based metadata processing.
