package com.blackbaud.deployment.core.domain.git;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.IteratorUtils;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@AllArgsConstructor
@Log4j
public class GithubRepository {

    private Repository repository;
    private UsernamePasswordCredentialsProvider githubCredentialsProvider;
    private Path workspace;
    private Git gitProject;

    public GithubRepository(Repository repository, UsernamePasswordCredentialsProvider credentials, Path workspace) throws IOException {
        this.repository = repository;
        this.githubCredentialsProvider = credentials;
        this.workspace = workspace;
        setGitProject();
    }

    private void setGitProject() throws IOException {
        File cloneDir = workspace.resolve(repository.getName()).toFile();
        cloneIfMissing(cloneDir);
        gitProject = Git.open(cloneDir);
    }

    public List<RevCommit> getCommits(String fromSha, String toSha) {
        Iterable<RevCommit> commits;
        try {
            commits = getCommitLogFetchAndRetryOnFailure(() -> gitProject.log()
                    .addRange(ObjectId.fromString(fromSha), ObjectId.fromString(toSha))
                    .call()
            );
        } catch (Exception e) {
            throw new CannotRetrieveCommitsException("Failed to retrieve commits between from=" + fromSha + " to=" + toSha + " for repo=" + repository.getName(), e);
        }
        return (List<RevCommit>) IteratorUtils.toList(commits.iterator());
    }

    private Iterable<RevCommit> getCommitLogFetchAndRetryOnFailure(RevCommitSource revCommitSource) throws Exception {
        try {
            return revCommitSource.getRevCommits();
        } catch (Exception e) {
            log.debug("Failed to resolve commits, fetching latest...", e);
            fetch();
            return revCommitSource.getRevCommits();
        }
    }

    public List<RevCommit> getCommitsUntil(String toSha) {
        Iterable<RevCommit> commits;
        try {
            commits = getCommitLogFetchAndRetryOnFailure(() -> gitProject.log()
                    .add(ObjectId.fromString(toSha))
                    .call()
            );
        } catch (Exception e) {
            throw new CannotRetrieveCommitsException("Failed to retrieve commits to=" + toSha + " for repo=" + repository.getName(), e);
        }
        return (List<RevCommit>) IteratorUtils.toList(commits.iterator());
    }

    @SneakyThrows
    private void cloneIfMissing(File targetDir) {
        if (targetDir.exists() == false) {
            log.debug("cloning to " + targetDir);
            Git.cloneRepository()
                    .setDirectory(targetDir)
                    .setURI(repository.getCloneUrl())
                    .setCredentialsProvider(githubCredentialsProvider)
                    .setBare(true)
                    .call();
        }

    }

    @SneakyThrows
    private void fetch() {
        File targetDir = gitProject.getRepository().getDirectory();
        log.debug("fetching to " + targetDir);
        FetchResult fetchResult = gitProject.fetch()
                .setCredentialsProvider(githubCredentialsProvider)
                .call();
        log.debug("fetch result for " + targetDir + " messages: " + fetchResult.getMessages());
    }


    private interface RevCommitSource {
        Iterable<RevCommit> getRevCommits() throws Exception;
    }

    public static class CannotRetrieveCommitsException extends RuntimeException {
        public CannotRetrieveCommitsException(String message, Exception cause) {
            super(message, cause);
        }
    }
}
