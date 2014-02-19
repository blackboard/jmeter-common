package blackboard.jmeter.functions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.TestListener;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.util.JMeterStopThreadException;
import org.apache.log.Logger;

import com.blackboard.learn.mobile.mlcs.tests.performance.replay.GlobalContext;
import com.blackboard.learn.mobile.mlcs.tests.performance.replay.PersistentFileReader;
/**
* ReadLine Function to read a 
*
* Parameters:
* - file name
* - use multiple threads boolean (optional - defaults to false)
*
* Returns:
* - the next line from the file or thread
* - empty string if IOexception is thrown at any time
*/


public class ReadLine extends AbstractFunction implements TestStateListener 
{
	private static final Logger _log = LoggingManager.getLoggerForClass();

	private static final List<String> _desc = new LinkedList<String>();

	private static final String KEY = "__ReadLine"; // Function name


	static 
	{
		_desc.add(JMeterUtils.getResString( "read_file_file_name" ));  //$NON-NLS-1$
		_desc.add(JMeterUtils.getResString( "function_name_parpot" )); //$NON-NLS-1$
		_desc.add(JMeterUtils.getResString("use_multiple_instance"));
		_desc.add(JMeterUtils.getResString( "desired_rate" )); //$NON-NLS-1$
		_desc.add(JMeterUtils.getResString( "current_rate" )); //$NON-NLS-1$
		_desc.add(JMeterUtils.getResString( "current_request"));
		_desc.add(JMeterUtils.getResString( "current_time" ));
	}

	private static final int MIN_PARAM_COUNT = 1;

	private static final int MAX_PARAM_COUNT = 2;
	
	private static final int MAX_THREAD_COUNT = 10;

	private Object[] _values;

	private BufferedReader _myBread = null; // BufferedReader

	private boolean _firstTime = true; // Is the file to be opened first?

	private boolean _multipleUse = false; // Multiple thread use?

	private int currentRate; // current rate of requests/minute

	private int desiredRate; // Ultimate rate of requests/minute
	
	private int requestNumber; // Current request and line number
	
	private long currentTime; // Current location in time relative to the beginning of the test

	private String _fileName; // needed for error messages
	
	private static PersistentFileReader globalFileReader = PersistentFileReader.INSTANCE;
	
	public static Map<String, BufferedReader> _readers = globalFileReader.getFileReaders();

	public ReadLine()
	{ 
		if ( _log.isDebugEnabled() ) 
		{
			_log.debug("++++++++ Construct " + this );
		}
	}

	private synchronized void closeFile() 
	{
		if ( _myBread == null ) //Is there a file open?
		{
			return;
		}
		String tn = Thread.currentThread().getName();
		_log.info(tn + " closing file " + _fileName);
		try
		{
			_myBread.close();
		}
		catch ( IOException e )
		{
			_log.error( "closeFile() error: " + e.toString(), e);//$NON-NLS-1$
		}

	}

	private synchronized void openFile(boolean _multipleUse) 
	{
		try
		{
			if ( _fileName == null ) 
			{
				Set<String> fileNames = _readers.keySet();
				if (!fileNames.isEmpty())
				{
					String latestFileName = (String) _readers.keySet().toArray()[ _readers.size() ]; // Grab the filename of the latest file opened.
					_myBread = _readers.get( latestFileName ); // If there is no fileName included, use the latest reader
				}
				else
				{
					_log.error( "No file name has been provided and no files have been opened." );
					throw new IOException("No file name provided and no files open");
				}
			}
			else
			{
				if ( _readers.containsKey(_fileName) ) // If the file has already been opened
				{
					/* Will implement this later. This is an option for the user to select whether or not the same file should be opened independently by all threads
					if ( _multipleUse )
					{
						_myBread = new BufferedReader( new FileReader( _fileName )); // If the latest fileName is included but the file is already open but we allow for multiple readers, then 
					}
					else
					{
						_myBread = _readers.get( _readers.size() ); // Get the latest 
					}
					*/
					_myBread = _readers.get( _fileName ); // set the reader to be the reader of the 
				}
				else
				{
					_myBread = new BufferedReader( new FileReader( _fileName ));
				}
			}		
		}
		catch ( IOException e )
		{
			_firstTime = false;
			_log.error( "openFile() error: " + e.toString(), e );
		}

	}
	
	
	public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
	{
		
		String myValue = "" ;
		long waitTime = getWaitTime();

		try{
		
			if ( _firstTime )
			{
				openFile( _multipleUse );
				_firstTime = false;
			}
			
			if ( _myBread != null )
			{	
				openFile( _multipleUse );
				if ( waitTime > 0.0 )
				{
					Thread.currentThread().sleep( waitTime );
				}
				else
				{
					LineNumberReader myLineNumberReader = new LineNumberReader( _myBread );
					while (myLineNumberReader.getLineNumber() < requestNumber - 1)
						myLineNumberReader.readLine();
					myValue = myLineNumberReader.readLine();
					return myValue;
				}

			}
			return myValue;
		}
		catch ( IOException e )
		{
			
		}
		catch ( InterruptedException e )
		{
			
		}
		
		return myValue;
	}

	public long getWaitTime()
	{
		long waitTime;
		
		return waitTime;
	}
	
	public synchronized void setParameters( Collection<CompoundVariable> parameters ) throws InvalidVariableException 
	{
		_log.debug("setParameter - Collection.size=" + parameters.size());
		_values = parameters.toArray();

		if ( _log.isDebugEnabled() )
		{
			for ( int i = 0; i < parameters.size(); i++ ) 
			{
				_log.debug( "i:" + ((CompoundVariable) _values[i]).execute());
			}

		}

		checkParameterCount(parameters, 4);

	}

	/** {@inheritDoc} */
	public String getReferenceKey() 
	{
		return KEY;
	}

	/** {@inheritDoc} */
	@Override
	public List<String> getArgumentDesc()
	{
		return _desc;
	}

	/** {@inheritDoc} */
	public void testEnded() 
	{
        this.testEnded(""); //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    public void testEnded(String host) 
    {
        closeFile();
    }

	@Override
	public void testStarted() 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testStarted(String arg0) 
	{
		// TODO Auto-generated method stub
		
	}


}