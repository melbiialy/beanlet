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
  - `destroyMethod`: Method annotated with `@Destroy` to execute before container shutdown
- **Factory Method**: For beans defined using `@Bean` methods in `@Configuration` classes

The `BeanDefinitionBuilder` provides a fluent API to construct these definitions, ensuring all required metadata is properly configured before the bean is instantiated.

## Bean Factory

The `BeanFactory` is the heart of the container. It transforms bean definitions into actual living objects ready to be used. This is where the magic happens - metadata becomes reality.

**Bean Creation Lifecycle:**

1. **Lookup**: When `getBean(beanName)` is called, the factory first checks if the bean already exists in cache based on its scope.

2. **Instantiation**: If not found, the factory delegates to the `CreatorRegistry` which selects the appropriate creator strategy:
   - `BeanInstantiator`: Creates beans from classes (via constructor)
   - `FactoryCreator`: Creates beans from `@Bean` factory methods

3. **Early Reference Registration**: The partially created bean is registered immediately to handle circular dependencies.

4. **Population**: The `DependencyInjector` scans for `@Autowired` fields and methods, recursively resolving and injecting dependencies.

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

## Dependency Resolution & Injection

After a bean is instantiated, the container must identify and inject its dependencies. The `DependencyInjector` handles this crucial phase.

**Injection Types:**

1. **Field Injection**: The injector scans all fields annotated with `@Autowired` and injects the appropriate bean instances.

2. **Method Injection**: Methods annotated with `@Autowired` (typically setters) are invoked with resolved dependencies as parameters.

**Resolution Strategy:**

- **Concrete Classes**: For concrete class dependencies, the container looks up the bean by its fully qualified class name.

- **Interfaces**: When the dependency is an interface, the resolution becomes more complex:
  - The container searches for all beans that implement the interface
  - If multiple implementations exist, `@Qualifier` annotation specifies which implementation to inject
  - If no qualifier is provided and multiple beans exist, the container looks for a `@Primary` bean
  - The type injection cache maintains mappings between interfaces and their implementations

**Recursive Resolution:**

Dependency injection is recursive. When injecting Bean A's dependencies, if Bean B is required but not yet created, the factory pauses A's population, creates and fully initializes B (including its own dependencies), then resumes A's population with the resolved B instance.

## Constructor Injection

The container supports constructor-based dependency injection, automatically resolving which constructor to use for bean instantiation.

**Constructor Selection Strategy:**

1. **Explicit Selection**: If a constructor is annotated with `@Autowired`, that constructor is chosen regardless of other constructors.

2. **Single Constructor**: If the class has only one constructor (including the default no-arg constructor), it's automatically selected.

3. **Multiple Constructors**: If multiple constructors exist without `@Autowired`, the container throws an exception requiring explicit annotation.

**Dependency Resolution:**

Once a constructor is selected, the `DependencyResolver` analyzes its parameters and recursively resolves each dependency from the container before invoking the constructor to create the bean instance.

## Bean Scopes

Bean scope determines the lifecycle and visibility of a bean instance within the container.

**SINGLETON (Default):**
- Only one instance is created per container
- The instance is cached in Level 1 cache and reused for all requests
- Created eagerly at startup (unless marked `@Lazy`)
- Ideal for stateless beans and shared resources

**PROTOTYPE:**
- A new instance is created every time the bean is requested
- Not cached - each `getBean()` call triggers a new instantiation
- Dependencies are injected each time a new instance is created
- Suitable for stateful beans or beans with per-request data

The scope is specified using the `@Scope` annotation on the bean class or `@Bean` method.

## Bean Lifecycle Management

The container manages the complete lifecycle of beans, from creation to destruction, with hooks for custom initialization and cleanup logic.

**Lifecycle Phases:**

1. **Instantiation**: Bean object is created via constructor
2. **Population**: Dependencies are injected into fields and setter methods
3. **Initialization**: `@PostConstruct` methods are invoked after all dependencies are satisfied
4. **Ready**: Bean is fully initialized and cached for use
5. **Destruction**: `@Destroy` methods are called before container shutdown (future implementation)

**@PostConstruct:**
- Annotated methods execute after dependency injection completes
- Useful for validation, resource initialization, or setup logic that requires dependencies
- Multiple `@PostConstruct` methods can exist (executed in undefined order)

**@Destroy:**
- Methods marked for execution during container shutdown
- Intended for cleanup operations like closing connections or releasing resources

## Configuration Classes

`@Configuration` classes provide a Java-based approach to bean definition as an alternative to component scanning.

**How it works:**

1. **@Configuration Annotation**: Mark a class with `@Configuration` to designate it as a bean definition source.

2. **@Bean Methods**: Methods annotated with `@Bean` within a configuration class define beans. The method's return type becomes the bean type.

3. **Factory Method Pattern**: The container invokes `@Bean` methods to create bean instances, treating the method as a factory.

4. **Method Parameters**: `@Bean` methods can declare parameters, which the container automatically resolves and injects before invocation.

**Example Flow:**

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
- Delayed initialization of expensive resources

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

While defined, the `@Value` annotation is designed for injecting property values into fields, parameters, or local variables. Implementation details may include property placeholder resolution (e.g., `@Value("${server.port}")`).

**Configuration Usage:**

Properties influence container behavior:
- `beanlet.scan.base-package`: Defines the root package for component scanning
- `logging.level`: Sets the logging verbosity (TRACE, DEBUG, INFO, WARN, ERROR)

## Application Context

The `ApplicationContext` is the top-level container interface that manages the entire bean lifecycle and orchestrates all subsystems.

**Responsibilities:**

1. **Property Loading**: Loads configuration from `application.yml` via `PropertySourceLoader`

2. **Logging Configuration**: Sets up logging levels based on loaded properties

3. **Component Scanning**: Triggers the scanning process to discover and register bean definitions

4. **Bean Factory Initialization**: Creates the `BeanFactory` with all necessary registries and resolvers

5. **Pre-Initialization**: Eagerly instantiates all non-lazy singleton beans during startup

6. **Bean Access**: Delegates `getBean()` calls to the underlying `BeanFactory`

**The `refresh()` Method:**

This is the heart of the container initialization process:

1. Loads properties from YAML
2. Scans classpath for components and configurations
3. Registers all discovered bean definitions
4. Initializes the bean factory with scope registries and creator strategies
5. Pre-instantiates singleton beans (except lazy ones)
6. Logs startup metrics and bean counts

The `ApplicationContext` abstracts away the complexity of manual bean factory setup, providing a simple entry point to bootstrap the entire container.

## Supported Annotations

Here's a complete reference of all annotations available in Beanlet:

### Stereotype Annotations
- **`@Component`**: Marks a class as a bean candidate for component scanning
- **`@Configuration`**: Designates a class as a source of bean definitions via `@Bean` methods

### Bean Definition Annotations
- **`@Bean`**: Declares a method as a bean factory method within `@Configuration` classes
- **`@Scope`**: Specifies the bean scope (SINGLETON or PROTOTYPE)
- **`@Lazy`**: Defers bean instantiation until first access
- **`@Primary`**: Marks a bean as the primary candidate when multiple beans of the same type exist

### Dependency Injection Annotations
- **`@Autowired`**: Marks constructors, fields, or methods for automatic dependency injection
- **`@Qualifier`**: Specifies which bean to inject when multiple candidates exist (used with bean's qualified name)
- **`@Value`**: Injects property values from configuration files (implementation-ready)
- **`@Required`**: Marks a dependency as mandatory (metadata only)

### Lifecycle Annotations
- **`@PostConstruct`**: Marks methods to execute after dependency injection completes
- **`@Destroy`**: Marks methods to execute before container shutdown

### Container Configuration Annotations
- **`@ComponentScan`**: Specifies base packages to scan for components (metadata support)

## Getting Started

**Basic Usage:**

```java
// 1. Create the application context
ApplicationContext context = new DefaultApplicationContext();

// 2. Initialize the container (scan, register, and create beans)
context.refresh();

// 3. Retrieve beans from the container
MyService service = (MyService) context.getBean("org.example.MyService");
service.doSomething();
```

**Configuration (`application.yml`):**

```yaml
beanlet:
  scan:
    base-package: "org.study"

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
├── annotation/          # All container annotations
├── beans/
│   ├── definition/      # BeanDefinition and metadata
│   └── factory/         # Bean factory and creation logic
│       └── support/     # Creator strategies, registries, injection
├── context/             # ApplicationContext implementation
├── core/
│   ├── scanning/        # Component scanning and definition extraction
│   └── util/            # Reflection and resolution utilities
├── env/                 # Property source and YAML loading
├── exception/           # Custom exceptions
└── logging/             # Logging configuration
```

## What You'll Learn

By exploring this project, you'll gain deep insights into:

- How dependency injection containers discover and register beans
- The strategy pattern for handling different bean creation methods
- Multi-level caching strategies for circular dependency resolution
- Reflection-based metadata extraction and runtime bean instantiation
- Property externalization and configuration management
- Bean lifecycle management with initialization and destruction hooks
- Scope management and the difference between singleton and prototype beans

This project demystifies the "magic" behind Spring Framework, showing that it's built on solid design patterns, clever caching strategies, and systematic reflection-based metadata processing.
