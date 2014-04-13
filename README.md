[![Build Status](https://travis-ci.org/fabito/gaemeleon.png?branch=master)](https://travis-ci.org/fabito/gaemeleon)
[![Coverage Status](https://coveralls.io/repos/fabito/gaemeleon/badge.png?branch=master)](https://coveralls.io/r/fabito/gaemeleon?branch=master)

gaemeleon
=========

This is an Apache's [commons-configuration](http://commons.apache.org/proper/commons-configuration/index.html) 
extension that enables a Java applications running on [Google AppEngine](https://developers.google.com/appengine/) to read and write configuration data from the [Datastore](https://developers.google.com/appengine/docs/java/datastore/) 
and optionally caching them in [Memcache](https://developers.google.com/appengine/docs/java/memcache/) to avoid unnecessary datastore hits.

There are 4 new Configuration implementations. 

### DatastoreConfiguration

Firstly, the [**DatastoreConfiguration**](https://github.com/fabito/gaemeleon/blob/master/core/src/main/java/com/github/fabito/gaemeleon/core/DatastoreConfiguration.java) which retrieves/stores configuration values from the Datastore.
By default, it uses the datastore kind "Configuration", the property name is the key and the property value is the "value" attribute. 
Both the kind name and value property name can be changed by using a specific constructor.
All datastore operations (get and put) are isolated in a dedicated transaction so that new Entity Groups don't "leak" to the client code.   

```java
DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
Configuration configuration = new DatastoreConfiguration(datastoreService);
```
### MemcacheConfiguration 

Secondly, there is the [**MemcacheConfiguration**](https://github.com/fabito/gaemeleon/blob/master/core/src/main/java/com/github/fabito/gaemeleon/core/MemcacheConfiguration.java) 
which is actually a Configuration decorator, which can (and should) be used to decorate the DatastoreConfiguration to avoid unnecessary hits to the datastore.

```java
Configuration datastoreConfig = new DatastoreConfiguration(datastoreService);
Configuration config = new MemcacheConfiguration(datastoreConfig, memcacheService);
```

### NamespaceConfiguration 

The [**NamespaceConfiguration**](https://github.com/fabito/gaemeleon/blob/master/core/src/main/java/com/github/fabito/gaemeleon/core/NamespaceConfiguration.java) is another decorator, which forces all operation to run within the specified namespace.

```java
Configuration datastoreConfig = new DatastoreConfiguration(datastoreService);
Configuration config = new NamespaceConfiguration(datastoreConfig, "myNamespace");
```

### AppVersionConfiguration 

The [**AppVersionConfiguration**](https://github.com/fabito/gaemeleon/blob/master/core/src/main/java/com/github/fabito/gaemeleon/core/AppVersionConfiguration.java) is another decorator that prepends the current version to the property's key. It uses SystemProperty's applicationVersion.get() method to fetch the current runtime version. Useful when having different configuration values per version is needed.

```java
Configuration datastoreConfig = new DatastoreConfiguration(datastoreService);
Configuration config = new AppVersionConfiguration(datastoreConfig);
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
    compile "com.github.fabito:gaemeleon-core:1.0"
}
```

#### Maven

```xml
<repository>
        <id>com.github.fabito</id>
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
  <groupId>com.github.fabito</groupId>
  <artifactId>gaemeleon-core</artifactId>
  <version>1.0</version>
</dependency>
```