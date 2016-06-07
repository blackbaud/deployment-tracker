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
import org.eclipse.jgit.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Log4j
public class GithubRepository {

    private Repository repository;
    private UsernamePasswordCredentialsProvider githubCredentialsProvider;

    @SneakyThrows
    public List<String> getCommitsBetween(String fromSha, String toSha) {
        File cloneDir = null;

        try {
            cloneDir = getCloneDir();
            clone(cloneDir);
            Git gitProject = Git.open(cloneDir);
            List<String> allCommits = new ArrayList<>();
            for (RevCommit commit : getCommits(gitProject, fromSha, toSha)) {
                allCommits.add(commit.getAuthorIdent().getName() + " - " + commit.getFullMessage());
            }
            return allCommits;
        } catch (Exception ex) {
            log.warn("Failed to retrieve commits for repo:" + repository.getCloneUrl() + " between sha:" + fromSha + " and sha:" + toSha, ex);
            return Arrays.asList("Failed");
        } finally {
            if (cloneDir != null && cloneDir.exists()) {
                try {
                    FileUtils.delete(cloneDir, FileUtils.RECURSIVE);
                } catch (IOException ioex) {
                    log.error("Failed to delete clone dir:" + cloneDir, ioex);
                }
            }
        }
    }

    private Iterable<RevCommit> getCommits(Git gitProject, String fromSha, String toSha) throws IncorrectObjectTypeException, MissingObjectException, GitAPIException {
        Iterable<RevCommit> commits = gitProject
                .log()
                .addRange(ObjectId.fromString(fromSha), ObjectId.fromString(toSha))
                .call();
        return commits;
    }

    private File getCloneDir() throws IOException {
        File tmpDir = File.createTempFile(repository.getName(), "");
        tmpDir.delete();
        return tmpDir;
    }

    private void clone(File targetDir) {
        if (targetDir.exists()) {
            throw new RuntimeException("Target directory must not exist, path=${targetDir.absolutePath}");
        }
        log.debug("cloning to " + targetDir);

        targetDir.getParentFile().mkdirs();
        try {
            Git.cloneRepository()
                    .setDirectory(targetDir)
                    .setURI(repository.getCloneUrl())
                    .setCredentialsProvider(githubCredentialsProvider)
                    .setBare(true)
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }
}
