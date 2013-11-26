package application.backend;

import java.net.*;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity 
{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{	
		Socket s = null;
		ServerSocket serverSocket = null;
		
		
		
    	try {
    		serverSocket = new ServerSocket(1234);
			s = serverSocket.accept();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button buttonSend = (Button) findViewById(R.id.buttonSend);
		
	    buttonSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TextView tV = (TextView) findViewById(R.id.textViewWithScroll);
				EditText eT = (EditText) findViewById(R.id.editTextChat);
				CharSequence text = tV.getText() + "\n"+"Pat:"+ eT.getText();
				eT.setText("");
				tV.setText(text);
			}
		}); 
	    
	    Button buttonExit = (Button) findViewById(R.id.buttonExit);
		
	    buttonExit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 finish();
		         System.exit(0);
			}
		}); 
	}
	
}