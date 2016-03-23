package com.blackbaud.deploymentstatus.resources

import com.blackbaud.deploymentstatus.ComponentTest
import groovyx.net.http.RESTClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Specification

import static com.blackbaud.deploymentstatus.core.CoreARandom.aRandom

@ComponentTest
class DeploymentStatusResourceWireSpec extends Specification {

    @Autowired
    private RESTClient client

    @Value("http://localhost:\${server.port}")
    private final String BASE_URI

}
