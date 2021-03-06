package com.blackbaud.deployment.core.domain.git;

import com.blackbaud.deployment.core.domain.ArtifactInfoEntity;
import lombok.SneakyThrows;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class GitLogParserFactory {

    @Autowired
    private GitLogRepository gitLogRepository;

    private RepositoryService repositoryService;

    @Value("${github.username}")
    private String githubUsername;

    @Value("${github.accessToken}")
    private String githubAccessToken;

    private UsernamePasswordCredentialsProvider githubCredentialsProvider;
    private Path workspace;

    @PostConstruct
    public void initGitLogParserFactory() {
        githubCredentialsProvider = new UsernamePasswordCredentialsProvider(githubUsername, githubAccessToken);
        GitHubClient gitHubClient = new GitHubClient();
        gitHubClient.setCredentials(githubUsername, githubAccessToken);
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

    public GitLogParser createParser(ArtifactInfoEntity artifact) {
        String mostRecentSha = gitLogRepository.getMostRecentShaForArtifact(artifact.getArtifactId());
        if (mostRecentSha == null) {
            return createGitLogParserForNewProject(artifact.getArtifactId(), artifact.getGitSha());
        } else {
            return createGitLogParser(artifact.getArtifactId(), mostRecentSha, artifact.getGitSha());
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
