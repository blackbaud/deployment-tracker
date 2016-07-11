package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.api.ArtifactReleaseDiff;
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

    public GitLogParser createParser(ArtifactInfoEntity oldArtifact, ArtifactInfoEntity newArtifact) {
        if (oldArtifact == null) {
            return createGitLogParserForNewProject(newArtifact.getArtifactId(), newArtifact.getGitSha());
        } else {
            return createGitLogParser(newArtifact.getArtifactId(), oldArtifact.getGitSha(), newArtifact.getGitSha());
        }
    }

    private GitLogParser createGitLogParser(String projectName, String fromSha, String toSha) {
        GithubRepository repo = getRepository(projectName);
        List<RevCommit> commits = repo.getCommits(fromSha, toSha);
        return new GitLogParser(commits);
    }

    private GitLogParser createGitLogParserForNewProject(String projectName, String toSha) {
        GithubRepository repo = getRepository(projectName);
        List<RevCommit> commits = repo.getCommitsUntil(toSha);
        return new GitLogParser(commits);
    }

    public class InvalidRepositoryException extends RuntimeException {
        public InvalidRepositoryException(String message) {
            super(message);
        }
    }
}
