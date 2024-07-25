

Client & Resource Server - Order Service
Authorization Server - AuthServer - Spring OAuth2


Web
SEcurity
Client
Thymeleaf
DevTools

com.google.api-client
google-api-client


SecurityConfig 

```java
SecurityFilterChain securityFilterChain(HttpSecurity http) {

  // all request should be authenticated
  // public pages -> "/" , "/home"
  .oauth2Login(Customizer.withDefaults())



  return http.build();
}

```

Home Page

```java


@GetMapping("/")
String home(Model model, @AuthenticationPrincipal OAuth2User user) {

  model.addAttribute("username", user.getAttribuite.("user"))
  return "index"
}
```

index.html
```html
head
  title: Spring Security

body
  Oauth2 Login with Srping Security

  You are successfully logged in  ${username}

  Private
    <a>

  Public

```


```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            clientId: ""
            clientSecret: ""

```

Create 2 Controllers, Public and Private

PrivateController
```java
@GetMapping("/messages")
String
```

PublicController
```java
@GetMapping("/public/messages")
String publicMessage(Model model) {

  return
}
```