package blackboard.jmeter.sampler;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

public class ConcurrentHttpRequestsSamplerGui extends AbstractSamplerGui
{

  private static final long serialVersionUID = -8825258141220885722L;
  public static String DEFAULT_URL1 = "http://www.oracle.com/index.html";
  public static String DEFAULT_URL2 = "http://www.apache.org/";
  private static final String SAMPLER_LABEL = "jp@bb - Concurrent Http Requests Sampler";
  private static final String SAMPLER_COMMENT = "Blackboard Concurrent Request Sampler.";
  private JTextField url1;
  private JTextField url2;

  public ConcurrentHttpRequestsSamplerGui()
  {
    init();
  }

  private void init()
  {
    setLayout( new BorderLayout( 0, 5 ) );
    setBorder( makeBorder() );
    add( makeTitlePanel(), BorderLayout.NORTH );
    JPanel mainPanel = new JPanel( new GridBagLayout() );

    GridBagConstraints labelConstraints = new GridBagConstraints();
    labelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;

    GridBagConstraints editConstraints = new GridBagConstraints();
    editConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
    editConstraints.weightx = 1.0;
    editConstraints.fill = GridBagConstraints.HORIZONTAL;

    addToPanel( mainPanel, labelConstraints, 0, 0, new JLabel( "URL1: ", JLabel.RIGHT ) );
    addToPanel( mainPanel, editConstraints, 1, 0, url1 = new JTextField( 200 ) );
    addToPanel( mainPanel, labelConstraints, 0, 1, new JLabel( "URL2: ", JLabel.RIGHT ) );
    addToPanel( mainPanel, editConstraints, 1, 1, url2 = new JTextField( 200 ) );
    JPanel container = new JPanel( new BorderLayout() );
    container.add( mainPanel, BorderLayout.NORTH );
    add( container, BorderLayout.CENTER );
  }

  private void addToPanel( JPanel panel, GridBagConstraints constraints, int col, int row, JComponent component )
  {
    constraints.gridx = col;
    constraints.gridy = row;
    panel.add( component, constraints );
  }

  @Override
  public TestElement createTestElement()
  {
    ConcurrentHttpRequestsSampler sampler = new ConcurrentHttpRequestsSampler();
    modifyTestElement( sampler );
    sampler.setComment( SAMPLER_COMMENT );
    return sampler;
  }

  @Override
  public void configure( TestElement element )
  {
    super.configure( element );

    url1.setText( element.getPropertyAsString( ConcurrentHttpRequestsSampler.URL1 ) );
    url2.setText( element.getPropertyAsString( ConcurrentHttpRequestsSampler.URL2 ) );
  }

  @Override
  public void modifyTestElement( TestElement sampler )
  {
    super.configureTestElement( sampler );

    if ( sampler instanceof ConcurrentHttpRequestsSampler )
    {
      ConcurrentHttpRequestsSampler concurrentHttpRequestsSampler = (ConcurrentHttpRequestsSampler) sampler;
      concurrentHttpRequestsSampler.setUrl1( url1.getText() );
      concurrentHttpRequestsSampler.setUrl2( url2.getText() );
    }
  }

  @Override
  public void clearGui()
  {
    super.clearGui();
    initFields();
  }

  private void initFields()
  {
    url1.setText( DEFAULT_URL1 );
    url2.setText( DEFAULT_URL2 );
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
