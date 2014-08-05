package com.ofg.mock

import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import groovy.grape.Grape
import groovy.transform.ToString
import org.codehaus.groovy.tools.RootLoader

class StubLoader {

    private static final String LATEST_MODULE = '*'
    private static final String MANIFEST_PATH = 'META-INF/MANIFEST.MF'
    private static final int STARTING_PORT = 12345
    private static final int MAX_PORT = 50000

    void loadStubs(ServiceConfigurationResolver resolver, String repository, int zookeeperPort) {
        Grape.addResolver(name: 'dependency-repository', root: repository)
        List<Module> parsedDeps = parseDeps(resolver.dependencies)
        parsedDeps.each { Module module ->
            println "Loading stub $module"
            Map depToGrab = [group: module.group, module: module.module, version: LATEST_MODULE, classifier: 'shadow', transitive: false]
            GroovyClassLoader runtimeCS = loadStubJar(depToGrab)
            String mainClassFullyQualifiedName = findMainClass(depToGrab, runtimeCS)
            runtimeCS.loadClass(mainClassFullyQualifiedName).main(resolver.basePath, module.unparsedDependency, (nextAvailablePort()).toString(), zookeeperPort.toString())
        }
    }

    private static int nextAvailablePort() {
        int port = STARTING_PORT
        while (port < MAX_PORT) {
            ServerSocket socket = null
            try {
                socket = new ServerSocket(port)
                return port
            } catch (IOException e) {}
            finally {
                if (socket != null)
                    try {
                        socket.close()
                    } catch (IOException e) {
                        e.printStackTrace()
                    }
            }
            port++
        }
    }

    private List<Module> parseDeps(Map<String, String> deps) {
        return deps.values().collect {
            List<String> splitDep = it.split('/').toList()
            String group = splitDep.subList(0, splitDep.size() - 1).join('.')
            String module = "${splitDep.last()}-stub"
            return new Module(group: group, module: module, unparsedDependency: it)
        }
    }

    private ClassLoader loadStubJar(Map<String, String> depToGrab) {
        ClassLoader runtimeCS = new GroovyClassLoader()
        Grape.grab([classLoader: runtimeCS], depToGrab)
        return runtimeCS
    }

    private String findMainClass(Map<String, String> depToGrab, ClassLoader classLoader) {
        Properties manifest = loadStubManifest(depToGrab, classLoader)
        return manifest.getProperty('Main-Class')
    }

    private Properties loadStubManifest(Map<String, String> depToGrab, ClassLoader classLoader) {
        URL[] classpath = Grape.resolve([classLoader: classLoader], depToGrab).collect { it.toURL() }
        ClassLoader rootLoader = new RootLoader(classpath, null)
        Properties manifest = new Properties()
        manifest.load(rootLoader.getResourceAsStream(MANIFEST_PATH))
        return manifest
    }

    @ToString(includePackage = false, includeNames = true)
    private class Module {
        String group
        String module
        String unparsedDependency
    }
}
