package blackboard.jmeter.sampler.ConcurrentHttpRequests;

/**
 * @author zyang
 */
public class Constants
{
  public static String ADD_COMMAND = "add";
  public static String REMOVE_COMMAND = "remove";
  public static final String RESULT_OPTION = "result_option";

  public static enum ResultOption
  {
    ALLPASS( 0 ),
    ONEPASS( 1 );

    private int value;

    ResultOption( int val )
    {
      value = val;
    }

    public int getOptionValue()
    {
      return value;
    }
  }
}
