package com.github.fabito.gaemeleon.gcs;

import com.google.appengine.tools.cloudstorage.*;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.Iterator;

public class GoogleCloudStoragePropertiesConfiguration extends AbstractConfiguration {

    private PropertiesConfiguration delegate;
    private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
            .initialRetryDelayMillis(10)
            .retryMaxAttempts(10)
            .totalRetryPeriodMillis(15000)
            .build());

    public GoogleCloudStoragePropertiesConfiguration(GcsFilename file) throws ConfigurationException {
        try {
            GcsInputChannel inputChannel = gcsService.openReadChannel(file, 0l);
            InputStream is = Channels.newInputStream(inputChannel);
            this.delegate = new PropertiesConfiguration();
            this.delegate.load(is);
        } catch (IOException e) {
            throw new ConfigurationException("Could not load file: " + file, e);
        }
    }

    public GoogleCloudStoragePropertiesConfiguration(String bucketName, String objectName) throws ConfigurationException {
        this(new GcsFilename(bucketName, objectName));
    }


    @Override
    protected void addPropertyDirect(String key, Object value) {
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean containsKey(String key) {
        return this.containsKey(key);
    }

    @Override
    public Object getProperty(String key) {
        return this.delegate.getProperty(key);
    }

    @Override
    public Iterator<String> getKeys() {
        return this.delegate.getKeys();
    }
}
