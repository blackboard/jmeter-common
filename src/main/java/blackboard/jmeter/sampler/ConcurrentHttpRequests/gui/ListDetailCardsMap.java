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

  private Map<String, String> _treeNodeNameToCardName = new HashMap<String, String>();
  private Map<String, DetailCard> _cardNameToRightPanel = new HashMap<String, DetailCard>();
  
  public void changeTreeNodeName(String oldName, String newName)
  {
    String cardName = _treeNodeNameToCardName.get( oldName );
    _treeNodeNameToCardName.remove( oldName );
    _treeNodeNameToCardName.put( newName, cardName );
  }
  
  public void removeTreeNode(String name)
  {
    String cardName = _treeNodeNameToCardName.get( name );
    _treeNodeNameToCardName.remove( name );
    _cardNameToRightPanel.remove( cardName );
  }
  
  public DetailCard getRightPanelByTreeNodeName(String name)
  {
    String cardName = _treeNodeNameToCardName.get( name );
    return _cardNameToRightPanel.get( cardName );
  }
  
  public String getCardNameByTreeNodeName(String name)
  {
    return _treeNodeNameToCardName.get( name );
  }
  
  public void add(String treeNodeName, String cardName, DetailCard panel)
  {
    _treeNodeNameToCardName.put( treeNodeName, cardName );
    _cardNameToRightPanel.put( cardName, panel );
  }
  
  public void clear()
  {
    _treeNodeNameToCardName.clear();
    _cardNameToRightPanel.clear();
  }
  
  public Set<String> getNodeNames()
  {
    Set<String> keys = _treeNodeNameToCardName.keySet();
    return keys;
  }
}
