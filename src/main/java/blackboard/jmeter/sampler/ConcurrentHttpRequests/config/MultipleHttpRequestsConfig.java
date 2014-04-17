package blackboard.jmeter.sampler.ConcurrentHttpRequests.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.PropertyIterator;

public class MultipleHttpRequestsConfig extends ConfigTestElement implements Serializable
{

  private static final long serialVersionUID = 6427354049535020395L;
  /** The name of the property used to store the Configs. */
  private static final String CONFIG = "MultipleHttpRequests.config"; //$NON-NLS-1$

  /**
   * Create a new Configs object with no Configs.
   */
  public MultipleHttpRequestsConfig()
  {
    setProperty( new CollectionProperty( CONFIG, new ArrayList<HttpRequestConfig>() ) );
  }

  /**
   * Get the Configs.
   * 
   * @return the Configs
   */
  public CollectionProperty getMultipleHttpRequestsConfig()
  {
    return (CollectionProperty) getProperty( CONFIG );
  }

  public List<HttpRequestConfig> getHttpRequestConfigAsList()
  {
    PropertyIterator iter = getMultipleHttpRequestsConfig().iterator();
    List<HttpRequestConfig> configs = new ArrayList<HttpRequestConfig>();
    while ( iter.hasNext() )
    {
      HttpRequestConfig config = (HttpRequestConfig) iter.next().getObjectValue();
      configs.add( config );
    }
    return configs;
  }

  /**
   * Clear the Configs.
   */
  @Override
  public void clear()
  {
    super.clear();
    setProperty( new CollectionProperty( CONFIG, new ArrayList<HttpRequestConfig>() ) );
  }

  /**
   * Set the list of Configs. Any existing Configs will be lost.
   * 
   * @param Configs the new Configs
   */
  public void setConfigs( List<HttpRequestConfig> configs )
  {
    setProperty( new CollectionProperty( CONFIG, configs ) );
  }
}
