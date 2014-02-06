package com.github.kpacha.yuicompressorserver;

import static org.mockito.Mockito.mock;
import junit.framework.TestCase;
import static org.mockito.Mockito.when;

public class YuiCompressorServerTest extends TestCase {

	private Configuration configuration;

	public void setUp(){
		configuration = mock( Configuration.class );
	}

	public void testRunWithoutCache(){
		new YuiCompressorServer( configuration );
	}

	public void testRunWithCache(){
		when( configuration.isCacheEnabled() ).thenReturn( true );
		new YuiCompressorServer( configuration );
	}
}
