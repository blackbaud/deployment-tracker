package com.blackbaud.deployment.core.domain.git

import spock.lang.Specification
import spock.lang.Unroll

class GitLogParserSpec extends Specification {

    private GitLogParser parser = new GitLogParser()

    @Unroll
    def "ParseStoryNumber"() {
        expect:
        parser.parseStoryId(commitMessage) == storyNumber

        where:
        commitMessage                             | storyNumber
        "LUM-1"                                   | "LUM-1"
        "lum-2"                                   | "LUM-2"
        "lUm 3"                                   | "LUM-3"
        "lum.4"                                   | "LUM-4"
        "lum7773: blah"                           | "LUM-7773"
        "https://jira.blackbaud.com/browse/LUM-5" | "LUM-5"
        "column"                                  | null
        "foo"                                     | null
        "lo-345"                                  | "LO-345"
        "lsf-385"                                 | "LSF-385"
    }
}
