package blackboard.jmeter.sampler.ConcurrentHttpRequests.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import blackboard.jmeter.Constants;

/**
 * ListPanel Contains a ButtonPanel and ListTree
 * 
 * @author zyang
 */
public class ListPanel extends JPanel
{

  private static final long serialVersionUID = 1188388767484217398L;
  ListTree _treePanel;

  public ListPanel( ListContentSplitPanel parentPanel )
  {
    super( new BorderLayout() );
    //Create the components.
    _treePanel = new ListTree( parentPanel );
    //_treePanel.setPreferredSize( new Dimension( 300, 150 ) );

    JButton addButton = new JButton( "Add" );
    addButton.setActionCommand( Constants.ADD_COMMAND );
    addButton.addActionListener( parentPanel );

    JButton removeButton = new JButton( "Remove" );
    removeButton.setActionCommand( Constants.REMOVE_COMMAND );
    removeButton.addActionListener( parentPanel );

    JPanel buttonPanel = new JPanel( new GridLayout( 0, 2 ) );
    buttonPanel.add( addButton );
    buttonPanel.add( removeButton );

    add( _treePanel, BorderLayout.CENTER );
    add( buttonPanel, BorderLayout.NORTH );
  }

  public ListTree getTreePanel()
  {
    return _treePanel;
  }

  public String getLastSelectedTreeNodeName()
  {
    Object obj = _treePanel.getTree().getLastSelectedPathComponent();
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
    _treePanel.clear();

  }
}
