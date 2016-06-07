package com.blackbaud.deployment.core.domain;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Log4j
public class GithubRepository {

    private Repository repository;
    private UsernamePasswordCredentialsProvider githubCredentialsProvider;
    private Path workspace;

    @SneakyThrows
    public List<String> getCommitsBetween(String fromSha, String toSha) {
        try {
            File cloneDir = workspace.resolve(repository.getName()).toFile();
            cloneOrFetch(cloneDir);
            Git gitProject = Git.open(cloneDir);
            List<String> allCommits = new ArrayList<>();
            for (RevCommit commit : getCommits(gitProject, fromSha, toSha)) {
                allCommits.add(commit.getAuthorIdent().getName() + " - " + commit.getFullMessage());
            }
            return allCommits;
        } catch (Exception ex) {
            log.warn("Failed to retrieve commits for repositoryUrl=" + repository.getCloneUrl() + " between sha:" + fromSha + " and sha:" + toSha, ex);
            return Arrays.asList("Failed");
        }
    }

    private Iterable<RevCommit> getCommits(Git gitProject, String fromSha, String toSha) throws IncorrectObjectTypeException, MissingObjectException, GitAPIException {
        Iterable<RevCommit> commits = gitProject
                .log()
                .addRange(ObjectId.fromString(fromSha), ObjectId.fromString(toSha))
                .call();
        return commits;
    }

    @SneakyThrows
    private void cloneOrFetch(File targetDir) {
        if (targetDir.exists()) {
            log.debug("fetching to " + targetDir);
            Git.open(targetDir).fetch();
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
}
