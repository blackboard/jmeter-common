package blackboard.jmeter.sampler.ConcurrentHttpRequests.gui;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.jmeter.protocol.http.config.gui.UrlConfigGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.apache.jmeter.util.JMeterUtils;

import blackboard.jmeter.sampler.ConcurrentHttpRequests.Constants;
import blackboard.jmeter.sampler.ConcurrentHttpRequests.config.HttpRequestConfig;
import blackboard.jmeter.sampler.ConcurrentHttpRequests.config.MultipleHttpRequestsConfig;

/**
 * The splitPanel contains: Left-ListPanel contains Add/Remove buttons and ListTree which lists the Http Request Names
 * Right - JPanel which contains DetailCard Panel, each Card has a HttpRequest Name and URL Config Panel
 * 
 * @author zyang
 */
public class ListContentSplitPanel extends JSplitPane implements ActionListener, TreeSelectionListener
{

  private static final long serialVersionUID = 4096032332485007430L;
  private ListPanel listPanel;
  private JPanel detailPanel;
  private ListDetailCardsMap map;
  private int requestSeq = 1;
  public static final String CONFIG = "List-Content";
  private static final String URL_CONFIG = "UrlConfigGui";
  private static final String HTTP_REQUEST_PREFIX = JMeterUtils.getResString( "web_testing_title" );

  public ListDetailCardsMap getMap()
  {
    return map;
  }

  public ListContentSplitPanel()
  {
    super( JSplitPane.HORIZONTAL_SPLIT );
    map = new ListDetailCardsMap();

    detailPanel = new JPanel( new CardLayout() );
    listPanel = new ListPanel( this );
    setLeftComponent( listPanel );
    setRightComponent( detailPanel );

    setOneTouchExpandable( true );
    setDividerLocation( 180 );
    setOpaque( true ); //content panes must be opaque
  }

  // Add New Request
  public void addNewRequest()
  {
    // Add tree node in left Panel
    String treeNodeName = HTTP_REQUEST_PREFIX + " " + requestSeq;
    treeNodeName = checkDupNodeName( treeNodeName );

    listPanel.getTreePanel().addObject( treeNodeName );

    //Add Card in right Panel
    // treeNodeName may also work for cardname
    String cardName = treeNodeName;
    cardName = checkDupCardName( cardName );
    DetailCard panel = addNewDetailCard( treeNodeName );
    map.add( treeNodeName, treeNodeName, panel );
    requestSeq++;
  }

  private String checkDupCardName( String cardName )
  {
    while ( map.containsNode( cardName ) )
    {
      cardName = cardName + UUID.randomUUID().toString().substring( 0, 6 );
    }
    return cardName;
  }

  private void setVisiblePanel( String cardName )
  {
    CardLayout layout = (CardLayout) detailPanel.getLayout();
    layout.show( detailPanel, cardName );
  }

  private DetailCard addNewDetailCard( String name )
  {
    DetailCard card = new DetailCard( name );
    detailPanel.add( card, name );
    setVisiblePanel( name );
    detailPanel.validate();
    return card;
  }

  // Remove New Request
  private void removeRequest( String treeNodeName )
  {
    listPanel.getTreePanel().removeCurrentNode();
    DetailCard panel = map.getRightPanelByTreeNodeName( treeNodeName );
    detailPanel.remove( panel );
    map.removeTreeNode( treeNodeName );
  }

  public void actionPerformed( ActionEvent e )
  {
    String command = e.getActionCommand();
    if ( Constants.ADD_COMMAND.equals( command ) )
    {
      addNewRequest();
    }
    else if ( Constants.REMOVE_COMMAND.equals( command ) )
    {
      String selectedNodeName = listPanel.getTreePanel().getCurrentNodeName();
      if ( selectedNodeName != null )
      {
        removeRequest( selectedNodeName );
      }
    }
  }

  @Override
  public void valueChanged( TreeSelectionEvent e )
  {
    // Update the name of last selected treeNode using the name in rightPanel
    if ( e.getOldLeadSelectionPath() != null )
    {
      DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) e.getOldLeadSelectionPath().getLastPathComponent();
      DetailCard lastPanel = map.getRightPanelByTreeNodeName( lastNode.toString() );
      String oldName = lastNode.toString();
      if ( lastPanel != null )
      {
        String newName = lastPanel.getNameField();
        if ( !newName.equals( oldName ) )
        {
          newName = checkDupNodeName( newName );
          lastPanel.setNameField( newName );
          lastNode.setUserObject( newName );
          map.changeTreeNodeName( oldName, newName );
        }
      }
      listPanel.getTreePanel().nodeChanged( lastNode );
    }
    // Display the new selected treeNode and its DetailCard in the right
    String selectedNodeName = listPanel.getLastSelectedTreeNodeName();
    if ( selectedNodeName != null )
    {
      setVisiblePanel( map.getCardNameByTreeNodeName( selectedNodeName ) );
    }
  }

  private String checkDupNodeName( String nodeName )
  {
    while ( map.containsNode( nodeName ) )
    {
      nodeName = nodeName + UUID.randomUUID().toString().substring( 0, 6 );
    }
    return nodeName;
  }

  // from GUI to Sampler
  public void modifyTestElement( TestElement sampler )
  {
    // update the current selected TreeNode name from the DetailCard
    // Refer to ActionRouter.performAction(...), Then invoke GuiPackage.getInstance().updateCurrentGui(), then here
    UpdateTreeNodeNamebyDetailCardNameField();
    // set Sampler from GUI
    Component[] comps = detailPanel.getComponents();
    MultipleHttpRequestsConfig wholeConfig = new MultipleHttpRequestsConfig();
    List<HttpRequestConfig> configs = new ArrayList<HttpRequestConfig>();
    for ( Component comp : comps )
    {
      DetailCard card = (DetailCard) comp;
      UrlConfigGui urlConfigGui = card.getUrlConfigPanel();
      TestElement element = urlConfigGui.createTestElement();
      element.setName( URL_CONFIG );

      HttpRequestConfig config = new HttpRequestConfig( card.getName(), element );
      configs.add( config );
    }
    wholeConfig.setConfigs( configs );
    sampler.setProperty( new TestElementProperty( CONFIG, wholeConfig ) );
  }

  /**
   * Check the current selected tree node name, if it is different from the name field in Detail card, then change it.
   * This scenario happens when the user clicks out side of the split panel after editing DetailCard name field. This is
   * different from the logic in valueChanged(TreeSelectEvent) which works for switching tree nodes and updates the last
   * tree node name according to the last DetailCard Name field.
   */
  private void UpdateTreeNodeNamebyDetailCardNameField()
  {
    if ( listPanel == null || listPanel.getTreePanel() == null || listPanel.getTreePanel().getCurrentNode() == null )
    {
      return;
    }
    DefaultMutableTreeNode currentNode = listPanel.getTreePanel().getCurrentNode();
    String oldName = currentNode.toString();
    DetailCard lastPanel = map.getRightPanelByTreeNodeName( oldName.toString() );

    if ( lastPanel != null )
    {
      String newName = lastPanel.getNameField();

      if ( !newName.equals( oldName ) )
      {
        newName = checkDupNodeName( newName );
        lastPanel.setNameField( newName );
        currentNode.setUserObject( newName );
        map.changeTreeNodeName( oldName, newName );
      }
    }
  }

  //TestElement to GUI
  public void configure( TestElement element )
  {
    clear();
    requestSeq = 1;
    MultipleHttpRequestsConfig wholeConfig = (MultipleHttpRequestsConfig) element.getProperty( CONFIG )
        .getObjectValue();
    List<HttpRequestConfig> configs = wholeConfig.getHttpRequestConfigAsList();
    ListTree tree = listPanel.getTreePanel();

    for ( HttpRequestConfig config : configs )
    {
      String name = config.getName();

      tree.addObject( name );
      DetailCard card = addNewDetailCard( name );
      card.getUrlConfigPanel().configure( config.getUrlConfig() );
      map.add( name, name, card );
      requestSeq++;
    }
  }

  public void clear()
  {
    listPanel.clear();
    detailPanel.removeAll();
    detailPanel.invalidate();
    map.clear();
  }
}
