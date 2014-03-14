commons-configuration-appengine
===============================

This is an Apache's [commons-configuration](http://commons.apache.org/proper/commons-configuration/index.html) 
extension which enables a Java application running on [Google AppEngine](https://developers.google.com/appengine/) read configuration data from the [Datastore](https://developers.google.com/appengine/docs/java/datastore/) 
and optionally caching them in [Memcache](https://developers.google.com/appengine/docs/java/memcache/) to avoid unnecessary datastore hits.

There are 2 new Configuration implementations. 

### DatastoreConfiguration

Firstly, the [**DatastoreConfiguration**](https://github.com/fabito/commons-configuration-appengine/blob/master/src/main/java/org/github/fabito/commons/configuration/appengine/DatastoreConfiguration.java) which retrieves/stores configuration values from the Datastore.
By default, it uses the datastore kind "Configuration", the property name is the key and the property value is the "value" attribute. 
Both the kind name and value property name can be changed by using a specific constructor.
All datastore operations (get and put) are isolated in a dedicated transaction so that new Entity Groups don't "leak" to the client code.   

```java
DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
Configuration configuration = new DatastoreConfiguration(datastoreService);
```
### MemcacheConfiguration 

Secondly, there is the [**MemcacheConfiguration**](https://github.com/fabito/commons-configuration-appengine/blob/master/src/main/java/org/github/fabito/commons/configuration/appengine/MemcacheConfiguration.java) 
which is actually a Configuration decorator, which can (and should) be used to decorate the DatastoreConfiguration to avoid unnecessary hits to the datastore.

```java
Configuration datastoreConfig = new DatastoreConfiguration(datastoreService);
Configuration config = new MemcacheConfiguration(datastoreConfig, memcacheService);
```
### Mixing Configuration Sources

The example below illustrates how one can still use the [**CompositeConfiguration**](http://commons.apache.org/proper/commons-configuration/userguide/howto_compositeconfiguration.html#Composite_Configuration_Details)
 with these new implementations. The order of precedence is first to last. So, if a property is not found in the application.properties file, it'll be searched in the Datastore.

```java
CompositeConfiguration config = new CompositeConfiguration();
config.addConfiguration(new PropertiesConfiguration("application.properties"));
config.addConfiguration(new DatastoreConfiguration(datastoreService));
```

### Setting up build tools 


#### Gradle

```groovy
repositories {
    mavenCentral()
    mavenRepo url: 'https://github.com/fabito/mvn-repo/raw/master/releases'
}
```
```groovy
dependencies {
    compile "org.github.fabito:commons-configuration-appengine:1.2"
}
```

#### Maven

```xml
<repository>
        <id>org.github.fabito</id>
        <url>https://github.com/fabito/mvn-repo/raw/master/releases</url>
        <snapshots>
                <enabled>false</enabled>
        </snapshots>
        <releases>
                <enabled>true</enabled>
        </releases>
</repository>
```

```xml
<dependency>
  <groupId>org.github.fabito</groupId>
  <artifactId>commons-configuration-appengine</artifactId>
  <version>1.2</version>
</dependency>
```