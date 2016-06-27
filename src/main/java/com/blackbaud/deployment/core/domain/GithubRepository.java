package com.blackbaud.deployment.core.domain;

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
        cloneOrFetch(cloneDir);
        gitProject = Git.open(cloneDir);
    }

    public List<RevCommit> getCommits(String fromSha, String toSha) {
        Iterable<RevCommit> commits = null;
        try {
            commits = gitProject
                    .log()
                    .addRange(ObjectId.fromString(fromSha), ObjectId.fromString(toSha))
                    .call();
        } catch (Exception e) {
            log.error("Could not retrieve commits between from=" + fromSha + " to=" + toSha + " for repo=" + repository.getName()+". This is a temporary workaround for cf message limit ", e);
            throw new CannotRetrieveCommitsException("Cannot retrieve commits between from=" + fromSha + " to=" + toSha + " for repo=" + repository.getName(), e);
        }
        return (List<RevCommit>) IteratorUtils.toList(commits.iterator());
    }

    public List<RevCommit> getCommitsUntil(String toSha) {
        Iterable<RevCommit> commits = null;
        try {
            commits = gitProject
                    .log()
                    .add(ObjectId.fromString(toSha))
                    .call();
        } catch (Exception e) {
            log.error("Could not retrieve commits to=" + toSha + " for repo=" + repository.getName()+". This is a temporary workaround for cf message limit ", e);
            throw new CannotRetrieveCommitsException("Cannot retrieve commits to=" + toSha + " for repo=" + repository.getName(), e);
        }
        return (List<RevCommit>) IteratorUtils.toList(commits.iterator());
    }

    @SneakyThrows
    private void cloneOrFetch(File targetDir) {
        if (targetDir.exists()) {
            log.debug("fetching to " + targetDir);
            FetchResult fetchResult = Git.open(targetDir).fetch().call();
            log.debug("fetch result for " + targetDir + " messages: " + fetchResult.getMessages());
        } else {
            log.debug("cloning to " + targetDir);
            Git.cloneRepository()
                    .setDirectory(targetDir)
                    .setURI(repository.getCloneUrl())
                    .setCredentialsProvider(githubCredentialsProvider)
                    .setBare(true)
                    .call();
        }

    }

    public class CannotRetrieveCommitsException extends RuntimeException {
        public CannotRetrieveCommitsException(String message, Exception cause) {
            super(message, cause);
        }
    }
}
