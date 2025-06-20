 



# 5-Day Testing Bootcamp for Spring Boot Developers

## Overview
This intensive week-long bootcamp is designed for fresh graduates with Java and Spring Boot experience to master testing practices, TDD methodology, and automation testing tools used in production environments.

## Sample Project: "Task Management API"
A Spring Boot application that provides REST APIs for managing tasks with user authentication, featuring:
- Task CRUD operations
- User management
- Task assignment and status tracking
- Database persistence (PostgreSQL)
- Redis caching for performance
- Input validation and error handling

## Learning Objectives
By the end of this bootcamp, developers will:
- Master TDD red-green-refactor cycle
- Write effective unit tests achieving 80%+ coverage
- Implement integration tests for Spring Boot applications
- Test REST APIs thoroughly
- Mock external dependencies appropriately
- Test database interactions and caching layers
- Apply testing best practices and patterns

---

## Day 1: Testing Fundamentals & TDD Introduction

### Hands-on Lab Session (8 hours)

**Part 1: Project Setup & First TDD Cycle**
- Set up Maven project with testing dependencies
- Create basic Task entity with TDD approach
- Write first failing test (Red)
- Implement minimal code to pass (Green)
- Refactor and improve (Refactor)
- Practice with TaskValidator class using TDD

**Key Concepts Covered:**
- `@Test`, `@BeforeEach`, `@AfterEach` annotations
- Assertion methods: `assertEquals`, `assertTrue`, `assertThrows`
- Test naming conventions
- Arrange-Act-Assert pattern

**Part 2: Advanced Unit Testing with Mockito**
- Create TaskService with repository dependency
- Write tests using `@Mock` and `@InjectMocks`
- Practice stubbing with `when().thenReturn()`
- Verify interactions with `verify()`
- Handle exceptions in tests with `doThrow()`

**Key Concepts Covered:**
- `@ExtendWith(MockitoExtension.class)`
- `@Mock`, `@InjectMocks`, `@Spy` annotations
- `ArgumentMatchers` and `ArgumentCaptor`
- Verifying method calls and arguments

### Dev Sync Deliverables (30 minutes)
- Code review of day's work
- Q&A session
- Preview of Day 2

---

## Day 2: Spring Boot Testing & MockMvc

### Hands-on Lab Session (8 hours)

**Part 1: Testing TaskController with MockMvc**
- Create TaskController with CRUD endpoints
- Write `@WebMvcTest` for controller layer
- Use MockMvc to test HTTP requests/responses
- Test request validation and error handling
- Mock service layer dependencies

**Key Concepts Covered:**
- `@WebMvcTest` annotation
- `MockMvc.perform()` methods
- `@MockBean` for mocking Spring beans
- Testing JSON serialization/deserialization
- HTTP status code assertions

**Part 2: Database Testing with @DataJpaTest**
- Create custom repository methods
- Use `@DataJpaTest` for repository testing
- Write tests for CRUD operations
- Test custom queries and specifications
- Handle database constraints and exceptions

**Key Concepts Covered:**
- `@DataJpaTest` annotation
- `TestEntityManager` for test data setup
- `@Sql` annotation for test data scripts
- Testing JPA relationships and cascading
- Custom repository method testing

### Dev Sync Deliverables (30 minutes)
- Code review and best practices discussion
- Common pitfalls in Spring Boot testing

---

## Day 3: API Testing & Integration Tests

### Hands-on Lab Session (8 hours)

**Part 1: Comprehensive API Testing with RestAssured**
- Set up `@SpringBootTest` for full integration tests
- Write RestAssured tests for all CRUD operations
- Test request/response body validation
- Test HTTP headers and status codes
- Implement test data builders pattern

**Key Concepts Covered:**
- `@SpringBootTest(webEnvironment = RANDOM_PORT)`
- RestAssured given().when().then() syntax
- JSON path assertions
- Request/response logging
- Test data builders and object mothers

**Part 2: Full Stack Integration Tests**
- Create end-to-end user workflow tests
- Test user authentication and authorization
- Implement database state verification
- Test error scenarios and edge cases
- Set up test profiles and configurations

**Key Concepts Covered:**
- `@TestPropertySource` for test configurations
- `@DirtiesContext` for context management
- Testing security configurations
- Database state assertions
- Parameterized tests with `@ParameterizedTest`

### Dev Sync Deliverables (30 minutes)
- Integration testing best practices
- When to write integration vs unit tests

---

## Day 4: Caching & Advanced Testing Patterns

### Hands-on Lab Session (8 hours)

**Part 1: Implementing and Testing Cache Layer**
- Add Redis caching to TaskService
- Set up embedded Redis for tests
- Write tests for cache hit/miss scenarios
- Test cache eviction policies
- Verify caching behavior with spies

**Key Concepts Covered:**
- `@Cacheable`, `@CacheEvict` annotations
- Embedded Redis setup with TestContainers
- Cache testing strategies
- Verifying caching behavior
- Performance test basics

**Part 2: Refactoring Tests with Advanced Patterns**
- Create custom assertion methods
- Implement Page Object pattern for API tests
- Build test data factories
- Extract common test utilities
- Implement contract testing basics

**Key Concepts Covered:**
- Builder pattern for test data
- Custom AssertJ assertions
- Test utility classes
- Page Object pattern for APIs
- Test categorization with `@Tag`

### Dev Sync Deliverables (30 minutes)
- Code quality metrics review
- Coverage analysis and improvement strategies

---

## Day 5: TDD Mastery & Real-world Scenarios

### Hands-on Lab Session (7.5 hours)

**Part 1: TDD Challenge - User Notification System**
- Build notification system from scratch using strict TDD
- Implement email and SMS notifications
- Add notification preferences and scheduling
- Handle external service failures
- Practice continuous refactoring

**Key Concepts Covered:**
- Outside-in vs inside-out TDD
- Mockist vs classicist approaches
- Refactoring under test coverage
- Handling third-party integrations
- Error handling strategies

**Part 2: Final Project - Complete Feature Implementation**
- Choose a complex feature (task scheduling, reporting, etc.)
- Implement using strict TDD approach
- Achieve 80%+ code coverage
- Include all testing layers
- Perform final code review

**Key Concepts Covered:**
- Maven Surefire configuration
- Test execution profiles
- Coverage reporting with JaCoCo
- Continuous testing strategies
- Code review checklist for tests

### Final Session (30 minutes)
**Wrap-up & Next Steps**
- Project presentations and code review
- Best practices summary
- Recommended reading and resources
- Q&A session

---

## Required Dependencies (pom.xml additions)

```xml
<dependencies>
    <!-- Testing Dependencies -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>it.ozimov</groupId>
        <artifactId>embedded-redis</artifactId>
        <version>0.7.3</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
<plugins>
    <!-- JaCoCo for code coverage -->
    <plugin>
        <groupId>org.jacoco</groupId>
        <artifactId>jacoco-maven-plugin</artifactId>
        <version>0.8.7</version>
        <executions>
            <execution>
                <goals>
                    <goal>prepare-agent</goal>
                </goals>
            </execution>
            <execution>
                <id>report</id>
                <phase>test</phase>
                <goals>
                    <goal>report</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</plugins>
</build>
```

## Assessment Criteria

### Daily Assessments
- Code quality and TDD adherence
- Test coverage metrics
- Understanding of concepts through Q&A
- Practical implementation skills

### Final Project Evaluation
- **Code Coverage**: Minimum 80% line coverage
- **Test Quality**: Meaningful assertions, proper test structure
- **TDD Implementation**: Evidence of red-green-refactor cycles
- **Best Practices**: Proper mocking, test organization, naming conventions
- **Integration**: All testing layers working together

## Resources and References

### Books
- "Test Driven Development: By Example" by Kent Beck
- "Growing Object-Oriented Software, Guided by Tests" by Freeman & Pryce
- "Effective Unit Testing" by Lasse Koskela

### Documentation
- [Spring Boot Testing Documentation](https://spring.io/guides/gs/testing-web/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [RestAssured Documentation](https://rest-assured.io/)

### Practice Exercises
- Daily coding katas focusing on TDD
- Code review sessions with peers
- Refactoring exercises on legacy code

## Success Metrics
- All participants achieve 80%+ code coverage on final project
- Demonstration of TDD red-green-refactor cycle
- Ability to write effective unit and integration tests
- Understanding of when and how to use different testing tools
- Confidence in testing complex Spring Boot applications