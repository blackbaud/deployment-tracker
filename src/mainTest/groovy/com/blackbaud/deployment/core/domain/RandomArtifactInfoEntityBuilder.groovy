package com.blackbaud.deployment.core.domain

import org.apache.commons.lang.StringUtils

import static com.blackbaud.deployment.api.ClientARandom.aRandom

class RandomArtifactInfoEntityBuilder extends ArtifactInfoEntity.ArtifactInfoEntityBuilder {

    public RandomArtifactInfoEntityBuilder() {
        artifactId(aRandom.text(20))
        buildVersion(aRandom.text(20))
        gitSha(aRandom.text(20))
        storyIds(new LinkedHashSet<String>(Arrays.asList(StringUtils.split(aRandom.words(20)))))
        authors(new LinkedHashSet<String>(Arrays.asList(StringUtils.split(aRandom.words(20)))))
        ;
    }

}
