package blackboard.jmeter.sampler.ConcurrentHttpRequests;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.jmeter.protocol.http.control.AuthManager;
import org.apache.jmeter.protocol.http.control.CacheManager;
import org.apache.jmeter.protocol.http.control.CookieManager;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSampleResult;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.NullProperty;
import org.apache.jmeter.testelement.property.PropertyIterator;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import blackboard.jmeter.sampler.ConcurrentHttpRequests.config.HttpRequestConfig;
import blackboard.jmeter.sampler.ConcurrentHttpRequests.config.MultipleHttpRequestsConfig;
import blackboard.jmeter.sampler.ConcurrentHttpRequests.gui.ListContentSplitPanel;

/**
 * The Sampler to Run multiple Http Requests Concurrently This Sampler won't execute this sample method since it's
 * extending HTTPSamplerBase with the purpose of reusing the addTestElement method. This helps to make it easy passing
 * the properties to the sub request samplers.
 * 
 * @author zyang
 */
public class ConcurrentHttpRequestsSampler extends HTTPSamplerBase implements Interruptible
{

  private static final long serialVersionUID = 2524409451854674747L;
  //Copy the NON_HTTP_RESPONSE text from HttpSamplerBase.java
  private static final String NON_HTTP_RESPONSE_CODE = "Non HTTP response code";
  private static final String NON_HTTP_RESPONSE_MESSAGE = "Non HTTP response message";

  private static final String RESPONSE_MESSAGE_FORMAT = "Number of samples in transaction : %s, number of failing samples : %s";
  private SampleResult result = new SampleResult();

  private static final Logger log = LoggingManager.getLoggerForClass();
  private ExecutorService executor;
  private JMeterContext jmeterContextOfParentThread;

  @Override
  public SampleResult sample( Entry arg0 )
  {
    // We must get Context here which will be used for the sub requests's variables replacement.
    // Early setting context to jmeterContextOfParentThread may miss the variable changes in the preprocessor
    jmeterContextOfParentThread = JMeterContextService.getContext();
    MultipleHttpRequestsConfig wholeConfig = (MultipleHttpRequestsConfig) getProperty( ListContentSplitPanel.CONFIG )
        .getObjectValue();
    List<HttpRequestConfig> configs = wholeConfig.getHttpRequestConfigAsList();

    result.setSampleLabel( getName() );
    result.sampleStart(); // start stopwatch

    try
    {
      // HttpRequest Config number is the thread pool size
      executor = Executors.newFixedThreadPool( configs.size() );
      for ( HttpRequestConfig config : configs )
      {
        // Use the UrlConfig Element to setup HttpSamplerBase
        String name = config.getName();
        TestElement urlConfigElement = config.getUrlConfig();
        HTTPSamplerBase sampler = new HTTPSamplerProxy();
        // Set sub request sampler with URLConfig
        sampler.addTestElement( urlConfigElement );

        addTestElementToChildSampler( sampler );

        // Set sub request sampler's name, the name may override the one which passed in from parent Concurrent sampler
        sampler.setName( name );
        sampler.setEnabled( true );
        Runnable worker = new HttpRequest( sampler );
        executor.execute( worker );
      }
      // This will make the executor accept no new threads
      // and finish all existing threads in the queue
      executor.shutdown();
      // Wait until all threads are finish. 
      // TODO This timeout can be retrieved from the longest timeout of children Http requests.
      executor.awaitTermination( 1000000, TimeUnit.MILLISECONDS );
      // result.sampleEnd() is unnecessary here because every call of result.addSubResult( result ) would extend the End Time.
      // Also calling result.sampleEnd() after result.addSubResult( result ) would report error "sampleEnd called twice java.lang.Throwable: Invalid call sequence"
      processResult();
    }
    catch ( Exception e )
    {
      result.sampleEnd(); // stop stopwatch
      result.setSuccessful( false );
      ByteArrayOutputStream text = new ByteArrayOutputStream( 200 );
      e.printStackTrace( new PrintStream( text ) );
      result.setResponseData( text.toByteArray() );

      result.setResponseMessage( NON_HTTP_RESPONSE_MESSAGE + ": " + e.getMessage() );
      result.setDataType( SampleResult.TEXT );

      result.setResponseCode( NON_HTTP_RESPONSE_CODE + ": " + e.getClass().getName() );
      log.error( e.getMessage() );
    }

    return result;

  }

  /**
   * Pass the properties from parent sampler to child. There maybe many properties which are useless to the child
   * sampler but don't hurt much. All the Managers, http Request Defaults value and Variables are the things we want to
   * pass.
   * 
   * @param childSampler
   */
  private void addTestElementToChildSampler( HTTPSamplerBase childSampler )
  {
    // Set the Http Defaults and User defined variables from Parent Concurrent Sampler to the sub request sampler
    PropertyIterator iter = this.propertyIterator();
    while ( iter.hasNext() )
    {
      JMeterProperty prop = iter.next();
      // The managers will be handled separately
      if ( prop instanceof CookieManager || prop instanceof CookieManager || prop instanceof CacheManager
           || prop instanceof AuthManager )
      {
        continue;
      }
      // Here we need to clone, otherwise, the properties will become same between the sampler and parent ConccurentHttpRequestsSampler
      JMeterProperty samplerProperty = childSampler.getProperty( prop.getName() );
      if ( samplerProperty instanceof NullProperty
           || ( samplerProperty instanceof StringProperty && samplerProperty.getStringValue().equals( "" ) ) )
      {
        childSampler.setProperty( prop.clone() );
      }
      else
      {
        samplerProperty.mergeIn( prop.clone() );
      }
    }

    // Set the managers from Parent Concurrent Sampler to the sub request sampler
    CookieManager cookieManager = this.getCookieManager();
    if ( cookieManager != null )
    {
      // CookieManager needs to be cloned to avoid ConcurrentModificationException while one sub request iterating the cookies and other changing the cookie.
      childSampler.setCookieManager( (CookieManager) cookieManager.clone() );
    }

    HeaderManager headerManager = this.getHeaderManager();
    if ( headerManager != null )
    {
      childSampler.setHeaderManager( headerManager );
    }

    CacheManager cacheManager = this.getCacheManager();
    if ( cacheManager != null )
    {
      childSampler.setCacheManager( cacheManager );
    }

    AuthManager authManager = this.getAuthManager();
    {
      childSampler.setAuthManager( authManager );
    }

  }

  /**
   * set the parent result according the subResults if there is any sub result failed, then the whole sample is
   * considered as "failed"
   */
  private void processResult()
  {
    int failureNum = 0;
    result.setSuccessful( true );
    SampleResult[] subResults = result.getSubResults();
    StringBuilder failureResponseCode = new StringBuilder();
    for ( SampleResult subResult : subResults )
    {
      if ( !subResult.isSuccessful() )
      {
        failureResponseCode.append( subResult.getResponseCode() );

        failureNum++;
      }
    }
    int resultOption = this.getPropertyAsInt( Constants.RESULT_OPTION, Constants.ResultOption.ALLPASS.getOptionValue() );
    if ( ( resultOption == Constants.ResultOption.ALLPASS.getOptionValue() && failureNum > 0 )
         || failureNum == subResults.length )
    {
      result.setSuccessful( false );
      result.setResponseCode( failureResponseCode.toString() );

    }
    else
    {
      result.setSuccessful( true );
      result.setResponseCodeOK(); // 200 code
    }
    result.setResponseMessage( String.format( RESPONSE_MESSAGE_FORMAT, subResults.length, failureNum ) );

  }

  protected SampleResult getResult()
  {
    return result;
  }

  class HttpRequest implements Runnable
  {
    private final HTTPSamplerBase sampler;

    HttpRequest( HTTPSamplerBase subSampler )
    {
      sampler = subSampler;
    }

    @Override
    public void run()
    {
      SampleResult subResult = null;
      try
      {
        // get context from parent thread, then set to current sub thread
        JMeterContextService.replaceContext( jmeterContextOfParentThread );
        JMeterContext context = JMeterContextService.getContext();
        // the samplingStarted field may affect the property value: value vs cachedValue
        context.setSamplingStarted( true );
        subResult = sampler.sample();
        // synchronized for the addSubResult method, since SampleResult.storeSubResult is not synchronized and may recreate the subResults Array by accident.
        synchronized ( result )
        {
          result.addSubResult( subResult );
        }
      }
      catch ( Exception e )
      {
        log.error( e.getMessage() );
      }
    }
  }

  @Override
  public boolean interrupt()
  {
    List<Runnable> remains = executor.shutdownNow();
    return null == remains || remains.size() == 0;
  }

  // This sampler won't execute this sample method since it's extending HTTPSamplerBase with the purpose of reusing the addTestElement method.
  @Override
  protected HTTPSampleResult sample( java.net.URL u, String method, boolean areFollowingRedirect, int depth )
  {
    return null;
  }
}
