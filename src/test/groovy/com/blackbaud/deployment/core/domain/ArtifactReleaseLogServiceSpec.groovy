package com.blackbaud.deployment.core.domain

import spock.lang.Specification

import java.time.ZoneId
import java.time.ZonedDateTime

class ArtifactReleaseLogServiceSpec extends Specification {

    def "should parse a release version into a ZonedDateTime"() {
        given:
        ArtifactReleaseLogService artifactReleaseInfoLogService = new ArtifactReleaseLogService()
        String releaseVersion = "20160818_200953"
        ZonedDateTime expected = ZonedDateTime.of(2016, 8, 18, 20, 9, 53, 0, ZoneId.of("UTC"));

        when:
        ZonedDateTime actual = artifactReleaseInfoLogService.convertReleaseVersionToDate(releaseVersion)

        then:
        actual == expected
    }

    def "should return null if parsing a release version fails"() {
        given:
            ArtifactReleaseLogService artifactReleaseInfoLogService = new ArtifactReleaseLogService()
            //String releaseVersion = "bad_format"
        String releaseVersion = "badformat"

        expect:
            artifactReleaseInfoLogService.convertReleaseVersionToDate(releaseVersion) == null
    }

}
