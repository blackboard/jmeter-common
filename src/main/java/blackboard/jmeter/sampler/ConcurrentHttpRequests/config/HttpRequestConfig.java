package blackboard.jmeter.sampler.ConcurrentHttpRequests.config;

import java.io.Serializable;

import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.testelement.property.TestElementProperty;
/**
 * HttpRequestConfig includes Http Request name and URL Config TestElement
 * @author zyang
 *
 */
public class HttpRequestConfig extends AbstractTestElement implements Serializable
{

  private static final long serialVersionUID = -5486704497421853408L;

  private static final String NAME = "HttpRequest.name";

  private static final String URL_CONFIG = "HttpRequest.url.config";

  public HttpRequestConfig()
  {
  }

  public HttpRequestConfig( String name, TestElement urlConfig )
  {
    setProperty( new StringProperty( NAME, name ) );
    setProperty( new TestElementProperty( URL_CONFIG, urlConfig ) );
  }

  @Override
  public void setName( String newName )
  {
    setProperty( new StringProperty( NAME, newName ) );
  }

  @Override
  public String getName()
  {
    return getPropertyAsString( NAME );
  }

  public void setUrlConfig( TestElement urlConfig )
  {
    setProperty( new TestElementProperty( URL_CONFIG, urlConfig ) );
  }

  public TestElement getUrlConfig()
  {
    JMeterProperty prop = this.getProperty( URL_CONFIG );
    return (TestElement) prop.getObjectValue();
  }
}
