commons-configuration-appengine
===============================

This is an Apache's [commons-configuration](http://commons.apache.org/proper/commons-configuration/index.html) 
extension which enables a Java application running on [Google AppEngine](https://developers.google.com/appengine/) read configuration data from the [Datastore](https://developers.google.com/appengine/docs/java/datastore/) 
and optionally caching them in [Memcache](https://developers.google.com/appengine/docs/java/memcache/) to avoid unnecessary datastore hits.

There are 2 new Configuration implementations. 

Firstly, the DatastoreConfiguration which retrieves/stores configuration values from the Datastore.
By default, it uses the datastore kind "Configuration", the property name is the key and the property value is the "value" attribute. Both the kind name and value property name can be changed by using a specific constructor.
All datastore operations (get and put) are isolated in a dedicated transaction so that new Entity Groups don't "leak" to the client code.   

```java
DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
Configuration configuration = new DatastoreConfiguration(datastoreService);
```
Secondly, there is the MemcacheConfiguration which is actually a Configuration decorator, which can (and should) be used
 to decorate the DatastoreConfiguration to avoid unnecessary hits to the datastore.

```java
DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

Configuration datastoreConfig = new DatastoreConfiguration(datastoreService);
Configuration config = new MemcacheConfiguration(datastoreConfig, memcacheService);
```

The example below illustrates how one can still use the CompositeConfiguration with these new implementations. The order of precedence is first to last.
So, if a property is not found in the application.properties file, it'll be searched in the Datastore.

```java
CompositeConfiguration config = new CompositeConfiguration();
config.addConfiguration(new PropertiesConfiguration("application.properties"));
config.addConfiguration(new DatastoreConfiguration(datastoreService));
```

Gradle Settings
===============
```groovy
repositories {
    mavenCentral()
    mavenRepo url: 'https://github.com/fabito/talesolutions-mvn-repo/raw/master/releases'
}
```
```groovy
dependencies {
    compile "org.github.fabito:commons-configuration-appengine:1.1"
}
```

Maven settings
==============

```xml
<repository>
        <id>org.github.fabito</id>
        <url>https://github.com/fabito/talesolutions-mvn-repo/raw/master/releases</url>
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
  <version>1.1</version>
</dependency>
```