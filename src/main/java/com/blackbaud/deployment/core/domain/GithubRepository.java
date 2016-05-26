package com.blackbaud.deployment.core.domain;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.egit.github.core.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

@AllArgsConstructor
public class GithubRepository {

    private Repository repository;
    private UsernamePasswordCredentialsProvider githubCredentialsProvider;

    public String getName() {
        return repository.getName();
    }

    public String getCloneUrl() {
        return repository.getCloneUrl();
    }

    @SneakyThrows
    public List<String> getCommitsBetween(String fromSha, String toSha) {
        Git gitProject = Git.open(getCloneDir());
        List<String> allCommits = new ArrayList<>();
        for (RevCommit commit : getCommits(gitProject, fromSha, toSha)) {
            allCommits.add(commit.getAuthorIdent().getName() + " - " + commit.getFullMessage());
        }
        return allCommits;
    }

    private Iterable<RevCommit> getCommits(Git gitProject, String fromSha, String toSha) throws IncorrectObjectTypeException, MissingObjectException, GitAPIException {
        Iterable<RevCommit> commits = gitProject
                .log()
                .addRange(ObjectId.fromString(fromSha), ObjectId.fromString(toSha))
                .call();
        return commits;
    }

    private File getCloneDir() throws IOException {
        File tmpDir = File.createTempFile(getName(), "");
        tmpDir.delete();
        clone(tmpDir);
        return tmpDir;
    }

    private void clone(File targetDir) {
        if (targetDir.exists()) {
            throw new RuntimeException("Target directory must not exist, path=${targetDir.absolutePath}");
        }

        targetDir.getParentFile().mkdirs();
        try {
            Git.cloneRepository()
                    .setDirectory(targetDir)
                    .setURI(getCloneUrl())
                    .setCredentialsProvider(githubCredentialsProvider)
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }
}
