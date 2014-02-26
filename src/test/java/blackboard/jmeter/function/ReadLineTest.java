package blackboard.jmeter.function;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import blackboard.jmeter.functions.ReadLine;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import static org.junit.Assert.*;

import com.blackboard.learn.mobile.mlcs.tests.performance.replay.GlobalContext;
import com.blackboard.learn.mobile.mlcs.tests.performance.replay.ReadLineWrapper;

public class ReadLineTest {
	
	private static final Logger log = LoggingManager.getLoggerForClass();
	private static ReadLineWrapper wrapFileReader = ReadLineWrapper.INSTANCE;
	private static GlobalContext context = GlobalContext.INSTANCE;
	
	// Should not be executed if no file has been given
	@Test
	public void testFailsWithOutFileNameEverPresent(){
		System.out.println("Ccontext in test: " + context);
		context.putObject("REQUEST_NUM", 0);
		wrapFileReader.close(); // Close all current instances of line readers
		try{
			ReadLine rl = setReadLineParams(new String(), new String()); // Give empty strings as parameters
			String executeResult = rl.execute();
			assert executeResult == null; // Nothing should be returned
			throw new InvalidVariableException();
		}
		catch(InvalidVariableException e)
		{
			log.info("Passed null parameters test");	
		}
	}
	
	// Should create a valid function when a filename is passed
	@Test
	public void testPassesWithFileName(){
		try{
			context.clear();
			context.putObject("REQUEST_NUM", 0);
			wrapFileReader.close();
			createData(); // Create test data for readline function
			ReadLine ln = setReadLineParams(new String("/Users/molague/work/jmeter-common/data.txt"), new String() );
			//System.out.println("file name is " + ln.getFileName());
			String s = ln.execute();
			System.out.println(s);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Test failed");
		}

	}
	
	// Should pass if no file name is given but the map is not empty.
	@Test
	public void testPassesWithPastFileName(){
		try{
			context.clear();
			context.putObject("REQUEST_NUM", 0);
			context.putObject("FILE_NAME", "/Users/molague/work/jmeter-common/data.txt");
			wrapFileReader.close();
			createData();
			ReadLine ln = setReadLineParams(new String(), new String());
			//System.out.println("file name is " + ln.getFileName());
			String s = ln.execute();
			System.out.println(s);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Test failed");
		}
	}
	
	@Test
	public void testPassesWithMulitpleCalls(){
		try{
			context.clear();
			context.putObject("REQUEST_NUM", 0);
			wrapFileReader.close();
			createData();
			for(int i = 0; i < 5; i++)
			{
				ReadLine ln = setReadLineParams(new String("/Users/molague/work/jmeter-common/data.txt"), new String());
				String s =ln.execute();
			}
			System.out.println("The request number is " + context.getObject("REQUEST_NUM"));
			assert Integer.parseInt( (String) context.getObject("REQUEST_NUM") ) == 5;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("Test failed");
		}
	}
	
	//Helper Methods
	
	public int randInt(int min, int max)
	{
		int result = min + new Random().nextInt(max);
		return result;
	}
	
	public ReadLine setReadLineParams(String p1, String p2) throws InvalidVariableException
	{
		
		ReadLine rl = new ReadLine();
		Collection<CompoundVariable> params = new LinkedList<CompoundVariable>();
		if (p1 != null && !p1.isEmpty())
		{
			params.add(new CompoundVariable(p1));
		}
		if(p2 != null && !p2.isEmpty()){
			params.add(new CompoundVariable(p2));
		}
		
		rl.setParameters(params);
		return rl;
	}
	
	public void createData()
	{
		try{
			BufferedWriter bf = new BufferedWriter(new FileWriter("data.txt"));
			for(int i = 0; i < 2000; i++){
				bf.write(String.valueOf(randInt(1,200)) + "\n");
			}
			bf.close();
		
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
}
