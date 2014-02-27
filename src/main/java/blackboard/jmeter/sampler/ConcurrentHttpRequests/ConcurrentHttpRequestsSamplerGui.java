package blackboard.jmeter.sampler.ConcurrentHttpRequests;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.jmeter.gui.UnsharedComponent;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

import blackboard.jmeter.sampler.ConcurrentHttpRequests.gui.ListContentSplitPanel;

/**
 * GUI class for Concurrent Http Requests Sampler The Sampler is to run multiple Http Requests Concurrently. 
 * To keep the sub Http Request Samplers running independently: 
 * JMeter Properties will be cloned to each sub sampler; 
 * CookieManager is also cloned to each sampler, this also avoids ConcurrentModificationException while one sub request iterating the
 * cookies and other changing the cookie. The issue may happen since we clone CookieManager to each sub sampler: any
 * changes happened during the sub sampler won't be reflected to the Thread CookieManager. For example, if any sub
 * sampler needs to add or update a cookie value, it only affects the cloned CookieManager, instead of the CookieManager
 * for the JMeter Thread. 
 * It would be ideal to merge sub sampler cookies back to the Thread cookieManager once all the
 * sub samplers finish, but needs a merge strategy.
 * 
 * @author zyang
 */

public class ConcurrentHttpRequestsSamplerGui extends AbstractSamplerGui implements UnsharedComponent
{

  private static final long serialVersionUID = -8825258141220885722L;
  private static final String SAMPLER_LABEL = "Concurrent Http Requests Sampler";
  private ListContentSplitPanel splitPanel;
  private static final String ALL_PASS = "Passes if all sub-requests pass";
  private static final String ONE_PASS = "Passes if one sub-request passes";
  private static final String RESULT_OPTION = "Result Option: ";
  
  private JRadioButton allPassButton;
  private JRadioButton onePassButton;

  public ConcurrentHttpRequestsSamplerGui()
  {
    init();
  }

  private void init()
  {
    setLayout( new BorderLayout( 0, 5 ) );
    setBorder( makeBorder() );
    add( makeTitlePanel(), BorderLayout.NORTH );

    JPanel contentPanel = new JPanel( new BorderLayout( 0, 5 ) );
    splitPanel = new ListContentSplitPanel();
    contentPanel.add( makeErrorOptionPanel(), BorderLayout.NORTH );
    contentPanel.add( splitPanel, BorderLayout.CENTER );

    add( contentPanel, BorderLayout.CENTER );
  }

  private Container makeErrorOptionPanel()
  {
    HorizontalPanel panel = new HorizontalPanel();
    panel.add( new JLabel( RESULT_OPTION, JLabel.RIGHT ) );

    allPassButton = new JRadioButton( ALL_PASS );
    allPassButton.setSelected( true );
    onePassButton = new JRadioButton( ONE_PASS );

    ButtonGroup group = new ButtonGroup();
    group.add( allPassButton );
    group.add( onePassButton );

    panel.add( allPassButton );
    panel.add( onePassButton );
    return panel;
  }

  @Override
  public TestElement createTestElement()
  {
    ConcurrentHttpRequestsSampler sampler = new ConcurrentHttpRequestsSampler();
    modifyTestElement( sampler );
    sampler.setProperty( Constants.RESULT_OPTION, Constants.ResultOption.ALLPASS.getOptionValue() );

    return sampler;
  }

  // From TestElement to GUI
  @Override
  public void configure( TestElement element )
  {
    super.configure( element );
    int resultOption = element.getPropertyAsInt( Constants.RESULT_OPTION );
    if ( resultOption == Constants.ResultOption.ALLPASS.getOptionValue() )
    {
      allPassButton.setSelected( true );
    }
    else
    {
      onePassButton.setSelected( true );
    }
    ConcurrentHttpRequestsSampler sampler = (ConcurrentHttpRequestsSampler) element;
    splitPanel.configure( sampler );
  }

  // From GUI to Sampler
  @Override
  public void modifyTestElement( TestElement sampler )
  {
    sampler.clear();
    sampler.setProperty( Constants.RESULT_OPTION, allPassButton.isSelected()
        ? Constants.ResultOption.ALLPASS.getOptionValue()
        : Constants.ResultOption.ONEPASS.getOptionValue() );
    splitPanel.modifyTestElement( sampler );
    this.configureTestElement( sampler );
  }

  @Override
  public void clearGui()
  {
    super.clearGui();
    splitPanel.clear();
  }

  @Override
  public String getLabelResource()
  {
    return this.getClass().getSimpleName();
  }

  @Override
  public String getStaticLabel()
  {
    return SAMPLER_LABEL;
  }

}
