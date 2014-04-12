package org.github.fabito.commons.configuration.appengine;

import static org.github.fabito.commons.configuration.appengine.NamespaceConfiguration.withinNamespace;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.github.fabito.commons.configuration.appengine.NamespaceConfiguration.VoidWork;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class NamespaceConfigurationTest extends BaseConfigurationTest<NamespaceConfiguration> {

	private static final String NAMESPACE = "tenant";
	protected Map<String, String> initialConfigurationN1 = ImmutableMap
			.<String, String> of("np1", "nv1", "np2", "nv2", "np3", "nv3", "np4", "nv4");
	
	@Before
	public void before() {
		withinNamespace(NAMESPACE, new VoidWork() {
			@Override
			void vrun() {
				addConfigurations(initialConfigurationN1);
			}
		});
	}
	
	@Override
	NamespaceConfiguration configuration() {
		return new NamespaceConfiguration(new DatastoreConfiguration(datastoreService), NAMESPACE);
	}
	
	@Override
	Map<String, String> initialValues() {
		return initialConfigurationN1;
	}

	@Test
	public void existentPropertyInDefaultNamespaceShouldNotExistInOtherNamespace() {
		assertNull(configuration.getString("p1"));
		assertNull(configuration.getString("p2"));
		assertNull(configuration.getString("p3"));
	}

	@Test
	public void existentPropertyInOtherNamespace() {
		assertThat(configuration.getString("np1"), is("nv1"));
		assertThat(configuration.getString("np2"), is("nv2"));
		assertThat(configuration.getString("np3"), is("nv3"));
	}
	
	@Test
	public void containsKey() {
		assertThat(configuration.containsKey("np1"), is(true));
	}
	
	@Test
	public void addPropertyDirect() {
		String key = "ppp";
		String value = "aaa";
		assertThat(configuration.getString(key), nullValue());
		configuration.addPropertyDirect(key, value);
		assertThat(configuration.getString(key), is(value));
	}
	
	@Test
	public void isEmptyShouldReturnTrue() {
		removeAllWithinNamespace();
		assertThat(configuration.isEmpty(), is(true));
	}

	protected void removeAllWithinNamespace() {
		withinNamespace(NAMESPACE, new VoidWork() {
			@Override
			void vrun() {
				removeAll();
			}
		});
	}

}