package com.ofg.stub

import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * Technical options related to running StubRunner
 */
@ToString(includeNames = true)
@CompileStatic
class StubRunnerOptions {

    /**
     * min port value of the Wiremock instance for the given collaborator
     */
    Integer minPortValue

    /**
     * max port value of the Wiremock instance for the given collaborator
     */
    Integer maxPortValue

    /**
     * root URL from where the JAR with stub mappings will be downloaded
     */
    String stubRepositoryRoot

    /**
     * group name of the dependency containing stub mappings
     */
    String stubsGroup

    /**
     * module name of the dependency containing stub mappings
     */
    String stubsModule

    /**
     * avoids local repository in dependency resolution
     */
    boolean skipLocalRepo

    /**
     * use per microservice stub resolution rather than one jar for all microservices
     */
    boolean useMicroserviceDefinitions

    /**
     * connect string to Zookeeper server
     */
    String zookeeperConnectString

    /**
     * testing zookeeper server port
     */
    Integer zookeeperPort

    /**
     * stub definition suffix
     */
    String stubDefinitionSuffix

    /**
     * stub definition classifier
     */
    String stubClassifier
    /**
     * Waits with resolution of collaborators 
     */
    boolean waitForServiceConnect

    /**
     * Globally used timeout in seconds for getting stubs
     */
    Integer waitTimeout

    StubRunnerOptions(Integer minPortValue, Integer maxPortValue, String stubRepositoryRoot, String stubsGroup,
                      String stubsModule, boolean skipLocalRepo, boolean useMicroserviceDefinitions,
                      String zookeeperConnectString, Integer zookeeperPort, String stubDefinitionSuffix,
                      String stubClassifier, boolean waitForServiceConnect, Integer waitTimeout) {
        this.minPortValue = minPortValue
        this.maxPortValue = maxPortValue
        this.stubRepositoryRoot = stubRepositoryRoot
        this.stubsGroup = stubsGroup
        this.stubsModule = stubsModule
        this.skipLocalRepo = skipLocalRepo
        this.useMicroserviceDefinitions = useMicroserviceDefinitions
        this.zookeeperConnectString = zookeeperConnectString
        this.zookeeperPort = zookeeperPort
        this.stubDefinitionSuffix = stubDefinitionSuffix
        this.waitForServiceConnect = waitForServiceConnect
        this.waitTimeout = waitTimeout
        this.stubClassifier = stubClassifier
    }

    StubRunnerOptions() {
    }
}
