# 🤝 Contributing to Labour-Net

Thank you for your interest in contributing to Labour-Net! This document provides guidelines and instructions for contributing.

---

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Setup](#development-setup)
- [Coding Standards](#coding-standards)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Reporting Bugs](#reporting-bugs)
- [Feature Requests](#feature-requests)

---

## 📜 Code of Conduct

This project adheres to a Code of Conduct. By participating, you are expected to uphold this code. Please be respectful and professional in all interactions.

### Our Standards

- **Be respectful** of differing viewpoints and experiences
- **Accept constructive criticism** gracefully
- **Focus on what is best** for the community
- **Show empathy** towards other community members

---

## 🎯 How Can I Contribute?

### 1. Reporting Bugs

Found a bug? Help us by reporting it!

**Before submitting a bug report:**
- Check the existing issues to avoid duplicates
- Verify the bug exists in the latest version
- Collect information about the bug

**Bug Report Template:**

```markdown
**Description:**
A clear description of the bug

**Steps to Reproduce:**
1. Go to '...'
2. Click on '....'
3. See error

**Expected Behavior:**
What you expected to happen

**Actual Behavior:**
What actually happened

**Environment:**
- OS: [e.g., Windows 10, Ubuntu 20.04]
- Java Version: [e.g., 17]
- Browser: [e.g., Chrome 96]
- Spring Boot Version: [e.g., 3.5.4]

**Screenshots:**
If applicable, add screenshots

**Additional Context:**
Any other relevant information
```

### 2. Suggesting Enhancements

Have an idea? We'd love to hear it!

**Enhancement Template:**

```markdown
**Feature Description:**
Clear description of the enhancement

**Use Case:**
Why is this enhancement needed?

**Proposed Solution:**
How should it work?

**Alternatives Considered:**
Other solutions you've thought about

**Additional Context:**
Any other relevant information
```

### 3. Code Contributions

Want to write code? Great!

**Good First Issues:**
- Look for issues labeled `good first issue`
- Start with small improvements
- Ask questions if unclear

---

## 💻 Development Setup

### Prerequisites

- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Git
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### Setup Steps

**1. Fork the Repository**

Click the "Fork" button on GitHub

**2. Clone Your Fork**

```bash
git clone https://github.com/YOUR_USERNAME/labour-net.git
cd labour-chain
```

**3. Add Upstream Remote**

```bash
git remote add upstream https://github.com/ORIGINAL_OWNER/labour-net.git
```

**4. Create Database**

```sql
CREATE DATABASE miniproject;
```

**5. Configure Application**

Copy `application.properties.example` to `src/main/resources/application.properties` and update values.

**6. Build Project**

```bash
mvn clean install
```

**7. Run Application**

```bash
mvn spring-boot:run
```

**8. Create a Branch**

```bash
git checkout -b feature/your-feature-name
```

---

## 📝 Coding Standards

### Java Style Guide

Follow standard Java conventions:

**Naming Conventions:**
- Classes: `PascalCase` (e.g., `UserController`)
- Methods: `camelCase` (e.g., `findUserById`)
- Variables: `camelCase` (e.g., `userName`)
- Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_SIZE`)

**Code Structure:**
```java
@Controller
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        // Implementation
        return "user-profile";
    }
}
```

### Best Practices

**1. Use Dependency Injection**
```java
// Good
@Autowired
public UserController(UserService userService) {
    this.userService = userService;
}

// Avoid field injection
@Autowired
private UserService userService;
```

**2. Handle Exceptions Properly**
```java
try {
    // Code
} catch (Exception e) {
    log.error("Error occurred: ", e);
    // Handle appropriately
}
```

**3. Use Transactions**
```java
@Transactional(readOnly = true)
public List<User> getAllUsers() {
    return userRepository.findAll();
}
```

**4. Validate Input**
```java
@PostMapping("/register")
public String register(@Valid @ModelAttribute User user, BindingResult result) {
    if (result.hasErrors()) {
        return "register";
    }
    // Process
}
```

**5. Write Clean Code**
- Keep methods short (< 20 lines)
- Use meaningful variable names
- Add comments for complex logic
- Remove unused imports and code

### HTML/CSS Guidelines

**HTML:**
- Use semantic HTML5 elements
- Properly indent code (2 spaces)
- Use Thymeleaf attributes correctly

```html
<div th:if="${user != null}">
    <span th:text="${user.name}">Name</span>
</div>
```

**CSS:**
- Use CSS variables for colors
- Keep selectors simple
- Mobile-first responsive design

```css
:root {
    --primary-color: #2563eb;
}

.button {
    background-color: var(--primary-color);
}
```

---

## 📝 Commit Guidelines

### Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation changes
- **style**: Code style changes (formatting)
- **refactor**: Code refactoring
- **test**: Adding tests
- **chore**: Maintenance tasks

### Examples

```bash
feat(auth): add password reset functionality

Implemented password reset with email verification.
Users can now reset forgotten passwords.

Closes #123
```

```bash
fix(jobs): resolve job application duplicate issue

Fixed bug where users could apply to same job multiple times.
Added validation check in JobController.

Fixes #456
```

---

## 🔄 Pull Request Process

### Before Submitting

1. **Update your branch:**
```bash
git fetch upstream
git rebase upstream/main
```

2. **Test your changes:**
```bash
mvn clean test
mvn spring-boot:run
```

3. **Review your code:**
- Remove debug statements
- Check for typos
- Ensure code follows standards

4. **Commit your changes:**
```bash
git add .
git commit -m "feat: add new feature"
```

5. **Push to your fork:**
```bash
git push origin feature/your-feature-name
```

### Creating Pull Request

1. Go to GitHub and click "New Pull Request"
2. Select your branch
3. Fill in the PR template:

```markdown
**Description:**
Brief description of changes

**Related Issue:**
Closes #123

**Type of Change:**
- [ ] Bug fix
- [ ] New feature
- [ ] Documentation update
- [ ] Refactoring

**Testing:**
- [ ] Tested locally
- [ ] All tests pass
- [ ] No console errors

**Screenshots:**
If applicable

**Checklist:**
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No new warnings generated
```

4. Request review from maintainers

### PR Review Process

- Maintainers will review your PR
- Address feedback promptly
- Make requested changes
- Once approved, PR will be merged

---

## 🧪 Testing Guidelines

### Writing Tests

**Unit Tests:**
```java
@Test
public void testUserRegistration() {
    User user = new User();
    user.setUsername("testuser");
    user.setPassword("Test@123");
    
    User saved = userService.register(user);
    
    assertNotNull(saved.getId());
    assertEquals("testuser", saved.getUsername());
}
```

**Integration Tests:**
```java
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testLoginPage() throws Exception {
        mockMvc.perform(get("/workers/login"))
            .andExpect(status().isOk())
            .andExpect(view().name("worker_login"));
    }
}
```

### Running Tests

```bash
# All tests
mvn test

# Specific test
mvn test -Dtest=UserControllerTest

# With coverage
mvn test jacoco:report
```

---

## 📚 Documentation

### Code Comments

```java
/**
 * Registers a new worker with Aadhaar verification.
 *
 * @param username the user's username
 * @param aadharNumber the Aadhaar number for verification
 * @return the registered User object
 * @throws InvalidAadharException if Aadhaar is invalid
 */
public User registerWorker(String username, String aadharNumber) {
    // Implementation
}
```

### README Updates

If your changes affect usage:
- Update README.md
- Add examples
- Update screenshots if needed

---

## 🏆 Recognition

Contributors will be recognized in:
- README.md Contributors section
- GitHub Contributors page
- Release notes

---

## 🤔 Questions?

- Open a GitHub issue
- Contact maintainers
- Check existing documentation

---

## 📄 License

By contributing, you agree that your contributions will be licensed under the project's MIT License.

---

**Thank you for contributing to Labour-Net! 🎉**
