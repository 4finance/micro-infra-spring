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

But lets look at the file (think internet shop and microservice that handles user registration, so it must create users,
sign them up for newsletters and send confirmation emails).

````json
    {
        "prod": {
            "this": "foo/bar/registration",
            "dependencies": {
                "users": "foo/bar/users",
                "newsletter": {
                    "path": "foo/bar/comms/newsletter",
                    "load-balancer": "random",
                    "contentTypeTemplate": "application/vnd.newsletter.$version+json",
                    "version": "v1"                    
                },
                "confirmation": {
                    "path": "foo/bar/security/confirmation",
                    "headers": {
                        "header1": "value1",
                        "header2": "value2"
                    }
                }
            }
        }
    }
````

### Deployment context ###

Your microservice is deployed in the "prod" environment (see the root of the json).

Having this you can use single zookeeper server for many contexts, and while a prod context is not maybe the best example,
it makes more sense when you have 7 jenkins agents building your application simultaneously - in that case you will just register
your microservices in contexts under agent names.

### Name of the microservice ###

Whatever is put here will be the fully qualified name of your microservice. 

In the above example microservice will be registered under `prod/foo/bar/registration` and since we're using ServiceDiscovery
 implemented in Curator you will have all instances of your service registered under generated UUIDs under the above path.
 Service Discovery will out of the box give you different strategies for high availability.
 
### Dependencies ###

The last section defines the dependencies that your microservice will be looking for. When starting everything up you will
have possibility to either use an out-of-the-box strategy or implement your own on what should happen when a dependency is
or is not available during the microservice boot and then what happens when a dependency disappears.

Dependencies are always defined with a key and a value. The key must be an unique identifier that you will reference from
your code, while the value is a map containing configuration properties. Here are supported properties:
* **path** - value of this property is fully qualified name of the dependency (when the path to the dependency changes, you do not have to change it everywhere in the code, just in this one place),
* **load-balancer** - sets a type of load balancer strategy (available values are `sticky`, `random` and `round-robin/round_robin/roundrobin`); if provided type is not a correct one or the type is not set then the default one is set, i.e. round robin strategy,
* **contentTypeTemplate** - template of a Content-Type HTTP header send to the service (it can contain `$version` variable that will be updated with the value assigned to the version property),
* **version** - contains a version number of the MIME type we are using sending requests to the service,
* **headers** - a map containing key-value entries that are directly set as HTTP headers of the request send to the service,
* **required** - specifies whether the service we are depending on is a mandatory one or is optional: when set to `true` during the startup phase of our microservice the exception will be thrown in case the service we are depending on is not available, on the other hand if value of the property is `false` then by default a message is logged with information the service is not available.

Usage 
-----

### Register your service ###

If you are using spring, checkout the [microdeps-spring-config](https://github.com/4finance/micro-infra-spring/blob/master/micro-deps/README.md)
that will create all the needed beans.

In all other cases it will most probably make sense for you to check out `com.ofg.infrastructure.discovery.util.MicroDepsService`.

This class takes all the configuration needed to start, then you call start() on it and your service will get registered.
Check out the javadocs for the specific usage.

If you want to have more control over the specific classes used inside, feel free to check out the code to see how they
 are used.

### Register custom Dependency Watchers ###

Use `MicroDepsService.registerDependencyStateChangeListener` or `DependencyWatcher.registerDependencyStateChangeListener`
if you have chosen to create all the classes on your own.

The object you pass of class `DependencyWatcherListener` is a very simple listener

````groovy
interface DependencyWatcherListener {        
    void stateChanged(String dependencyName, DependencyState newState)        
}
````

Now every time any of your dependency changes, you will get a notification with the dependencyName (the key from dependencies)
and the state of it - it can be either that at least one is CONNECTED or all are DISCONNECTED. Just bare in mind you will be notified
every time a node connects or disconnects so you might be getting number of CONNECTED notifications in a row.

### Resolve dependency endpoints ###

Use `com.ofg.infrastructure.discovery.ServiceResolver.getUrl(dependency)`, where dependency is the key from you dependency list.

This will give you url to your dependency that zookeeper thinks is alive. It is a good idea to run this every time you are accessing
your dependency to be almost sure you're getting one that is alive and that you're using you HA strategy.

Just bare in mind, that if a service dies unexpectedly without proper closing the zookeeper connection there will be a timeout
after which zookeeper will realise your dependency is dead. So allow timeouts there.

Stubs
-----

Another important case that micro-deps is trying to solve is microservice stubs. 
Stubs are helpful when you're doing your day-to-day development and you want to run stubs of all the dependencies 
so that your service can start quickly, without the need to set up the whole tree of dependencies first.

### Stub definition ###

First thing you need to make sure is that you develop another project that will be an equivalent of your real project,
 but it's only use will be stubbing the API.
 
So make sure that your project has an equivalent with -stub suffix and mimics all the API you are providing. Then make sure 
you deploy it to your favorite dependency repository.

Your stub should be an executable jar with a main function that accepts 4 parameters
`zookeper context, fully qualified name, port to bind to, zookeeper port`

Then it should use the above config to register in zookeeper. An example class that does it that can be almost 1:1 copy-pasted
can be found at [boot-microservice-stub example](https://github.com/4finance/boot-microservice-stub/blob/master/src/main/groovy/com/ofg/BootMicroserviceStubApplication.groovy).
You can also use the project to see how the stub can be done.

### Stub deployment ###

Now the convention is that the fully qualified name of you microservice is also the fully qualified name in your dependency 
repository. So in our case if this was a maven project groupId would be `foo.bar` and artifactId `registration`. The stub
project should have groupId `foo.bar` and artifactId `registration-stub`.

### Running stubs ###

micro-deps acts for this as a runnable jar

`java -jar micro-deps-VERSION-fatJar.jar`

#### Test zookeeper server

What it does on start is
* Starts testing zookeeper instance (providing you with the port)
* Starts a simple local server that allows you to send GET to http://localhost:PORT/stop to stop it

#### Dependency resolution and downloading

There are couple of parameters you can pass to the jar
````
 -c VAL : json configuration with dependencies to load (exclusive with -f)
 -f VAL : path to file with json config (exclusive with -c)
 -mp N  : optional port number on which zookeeper rest server will be started.
          It will expose one method on /stop to stop the server. Default is
          18081
 -p N   : optional port number on which zookeeper mock will be started. Default
          is 2181
 -r VAL : url to repository with stubs
````

You can override the default zookeeper port/stop server, but more importantly you can pass you microservice.json file,
which then will be read and micro-deps will try downloading and running the stubs specified in dependencies section,
using the repository passed with -r option.

At the end of the day you will have a running local zookeeper instance with all the dependencies your project needs run
and registered.

API Version Handling
----------------

There are two approaches that can be taken.

### Versioned service names ###

The straight-forward approach is that you can add version to your microservice name. Your microservice.json can look like this

````json
    {
        "prod": {
            "this": "foo/bar/registration/12",
            "dependencies": {
                "users": {
                    "path": "foo/bar/users/8"
                },
                "newsletter": {
                    "path": "foo/bar/comms/newsletter/14"
                },
                "confirmation": {
                    "path": "foo/bar/security/confirmation/3"
                }
            }
        }
    }
````

The good thing with this approach is that it is very easy to manage versions, because every service must support only it's API version,
but when you think about it it's almost impossible to follow that approach when you have a database and it's schema
changes from version to version (like you cannot have old hibernate entities and new hibernate entities running against the same DB).

Also bear in mind that in this case stubs described in previous section are not supported. Pull-Requests are welcome though ;-)

### Header-level support ###

Another approach is to have your microservice support different versions of API using the headers. 
 
As you extend your webservice and change the API, you will have to support multiple versions of it (as long 
as there are any other microservices using the older versions).

Imagine you used to have ConfirmationController that would send out an unique link over email to any address.
 Then the API has changed and instead of accepting full email address you're accepting only username, 
 because for some reason the domain is always the same. And actually it's the only allowed email domain now.

Spring/groovy example can look like this

````groovy
    @RestController
    @RequestMapping(value = "/confirmation")
    class ConfirmationController {
    
        private final EmailSender emailSender
    
        private final String emailDomain = '@foo.bar'
        
        @Autowired
        PaymentOrderController(EmailSender emailSender) {
            this.emailSender = emailSender
        }
    
        @RequestMapping(method = RequestMethod.POST, 
                consumes = 'application/vnd.mymoid-adapter.v1+json',
                produces = 'application/vnd.mymoid-adapter.v1+json')
        ResponseEntity<String> createPaymentOrder(@RequestParam('emailAddress') emailAddress) {
            if (!emailAddress.endsWith(emailDomain)) {
                return new ResponseEntity("Email $emailAddress is not in $emailDomain", HttpStatus.PRECONDITION_FAILED)
            }
            emailSender.sendEmailTo(emailAddress)
            return new ResponseEntity('Email sent', HttpStatus.OK)
        }
        
        @RequestMapping(method = RequestMethod.POST, 
                consumes = 'application/vnd.mymoid-adapter.v2+json',
                produces = 'application/vnd.mymoid-adapter.v2+json')
        ResponseEntity<String> createPaymentOrder(@RequestParam('userLogin') userLogin) {
            emailSender.sendEmailTo(userLogin + emailDomain)
            return new ResponseEntity('Email sent', HttpStatus.OK)
        }
    }
````     

If you are not using Spring for sure your REST library handles consumes/produces in a similar way. If it does not, then
you probably should change your library ;-).
