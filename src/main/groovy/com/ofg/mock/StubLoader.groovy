package com.ofg.mock

import groovy.grape.Grape
import groovy.transform.ToString

class StubLoader {

    private static final String LATEST_MODULE = '*'
    private static final String MANIFEST_PATH = 'META-INF/MANIFEST.MF'

    void loadStubs(Map<String, String> deps, String repository) {
        List excludes = [[group: 'org.codehaus.groovy', module: 'groovy-all']]
        Grape.addResolver(name: 'dependency-repository', root: repository)
        List<Module> parsedDeps = parseDeps(deps)
        parsedDeps.each { Module module ->
            println "Loading stub $module"
            Map depToGrab = [group: module.group, module: module.module, version: LATEST_MODULE]                        
            String mainClassFullyQualifiedName = findMainClassFromManifest(depToGrab, excludes)            
            GroovyClassLoader runtimeCS = loadStubJar(depToGrab, excludes)
            runtimeCS.loadClass(mainClassFullyQualifiedName).main()    
        }        
    }

    private List<Module> parseDeps(Map<String, String> deps) {
        return deps.values().collect {
            List<String> splitDep = it.split('/').toList()
            String group = splitDep.subList(0, splitDep.size() - 1).join('.')
            String module = "${splitDep.last()}-stub"
            return new Module(group: group, module: module)
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
    }
}
