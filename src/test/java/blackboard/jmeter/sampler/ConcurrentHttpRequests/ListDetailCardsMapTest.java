package blackboard.jmeter.sampler.ConcurrentHttpRequests;

import static org.mockito.Mockito.mock;

import java.util.Locale;

import org.apache.jmeter.util.JMeterUtils;
import org.junit.Before;
import org.junit.Test;

import blackboard.jmeter.sampler.ConcurrentHttpRequests.gui.DetailCard;
import blackboard.jmeter.sampler.ConcurrentHttpRequests.gui.ListDetailCardsMap;

public class ListDetailCardsMapTest
{
  ListDetailCardsMap map;
  private static String NODE_NAME_PREFIX = "HTTP REQUEST ";
  private static String CARD_NAME_PREFIX = "CARD NAME ";

  @Before
  public void setUp()
  {
    JMeterUtils.setLocale(new Locale("ignoreResources"));
    map = new ListDetailCardsMap();
    map.add( NODE_NAME_PREFIX + "1", CARD_NAME_PREFIX + "1", mock( DetailCard.class ) );
    map.add( NODE_NAME_PREFIX + "2", CARD_NAME_PREFIX + "2", mock( DetailCard.class ) );
    map.add( NODE_NAME_PREFIX + "3", CARD_NAME_PREFIX + "3", mock( DetailCard.class ) );
  }

  @Test
  public void testgetCardNameByTreeNodeName()
  {
    map.add( NODE_NAME_PREFIX, CARD_NAME_PREFIX, mock( DetailCard.class ) );
    org.junit.Assert.assertEquals( map.getCardNameByTreeNodeName( NODE_NAME_PREFIX ), CARD_NAME_PREFIX );
  }

  @Test
  public void testgetNodeNames()
  {
    org.junit.Assert.assertEquals( map.getNodeNames().size(), 3 );
    org.junit.Assert.assertTrue( map.getNodeNames().contains( NODE_NAME_PREFIX + "1" ) );
    org.junit.Assert.assertTrue( map.getNodeNames().contains( NODE_NAME_PREFIX + "2" ) );
    org.junit.Assert.assertTrue( map.getNodeNames().contains( NODE_NAME_PREFIX + "3" ) );
  }

  @Test
  public void testChangeTreeName()
  {
    map.changeTreeNodeName( NODE_NAME_PREFIX + "1", NODE_NAME_PREFIX + "5" );
    org.junit.Assert.assertEquals( map.getNodeNames().size(), 3 );
    org.junit.Assert.assertEquals( map.getCardNameByTreeNodeName( NODE_NAME_PREFIX + "5" ), CARD_NAME_PREFIX + "1" );
    org.junit.Assert.assertNull( map.getCardNameByTreeNodeName( NODE_NAME_PREFIX + "1" ) );
  }

  @Test
  public void testClear()
  {
    map.clear();
    org.junit.Assert.assertEquals( map.getNodeNames().size(), 0 );
  }

}
