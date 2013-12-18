package application.backend;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
//import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.*;

public class MainActivity extends Activity 
{
	private Socket clientSocket;
	private String messageToSend = "";
	private static  int SERVERPORT = 50009;
	private static final String SERVER_IP = "162.209.100.18";
	private Handler messageHandler = new Handler();
	private Handler videoHandler = new Handler();
	private String userName = "Anonymous";




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
		this.portEntryDialog();
		this.userNameEntryDialog();
		this.receiveMsg();
		new Thread(new SendThread()).start();
		this.playVideo();


	}

	private void scrollToBottom()
	{
		final ScrollView mScrollView = (ScrollView) findViewById(R.id.ScrollView);
		mScrollView.post(new Runnable()
		{
			TextView mTextStatus = (TextView) findViewById(R.id.textViewWithScroll);

			public void run()
			{ 
				mScrollView.smoothScrollTo(0, mTextStatus.getBottom());
			} 
		});
	}

	public void playVideo()
	{
		try 
		{
			final VideoView videoView =(VideoView)findViewById(R.id.videoView);
			Uri video = Uri.parse("rtsp://r7---sn-p5qlsu7l.c.youtube.com/CiILENy73wIaGQn_ndoLkXR2JxMYDSANFEgGUgZ2aWRlb3MM/0/0/0/video.3gp");
			videoView.setVideoURI(video);
			videoView.start();
		}
		catch(Exception e)
		{
			System.out.println("Video Play Error :"+e.getMessage());
		}
	}
	public void onSendClick(View view) {

		try {

			EditText eT = (EditText) findViewById(R.id.editTextChat);
			messageToSend =eT.getText().toString();
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())),true);
			JSONObject outJson = new JSONObject();
			if(this.userName.equals("admin") || this.userName.equals("Admin"))
			{
				if(messageToSend.length() >= 4 && messageToSend.substring(0, 4).equals( "rtsp"))
				{
					JSONObject adminRequest = new JSONObject();
					adminRequest.put("video", this.messageToSend);
					outJson.put("Admin", adminRequest);
				}
				else
				{
					JSONObject message = new JSONObject();
					message.put("user", this.userName ); 
					message.put("text", this.messageToSend);
					outJson.put("Message", message);
				}


			}
			else
			{
				JSONObject message = new JSONObject();
				message.put("user", this.userName ); 
				message.put("text", this.messageToSend);
				outJson.put("Message", message);
			}
			out.println(outJson);
			out.flush();
			eT.setText("");

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	class SendThread implements Runnable {

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

	public void receiveMsg()
	{
		ReceiveThread receiveThread = new ReceiveThread();
		Thread receive = new Thread(receiveThread);
		receive.start();
	}

	class ReceiveThread implements Runnable 
	{
		private boolean running = true;
		private Socket socket = null ;
		private BufferedReader in = null;

		@Override
		public void run() 
		{
			try {
				InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
				socket = new Socket(serverAddr, SERVERPORT);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}

			while(running)
			{
				JSONObject inJson = null;
				try {

					inJson = new JSONObject(in.readLine());

				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(inJson != null)
				{
					if(inJson.has("Message"))
					{
						displayMsg(inJson);
					}
					if(inJson.has("Admin"))
					{
						ChangeVideo(inJson);
					}
				}
			}
		}
	}

	public void userNameEntryDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Choose user name.");
		final EditText input = new EditText(this);
		input.setText("Anonymous");
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (input.getText().toString()!=""||input.getText().toString()!="admin"||input.getText().toString()!="Admin")
				{
					userName = input.getText().toString();
				}
			}
		});
		builder.show();
	}

	public void portEntryDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Entrez le port");
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (input.getText().toString()!=""||input.getText().toString()!="admin"||input.getText().toString()!="Admin")
				{
					SERVERPORT = Integer.parseInt(input.getText().toString());
				}
			}
		});
		builder.show();
	}

	public void displayMsg(JSONObject inJson)
	{ 
		final JSONObject jsonObj = inJson;

		this.messageHandler.post(new Runnable() {
			@Override
			public void run() 
			{
				String user = null;
				String text = null;
				try {
					JSONObject message = jsonObj.getJSONObject("Message");
					user = message.getString("user");
					text = message.getString("text");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				TextView tV = (TextView) findViewById(R.id.textViewWithScroll);
				tV.setText(tV.getText()+ "\n"+ user + ": " + text);
				scrollToBottom();
			}
		});

	}

	public void ChangeVideo(JSONObject inJson)
	{ 
		final JSONObject jsonObj = inJson;

		this.videoHandler.post(new Runnable() {
			@Override
			public void run() 
			{
				String videoUri = null;
				try {
					JSONObject changeVideo = jsonObj.getJSONObject("Admin");
					videoUri = changeVideo.getString("video");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				final VideoView videoView = (VideoView)findViewById(R.id.videoView);
				Uri video = Uri.parse(videoUri);
				videoView.setVideoURI(video);
				videoView.start();

			}
		});

	}


}