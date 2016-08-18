package com.blackbaud.deployment.core.domain.git;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Component
public class GitLogService {
    @Autowired
    GitLogRepository gitLogRepository;

    public StoriesAndDevelopers getStoriesAndDevelopers(String artifactId, String fromSha, String toSha) {
        Set<String> stories = new TreeSet<>();
        Set<String> developers = new TreeSet<>();

        fetchGitLogEntries(artifactId, fromSha, toSha).forEach(gitLog -> {
            developers.add(gitLog.author);
            if (gitLog.storyId != null) {
                stories.add(gitLog.storyId);
            }
        });

        log.debug("addStoriesAndDevelopersFromDb got stories={} and developers={}", stories, developers);
        return new StoriesAndDevelopers(stories, developers);
    }

    private List<GitLogEntity> fetchGitLogEntries(String artifactId, String fromSha, String toSha) {
        if (toSha == null) {
            return Collections.emptyList();
        } else if (fromSha == null) {
            return gitLogRepository.fetchGitLogUntilSha(artifactId, toSha);
        } else {
            return gitLogRepository.fetchGitLogForCurrentAndPreviousGitShas(artifactId, toSha, fromSha);
        }
    }
}
