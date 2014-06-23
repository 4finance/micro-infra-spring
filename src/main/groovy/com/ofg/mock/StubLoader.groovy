package com.ofg.mock

import com.ofg.infrastructure.discovery.ServiceConfigurationResolver
import groovy.grape.Grape
import groovy.transform.ToString

class StubLoader {

    private static final String LATEST_MODULE = '*'
    private static final String MANIFEST_PATH = 'META-INF/MANIFEST.MF'
    private static final int STARTING_PORT = 12345
    private static final int MAX_PORT = 50000

    void loadStubs(ServiceConfigurationResolver resolver, String repository, int zookeeperPort) {
        List excludes = [[group: 'org.codehaus.groovy', module: 'groovy-all']]
        Grape.addResolver(name: 'dependency-repository', root: repository)
        List<Module> parsedDeps = parseDeps(resolver.dependencies)
        parsedDeps.each { Module module ->
            println "Loading stub $module"
            Map depToGrab = [group: module.group, module: module.module, version: LATEST_MODULE, classifier: 'shadow']
            String mainClassFullyQualifiedName = findMainClassFromManifest(depToGrab, excludes)
            GroovyClassLoader runtimeCS = loadStubJar(depToGrab, excludes)
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

    private GroovyClassLoader loadStubJar(LinkedHashMap<String, String> depToGrab, List excludes) {
        GroovyClassLoader runtimeCS = new GroovyClassLoader()
        Grape.grab([classLoader: runtimeCS, excludes: excludes], depToGrab)
        return runtimeCS
    }

    private String findMainClassFromManifest(LinkedHashMap<String, String> depToGrab, List excludes) {
        GroovyClassLoader classLoader = new GroovyClassLoader(null)
        Grape.grab([classLoader: classLoader, excludes: excludes], depToGrab)
        Properties loadedManifest = new Properties()
        loadedManifest.load(classLoader.getResourceAsStream(MANIFEST_PATH))
        String mainClassFullyQualifiedName = loadedManifest.getProperty('Main-Class')
        return mainClassFullyQualifiedName
    }

    @ToString(includePackage = false, includeNames = true)
    private class Module {
        String group
        String module
        String unparsedDependency
    }
}
