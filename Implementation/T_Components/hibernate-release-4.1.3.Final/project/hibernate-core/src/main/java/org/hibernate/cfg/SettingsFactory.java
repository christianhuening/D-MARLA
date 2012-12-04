/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.cfg;

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

import org.jboss.logging.Logger;

import org.hibernate.ConnectionReleaseMode;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cache.internal.NoCachingRegionFactory;
import org.hibernate.cache.internal.RegionFactoryInitiator;
import org.hibernate.cache.internal.StandardQueryCacheFactory;
import org.hibernate.cache.spi.QueryCacheFactory;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.transaction.spi.TransactionFactory;
import org.hibernate.hql.spi.QueryTranslatorFactory;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.classloading.spi.ClassLoaderService;
import org.hibernate.service.jta.platform.spi.JtaPlatform;
import org.hibernate.tuple.entity.EntityTuplizerFactory;

/**
 * Reads configuration properties and builds a {@link Settings} instance.
 *
 * @author Gavin King
 */
public class SettingsFactory implements Serializable {

	private static final long serialVersionUID = -1194386144994524825L;

	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SettingsFactory.class.getName());

	public static final String DEF_CACHE_REG_FACTORY = NoCachingRegionFactory.class.getName();

	public SettingsFactory() {
	}

	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) {
		final boolean debugEnabled =  LOG.isDebugEnabled();
		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
		Settings settings = new Settings();

		//SessionFactory name:

		String sessionFactoryName = props.getProperty( Environment.SESSION_FACTORY_NAME );
		settings.setSessionFactoryName( sessionFactoryName );
		settings.setSessionFactoryNameAlsoJndiName(
				ConfigurationHelper.getBoolean( AvailableSettings.SESSION_FACTORY_NAME_IS_JNDI, props, true )
		);

		//JDBC and connection settings:

		//Interrogate JDBC metadata
		ExtractedDatabaseMetaData meta = jdbcServices.getExtractedMetaDataSupport();

		settings.setDataDefinitionImplicitCommit( meta.doesDataDefinitionCauseTransactionCommit() );
		settings.setDataDefinitionInTransactionSupported( meta.supportsDataDefinitionInTransaction() );

		//use dialect default properties
		final Properties properties = new Properties();
		properties.putAll( jdbcServices.getDialect().getDefaultProperties() );
		properties.putAll( props );

		// Transaction settings:
		settings.setJtaPlatform( serviceRegistry.getService( JtaPlatform.class ) );

		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(Environment.FLUSH_BEFORE_COMPLETION, properties);
		if ( debugEnabled ) {
			LOG.debugf( "Automatic flush during beforeCompletion(): %s", enabledDisabled(flushBeforeCompletion) );
		}
		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);

		boolean autoCloseSession = ConfigurationHelper.getBoolean(Environment.AUTO_CLOSE_SESSION, properties);
		if ( debugEnabled ) {
			LOG.debugf( "Automatic session close at end of transaction: %s", enabledDisabled(autoCloseSession) );
		}
		settings.setAutoCloseSessionEnabled(autoCloseSession);

		//JDBC and connection settings:

		int batchSize = ConfigurationHelper.getInt(Environment.STATEMENT_BATCH_SIZE, properties, 0);
		if ( !meta.supportsBatchUpdates() ) {
			batchSize = 0;
		}
		if ( batchSize > 0 && debugEnabled ) {
			LOG.debugf( "JDBC batch size: %s", batchSize );
		}
		settings.setJdbcBatchSize(batchSize);

		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(Environment.BATCH_VERSIONED_DATA, properties, false);
		if ( batchSize > 0 && debugEnabled ) {
			LOG.debugf( "JDBC batch updates for versioned data: %s", enabledDisabled(jdbcBatchVersionedData) );
		}
		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);

		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(
				Environment.USE_SCROLLABLE_RESULTSET,
				properties,
				meta.supportsScrollableResults()
		);
		if ( debugEnabled ) {
			LOG.debugf( "Scrollable result sets: %s", enabledDisabled(useScrollableResultSets) );
		}
		settings.setScrollableResultSetsEnabled(useScrollableResultSets);

		boolean wrapResultSets = ConfigurationHelper.getBoolean(Environment.WRAP_RESULT_SETS, properties, false);
		if ( debugEnabled ) {
			LOG.debugf( "Wrap result sets: %s", enabledDisabled(wrapResultSets) );
		}
		settings.setWrapResultSetsEnabled(wrapResultSets);

		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(Environment.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
		if ( debugEnabled ) {
			LOG.debugf( "JDBC3 getGeneratedKeys(): %s", enabledDisabled(useGetGeneratedKeys) );
		}
		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);

		Integer statementFetchSize = ConfigurationHelper.getInteger(Environment.STATEMENT_FETCH_SIZE, properties);
		if ( statementFetchSize != null && debugEnabled ) {
			LOG.debugf( "JDBC result set fetch size: %s", statementFetchSize );
		}
		settings.setJdbcFetchSize(statementFetchSize);

		String releaseModeName = ConfigurationHelper.getString( Environment.RELEASE_CONNECTIONS, properties, "auto" );
		if ( debugEnabled ) {
			LOG.debugf( "Connection release mode: %s", releaseModeName );
		}
		ConnectionReleaseMode releaseMode;
		if ( "auto".equals(releaseModeName) ) {
			releaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
		}
		else {
			releaseMode = ConnectionReleaseMode.parse( releaseModeName );
			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
					! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {
				LOG.unsupportedAfterStatement();
				releaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
			}
		}
		settings.setConnectionReleaseMode( releaseMode );

		//SQL Generation settings:

		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
		if ( defaultSchema != null && debugEnabled ) {
			LOG.debugf( "Default schema: %s", defaultSchema );
		}
		if ( defaultCatalog != null && debugEnabled ) {
			LOG.debugf( "Default catalog: %s", defaultCatalog );
		}
		settings.setDefaultSchemaName( defaultSchema );
		settings.setDefaultCatalogName( defaultCatalog );

		Integer maxFetchDepth = ConfigurationHelper.getInteger( Environment.MAX_FETCH_DEPTH, properties );
		if ( maxFetchDepth != null ) {
			LOG.debugf( "Maximum outer join fetch depth: %s", maxFetchDepth );
		}
		settings.setMaximumFetchDepth( maxFetchDepth );

		int batchFetchSize = ConfigurationHelper.getInt(Environment.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
		if ( debugEnabled ) {
			LOG.debugf( "Default batch fetch size: %s", batchFetchSize );
		}
		settings.setDefaultBatchFetchSize( batchFetchSize );

		boolean comments = ConfigurationHelper.getBoolean( Environment.USE_SQL_COMMENTS, properties );
		if ( debugEnabled ) {
			LOG.debugf( "Generate SQL with comments: %s", enabledDisabled(comments) );
		}
		settings.setCommentsEnabled( comments );

		boolean orderUpdates = ConfigurationHelper.getBoolean( Environment.ORDER_UPDATES, properties );
		if ( debugEnabled ) {
			LOG.debugf( "Order SQL updates by primary key: %s", enabledDisabled(orderUpdates) );
		}
		settings.setOrderUpdatesEnabled( orderUpdates );

		boolean orderInserts = ConfigurationHelper.getBoolean(Environment.ORDER_INSERTS, properties);
		if ( debugEnabled ) {
			LOG.debugf( "Order SQL inserts for batching: %s", enabledDisabled(orderInserts) );
		}
		settings.setOrderInsertsEnabled( orderInserts );

		//Query parser settings:

		settings.setQueryTranslatorFactory( createQueryTranslatorFactory( properties, serviceRegistry ) );

		Map querySubstitutions = ConfigurationHelper.toMap( Environment.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties );
		if ( debugEnabled ) {
			LOG.debugf( "Query language substitutions: %s", querySubstitutions );
		}
		settings.setQuerySubstitutions( querySubstitutions );

		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( Environment.JPAQL_STRICT_COMPLIANCE, properties, false );
		if ( debugEnabled ) {
			LOG.debugf( "JPA-QL strict compliance: %s", enabledDisabled(jpaqlCompliance) );
		}
		settings.setStrictJPAQLCompliance( jpaqlCompliance );

		// Second-level / query cache:

		boolean useSecondLevelCache = ConfigurationHelper.getBoolean( Environment.USE_SECOND_LEVEL_CACHE, properties, true );
		if ( debugEnabled ) {
			LOG.debugf( "Second-level cache: %s", enabledDisabled(useSecondLevelCache) );
		}
		settings.setSecondLevelCacheEnabled( useSecondLevelCache );

		boolean useQueryCache = ConfigurationHelper.getBoolean(Environment.USE_QUERY_CACHE, properties);
		if ( debugEnabled ) {
			LOG.debugf( "Query cache: %s", enabledDisabled(useQueryCache) );
		}
		settings.setQueryCacheEnabled( useQueryCache );
		if (useQueryCache) {
			settings.setQueryCacheFactory( createQueryCacheFactory( properties, serviceRegistry ) );
		}

		// The cache provider is needed when we either have second-level cache enabled
		// or query cache enabled.  Note that useSecondLevelCache is enabled by default
		settings.setRegionFactory( createRegionFactory( properties, ( useSecondLevelCache || useQueryCache ), serviceRegistry ) );

		boolean useMinimalPuts = ConfigurationHelper.getBoolean(
				Environment.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
		);
		if ( debugEnabled ) {
			LOG.debugf( "Optimize cache for minimal puts: %s", enabledDisabled(useMinimalPuts) );
		}
		settings.setMinimalPutsEnabled( useMinimalPuts );

		String prefix = properties.getProperty( Environment.CACHE_REGION_PREFIX );
		if ( StringHelper.isEmpty(prefix) ) {
			prefix=null;
		}
		if ( prefix != null && debugEnabled ) {
			LOG.debugf( "Cache region prefix: %s", prefix );
		}
		settings.setCacheRegionPrefix( prefix );

		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean( Environment.USE_STRUCTURED_CACHE, properties, false );
		if ( debugEnabled ) {
			LOG.debugf( "Structured second-level cache entries: %s", enabledDisabled(useStructuredCacheEntries) );
		}
		settings.setStructuredCacheEntriesEnabled( useStructuredCacheEntries );


		//Statistics and logging:

		boolean useStatistics = ConfigurationHelper.getBoolean( Environment.GENERATE_STATISTICS, properties );
		if ( debugEnabled ) {
			LOG.debugf( "Statistics: %s", enabledDisabled(useStatistics) );
		}
		settings.setStatisticsEnabled( useStatistics );

		boolean useIdentifierRollback = ConfigurationHelper.getBoolean( Environment.USE_IDENTIFIER_ROLLBACK, properties );
		if ( debugEnabled ) {
			LOG.debugf( "Deleted entity synthetic identifier rollback: %s", enabledDisabled(useIdentifierRollback) );
		}
		settings.setIdentifierRollbackEnabled( useIdentifierRollback );

		//Schema export:

		String autoSchemaExport = properties.getProperty( Environment.HBM2DDL_AUTO );
		if ( "validate".equals(autoSchemaExport) ) {
			settings.setAutoValidateSchema( true );
		}
		if ( "update".equals(autoSchemaExport) ) {
			settings.setAutoUpdateSchema( true );
		}
		if ( "create".equals(autoSchemaExport) ) {
			settings.setAutoCreateSchema( true );
		}
		if ( "create-drop".equals( autoSchemaExport ) ) {
			settings.setAutoCreateSchema( true );
			settings.setAutoDropSchema( true );
		}
		settings.setImportFiles( properties.getProperty( Environment.HBM2DDL_IMPORT_FILES ) );

		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( Environment.DEFAULT_ENTITY_MODE ) );
		if ( debugEnabled ) {
			LOG.debugf( "Default entity-mode: %s", defaultEntityMode );
		}
		settings.setDefaultEntityMode( defaultEntityMode );

		boolean namedQueryChecking = ConfigurationHelper.getBoolean( Environment.QUERY_STARTUP_CHECKING, properties, true );
		if ( debugEnabled ) {
			LOG.debugf( "Named query checking : %s", enabledDisabled(namedQueryChecking) );
		}
		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );

		boolean checkNullability = ConfigurationHelper.getBoolean(Environment.CHECK_NULLABILITY, properties, true);
		if ( debugEnabled ) {
			LOG.debugf( "Check Nullability in Core (should be disabled when Bean Validation is on): %s", enabledDisabled(checkNullability) );
		}
		settings.setCheckNullability(checkNullability);

		MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( properties );
		if ( debugEnabled ) {
			LOG.debugf( "multi-tenancy strategy : %s", multiTenancyStrategy );
		}
		settings.setMultiTenancyStrategy( multiTenancyStrategy );

		// TODO: Does EntityTuplizerFactory really need to be configurable? revisit for HHH-6383
		settings.setEntityTuplizerFactory( new EntityTuplizerFactory() );

//		String provider = properties.getProperty( Environment.BYTECODE_PROVIDER );
//		log.info( "Bytecode provider name : " + provider );
//		BytecodeProvider bytecodeProvider = buildBytecodeProvider( provider );
//		settings.setBytecodeProvider( bytecodeProvider );

		return settings;

	}

//	protected BytecodeProvider buildBytecodeProvider(String providerName) {
//		if ( "javassist".equals( providerName ) ) {
//			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
//		}
//		else {
//            LOG.debug("Using javassist as bytecode provider by default");
//			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
//		}
//	}

	private static String enabledDisabled(boolean value) {
		return value ? "enabled" : "disabled";
	}

	protected QueryCacheFactory createQueryCacheFactory(Properties properties, ServiceRegistry serviceRegistry) {
		String queryCacheFactoryClassName = ConfigurationHelper.getString(
				Environment.QUERY_CACHE_FACTORY, properties, StandardQueryCacheFactory.class.getName()
		);
		LOG.debugf( "Query cache factory: %s", queryCacheFactoryClassName );
		try {
			return (QueryCacheFactory) serviceRegistry.getService( ClassLoaderService.class )
					.classForName( queryCacheFactoryClassName )
					.newInstance();
		}
		catch (Exception e) {
			throw new HibernateException( "could not instantiate QueryCacheFactory: " + queryCacheFactoryClassName, e );
		}
	}

	private static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled, ServiceRegistry serviceRegistry) {
		String regionFactoryClassName = RegionFactoryInitiator.mapLegacyNames(
				ConfigurationHelper.getString(
						Environment.CACHE_REGION_FACTORY, properties, null
				)
		);
		if ( regionFactoryClassName == null || !cachingEnabled) {
			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
		}
		LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
		try {
			try {
				return (RegionFactory) serviceRegistry.getService( ClassLoaderService.class )
						.classForName( regionFactoryClassName )
						.getConstructor( Properties.class )
						.newInstance( properties );
			}
			catch ( NoSuchMethodException e ) {
				// no constructor accepting Properties found, try no arg constructor
				LOG.debugf(
						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
						regionFactoryClassName
				);
				return (RegionFactory) serviceRegistry.getService( ClassLoaderService.class )
						.classForName( regionFactoryClassName )
						.newInstance();
			}
		}
		catch ( Exception e ) {
			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
		}
	}
	//todo remove this once we move to new metamodel
	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
		// todo : REMOVE!  THIS IS TOTALLY A TEMPORARY HACK FOR org.hibernate.cfg.AnnotationBinder which will be going away
		String regionFactoryClassName = RegionFactoryInitiator.mapLegacyNames(
				ConfigurationHelper.getString(
						Environment.CACHE_REGION_FACTORY, properties, null
				)
		);
		if ( regionFactoryClassName == null ) {
			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
		}
		LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
		try {
			try {
				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
						.getConstructor( Properties.class )
						.newInstance( properties );
			}
			catch ( NoSuchMethodException e ) {
				// no constructor accepting Properties found, try no arg constructor
				LOG.debugf(
						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
						regionFactoryClassName
				);
				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
						.newInstance();
			}
		}
		catch ( Exception e ) {
			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
		}
	}

	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties, ServiceRegistry serviceRegistry) {
		String className = ConfigurationHelper.getString(
				Environment.QUERY_TRANSLATOR, properties, "org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory"
		);
		LOG.debugf( "Query translator: %s", className );
		try {
			return (QueryTranslatorFactory) serviceRegistry.getService( ClassLoaderService.class )
					.classForName( className )
					.newInstance();
		}
		catch ( Exception e ) {
			throw new HibernateException( "could not instantiate QueryTranslatorFactory: " + className, e );
		}
	}
}
