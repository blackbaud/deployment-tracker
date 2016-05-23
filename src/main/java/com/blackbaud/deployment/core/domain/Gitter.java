package com.blackbaud.deployment.core.domain;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Component
// TODO: actual implementation
public class Gitter {
    public String getCommitsBetween(String artifactId, String sha1, String sha2) {
        return "----------Commits----------";
    }

    public List<URL> parseStories(String commits) {
        try {
            return Arrays.asList(new URL("http", "jira.blackbaud.com", "/browse/LUM-7759"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
