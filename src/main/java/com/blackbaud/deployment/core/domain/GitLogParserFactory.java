package com.blackbaud.deployment.core.domain;

import lombok.SneakyThrows;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Component
public class GitLogParserFactory {

    private RepositoryService repositoryService;
    private String githubUsername = "Blackbaud-OdsDeploy";
    private String githubPassword = "0DPassword2!";

    private UsernamePasswordCredentialsProvider githubCredentialsProvider;
    private Path workspace;

    public GitLogParserFactory() {
        githubCredentialsProvider = new UsernamePasswordCredentialsProvider(githubUsername, githubPassword);
        GitHubClient gitHubClient = new GitHubClient();
        gitHubClient.setCredentials(githubUsername, githubPassword);
        repositoryService = new RepositoryService(gitHubClient);
        setupWorkspaceDir();
    }

    @SneakyThrows
    private void setupWorkspaceDir() {
        workspace = Files.createTempDirectory("workspace");
        workspace.toFile().mkdirs();
        workspace.toFile().deleteOnExit();
    }

    @SneakyThrows
    private GithubRepository getRepository(String projectName) {
        for (Repository repo : repositoryService.getOrgRepositories("Blackbaud")) {
            if (repo.getName().equals(projectName)) {
                return new GithubRepository(repo, githubCredentialsProvider, workspace);
            }
        }
        throw new InvalidRepositoryException("Cannot find repository with name " + projectName);
    }

    private GitLogParser getGitLogParser(String projectName, String fromSha, String toSha) {
        GithubRepository repo = getRepository(projectName);
        List<RevCommit> commits = repo.getCommits(fromSha, toSha);
        return new GitLogParser(commits);
    }

    private GitLogParser getGitLogParserForNewProject(String projectName, String toSha) {
        GithubRepository repo = getRepository(projectName);
        List<RevCommit> commits = repo.getCommitsUntil(toSha);
        return new GitLogParser(commits);
    }

    private GitLogParser getEmptyGitLogParser() {
        return new GitLogParser(Collections.emptyList());
    }

    public GitLogParser createParser(String artifactId, String prodSha, String devSha) {
        if (prodSha != null && devSha != null) {
            return getGitLogParser(artifactId, prodSha, devSha);
        } else if (devSha != null) {
            return getGitLogParserForNewProject(artifactId, devSha);
        }
        return getEmptyGitLogParser();
    }

    public class InvalidRepositoryException extends RuntimeException {
        public InvalidRepositoryException(String message) {
            super(message);
        }
    }
}
