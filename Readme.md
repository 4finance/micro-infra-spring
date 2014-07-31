[![Build Status](https://travis-ci.org/4finance/micro-deps.svg?branch=master)](https://travis-ci.org/4finance/micro-deps)

Microservice Dependency Manager
===============================
                                    

The library you're browsing is using [Curator](http://curator.apache.org/)/[ZooKeeper](http://zookeeper.apache.org/) 
to give you proper service discovery and dependency monitoring using rather convention then configuration.

You can read more on how the service discovery is done using Curator
on the [Service Discovery](http://curator.apache.org/curator-x-discovery/index.html) documentation page.
 
microservice.json file
----------------------

The heart of your application is the microservice.json file. The file defines three things:
* The context in which your microservice will be deployed
* Fully qualified name of your microservice
* List of the dependencies

But lets look at the file

````json
    {
        "prod": {
            "this": "foo/bar/registration",
            "dependencies": {
                "users": "foo/bar/users",
                "newsletter": "foo/bar/comms/newsletter",
                "confirmation": "foo/bar/security/confirmation"
            }
        }
    }
````

Imagine a world where you are creating yet another internet shop. We will see how the three things mentioned above
are done.

### Deployment context ###

Your microservice is deployed in the "prod" environment (see the root of the json).

Having this you can use single zookeeper server for many contexts, and while a prod context is not maybe the best example,
it makes more sense where you have 7 jenkins agents building your application simultaneously, In that case you will just register
your microservices in contexts under agent names.

### Name of the microservice ###

Whatever is put in this will be the fully qualified name of your microservice. 

Your microservice in this case will be registered under `prod/foo/bar/registration` and since we're using ServiceDiscovery
 implemented in Curator you will have all instances of your service registered under generated UUIDs under the above path.
 
### Dependencies ###

The last section defines the dependencies that your microservice will be looking for. When starting everything up you will
have possibility to either use an out-of-the-box strategy or implement your own on what should happen when a dependency is
or is not available on your microservice boot and then what happens when a dependency disappears.

Dependencies are always defined with a key and a value. The key must be an unique identifier that you will reference from
your code, while the value is a fully qualified name of the dependency. When the path to the dependency changes, you will
not have to change it everywhere in the code, just in this one place.

Usage 
-----

If you are using spring, checkout the [microdeps-spring-config](github.com/4finance/micro-deps-spring-config) 
that will create all the needed beans.

