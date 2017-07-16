package com.example.bailmanmonitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ClientThread implements Runnable{
	private Socket s;
	public Handler handler;
	public Handler revHandler;
	BufferedReader br=null;
	OutputStream os=null;
	public ClientThread(Handler handler){
		this.handler=handler;
	}
	public void run(){
		try{
			String IP=null;
			
			IP=MainActivity.getIP();
	//		s=new Socket("172.31.197.1",30000);
			s=new Socket(IP,30000);
			br=new BufferedReader(new InputStreamReader(s.getInputStream()));
			os=s.getOutputStream();

			
			new Thread(){
				@Override
				public void run(){
					String content=null;
					
					
					
					///////////////////////////////////////
					////��������߳�//////////////////////////
					new Timer().schedule(new TimerTask()
					{
					@Override
					public void run(){
						handler.sendEmptyMessage(0x1234);
						}
					},0,1200);
					////////////////////////////////////////
					////////////////////////////////////////
					
					
					
					
					try{
						while(true){
							
							Message msg=new Message();
							content=br.readLine();
							
							if(content!=null){
								msg.what=0x123;
								msg.obj=content;
								handler.sendMessage(msg);
							}
						}
					}
					catch(IOException e){
						e.printStackTrace();
					}
				}
				
			}.start();
			Looper.prepare();
			revHandler=new Handler(){
				@Override
				public void handleMessage(Message msg){
					if(msg.what==0x345){
						try{
							Log.e("data",msg.obj.toString());
							os.write((msg.obj.toString()+"\r\n").getBytes("utf-8"));
						}catch(Exception e){
							e.printStackTrace();
							Message msgs=new Message();
							msgs.what=0x001;
							msgs.obj="Disconnection";
							handler.sendMessage(msgs);
						}
					}
				}
			};
			Looper.loop();
		}catch(SocketTimeoutException el){
			System.out.println("timeout");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}

