package application.backend;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
//import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity 
{


	private TextView tV;
	private Thread client;
	private Socket clientSocket;
	private PrintWriter printWriter;
	private String messageToSend = "";
	private static InputStreamReader inputStreamReader;
	private static BufferedReader bufferedReader;
	private static String message;
	private static final int SERVERPORT = 50008;
	private static final String SERVER_IP = "162.209.100.18";


	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{	

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button buttonSend = (Button) findViewById(R.id.buttonSend);
		Button buttonExit = (Button) findViewById(R.id.buttonExit);
		buttonSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				MainActivity.this.onSendClick(view);
			}
		});
		buttonExit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				System.exit(0);
			}
		}); 

		new Thread(new ClientThread()).start();


	}

	public void onSendClick(View view) {

		try {

			EditText eT = (EditText) findViewById(R.id.editTextChat);
			TextView tV = (TextView) findViewById(R.id.textViewWithScroll);
			messageToSend = eT.getText().toString();
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())),true);
			out.println(messageToSend);
			eT.setText("");

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	class ClientThread implements Runnable {
		@Override
		public void run() {
			try {
				InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
				clientSocket = new Socket(serverAddr, SERVERPORT);

			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}


}