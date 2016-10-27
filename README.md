# deployment-tracker
A Spring Boot service that tracks releases of artifacts into all environment. Its UI is [release-the-tracken](https://github.com/blackbaud/release-the-tracken)

## Removing an artifact from the deployment tracker
To remove an artifact as a releasable artifact and prevent it from displaying in the release tracker,
add the name of the artifact to the ReleaseService class in the nonReleasable list.

## Running locally
- Since this service uses the Github API, you'll need to authenticate with your personal github account. 
```
./gradlew -Pgithub.username={your username} -Pgithub.accessToken={access token} bootrun
```
- To generate your github access token
    - https://help.github.com/articles/creating-an-access-token-for-command-line-use/
