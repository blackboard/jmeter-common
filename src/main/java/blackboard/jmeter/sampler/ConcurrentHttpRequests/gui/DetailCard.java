package blackboard.jmeter.sampler.ConcurrentHttpRequests.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.jmeter.protocol.http.config.gui.UrlConfigGui;
import org.apache.jmeter.util.JMeterUtils;

public class DetailCard extends JPanel
{
  /**
   * HttpRequest Name and UrlConfig
   */
  private static final long serialVersionUID = -8391261438450158997L;
  private JTextField _nameField;
  private UrlConfigGui _urlConfigPanel;

  public DetailCard( String name )
  {
    super( new BorderLayout() );

    JPanel namePanel = new JPanel( new GridBagLayout() );
    GridBagConstraints labelConstraints = new GridBagConstraints();
    labelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;

    GridBagConstraints editConstraints = new GridBagConstraints();
    editConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
    editConstraints.weightx = 1.0;
    editConstraints.fill = GridBagConstraints.HORIZONTAL;
    addToPanel( namePanel, labelConstraints, 0, 0, new JLabel( JMeterUtils.getResString( "web_testing_title" ) + " "
                                                               + JMeterUtils.getResString( "name" ), JLabel.RIGHT ) );
    addToPanel( namePanel, editConstraints, 1, 0, _nameField = new JTextField( 15 ) );
    _nameField.setText( name );

    this.add( namePanel, BorderLayout.NORTH );

    _urlConfigPanel = new UrlConfigGui();
    this.add( _urlConfigPanel, BorderLayout.CENTER );
  }

  private void addToPanel( JPanel panel, GridBagConstraints constraints, int col, int row, JComponent component )
  {
    constraints.gridx = col;
    constraints.gridy = row;
    panel.add( component, constraints );
  }

  public String getNameField()
  {
    return _nameField.getText();
  }

  public String getName()
  {
    return _nameField.getText();

  }

  public void setNameField( String name )
  {
    _nameField.setText( name );
  }

  public UrlConfigGui getUrlConfigPanel()
  {
    return _urlConfigPanel;
  }
}
