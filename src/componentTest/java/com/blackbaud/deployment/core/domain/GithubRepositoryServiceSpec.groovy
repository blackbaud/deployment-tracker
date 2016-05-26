package com.blackbaud.deployment.core.domain

import spock.lang.Specification

class GithubRepositoryServiceSpec extends Specification {

    private GithubRepositoryService service = new GithubRepositoryService()

    def "ParseStories"(){
        given:
        List<String> commits = ["LUM-1",
                                "lum-2",
                                "lUm 3",
                                "lum.4",
                                "https://jira.blackbaud.com/browse/LUM-5"];
        expect:
        Set<URL> urls = service.parseStories(commits)
        urls.size() == commits.size()
    }
}
