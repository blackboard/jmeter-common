package blackboard.jmeter;

/**
 * 
 * @author zyang
 *
 */
public class Constants
{
  public static String ADD_COMMAND = "add";
  public static String REMOVE_COMMAND = "remove";
  public static final String RESULT_OPTION = "result_option";
  
  public static enum ResultOption
  {
    ALLPASS (0),
    ONEPASS (1);
    
    private int _value;
    ResultOption(int value)
    {
      _value = value;
    }
    public int getOptionValue()
    {
      return _value;
    }
  }
}
