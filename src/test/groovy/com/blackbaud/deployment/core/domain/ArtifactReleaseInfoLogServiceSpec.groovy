package com.blackbaud.deployment.core.domain

import spock.lang.Specification

import java.time.ZoneId
import java.time.ZonedDateTime

class ArtifactReleaseInfoLogServiceSpec extends Specification {

    def "should parse a release version into a ZonedDateTime"() {
        given:
            ArtifactReleaseInfoLogService artifactReleaseInfoLogService = new ArtifactReleaseInfoLogService()
            String releaseVersion = "20160818_200953"
            ZonedDateTime expected = ZonedDateTime.of(2016, 8, 18, 20, 9, 53, 0, ZoneId.of("UTC"));

        when:
            ZonedDateTime actual = artifactReleaseInfoLogService.convertReleaseVersionToDate(releaseVersion)

        then:
            actual == expected
    }

}
