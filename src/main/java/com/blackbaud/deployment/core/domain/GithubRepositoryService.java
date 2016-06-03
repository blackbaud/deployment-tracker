package com.blackbaud.deployment.core.domain;

import lombok.SneakyThrows;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Component;

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

    // TODO: early exit if shas are the same
    public Set<String> getStories(String artifactId, String fromSha, String toSha) {
        GithubRepository repo = getRepository(artifactId);
        List<String> commits = repo.getCommitsBetween(fromSha, toSha);
        return parseStories(commits);
    }

    @SneakyThrows
    public List<String> getCommitsBetween(String artifactId, String fromSha, String toSha) {
        GithubRepository repo = getRepository(artifactId);
        return repo.getCommitsBetween(fromSha, toSha);
    }

    @SneakyThrows
    private GithubRepository getRepository(String projectName) {
        for (Repository repo : repositoryService.getOrgRepositories("Blackbaud")) {
            if (repo.getName().equals(projectName)) {
                return new GithubRepository(repo, githubCredentialsProvider);
            }
        }
        throw new RuntimeException("Cannot find repository " + projectName);
    }

    private Set<String> parseStories(List<String> commits) {
        Set<String> storyUrls = new HashSet<>();
        Pattern pattern = Pattern.compile("lum[^0-9](\\d*)\\w*");
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
