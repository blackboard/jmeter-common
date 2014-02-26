package blackboard.jmeter.sampler.ConcurrentHttpRequests;

import java.util.Locale;

import org.apache.jmeter.util.JMeterUtils;
import org.junit.Before;
import org.junit.Test;

import blackboard.jmeter.sampler.ConcurrentHttpRequests.gui.ListContentSplitPanel;
import blackboard.jmeter.sampler.ConcurrentHttpRequests.gui.ListPanel;

public class ListPanelTest
{
  ListPanel panel;

  @Before
  public void setUp()
  {
    JMeterUtils.setLocale(new Locale("ignoreResources"));
    panel = new ListPanel( new ListContentSplitPanel() );
    panel.getTreePanel().addObject( "Tree Node" );
  }

  @Test
  public void testGetLastSelectedTreeNodeName()
  {
    String treeNode = panel.getLastSelectedTreeNodeName();
    org.junit.Assert.assertEquals( treeNode, "Tree Node" );
  }

  @Test
  public void testClear()
  {
    panel.clear();
    org.junit.Assert.assertEquals( 0, panel.getTreePanel().getNodeCount() );
  }
}
