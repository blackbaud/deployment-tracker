package com.blackbaud.deployment.core.domain

import com.blackbaud.deployment.api.DeploymentInfo
import com.blackbaud.deployment.api.DevProdDeploymentInfos
import spock.lang.Specification
import static com.blackbaud.deployment.core.CoreARandom.aRandom

class ReleaseServiceSpec extends Specification {

    private ReleaseService releaseService = new ReleaseService();
    private DeploymentInfoService mockService = Mock()

    def setup(){
        releaseService.deploymentInfoService = mockService
    }

    def "getCurrentSummary returns artifact with different versions in dev and prod"() {
        given:
        DeploymentInfo devDeploymentInfo = aRandom.deploymentInfo().build()
        DeploymentInfo prodDeploymentInfo = aRandom.deploymentInfo().artifactId(devDeploymentInfo.artifactId).build()

        inDev(devDeploymentInfo)
        inProd(prodDeploymentInfo)

        expect:
        String artifactId = devDeploymentInfo.artifactId
        DevProdDeploymentInfos devProdDeploymentInfos = new DevProdDeploymentInfos(devDeploymentInfo, prodDeploymentInfo)
        releaseService.getCurrentSummary() == [ (artifactId) : devProdDeploymentInfos]
    }

    def "getCurrentSummary does not returns artifact with same versions in dev and prod"() {
        given:
        DeploymentInfo deploymentInfo = aRandom.deploymentInfo().build()
        inDev(deploymentInfo)
        inProd(deploymentInfo)

        expect:
        releaseService.getCurrentSummary() == [:]
    }

    def "getCurrentSummary returns artifact that is in prod but not dev"() {
        given:
        DeploymentInfo prodDeploymentInfo = aRandom.deploymentInfo().build()
        inProd(prodDeploymentInfo)
        nothingInDev()

        expect:
        String artifactId = prodDeploymentInfo.artifactId
        DevProdDeploymentInfos devProdDeploymentInfos = new DevProdDeploymentInfos(null, prodDeploymentInfo)
        releaseService.getCurrentSummary() == [ (artifactId) : devProdDeploymentInfos]
    }

    def "getCurrentSummary returns artifact that is in dev but not prod"() {
        given:
        DeploymentInfo devDeploymentInfo = aRandom.deploymentInfo().build()
        inDev(devDeploymentInfo)
        nothingInProd()

        expect:
        String artifactId = devDeploymentInfo.artifactId
        DevProdDeploymentInfos devProdDeploymentInfos = new DevProdDeploymentInfos(devDeploymentInfo, null)
        releaseService.getCurrentSummary() == [ (artifactId) : devProdDeploymentInfos]
    }

    def inDev(DeploymentInfo deploymentInfo){
        mockService.findManyByFoundationAndSpace(ReleaseService.DEV_FOUNDATION, ReleaseService.DEV_SPACE) >> [deploymentInfo]
    }

    def inProd(DeploymentInfo deploymentInfo){
        mockService.findManyByFoundationAndSpace(ReleaseService.PROD_FOUNDATION, ReleaseService.PROD_SPACE) >> [deploymentInfo]
    }

    def nothingInDev(){
        mockService.findManyByFoundationAndSpace(ReleaseService.DEV_FOUNDATION, ReleaseService.DEV_SPACE) >> []
    }

    def nothingInProd(){
        mockService.findManyByFoundationAndSpace(ReleaseService.PROD_FOUNDATION, ReleaseService.PROD_SPACE) >> []
    }
}
