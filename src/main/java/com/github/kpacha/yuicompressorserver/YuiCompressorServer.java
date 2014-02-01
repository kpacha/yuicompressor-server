package com.github.kpacha.yuicompressorserver;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;

import com.github.kpacha.yuicompressorserver.adapter.AdapterFactory;
import com.github.kpacha.yuicompressorserver.compressor.CachedCompressor;
import com.github.kpacha.yuicompressorserver.compressor.Compressor;
import com.github.kpacha.yuicompressorserver.compressor.YuiCompressor;
import com.github.kpacha.yuicompressorserver.utils.Md5Hasher;

/**
 * The YuiCompressorServer starts a jetty server and registers a
 * YuiCompressorHandler with all its dependencies.
 * 
 * @author kpacha
 */
public class YuiCompressorServer {
    private static final String CACHE_NAME = "yuicompressor-server";

    private static Logger logger = Logger.getLogger(YuiCompressorServer.class);

    private Server server;
    private Md5Hasher hasher;

    /**
     * Main method of the standalone yuicompressor-server
     * 
     * @param args
     * @throws Exception
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
     */
    public YuiCompressorServer(Configuration configuration) {
	hasher = new Md5Hasher();
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

    private Compressor getCompressor(Configuration configuration) {
	Compressor compressor = new YuiCompressor(new AdapterFactory());
	if (configuration.isCacheEnabled()) {
	    logger.debug("Init cache layer ...");
	    try {
		compressor = new CachedCompressor(compressor, hasher,
			getFreshCache());
	    } catch (CacheException e) {
		logger.warn("CacheException while instantiating the cache layer. Working without cache. Message: "
			+ e.getMessage());
	    }
	}
	return compressor;
    }

    private Cache getFreshCache() throws CacheException {
	return CacheManager.create().getCache(CACHE_NAME);
    }

}
