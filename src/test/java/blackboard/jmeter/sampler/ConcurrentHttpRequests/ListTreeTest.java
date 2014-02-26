package blackboard.jmeter.sampler.ConcurrentHttpRequests;

import java.util.Locale;

import org.apache.jmeter.util.JMeterUtils;
import org.junit.Before;
import org.junit.Test;

import blackboard.jmeter.sampler.ConcurrentHttpRequests.gui.ListContentSplitPanel;
import blackboard.jmeter.sampler.ConcurrentHttpRequests.gui.ListTree;

public class ListTreeTest
{
  ListTree listTree;

  @Before
  public void setUp()
  {
    JMeterUtils.setLocale(new Locale("ignoreResources"));
    listTree = new ListTree( new ListContentSplitPanel() );
  }

  @Test
  public void testAddObject()
  {
    listTree.addObject( "Node1" );
    listTree.addObject( "Node2" );
    org.junit.Assert.assertEquals( listTree.getNodeCount(), 2 );

  }

  @Test
  public void testClear()
  {
    listTree.addObject( "Node1" );
    listTree.addObject( "Node2" );
    listTree.clear();
    org.junit.Assert.assertEquals( listTree.getNodeCount(), 0 );

  }

  @Test
  public void testRemoveCurrentNode()
  {
    listTree.addObject( "Node1" );
    listTree.addObject( "Node2" );
    listTree.removeCurrentNode();
    org.junit.Assert.assertEquals( listTree.getNodeCount(), 1 );

  }
}
