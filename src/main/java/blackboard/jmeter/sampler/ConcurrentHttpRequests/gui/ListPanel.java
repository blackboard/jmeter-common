package blackboard.jmeter.sampler.ConcurrentHttpRequests.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.apache.jmeter.util.JMeterUtils;

import blackboard.jmeter.sampler.ConcurrentHttpRequests.Constants;

/**
 * ListPanel Contains a ButtonPanel and ListTree
 * 
 * @author zyang
 */
public class ListPanel extends JPanel
{

  private static final long serialVersionUID = 1188388767484217398L;
  ListTree treePanel;

  public ListPanel( ListContentSplitPanel parentPanel )
  {
    super( new BorderLayout() );
    //Create the components.
    treePanel = new ListTree( parentPanel );

    JButton addButton = new JButton( JMeterUtils.getResString( "add" ) );
    addButton.setActionCommand( Constants.ADD_COMMAND );
    addButton.addActionListener( parentPanel );

    JButton removeButton = new JButton( JMeterUtils.getResString( "remove" ) );
    removeButton.setActionCommand( Constants.REMOVE_COMMAND );
    removeButton.addActionListener( parentPanel );

    JPanel buttonPanel = new JPanel( new GridLayout( 0, 2 ) );
    buttonPanel.add( addButton );
    buttonPanel.add( removeButton );

    add( treePanel, BorderLayout.CENTER );
    add( buttonPanel, BorderLayout.NORTH );
  }

  public ListTree getTreePanel()
  {
    return treePanel;
  }

  public String getLastSelectedTreeNodeName()
  {
    Object obj = treePanel.getTree().getLastSelectedPathComponent();
    if ( obj != null )
    {
      return obj.toString();
    }
    else
    {
      return null;
    }
  }

  public void clear()
  {
    treePanel.clear();
  }
}
