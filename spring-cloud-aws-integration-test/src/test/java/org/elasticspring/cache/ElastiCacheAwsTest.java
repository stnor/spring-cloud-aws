/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elasticspring.cache;

import org.elasticspring.support.profile.AmazonWebserviceProfileValueSource;
import org.elasticspring.support.profile.IfAmazonWebserviceEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@ProfileValueSourceConfiguration(AmazonWebserviceProfileValueSource.class)
public class ElastiCacheAwsTest {

	@SuppressWarnings("SpringJavaAutowiringInspection")
	@Autowired
	private CachingService cachingService;

	@Autowired
	private CacheManager cacheManager;

	@Before
	public void resetInvocationCount() throws Exception {
		//Clear cache before running tests,
		Cache cacheCluster = this.cacheManager.getCache("CacheCluster");
		cacheCluster.clear();

		this.cachingService.resetInvocationCount();
	}

	@Test
	@IfAmazonWebserviceEnvironment
	public void expensiveServiceWithCacheManager() throws Exception {
		assertEquals(0, this.cachingService.getInvocationCount().get());

		assertEquals("FOO", this.cachingService.expensiveMethod("foo"));
		assertEquals(1, this.cachingService.getInvocationCount().get());

		assertEquals("FOO", this.cachingService.expensiveMethod("foo"));
		assertEquals(1, this.cachingService.getInvocationCount().get());

		assertEquals("BAR", this.cachingService.expensiveMethod("bar"));
		assertEquals(2, this.cachingService.getInvocationCount().get());
	}
}