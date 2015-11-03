package com.github.fabito.gaemeleon.core;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CachingConfigurationTest {

    private CachingConfiguration config;
    private Configuration delegate = mock(Configuration.class);

    @Before
    public void setup() {
        config = new CachingConfiguration(delegate);
        when(delegate.getProperty("p2")).thenReturn("v2");
        when(delegate.getProperty("p3")).thenReturn("v3");
        when(delegate.getProperty("p4")).thenReturn("v4");
    }

    @Test
    public void shouldAddItemsToCache() {
        config.getString("p2");
        config.getString("p3");
        config.getString("p4");
        assertThat(config.getCache().size(), is(3l));
    }

}