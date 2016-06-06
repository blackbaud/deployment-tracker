package com.blackbaud.deployment.core.domain;

import lombok.SneakyThrows;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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

    public Set<String> getStories(String artifactId, String fromSha, String toSha) {
        if(fromSha.equals(toSha)) return Collections.emptySet();
        GithubRepository repo = getRepository(artifactId);
        List<String> commits = repo.getCommitsBetween(fromSha, toSha);
        return parseStories(commits);
    }

    @SneakyThrows
    private GithubRepository getRepository(String projectName) {
        for (Repository repo : repositoryService.getOrgRepositories("Blackbaud")) {
            if (repo.getName().equals(projectName)) {
                return new GithubRepository(repo, githubCredentialsProvider);
            }
        }
        throw new RuntimeException("Cannot find repository: " + projectName);
    }

    private Set<String> parseStories(List<String> commits) {
        Set<String> storyUrls = new TreeSet<>();
        Pattern pattern = Pattern.compile("lum[^0-9]?(\\d*)");
        for (String commit : commits) {
            Matcher m = pattern.matcher(commit.toLowerCase());
            if (m.find()) {
                storyUrls.add("LUM-" + m.group(1));
            }
        }
        return storyUrls;
    }

//    private String getStoryLink(String storyNumber) {
//        try {
//            return new URL("http", "jira.blackbaud.com", "/browse/LUM-" + storyNumber);
//        } catch (MalformedURLException e) {}
//        return null;
//    }
}
