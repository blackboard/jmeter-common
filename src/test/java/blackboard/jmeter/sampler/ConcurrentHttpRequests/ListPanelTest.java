package blackboard.jmeter.sampler.ConcurrentHttpRequests;

import org.junit.Before;
import org.junit.Test;

import blackboard.jmeter.sampler.ConcurrentHttpRequests.gui.ListContentSplitPanel;
import blackboard.jmeter.sampler.ConcurrentHttpRequests.gui.ListPanel;

public class ListPanelTest
{
  ListPanel _panel;

  @Before
  public void setUp()
  {
    _panel = new ListPanel( new ListContentSplitPanel() );
    _panel.getTreePanel().addObject( "Tree Node" );
  }

  @Test
  public void testGetLastSelectedTreeNodeName()
  {
    String treeNode = _panel.getLastSelectedTreeNodeName();
    org.junit.Assert.assertEquals( treeNode, "Tree Node" );
  }

  @Test
  public void testClear()
  {
    _panel.clear();
    org.junit.Assert.assertEquals( 0, _panel.getTreePanel().getNodeCount() );
  }
}
