# deployment-tracker
A Spring Boot service that tracks releases of artifacts into all environment. Its UI is [release-the-tracken](https://github.com/blackbaud/release-the-tracken)

## Running locally
- Since this service uses the Github API, you'll need to authenticate with your personal github account. 
```
./gradlew -Pgithub.username={your username} -Pgithub.accessToken={access token} bootrun
```
- To generate your github access token
    - https://help.github.com/articles/creating-an-access-token-for-command-line-use/
