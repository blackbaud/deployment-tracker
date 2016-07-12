package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ArtifactInfoConverter;
import com.blackbaud.deployment.api.ArtifactInfo;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeSet;

@Component
@Log4j
public class ArtifactInfoService {

    @Autowired
    private ArtifactInfoConverter converter;


    @Autowired
    private ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    private GitLogParserFactory gitLogParserFactory;

    public ArtifactInfo create(String artifactId, String buildVersion, ArtifactInfoEntity newArtifact) {
        ArtifactInfoEntity lastArtifact = artifactInfoRepository.findFirstByArtifactIdAndBuildVersionLessThanOrderByBuildVersionDesc(artifactId, buildVersion);
        log.debug("last artifact " + lastArtifact);
        GitLogParser parser = gitLogParserFactory.createParser(lastArtifact, newArtifact);
        newArtifact.setArtifactId(artifactId);
        newArtifact.setBuildVersion(buildVersion);
        newArtifact.setAuthors(new TreeSet<>(parser.getDevelopers()));
        newArtifact.setStoryIds(new TreeSet<>(parser.getStories()));
        return converter.toApi(artifactInfoRepository.save(newArtifact));

    }

    public List<ArtifactInfoEntity> findBetweenBuildVersions(String artifactId, String fromVersion, String toVersion) {
        if (fromVersion == null) {
            return artifactInfoRepository.findByArtifactIdAndBuildVersionLessThanEqual(artifactId, toVersion);
        }
        if (toVersion == null) {
            return artifactInfoRepository.findByArtifactIdAndBuildVersionGreaterThan(artifactId, fromVersion);
        }
        return artifactInfoRepository.findByArtifactIdAndBuildVersionGreaterThanAndBuildVersionLessThanEqual(artifactId, fromVersion, toVersion);
    }
}
