package blackboard.jmeter.sampler.ConcurrentHttpRequests;

import org.junit.Before;
import org.junit.Test;

import blackboard.jmeter.sampler.ConcurrentHttpRequests.gui.ListContentSplitPanel;
import blackboard.jmeter.sampler.ConcurrentHttpRequests.gui.ListTree;

public class ListTreeTest
{
  ListTree _listTree;

  @Before
  public void setUp()
  {
    _listTree = new ListTree( new ListContentSplitPanel() );
  }

  @Test
  public void testAddObject()
  {
    _listTree.addObject( "Node1" );
    _listTree.addObject( "Node2" );
    org.junit.Assert.assertEquals( _listTree.getNodeCount(), 2 );

  }

  @Test
  public void testClear()
  {
    _listTree.addObject( "Node1" );
    _listTree.addObject( "Node2" );
    _listTree.clear();
    org.junit.Assert.assertEquals( _listTree.getNodeCount(), 0 );

  }

  @Test
  public void testRemoveCurrentNode()
  {
    _listTree.addObject( "Node1" );
    _listTree.addObject( "Node2" );
    _listTree.removeCurrentNode();
    org.junit.Assert.assertEquals( _listTree.getNodeCount(), 1 );

  }
}
