commons-configuration-appengine
===============================

This is an Apache's [commons-configuration](http://commons.apache.org/proper/commons-configuration/index.html) 
extension which enables a Java application running on [Google AppEngine](https://developers.google.com/appengine/) read configuration data from the [Datastore](https://developers.google.com/appengine/docs/java/datastore/) 
and optionally caching them in [Memcache](https://developers.google.com/appengine/docs/java/memcache/) to avoid unnecessary datastore hits.

```java
DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
Configuration configuration = new DatastoreConfiguration(datastoreService);

```

```java
DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

Configuration datastoreConfig = new DatastoreConfiguration(datastoreService);
Configuration config = new MemcacheConfiguration(datastoreConfig, memcacheService);

```


```java
CompositeConfiguration config = new CompositeConfiguration();
config.addConfiguration(new PropertiesConfiguration("application.properties"));
config.addConfiguration(new DatastoreConfiguration(DatastoreServiceFactory.getDatastoreService()));
```
In the example above, the order of precedence is first to last.
So, if a property is not found in the application.properties file, it'll be searched in the Datastore.


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




