package com.github.kpacha.yuicompressorserver;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

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
	private static final String MAX_FORM_SIZE_PROPERTY = "org.eclipse.jetty.server.Request.maxFormContentSize";

	private static final int MAX_THREADS = 500;
	private static final int OUTPUT_BUFFER_SIZE = 32768;
	private static final int REQUEST_HEADER_SIZE = 8192;
	private static final int RESPONSE_HEADER_SIZE = 8192;
	private static final int CONECTION_IDLE_TIMEOUT = 30000;

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

		server = new Server(getThreadPool());
		HttpConfiguration http_config = getHttpConfiguration();

		ServerConnector http = new ServerConnector(server,
				new HttpConnectionFactory(http_config));
		http.setPort(configuration.getPort());
		http.setIdleTimeout(CONECTION_IDLE_TIMEOUT);
		server.addConnector(http);
		server.setAttribute(MAX_FORM_SIZE_PROPERTY,
				configuration.getMaxFormSize());
		server.setHandler(new YuiCompressorHandler(
				getCompressor(configuration), hasher));
	}

	/**
	 * Get the threadpool object.
	 */
	public QueuedThreadPool getThreadPool() {
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMaxThreads(MAX_THREADS);

		return threadPool;
	}

	/**
	 * Get the HttpConfiguration object.
	 */
	public HttpConfiguration getHttpConfiguration() {
		HttpConfiguration http_config = new HttpConfiguration();
		http_config.setOutputBufferSize(OUTPUT_BUFFER_SIZE);
		http_config.setRequestHeaderSize(REQUEST_HEADER_SIZE);
		http_config.setResponseHeaderSize(RESPONSE_HEADER_SIZE);
		return http_config;
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
