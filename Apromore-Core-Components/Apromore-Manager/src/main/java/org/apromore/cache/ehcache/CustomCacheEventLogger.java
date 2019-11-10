//package org.apromore;
////
////import net.sf.ehcache.event.CacheEventListener;
////import net.sf.ehcache.event.Cache;
////import org.ehcache.event.CacheEvent;
////import org.ehcache.event.CacheEventListener;
////import org.slf4j.Logger;
////import org.slf4j.LoggerFactory;
////
////public class CustomCacheEventLogger implements CacheEventListener<Object, Object> {
////
////    private static final Logger LOG = LoggerFactory.getLogger(CustomCacheEventLogger.class);
////
////    @Override
////    public void onEvent(CacheEvent cacheEvent) {
////        LOG.info("Cache event = {}, Key = {},  Old value = {}, New value = {}", cacheEvent.getType(),
////                cacheEvent.getKey(), cacheEvent.getOldValue(), cacheEvent.getNewValue());
////    }
////}