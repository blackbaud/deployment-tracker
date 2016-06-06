package com.blackbaud.deployment.core.domain

import spock.lang.Specification

class GithubRepositoryServiceSpec extends Specification {

    private GithubRepositoryService service = new GithubRepositoryService()

    def "ParseStories"() {
        given:
        List<String> commits = ["LUM-1",
                                "lum-2",
                                "lUm 3",
                                "lum.4",
                                "lum7773: blah",
                                "https://jira.blackbaud.com/browse/LUM-5"];
        expect:
        service.parseStories(commits) == [
                "LUM-1",
                "LUM-2",
                "LUM-3",
                "LUM-4",
                "LUM-5",
                "LUM-7773"
        ] as Set
    }
}
