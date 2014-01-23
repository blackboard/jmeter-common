package blackboard.jmeter.sampler;

import org.apache.jmeter.samplers.SampleResult;
import org.junit.Test;

import blackboard.jmeter.sampler.ConcurrentHttpRequestsSamplerGui;

public class ConcurrentHttpRequestsSamplerIntegTest
{
  @Test
  public void testSamplerWithValidUrls()
  {
    ConcurrentHttpRequestsSampler sampler = new ConcurrentHttpRequestsSampler();
    sampler.setUrl1( ConcurrentHttpRequestsSamplerGui.DEFAULT_URL1 );
    sampler.setUrl2( ConcurrentHttpRequestsSamplerGui.DEFAULT_URL2 );
    sampler.sample( null );
    SampleResult result = sampler.getResult();
    org.junit.Assert.assertNotNull( result );
    org.junit.Assert.assertEquals( result.getResponseCode(), "200" );
    org.junit.Assert.assertNotNull( result.getSubResults() );
    org.junit.Assert.assertEquals( result.getSubResults().length, 2 );
  }

  @Test
  public void testSamplerWithInvalidUrls()
  {
    ConcurrentHttpRequestsSampler sampler = new ConcurrentHttpRequestsSampler();
    sampler.setUrl1( ConcurrentHttpRequestsSamplerGui.DEFAULT_URL1 );
    //Invalid url
    sampler.setUrl2( "http://abc.d.e" );
    sampler.sample( null );
    SampleResult result = sampler.getResult();
    org.junit.Assert.assertNotNull( result );
    org.junit.Assert.assertNotEquals( result.getResponseCode(), "200" );
    org.junit.Assert.assertNotNull( result.getSubResults() );
    org.junit.Assert.assertEquals( result.getSubResults().length, 2 );
  }
}
