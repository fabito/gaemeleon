package org.github.fabito.gaemeleon.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.Configuration;
import org.github.fabito.gaemeleon.core.MemcacheConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class MemcacheConfigurationTest {

    private static final String V1 = "v1";

	private static final String P1 = "p1";

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig(), new LocalMemcacheServiceTestConfig());
	
    private MemcacheConfiguration config;
    private MemcacheService memcacheService;
	private Configuration delegate = mock(Configuration.class);
	
    @Before
    public void setup() {
        helper.setUp();
        memcacheService = MemcacheServiceFactory.getMemcacheService();
        config = new MemcacheConfiguration(delegate, memcacheService);
        when(delegate.getProperty(P1)).thenReturn(V1);
        when(delegate.getProperty("p2")).thenReturn("v2");
        when(delegate.getProperty("p3")).thenReturn("v3");
        when(delegate.getProperty("p4")).thenReturn("v4");
    }

	@Test
	public void memcacheShouldReflect() {
		config.getString(P1);
		config.getString("p2");
		config.getString("p3");
		config.getString("p4");
		assertThat(memcacheService.getStatistics().getItemCount(), is(4l));
	}
    
	@Test
	public void testIsEmpty() {
		when(delegate.isEmpty()).thenReturn(true);
		assertThat(config.isEmpty(), is(true));
	}

	@Test
	public void whenCacheDoesNotContainsKeyShouldInvokeDelegate() {
		when(delegate.containsKey(P1)).thenReturn(true);
		assertThat(config.containsKey(P1), is(true));
		verify(delegate, times(1)).containsKey(P1);
	}

	@Test
	public void whenCacheContainsKeyDelegateShouldNotBeInvoked() {
		config.getString(P1);
		assertThat(config.containsKey(P1), is(true));
		verify(delegate, times(0)).containsKey(P1);
	}

	@Test
	public void delegateShouldBeInvokedOnlyOnce() {
		String v = config.getString(P1);
		v = config.getString(P1);
		v = config.getString(P1);
		v = config.getString(P1);
		v = config.getString(P1);
		verify(delegate, times(1)).getProperty(P1);
		assertThat(v, is(V1));
		assertThat((String) memcacheService.get(config.cacheKey(P1)), is(V1));
	}

	@Test
	public void delegateShouldBeInvokedMultipleTimesWhenCacheExpires() {
		String v = config.getString(P1);
		memcacheService.clearAll();
		v = config.getString(P1);
		memcacheService.clearAll();
		v = config.getString(P1);
		memcacheService.clearAll();
		verify(delegate, times(3)).getProperty(P1);
		assertThat(v, is(V1));
		assertThat(memcacheService.get(config.cacheKey(P1)), nullValue());
	}
	
	@Test
	public void addPropertyDirectShoudDelegateToGetProperty() {
		config.addPropertyDirect("p666", "v666");
		verify(delegate, times(1)).addProperty("p666", "v666");
	}

	@Test
	public void getKeysShouldBeDelegated() {
		config.getKeys();
		verify(delegate, times(1)).getKeys();		
	}

}