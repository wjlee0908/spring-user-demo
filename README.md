## Spring User Demo

### Description

This project is a demonstration of user authentication implemented using the Spring Framework. It provides a simple and
efficient way to authenticate users within a Spring-based application.

### Features

- User CRUD operations
- User registration
- Login/logout functionality
    - RESTful API implementation
- Password encryption
- JWT authentication
    - `feature/BT-38/jwt-login` branch
- Session authentication
    - With session information stored in Redis
    - Session refresh
- Logging using `log4j2`

### Technologies Used

- `Spring Security`
- `Spring Session`
- `log4j2`
- `slf4j`
- `Spring Boot`
- `Spring Framework`

### Installation

1. Clone the repository:

```
git clone https://github.com/wjlee0908/spring-user-demo.git
```

2. Navigate to the project directory:

```
cd spring-user-demo
```

3. Build the project:

```
./gradlew build
```

4. Run the application:

```
./gradlew bootRun
```

### Usage

Once the application is running, you can access it through your web browser at `http://localhost:8080`. From there, you
can register as a new user, log in, and explore the authentication features.

### License

This project is licensed under the [MIT License](LICENSE).