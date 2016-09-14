# deployment-tracker
A Spring Boot service that tracks releases of artifacts into all environment. Its UI is [release-the-tracken](https://github.com/blackbaud/release-the-tracken)

## Running locally
Since this service uses the Github API, you'll need to authenticate with your personal github account. 
- In `application.properties`, change `github.username` to your github username. Obviously, don't check in this change 
- Then 
    ```
    ./gradlew -Pgithub.password={your password or access token} bootrun
    ```