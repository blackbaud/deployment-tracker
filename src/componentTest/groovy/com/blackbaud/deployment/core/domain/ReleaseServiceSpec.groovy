package com.blackbaud.deployment.core.domain

import com.blackbaud.deployment.api.DeploymentInfo
import com.blackbaud.deployment.api.DevProdDeploymentInfos
import spock.lang.Specification
import static com.blackbaud.deployment.core.CoreARandom.aRandom

class ReleaseServiceSpec extends Specification {

    private ReleaseService releaseService = new ReleaseService();
    private DeploymentInfoService mockDeploymentInfoService = Mock()
    private GithubRepositoryService mockGithubRepoService = Mock()

    def setup(){
        releaseService.deploymentInfoService = mockDeploymentInfoService
        releaseService.repositoryService = mockGithubRepoService
    }

    def "getCurrentSummary returns artifact with different versions in dev and prod"() {
        given:
        DeploymentInfo devDeploymentInfo = aRandom.deploymentInfo().build()
        String artifactId = devDeploymentInfo.artifactId
        DeploymentInfo prodDeploymentInfo = aRandom.deploymentInfo().artifactId(artifactId).build()

        inDev(devDeploymentInfo)
        inProd(prodDeploymentInfo)

        DevProdDeploymentInfos expected = DevProdDeploymentInfos.builder()
                .dev(devDeploymentInfo)
                .prod(prodDeploymentInfo)
                .stories(null)
                .build();

        expect:
        releaseService.getCurrentSummary() == [ (artifactId) : expected]
    }

    def "getCurrentSummary returns artifact that is in prod but not dev"() {
        given:
        DeploymentInfo prodDeploymentInfo = aRandom.deploymentInfo().build()
        inProd(prodDeploymentInfo)
        nothingInDev()

        String artifactId = prodDeploymentInfo.artifactId
        DevProdDeploymentInfos expected = DevProdDeploymentInfos.builder()
                .dev(null)
                .prod(prodDeploymentInfo)
                .stories(null)
                .build();

        expect:
        releaseService.getCurrentSummary() == [ (artifactId) : expected]
    }

    def "getCurrentSummary returns artifact that is in dev but not prod"() {
        given:
        DeploymentInfo devDeploymentInfo = aRandom.deploymentInfo().build()
        inDev(devDeploymentInfo)
        nothingInProd()

        String artifactId = devDeploymentInfo.artifactId
        DevProdDeploymentInfos expected = DevProdDeploymentInfos.builder()
                .dev(devDeploymentInfo)
                .prod(null)
                .stories(null)
                .build();

        expect:
        releaseService.getCurrentSummary() == [ (artifactId) : expected]
    }

    def inDev(DeploymentInfo deploymentInfo){
        mockDeploymentInfoService.findManyByFoundationAndSpace(ReleaseService.DEV_FOUNDATION, ReleaseService.DEV_SPACE) >> [deploymentInfo]
    }

    def inProd(DeploymentInfo deploymentInfo){
        mockDeploymentInfoService.findManyByFoundationAndSpace(ReleaseService.PROD_FOUNDATION, ReleaseService.PROD_SPACE) >> [deploymentInfo]
    }

    def nothingInDev(){
        mockDeploymentInfoService.findManyByFoundationAndSpace(ReleaseService.DEV_FOUNDATION, ReleaseService.DEV_SPACE) >> []
    }

    def nothingInProd(){
        mockDeploymentInfoService.findManyByFoundationAndSpace(ReleaseService.PROD_FOUNDATION, ReleaseService.PROD_SPACE) >> []
    }
}
