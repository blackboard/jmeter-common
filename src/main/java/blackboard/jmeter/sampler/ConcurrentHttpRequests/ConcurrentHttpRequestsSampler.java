package blackboard.jmeter.sampler.ConcurrentHttpRequests;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import blackboard.jmeter.Constants;
import blackboard.jmeter.sampler.ConcurrentHttpRequests.config.HttpRequestConfig;
import blackboard.jmeter.sampler.ConcurrentHttpRequests.config.MultipleHttpRequestsConfig;
import blackboard.jmeter.sampler.ConcurrentHttpRequests.gui.ListContentSplitPanel;

/**
 * The Sampler to Run multiple Http Requests Concurrently
 * 
 * @author zyang
 */
public class ConcurrentHttpRequestsSampler extends AbstractSampler implements Interruptible
{

  private static final long serialVersionUID = 2524409451854674747L;
  //Copy the NON_HTTP_RESPONSE text from HttpSamplerBase.java
  private static final String NON_HTTP_RESPONSE_CODE = "Non HTTP response code";
  private static final String NON_HTTP_RESPONSE_MESSAGE = "Non HTTP response message";

  private static final String RESPONSE_MESSAGE_FORMAT = "Number of samples in transaction : %s, number of failing samples : %s";
  private SampleResult _result = new SampleResult();

  private static final Logger log = LoggingManager.getLoggerForClass();
  ExecutorService _executor;

  @Override
  public SampleResult sample( Entry arg0 )
  {
    MultipleHttpRequestsConfig wholeConfig = (MultipleHttpRequestsConfig) getProperty( ListContentSplitPanel.CONFIG )
        .getObjectValue();
    List<HttpRequestConfig> configs = wholeConfig.getHttpRequestConfigAsList();

    _result.setSampleLabel( getName() );
    _result.sampleStart(); // start stopwatch

    try
    {
      // HttpRequest Config number is the thread pool size
      ExecutorService _executor = Executors.newFixedThreadPool( configs.size() );
      for ( HttpRequestConfig config : configs )
      {
        // Use the UrlConfig Element to setup HttpSamplerBase
        String name = config.getName();
        TestElement element = config.getUrlConfig();
        HTTPSamplerBase sampler = new HTTPSamplerProxy();
        sampler.setName( name );
        sampler.addTestElement( element );

        Runnable worker = new HttpRequest( sampler );
        _executor.execute( worker );
      }
      // This will make the executor accept no new threads
      // and finish all existing threads in the queue
      _executor.shutdown();
      // Wait until all threads are finish. 
      // TODO This timeout can be retrieved from the longest timeout of children Http requests.
      _executor.awaitTermination( 100000, TimeUnit.MILLISECONDS );
      // _result.sampleEnd() is unnecessary here because every call of _result.addSubResult( result ) would extend the End Time.
      // Also calling _result.sampleEnd() after _result.addSubResult( result ) would report error "sampleEnd called twice java.lang.Throwable: Invalid call sequence"
      processResult();
    }
    catch ( Exception e )
    {
      _result.sampleEnd(); // stop stopwatch
      _result.setSuccessful( false );
      ByteArrayOutputStream text = new ByteArrayOutputStream( 200 );
      e.printStackTrace( new PrintStream( text ) );
      _result.setResponseData( text.toByteArray() );

      _result.setResponseMessage( NON_HTTP_RESPONSE_MESSAGE + ": " + e.getMessage() );
      _result.setDataType( SampleResult.TEXT );

      _result.setResponseCode( NON_HTTP_RESPONSE_CODE + ": " + e.getClass().getName() );
      log.error( e.getMessage() );
    }

    return _result;

  }

  /**
   * set the parent result according the subResults if there is any sub result failed, then the whole sample is
   * considered as "failed"
   */
  private void processResult()
  {
    int failureNum = 0;
    _result.setSuccessful( true );
    SampleResult[] subResults = _result.getSubResults();
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
    if ( ( resultOption == Constants.ResultOption.ALLPASS.getOptionValue() && failureNum > 0 ) || failureNum == subResults.length )
    {
      _result.setSuccessful( false );
      _result.setResponseCode( failureResponseCode.toString() );

    }
    else
    {
      _result.setSuccessful( true );
      _result.setResponseCodeOK(); // 200 code
    }
    _result.setResponseMessage( String.format( RESPONSE_MESSAGE_FORMAT, subResults.length, failureNum ) );

  }

  protected SampleResult getResult()
  {
    return _result;
  }

  class HttpRequest implements Runnable
  {
    HTTPSamplerBase sampler;

    HttpRequest( HTTPSamplerBase sampler )
    {
      this.sampler = sampler;
    }

    @Override
    public void run()
    {
      SampleResult result = null;
      try
      {
        result = sampler.sample();
        // synchronized for the addSubResult method, since SampleResult.storeSubResult is not synchronized and may recreate the subResults Array by accident.
        synchronized ( _result )
        {
          _result.addSubResult( result );
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
    List<Runnable> remains = _executor.shutdownNow();
    return null == remains || remains.size() == 0;
  }

}
