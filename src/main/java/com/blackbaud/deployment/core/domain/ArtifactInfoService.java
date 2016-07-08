package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ArtifactInfoConverter;
import com.blackbaud.deployment.api.ArtifactInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;

@Component
public class ArtifactInfoService {

    @Autowired
    private ArtifactInfoConverter converter;


    @Autowired
    private ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    private GitLogParserFactory gitLogParserFactory;

    public ArtifactInfo put(String artifactId, String buildVersion, ArtifactInfoEntity newArtifact){
        ArtifactInfoEntity lastArtifact = artifactInfoRepository.findFirstByArtifactIdOrderByBuildVersionDesc(artifactId);
        GitLogParser parser = gitLogParserFactory.createParser(lastArtifact, newArtifact);
        newArtifact.setArtifactId(artifactId);
        newArtifact.setBuildVersion(buildVersion);
        newArtifact.setAuthors(new LinkedHashSet<>(parser.getDevelopers()));
        newArtifact.setStoryIds(new LinkedHashSet<>(parser.getStories()));
        return converter.toApi(artifactInfoRepository.save(newArtifact));
    }
}
