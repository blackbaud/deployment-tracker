package com.blackbaud.deployment.core.domain.git;

import com.blackbaud.deployment.api.ArtifactInfo;
import com.blackbaud.deployment.api.ArtifactReleaseDiff;
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

    public StoriesAndDevelopers getStoriesAndDevelopersForDependencies(ArtifactReleaseDiff artifactReleaseDiff) {
        if (noPreviousOrCurrentRelease(artifactReleaseDiff) || noPreviousOrCurrentDependencies(artifactReleaseDiff)) {
            return new StoriesAndDevelopers(new TreeSet<>(), new TreeSet<>());
        }
        List<ArtifactInfo> currentReleaseDependencies = artifactReleaseDiff.getCurrentRelease().getDependencies();
        List<ArtifactInfo> prevReleaseDependencies = artifactReleaseDiff.getPrevRelease().getDependencies();
        ArtifactInfo currentReleaseDependency = currentReleaseDependencies.get(0);
        ArtifactInfo prevReleaseDependency = prevReleaseDependencies.get(0);
        return getStoriesAndDevelopers(currentReleaseDependency.getArtifactId(), prevReleaseDependency.getGitSha(), currentReleaseDependency.getGitSha());
    }

    private boolean noPreviousOrCurrentRelease(ArtifactReleaseDiff artifactReleaseDiff) {
        return artifactReleaseDiff.getCurrentRelease() == null || artifactReleaseDiff.getPrevRelease() == null;
    }

    private boolean noPreviousOrCurrentDependencies(ArtifactReleaseDiff diff) {
        return !diff.prevReleaseHasDependencies() || !diff.currentReleaseHasDependencies();
    }
}
