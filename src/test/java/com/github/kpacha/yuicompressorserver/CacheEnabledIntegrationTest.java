package com.github.kpacha.yuicompressorserver;

import org.junit.BeforeClass;

public class CacheEnabledIntegrationTest extends AbstractIntegrationTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	setUpBeforeClass(true);
    }
}
