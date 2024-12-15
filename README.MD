# Reqres and DemoQA Testing Project

This project tests the following:

1. **Reqres REST API**:
    - Validates API functionality on the [Reqres website](https://reqres.in/), including user registration, data retrieval, and error handling.
2. **DemoQA UI**:
    - Automates browser interactions to validate functionality on the [DemoQA website](https://demoqa.com/).

## Features

- **API Tests**:
    - User retrieval on Reqres
    - User registration (successful and unsuccessful scenarios)
    - Resource sorting validation
    - Avatar and email consistency checks
    - Deletion operations
    - Synchronization between server and client dates
- **UI Tests**:
    - Validation of form inputs on DemoQA
    - Automated browser interactions

## Technologies Used

- **Java**: Programming language for the test suite
- **REST-assured**: Library for API testing
- **Selenide**: Framework for concise UI testing (used for DemoQA tests)
- **JUnit**: Framework for writing and running tests
- **Maven**: Build automation tool
- **Allure**: Reporting tool for test results

## Project Structure

```
reqres.in-rest-test/
├── src/main/java      # Application source code
├── src/test/java      # Test classes
├── pom.xml            # Maven configuration
└── .idea/             # IntelliJ project files
```

## How to Set Up and Run the Tests

### Prerequisites

1. **Java Development Kit (JDK)**:
    - Install [JDK 11 or higher](https://www.oracle.com/java/technologies/javase-downloads.html).
    - Set the `JAVA_HOME` environment variable.
2. **Maven**:
    - Install [Maven](https://maven.apache.org/download.cgi).
    - Add Maven to your `PATH`.
3. **IntelliJ IDEA** (optional):
    - For better code navigation and running tests.
4. **BrowserDriver**:
    - Install the appropriate WebDriver for your browser (e.g., ChromeDriver).
    - Add the driver to your `PATH`.

### Configuration

- Update the `application.properties` file with the base URL and API endpoint information:
  ```properties
  base.url=https://reqres.in
  api.path=/api
  ```

### Running the Tests

#### API Tests
1. Clone the repository:
   ```bash
   git clone https://github.com/Konstantin-Pavlov/api-ui-tests
   cd reqres.in-rest-test
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Execute the API tests:
   ```bash
   mvn test
   ```

#### UI Tests
1. Update the WebDriver path in your system.
2. Run the UI tests:
   ```bash
   mvn test -Dtest=DemoQaUiTest
   ```

### Generating Allure Reports

1. Run the tests to generate the results:
   ```bash
   mvn test
   ```

2. Serve the Allure report:
   ```bash
   allure serve target/allure-results
   ```

## Test Cases

### API Tests

#### User Not Found
- Ensures a user with a specific ID does not exist on Reqres.

#### Avatar Consistency
- Verifies that user avatars contain their IDs and emails end with `@reqres.in`.

#### Successful and Unsuccessful Registration
- Tests for user registration with valid and invalid data.

#### Resource Sorting
- Confirms that the LIST<RESOURCE> operation returns data sorted by year.

#### User Deletion
- Validates that deleting a user results in the correct response code.

#### Server and Client Date Synchronization
- Ensures that the server update time matches the local machine's UTC time.

### UI Tests

#### Form Input Validation
- Automates filling forms on DemoQA and validates successful submissions.

#### Browser Interactions
- Validates UI elements and interactions, including button clicks and field validation.

## Contributing

Contributions are welcome! Feel free to submit a pull request or report issues.

## License

This project is open-source and available under the [MIT License](LICENSE).
