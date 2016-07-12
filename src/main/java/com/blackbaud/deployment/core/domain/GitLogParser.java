package com.blackbaud.deployment.core.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Log4j
public class GitLogParser {
    private List<RevCommit> commits;
    private static final Pattern pattern = Pattern.compile("(lum|lo)[^0-9]?(\\d+)");

    public Set<String> getStories() {
        log.debug("Commit list size: " + commits.size());
        if (commits.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> storyUrls = new TreeSet<>();
        for (RevCommit commit : commits) {
            log.debug("checking commit " + commit.getId().getName() + " - " + commit.getFullMessage());
            String storyId = parseStoryId(commit.getFullMessage());
            if (storyId != null) {
                storyUrls.add(storyId);
            }
        }
        return storyUrls;
    }

    private String parseStoryId(String commitMessage) {
        Matcher m = pattern.matcher(commitMessage.toLowerCase());
        return m.find() ? m.group(1).toUpperCase() + "-" + m.group(2) : null;
    }

    public Set<String> getDevelopers() {
        if (commits.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> developers = new TreeSet<>();
        for (RevCommit commit : commits) {
            log.debug("checking commit " + commit.getId().getName() + " - " + commit.getAuthorIdent().getName());
            developers.add(commit.getAuthorIdent().getName());
        }
        return developers;
    }
}
