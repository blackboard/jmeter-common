package blackboard.jmeter.functions;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import com.blackboard.learn.mobile.mlcs.tests.performance.replay.GlobalContext;
import com.blackboard.learn.mobile.mlcs.tests.performance.replay.ReadLineWrapper;
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
	private static final Logger log = LoggingManager.getLoggerForClass();
	private static final List<String> desc = new LinkedList<String>();
	private static final String KEY = "__ReadLine"; // Function name
	
	private Object[] values = new Object[2]; //Values passed to initialize the function
	private LineNumberReader _myBread = null; // BufferedReader
	private boolean firstTime = true; // Is the file to be opened first?
	private boolean multipleUse = false; // Multiple thread use?
	private String fileName; // needed for error messages
	private int requestNumber; // Line number that we want to extract
	
	public static GlobalContext context = GlobalContext.INSTANCE; // Provides access to outside variables
	private static ReadLineWrapper globalFileReader = ReadLineWrapper.INSTANCE;
	public static Map<String, LineNumberReader> readers = globalFileReader.getFileReaders();
	
	static 
	{
		desc.add("read_file_file_name");  //$NON-NLS-1$
		desc.add("use_multiple_instance");
	}
	
	public ReadLine()
	{ 
		super();
		if ( log.isDebugEnabled() ) 
		{
			log.debug("++++++++ Construct " + this );
		}
		
		loadExternalVariables();
	}
	
	private synchronized void loadExternalVariables()
	{
		requestNumber = (int) context.getObject("REQUEST_NUM");
	}

	private synchronized void closeFile() 
	{
		if ( _myBread == null ) //Is there a file open?
		{
			return;
		}
		String tn = Thread.currentThread().getName();
		log.info(tn + " closing file " + fileName);
		try
		{
			_myBread.close();
		}
		catch ( IOException e )
		{
			log.error( "closeFile() error: " + e.toString(), e);//$NON-NLS-1$
		}
	}

	private synchronized void openFile(boolean multipleUse) 
	throws InvalidVariableException{
		try
		{
			if ( fileName == null ) 
			{
				Set<String> fileNames = readers.keySet();
				if (!fileNames.isEmpty())
				{
					String latestFileName = (String) readers.keySet().toArray()[ readers.size() ]; // Grab the filename of the latest file opened.
					_myBread = readers.get( latestFileName ); // If there is no fileName included, use the latest reader
				}
				else
				{
					log.error( "No file name has been provided and no files have been opened." );
					throw new IOException("No file name provided and no files open");
				}
			}
			else
			{
				if ( readers.containsKey( fileName ) ) // If the file has already been opened
				{
					_myBread = readers.get( fileName ); // set the reader to be the reader of the 
					log.warn("Found in context");
				}
				else
				{
					_myBread = new LineNumberReader( new FileReader( fileName ));
				}
			}		
		}
		catch ( IOException e )
		{
			firstTime = false;
			log.error( "openFile() error: " + e.toString() );
			throw new InvalidVariableException();
		}

	}
	
	public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
	throws InvalidVariableException{
		
		String myValue = "" ;
		try{
		
			if ( firstTime )
			{
				openFile( multipleUse );
				firstTime = false;
			}
			
			if ( _myBread != null )
			{	
				openFile( multipleUse );
								
				while (_myBread.getLineNumber() < requestNumber - 1)
					_myBread.readLine();
				myValue = _myBread.readLine();
				context.putObject("REQUEST_NUM", requestNumber + 1);
			}
			return myValue;
		}
		catch ( IOException e )
		{
			log.warn("The line at " + requestNumber + " could not be read");
		}
		
		return myValue;
	}
	
	public synchronized void setParameters( Collection<CompoundVariable> parameters) throws InvalidVariableException
	{
		log.debug("setParemter - Collection.size " + parameters.size());
		checkParameterCount(parameters, 0, 2);

		for(int i = 0; i < parameters.size(); i++ )
		{
			String param =((CompoundVariable) parameters.toArray()[i]).execute();
			values[i] = param;

		}
		
		if(parameters.size() == 0)
		{
			if(context.getObject("FILE_NAME") == null )
			{
				throw new InvalidVariableException();
			}
			else
			{
				fileName = (String) context.getObject("FILE_NAME");
			}
		}
		else if(parameters.size() == 1){
			fileName =  (String) values[0];
		}
		else
		{
			fileName = values[0].toString();
			multipleUse = values[1].toString().equals("true") ? true : false;
		}
		
		if(context.getObject("FILE_NAME") == null){
			context.putObject("FILE_NAME", fileName);
		}
		
		
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
		return desc;
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
	
	public String getFileName(){
		return fileName;
	}


}