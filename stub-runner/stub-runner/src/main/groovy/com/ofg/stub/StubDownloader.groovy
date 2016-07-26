package com.ofg.stub

interface StubDownloader {

	File downloadAndUnpackStubJar(String stubsGroup, String stubsModule, String classifier)

}