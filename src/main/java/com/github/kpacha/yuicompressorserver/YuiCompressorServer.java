package com.github.kpacha.yuicompressorserver;

import java.security.NoSuchAlgorithmException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;

import com.github.kpacha.yuicompressorserver.adapter.AdapterFactory;
import com.github.kpacha.yuicompressorserver.compressor.CachedCompressor;
import com.github.kpacha.yuicompressorserver.compressor.Compressor;
import com.github.kpacha.yuicompressorserver.compressor.YuiCompressor;
import com.github.kpacha.yuicompressorserver.utils.BufferedContentHasher;

/**
 * The YuiCompressorServer starts a jetty server and registers a
 * YuiCompressorHandler with all its dependencies.
 * 
 * @author kpacha
 */
public class YuiCompressorServer {
    private static final String CACHE_NAME = "yuicompressor-server";
    private static final int CACHE_TTL = 604800; // a week
    private static final int CACHE_TIME_TO_IDLE = 604800; // a week
    private static final boolean CACHE_ETERNAL = false;
    private static final boolean CACHE_OVERFLOW_TO_DISK = false;
    private static final int CACHE_MAX_ITEMS = 5000;

    private static Logger logger = Logger.getLogger(YuiCompressorServer.class);

    private Server server;
    private BufferedContentHasher hasher;

    /**
     * Main method of the standalone yuicompressor-server
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {
	YuiCompressorServer server = new YuiCompressorServer(new Configuration(
		args));
	server.run();
    }

    /**
     * Set up the server with all its dependencies
     * 
     * @param configuration
     * @throws Exception
     */
    public YuiCompressorServer(Configuration configuration) throws Exception {
	hasher = new BufferedContentHasher(configuration.getAlgorithm());
	server = new Server(configuration.getPort());
	server.setHandler(new YuiCompressorHandler(
		getCompressor(configuration), hasher));
    }

    /**
     * Run it!
     * 
     * @throws Exception
     */
    public void run() throws Exception {
	server.start();
	server.join();
    }

    private Compressor getCompressor(Configuration configuration)
	    throws NoSuchAlgorithmException {
	Compressor compressor = new YuiCompressor(new AdapterFactory());
	if (configuration.isCacheEnabled()) {
	    logger.debug("Instantiating the cache layer");
	    compressor = new CachedCompressor(compressor, hasher,
		    getFreshCache());
	}
	return compressor;
    }

    private Cache getFreshCache() {
	CacheManager singletonManager = CacheManager.create();
	Cache memoryOnlyCache = new Cache(CACHE_NAME, CACHE_MAX_ITEMS,
		CACHE_OVERFLOW_TO_DISK, CACHE_ETERNAL, CACHE_TTL,
		CACHE_TIME_TO_IDLE);
	singletonManager.addCache(memoryOnlyCache);
	return singletonManager.getCache(CACHE_NAME);
    }

}
