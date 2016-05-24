package com.ofg.stub

import com.ofg.infrastructure.discovery.MicroserviceConfiguration

interface StubDownloader {

	File downloadAndUnpackStubJar(boolean workOffline, String stubRepositoryRoot, String stubsGroup, String
			stubsModule, String classifier)

}