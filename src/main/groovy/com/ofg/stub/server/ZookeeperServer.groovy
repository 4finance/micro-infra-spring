package com.ofg.stub.server

import org.apache.commons.lang.StringUtils
import org.apache.curator.RetryPolicy
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.RetryNTimes
import org.apache.curator.test.TestingServer

class ZookeeperServer {

    private static final RetryPolicy RETRY_POLICY = new RetryNTimes(50, 100)

    private CuratorFramework curatorFramework
    private String localZookeeperPath
    private TestingServer zookeeperServer

    ZookeeperServer(String localZookeeperPath) {
        this.localZookeeperPath = localZookeeperPath
    }

    ZookeeperServer(int zookeeperPort) {
        this.zookeeperServer = new TestingServer(zookeeperPort)
    }

    void start() {
        curatorFramework = CuratorFrameworkFactory.newClient(getConnectString(), RETRY_POLICY)
        curatorFramework.start()
    }

    CuratorFramework getCuratorFramework() {
        return curatorFramework
    }

    void shutdown() {
        curatorFramework.close()
        zookeeperServer?.close()
    }

    String getConnectString() {
        String connectString = zookeeperServer ? zookeeperServer.connectString : localZookeeperPath
        if (StringUtils.isBlank(connectString)) {
            throw new IllegalArgumentException('You have to provide either Zookeeper port or a path to a local Zookeeper')
        }
        return connectString
    }

}
