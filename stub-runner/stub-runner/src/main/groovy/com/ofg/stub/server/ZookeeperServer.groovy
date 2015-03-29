package com.ofg.stub.server

import groovy.transform.CompileStatic
import org.apache.commons.lang.StringUtils
import org.apache.curator.RetryPolicy
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.curator.test.TestingServer

@CompileStatic
class ZookeeperServer {

    private static final RetryPolicy RETRY_POLICY = new ExponentialBackoffRetry(50, 20, 500)

    private CuratorFramework curatorFramework
    private String localZookeeperPath
    private TestingServer server

    ZookeeperServer(String localZookeeperPath) {
        this.localZookeeperPath = localZookeeperPath
    }

    ZookeeperServer(int zookeeperPort) {
        this.server = new TestingServer(zookeeperPort)
    }

    void start() {
        curatorFramework = CuratorFrameworkFactory.newClient(connectString, RETRY_POLICY)
        curatorFramework.start()
    }

    CuratorFramework getCuratorFramework() {
        return curatorFramework
    }

    void shutdown() {
        curatorFramework?.close()
        server?.close()
    }

    String getConnectString() {
        String connectString = server ? server.connectString : localZookeeperPath
        if (StringUtils.isBlank(connectString)) {
            throw new IllegalArgumentException('You have to provide either Zookeeper port or a path to a local Zookeeper')
        }
        return connectString
    }

}
