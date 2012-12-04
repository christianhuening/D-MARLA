package org.hibernate.cache.infinispan.naturalid;

import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;

/**
 * @author Strong Liu <stliu@hibernate.org>
 */
class ReadOnlyAccess extends TransactionalAccess {
	private static final Log log = LogFactory.getLog( ReadOnlyAccess.class );

	ReadOnlyAccess(NaturalIdRegionImpl naturalIdRegion) {
		super( naturalIdRegion );
	}

	@Override
	public SoftLock lockItem(Object key, Object version) throws CacheException {
		throw new UnsupportedOperationException( "Illegal attempt to edit read only item" );
	}

	@Override
	public SoftLock lockRegion() throws CacheException {
		throw new UnsupportedOperationException( "Illegal attempt to edit read only item" );
	}

	@Override
	public void unlockItem(Object key, SoftLock lock) throws CacheException {
		log.error( "Illegal attempt to edit read only item" );
	}

	@Override
	public void unlockRegion(SoftLock lock) throws CacheException {
		log.error( "Illegal attempt to edit read only item" );
	}

	@Override
	public boolean update(Object key, Object value) throws CacheException {
		throw new UnsupportedOperationException( "Illegal attempt to edit read only item" );
	}

	@Override
	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
		throw new UnsupportedOperationException( "Illegal attempt to edit read only item" );
	}
}
