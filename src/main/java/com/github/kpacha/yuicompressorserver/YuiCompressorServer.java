package com.github.kpacha.yuicompressorserver;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

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
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_ALGORITHM = "SHA-1";
    private static final String CACHE_NAME = "yuicompressor-server";
    private static final int CACHE_TTL = 604800; // a week
    private static final int CACHE_TIME_TO_IDLE = 604800; // a week
    private static final boolean CACHE_ETERNAL = false;
    private static final boolean CACHE_OVERFLOW_TO_DISK = false;
    private static final int CACHE_MAX_ITEMS = 5000;

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	Compressor compressor = new YuiCompressor(new AdapterFactory());
	if (shouldCache(args)) {
	    compressor = new CachedCompressor(compressor,
		    new BufferedContentHasher(getAlgorithm(args)),
		    getFreshCache());
	}

	Server server = new Server(getPort(args));
	server.setHandler(new YuiCompressorHandler(compressor));

	server.start();
	server.join();
    }

    private static String getAlgorithm(String[] args) {
	String hashAlgorithm = DEFAULT_ALGORITHM;
	if (args.length > 1) {
	    hashAlgorithm = args[1];
	}
	return hashAlgorithm;
    }

    private static int getPort(String[] args) {
	int port = DEFAULT_PORT;
	if (args.length > 0) {
	    port = Integer.parseInt(args[0]);
	}
	return port;
    }

    private static boolean shouldCache(String[] args) {
	boolean shouldCache = true;
	if (args.length > 2) {
	    shouldCache = Integer.parseInt(args[2]) != 0;
	}
	return shouldCache;
    }

    private static Cache getFreshCache() {
	CacheManager singletonManager = CacheManager.create();
	Cache memoryOnlyCache = new Cache(CACHE_NAME, CACHE_MAX_ITEMS,
		CACHE_OVERFLOW_TO_DISK, CACHE_ETERNAL, CACHE_TTL,
		CACHE_TIME_TO_IDLE);
	singletonManager.addCache(memoryOnlyCache);
	return singletonManager.getCache(CACHE_NAME);
    }

}
