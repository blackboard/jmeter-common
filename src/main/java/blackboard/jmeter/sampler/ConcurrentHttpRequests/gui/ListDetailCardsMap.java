package blackboard.jmeter.sampler.ConcurrentHttpRequests.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * store 2 maps:
 * the map of treeNodeName : cardName
 * the map of cardName : DetailCard
 * 
 * the treeNodeName may change
 * @author zyang
 *
 */
public class ListDetailCardsMap
{

  private Map<String, String> treeNodeNameToCardName = new HashMap<String, String>();
  private Map<String, DetailCard> cardNameToRightPanel = new HashMap<String, DetailCard>();
  
  public void changeTreeNodeName(String oldName, String newName)
  {
    String cardName = treeNodeNameToCardName.get( oldName );
    treeNodeNameToCardName.remove( oldName );
    treeNodeNameToCardName.put( newName, cardName );
  }
  
  public void removeTreeNode(String name)
  {
    String cardName = treeNodeNameToCardName.get( name );
    treeNodeNameToCardName.remove( name );
    cardNameToRightPanel.remove( cardName );
  }
  
  public DetailCard getRightPanelByTreeNodeName(String name)
  {
    String cardName = treeNodeNameToCardName.get( name );
    return cardNameToRightPanel.get( cardName );
  }
  
  public String getCardNameByTreeNodeName(String name)
  {
    return treeNodeNameToCardName.get( name );
  }
  
  public void add(String treeNodeName, String cardName, DetailCard panel)
  {
    treeNodeNameToCardName.put( treeNodeName, cardName );
    cardNameToRightPanel.put( cardName, panel );
  }
  
  public void clear()
  {
    treeNodeNameToCardName.clear();
    cardNameToRightPanel.clear();
  }
  
  public Set<String> getNodeNames()
  {
    Set<String> keys = treeNodeNameToCardName.keySet();
    return keys;
  }

  public boolean containsNode( String nodeName )
  {
    return treeNodeNameToCardName.containsKey( nodeName );
  }
}
