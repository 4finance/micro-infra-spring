package com.ofg.stub

import com.ofg.infrastructure.discovery.MicroserviceConfiguration

interface StubDownloader {

	File downloadAndUnpackStubJar(String stubsGroup, String stubsModule, String classifier)

}