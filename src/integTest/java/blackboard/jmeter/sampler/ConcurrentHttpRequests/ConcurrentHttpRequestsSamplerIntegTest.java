package blackboard.jmeter.sampler.ConcurrentHttpRequests;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.apache.jmeter.util.JMeterUtils;
import org.junit.Before;
import org.junit.Test;

import blackboard.jmeter.sampler.ConcurrentHttpRequests.config.HttpRequestConfig;
import blackboard.jmeter.sampler.ConcurrentHttpRequests.config.MultipleHttpRequestsConfig;
import blackboard.jmeter.sampler.ConcurrentHttpRequests.gui.ListContentSplitPanel;

public class ConcurrentHttpRequestsSamplerIntegTest
{
  ConcurrentHttpRequestsSampler _sampler;
  //2 valid urls, and 1 invalid
  String[] _urls = new String[] { "http://www.google.com", "http://www.baidu.com", "http://www.abc.e.e.f.com" };

  @Before
  public void setUp()
  {
    JMeterUtils.setLocale(new Locale("ignoreResources"));
    _sampler = new ConcurrentHttpRequestsSampler();

    MultipleHttpRequestsConfig wholeConfig = new MultipleHttpRequestsConfig();
    List<HttpRequestConfig> configs = new ArrayList<HttpRequestConfig>();
    for ( int i = 0; i < 3; i++ )
    {
      HTTPSampler urlConfigElement = new HTTPSampler();
      urlConfigElement.setPath( _urls[ i ] );
      urlConfigElement.setMethod( "GET" );
      urlConfigElement.setName( "Http Request " + i );

      urlConfigElement.setName( "Url Config" );

      HttpRequestConfig config = new HttpRequestConfig( "Http Request " + i, urlConfigElement );
      configs.add( config );
    }
    wholeConfig.setConfigs( configs );
    _sampler.setProperty( new TestElementProperty( ListContentSplitPanel.CONFIG, wholeConfig ) );
  }

  @Test
  public void testSampleAllPass()
  {
    _sampler.setProperty( Constants.RESULT_OPTION, Constants.ResultOption.ALLPASS.getOptionValue() );
    _sampler.sample( null );
    org.junit.Assert.assertEquals( 3, _sampler.getResult().getSubResults().length );
    org.junit.Assert.assertEquals( _sampler.getResult().isSuccessful(), false );
  }

  @Test
  public void testSampleOnePass()
  {
    _sampler.setProperty( Constants.RESULT_OPTION, Constants.ResultOption.ONEPASS.getOptionValue() );
    _sampler.sample( null );
    org.junit.Assert.assertEquals( 3, _sampler.getResult().getSubResults().length );
    org.junit.Assert.assertEquals( _sampler.getResult().isSuccessful(), true );
  }
}
