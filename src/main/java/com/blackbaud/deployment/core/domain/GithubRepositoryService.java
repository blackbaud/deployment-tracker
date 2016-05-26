package com.blackbaud.deployment.core.domain;

import org.apache.commons.logging.Log;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GithubRepositoryService {

    private RepositoryService repositoryService;
    private String githubUsername = "Blackbaud-OdsDeploy";
    private String githubPassword = "0DPassword2!";
    private UsernamePasswordCredentialsProvider githubCredentialsProvider;

    public GithubRepositoryService() {
        githubCredentialsProvider = new UsernamePasswordCredentialsProvider(githubUsername, githubPassword);
        GitHubClient gitHubClient = new GitHubClient();
        gitHubClient.setCredentials(githubUsername, githubPassword);
        repositoryService = new RepositoryService(gitHubClient);
    }

    // TODO find a better way to get repository (pattern matching?)
    public GithubRepository getRepository(String projectName){
        try {
            for (Repository repo : repositoryService.getOrgRepositories("Blackbaud")) {
                if (repo.getName().equals(projectName)) {
                    return new GithubRepository(repo, githubCredentialsProvider);
                }
            }
        } catch (IOException e) {}
        throw new RuntimeException("Cannot find repository " + projectName);
    }

    public List<String> getCommitsBetween(String artifactId, String fromSha, String toSha) throws IOException, GitAPIException {
        GithubRepository repo = getRepository(artifactId);
        return repo.getCommitsBetween(fromSha, toSha);
    }

    public Set<URL> parseStories(List<String> commits) {
        Set<URL> storyUrls = new HashSet<>();
        Pattern pattern = Pattern.compile("lum.(\\d*)\\w*");
        for (String commit : commits) {
            Matcher m = pattern.matcher(commit.toLowerCase());
            if (m.find()) {
                storyUrls.add(getStoryLink(m.group(1)));
            }
        }
        return storyUrls;
    }

    private URL getStoryLink(String storyNumber) {
        try {
            return new URL("http", "jira.blackbaud.com", "/browse/LUM-" + storyNumber);
        } catch (MalformedURLException e) {}
        return null;
    }

}
