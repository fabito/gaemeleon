package com.github.fabito.gaemeleon.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.Configuration;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entities;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;

/**
 * {@link Configuration} implementation which retrieves/stores configuration values from Google App Engine's Datastore. 
 * 
 * By default, it uses the datastore kind "Configuration", the property name is the key and the
 * property value is the "value" attribute. 
 * Both the kind name and valueproperty name can be changed by using a specific constructor.
 * 
 * All datastore operations (get and put) are isolated in a dedicated transaction so that new
 * Entity Groups don't "leak" to the client code.
 * 
 * @author fabio
 * 
 */
public class DatastoreConfiguration extends AbstractConfiguration {

	public static final int STRING_MAX_LENGTH = 500;

	private static final Logger LOGGER = Logger.getLogger(DatastoreConfiguration.class.getSimpleName());
	
	public static final String DEFAULT_ENTITY_KIND = "Configuration";
	public static final String DEFAULT_PROPERTY_VALUE = "value";

	private final DatastoreService datastoreService;
	private final String entityKind;
	private final String propertyValue; 

	public DatastoreConfiguration() {
		this(DatastoreServiceFactory.getDatastoreService(), DEFAULT_ENTITY_KIND, DEFAULT_PROPERTY_VALUE);
	}
	
	@Inject
	public DatastoreConfiguration(final DatastoreService datastoreService) {
		this(datastoreService, DEFAULT_ENTITY_KIND, DEFAULT_PROPERTY_VALUE);
	}

	@Inject
	public DatastoreConfiguration(final DatastoreService datastoreService, final String entityKind, final String propertyValue) {
		super();
		this.datastoreService = datastoreService;
		this.entityKind = entityKind;
		this.propertyValue = propertyValue;
	}
	
	@Override
	public boolean containsKey(final String key) {
		try {
			datastoreService.get(newKey(key));
			return true;
		} catch (final EntityNotFoundException e) {
			return false;
		}
	}

	@Override
	public Iterator<String> getKeys() {
		final Iterable<Entity> iterable = datastoreService.prepare(new Query(entityKind).setKeysOnly()).asIterable();
		final List<String> list = new LinkedList<String>();
		for (final Entity entity : iterable) {
			list.add(entity.getKey().getName());
		}
		return list.iterator();
	}

	@Override
	public Object getProperty(final String key) {
		try {
			LOGGER.finer("Fetching property from datastore: " + key);
			final Entity entity = getInsideTransaction(newKey(key));
			final Object value = entity.getProperty(propertyValue);
			return value instanceof Text ? ((Text)value).getValue() : value;
		} catch (final EntityNotFoundException e) {
			LOGGER.finer("Property not found in datastore: " + key);
			if (isThrowExceptionOnMissing()) {
				throw new NoSuchElementException("key not found '" + key + "'");
			}
			return null;
		}
	}

	@Override
	public boolean isEmpty() {
		final Query q = new Query(Entities.KIND_METADATA_KIND);
		final Filter kindFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
				FilterOperator.EQUAL, Entities.createKindKey(entityKind));
		q.setFilter(kindFilter);
		return !datastoreService.prepare(q).asIterator().hasNext();
	}

	@Override
	protected void addPropertyDirect(final String key, final Object value) {
		LOGGER.finer(String.format("Adding new property {key=%s, value=%s}", key, value));
		final Entity entity = new Entity(newKey(key));
		entity.setProperty(propertyValue, val(value));
		putInsideTransaction(entity);
	}

	private Object val(Object value) {
		if (value instanceof String) {
			String valueStr = (String) value;
			if (valueStr.length() > STRING_MAX_LENGTH) {
				return new Text(valueStr);
			}
		}
		return value;
	}
	
	protected Key newKey(final String key) {
		return KeyFactory.createKey(entityKind, key);
	}
	
	private void putInsideTransaction(final Entity featureEntity) {
		final Transaction txn = datastoreService.beginTransaction();
		try {
			datastoreService.put(txn, featureEntity);
			txn.commit();
		} finally  {
			if(txn.isActive()) {
				txn.rollback();
			}
		}
	}

	private Entity getInsideTransaction(final Key key) throws EntityNotFoundException {
		final Transaction txn = datastoreService.beginTransaction();
		Entity featureEntity;
		try {
			featureEntity = datastoreService.get(txn, key);
			txn.commit();
		} finally  {
			if(txn.isActive()) {
				txn.rollback();
			}
		}
		return featureEntity;
	}

	private void deleteInsideTransaction(Key newKey) {
		final Transaction txn = datastoreService.beginTransaction();
		try {
			datastoreService.delete(txn, newKey);
			txn.commit();
		} finally  {
			if(txn.isActive()) {
				txn.rollback();
			}
		}		
	}
	
	@Override
	protected void clearPropertyDirect(String key) {
		LOGGER.finer("Removing property from datastore: " + key);
		deleteInsideTransaction(newKey(key));
	}
	
}