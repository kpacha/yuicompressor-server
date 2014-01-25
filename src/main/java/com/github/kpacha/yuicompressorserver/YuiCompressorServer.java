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

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
	int port = getPort(args);
	String hashAlgorithm = getAlgorithm(args);

	Compressor compressor = new CachedCompressor(new YuiCompressor(
		new AdapterFactory()),
		new BufferedContentHasher(hashAlgorithm), getFreshCache());

	Server server = new Server(port);
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

    private static Cache getFreshCache() {
	CacheManager singletonManager = CacheManager.create();
	Cache memoryOnlyCache = new Cache("testCache", 5000, false, false, 5, 2);
	singletonManager.addCache(memoryOnlyCache);
	return singletonManager.getCache("testCache");
    }

}
