commons-configuration-appengine
===============================

This is an Apache's [commons-configuration](http://commons.apache.org/proper/commons-configuration/index.html) 
extension which enables a Java application running on [Google AppEngine](https://developers.google.com/appengine/) read configuration data from the [Datastore](https://developers.google.com/appengine/docs/java/datastore/) 
and optionally caching them in [Memcache](https://developers.google.com/appengine/docs/java/memcache/) to avoid unnecessary datastore hits.

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

Gradle Settings
===============

org.github.fabito:commons-configuration-appengine:1.1


