package blackboard.jmeter.sampler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * Running multiple Http Requests Concurrently
 * @ToDo
 * Now it only supports setting 2 URLs and execute using Get Http Method. This needs to be evolved to
 * support dynamic number and more attributes configuration for Http Requests.
 * @author zyang
 *
 */
public class ConcurrentHttpRequestsSampler extends AbstractSampler implements Interruptible
{

  private static final long serialVersionUID = 2524409451854674747L;
  //Copy the NON_HTTP_RESPONSE text from HttpSamplerBase.java
  private static final String NON_HTTP_RESPONSE_CODE = "Non HTTP response code";
  private static final String NON_HTTP_RESPONSE_MESSAGE = "Non HTTP response message";

  private static final String RESPONSE_MESSAGE_FORMAT = "Number of samples in transaction : %s, number of failing samples : %s";
  public static final String URL1 = "URL1";
  public static final String URL2 = "URL2";
  private SampleResult _result = new SampleResult();

  private static final Logger log = LoggingManager.getLoggerForClass();
  ExecutorService _executor;

  @Override
  public SampleResult sample( Entry arg0 )
  {
    String[] urls = getUrls();
    _result.setSampleLabel( getName() );
    _result.sampleStart(); // start stopwatch

    try
    {
      ExecutorService _executor = Executors.newFixedThreadPool( urls.length );
      for ( int i = 0; i < urls.length; i++ )
      {
        Runnable worker = new HttpRequest( urls[ i ] );
        _executor.execute( worker );
      }
      // This will make the executor accept no new threads
      // and finish all existing threads in the queue
      _executor.shutdown();
      // Wait until all threads are finish
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
    SampleResult[] subResults = _result.getSubResults();
    StringBuilder failureResponseCode = new StringBuilder();
    for ( SampleResult subResult : subResults )
    {
      if ( !subResult.isSuccessful() )
      {
        _result.setSuccessful( false );
        failureResponseCode.append( subResult.getResponseCode() );

        failureNum++;
      }
    }
    //Failed sub requests exist
    if ( failureNum > 0 )
    {
      _result.setResponseCode( failureResponseCode.toString() );
    }
    //All the sub requests succeed
    else
    {
      _result.setSuccessful( true );
      _result.setResponseCodeOK(); // 200 code
    }
    _result.setResponseMessage( String.format( RESPONSE_MESSAGE_FORMAT, subResults.length, failureNum ) );

  }

  public String[] getUrls()
  {
    return new String[] { getPropertyAsString( URL1 ), getPropertyAsString( URL2 ) };
  }

  protected SampleResult getResult()
  {
    return _result;
  }

  public void setUrl1( String url )
  {
    setProperty( URL1, url );
  }

  public void setUrl2( String url )
  {
    setProperty( URL2, url );
  }

  class HttpRequest implements Runnable
  {
    String url;

    HttpRequest( String url )
    {
      this.url = url;
    }

    @Override
    public void run()
    {
      HTTPSampler sampler = new HTTPSampler();
      sampler.setPath( url );
      sampler.setMethod( "GET" );
      sampler.setName( "Http Request" );

      SampleResult result = null;
      try
      {
        result = sampler.sample();
        _result.addSubResult( result );
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
