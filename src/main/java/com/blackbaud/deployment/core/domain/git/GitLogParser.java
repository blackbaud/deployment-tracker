package com.blackbaud.deployment.core.domain.git;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.eclipse.jgit.revwalk.RevCommit;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Log4j
public class GitLogParser {
    private List<RevCommit> commits;
    private static final Pattern pattern = Pattern.compile("(lum|lo)[^0-9]?(\\d+)");

    public List<GitLogEntity> getGitLogEntities(String artifactId) {
        log.debug("Commit list size: " + commits.size());
        if (commits.isEmpty()) {
            return Collections.emptyList();
        }

        List<GitLogEntity> gitLogEntities = new ArrayList<>();

        for (RevCommit commit : Lists.reverse(commits)) {
            Instant instant = Instant.ofEpochSecond(commit.getCommitTime());
            GitLogEntity entity = GitLogEntity.builder()
                    .artifactId(artifactId)
                    .gitSha(commit.getName())
                    .author(commit.getAuthorIdent().getName())
                    .storyId(parseStoryId(commit.getFullMessage()))
                    .commitTime(ZonedDateTime.ofInstant(instant, ZoneId.of("UTC")))
                    .build();
            gitLogEntities.add(entity);
        }

        return gitLogEntities;
    }

    private String parseStoryId(String commitMessage) {
        Matcher m = pattern.matcher(commitMessage.toLowerCase());
        return m.find() ? m.group(1).toUpperCase() + "-" + m.group(2) : null;
    }

}
