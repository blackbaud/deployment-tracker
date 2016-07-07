package com.blackbaud.deployment.core.domain;

import com.blackbaud.deployment.ArtifactInfoConverter;
import com.blackbaud.deployment.api.ArtifactInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArtifactInfoService {

    @Autowired
    private ArtifactInfoConverter converter;


    @Autowired
    private ArtifactInfoRepository artifactInfoRepository;

    @Autowired
    private GitLogParserFactory gitLogParserFactory;

    public ArtifactInfo put(String artifactId, String buildVersion, ArtifactInfoEntity newArtifact){
        newArtifact.setArtifactId(artifactId);
        newArtifact.setBuildVersion(buildVersion);
        ArtifactInfoEntity lastArtifact = artifactInfoRepository.findFirstByArtifactIdOrderByBuildVersionDesc(artifactId);
        String oldSha = lastArtifact == null ? null : lastArtifact.getGitSha();
        GitLogParser parser = gitLogParserFactory.createParser(artifactId, oldSha, newArtifact.getGitSha());
        LinkedHashSetDelimitedStringConverter linkedHashSetConverter = new LinkedHashSetDelimitedStringConverter();
        newArtifact.setAuthors(linkedHashSetConverter.convertToEntityAttribute(StringUtils.join(parser.getDevelopers(), ",")));
        newArtifact.setStoryIds(linkedHashSetConverter.convertToEntityAttribute(StringUtils.join(parser.getStories(), ",")));
        return converter.toApi(artifactInfoRepository.save(newArtifact));
    }
}
