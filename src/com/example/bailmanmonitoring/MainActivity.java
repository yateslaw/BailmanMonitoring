package com.example.bailmanmonitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

import android.app.Activity;  
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;  
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;  
import android.os.Handler;  
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.Button;  
import android.widget.DatePicker;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.TextView;
  
public class MainActivity extends Activity {  
	///////////////////////////////////////////////////////////////////
	//Save Data
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	///////////////////////////////////////////////////////////////////
	
	TextView BuletoothstateShow;
	TextView datastate;
	TextView Longitude_Show;
	TextView Latitude_Show;

	int BlueFlag=0,flag=0,data_flag=0;
	
	
    private Button reconnection;  
    private Button endButton;  
    private Button lineButton; 
    private Button Identification;
    private TextView show;
    private TextView ShowContent;
//    private EditText editText;
    private Button Down;
    Button Bluetooth;
    private String mDeviceAddress,admin,password;
    
    
    private Button SaveIP;
    private Button UserName;
    EditText InputIP;
    EditText InputUserName;
    Button StopBT;
    Button Map;
    private static final int REQUEST_ENABLE_BT = 1;
    
    
    String AchartData=null;
    String[]Data;
    String[]TestData={"a","b","c"};
    String UploadData=null;
    int i=0;
    int wifistatei=0;
    
    static String IP=null;
    static String UserNameText=null;

    static String UserData=null;
    
    ////////////////////////////////////////
    //About GPSLocation
    static String Longitude="null";
    static String Latitude="null";
    String Get_Bluetooth_Data=null;
    static String GPSLocation=null;
    ////////////////////////////////////////
    
    
    /////////////////////////////////////////
    //About Bluetooth
    int Bluetooth_flag=0;
    static String BluetoothID=null;
	String AllBluetoothData=null;
    /////////////////////////////////////////
	private BluetoothAdapter mBluetoothAdapter;
    private int year;
    private int month;
    private int day;
    private BluetoothGattService myBluetoothGattService =null;
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic=null;
	String y;
	String m;
	String d;
	String s;
    
    Handler handler; 
    ClientThread clientThread;//about wifi thread
    Handler handler_bluetooth;//about bluetooth conversation thread
    private BluetoothLeService mBluetoothLeService;
   static  int BluetoothState_flag=0;
   AudioManager aManager;
   Vibrator vibrator;
   MediaPlayer mPlayer;
    LocationManager locManager;//About gps 
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            	 Log.i("blue", "Disconnected from GATT server.");
            	 BuletoothstateShow.setText("Bluetooth:Disconnection");
 				mPlayer = MediaPlayer.create(
 					MainActivity.this,R.raw.music);
 				// 设置循环播放
 				mPlayer.setLooping(false);
 				// 开始播放
 				mPlayer.start();
 				long[] pattern = {1000, 1000, 1000, 1000, 1000, 1000};
 				vibrator.vibrate(pattern,1);
 				AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this)
 				.setMessage("蓝牙断开了!!")
                .setTitle("温馨提示");
                setPositiveButton(builder)
    			.create()
    			.show();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
           	     Log.e("blue", intent.getStringExtra(BluetoothLeService.EXTRA_DATA));  
            	 if(intent.getStringExtra(BluetoothLeService.EXTRA_DATA).equals("SZUBEES#"))
                 {
            		 BluetoothState_flag++;
             		String UpData=null;
             		String UploadData_GPSLocation=null;
             		BluetoothID="SZUBEES#";
             		UploadData_GPSLocation=UserNameText+"@"+BluetoothID+"@"+"UPLOAD"+"@"+Longitude+"@"+Latitude+"@"+"null";
             		try{
             			Message msgUp =new Message();
             			msgUp.what=0x345;
             			msgUp.obj=UploadData_GPSLocation;			
             			clientThread.revHandler.sendMessage(msgUp);	
             		}catch(Exception e){
             			e.printStackTrace();
             		}
             		if(BluetoothState_flag==0){BuletoothstateShow.setText("Bluetooth:");}
        			if(BluetoothState_flag==1){BuletoothstateShow.setText("Bluetooth:I");}
        			if(BluetoothState_flag==2){BuletoothstateShow.setText("Bluetooth:I I");}
        			if(BluetoothState_flag==3){BuletoothstateShow.setText("Bluetooth:I I I");}
        			if(BluetoothState_flag==4){BuletoothstateShow.setText("Bluetooth:I I I I");BluetoothState_flag=0;}
                 }
            }
        }
		private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder) {
			return builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					mPlayer.stop();
					vibrator.cancel();
					Intent i = getBaseContext().getPackageManager() 
							.getLaunchIntentForPackage(getBaseContext().getPackageName()); 
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
							startActivity(i);
				}
				
			});
			
		}
    };
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
 
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_main); 

//        show=(TextView)findViewById(R.id.show);
        final TextView wifistate=(TextView)findViewById(R.id.wifistate);
       
        ShowContent=(TextView)findViewById(R.id.Buletoothstate);
        
        
     //  reconnection=(Button)findViewById(R.id.reconnection);
  //      reconnection.setOnClickListener(new ReconnectionClickLintener());
        endButton = (Button) findViewById(R.id.end); 
        endButton.setOnClickListener(new EndClickLintener());  
        SaveIP=(Button)findViewById(R.id.button7);
        SaveIP.setOnClickListener(new SaveIPClickListener());
        InputIP=(EditText)findViewById(R.id.editText1);
        
        UserName=(Button)findViewById(R.id.button8);
        UserName.setOnClickListener(new UserNameClickListener());
        
        InputUserName=(EditText)findViewById(R.id.editText2);     
        BuletoothstateShow=(TextView)findViewById(R.id.Buletoothstate);
        datastate=(TextView)findViewById(R.id.datastate);
        
        final Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        
        ////////////////////////////////////////////////////////////////////////////////////////
        //Save Data
		preferences=getSharedPreferences("com.example.androidacharengine",Context.MODE_PRIVATE);
		editor=preferences.edit();
		vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        //////////////////////////////////////////////////////////////////////////////////////// 
		
		
		////////////////////////////////////////////////////////////////////////////////////////

		IP=preferences.getString("IP",null);
		InputIP.setText(IP);		
		UserNameText=preferences.getString("UserNameText",null);
		InputUserName.setText(UserNameText);
		final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
		 Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
	     bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	    final Object[] Data=new String[1024];
         handler = new Handler(){//This handler is used to connected with computer through wifi
        	@Override
        	public void handleMessage(Message msg){
        		if(msg.what==0x1234){//Send xintiaobao
        		        			Message msgx =new Message();
        		        			msgx.what=0x345;
        		        	//		msgx.obj=editText.getText().toString();
        		        			msgx.obj="xintiaobao";
        		        	//		show.append(msgx.obj.toString());
        		        			clientThread.revHandler.sendMessage(msgx);
        		}
        		if(msg.what==0x0001)
        		{
        			String state=msg.obj.toString();
        			wifistate.setText(" WifiState:"+state);
        			finish();
        		}
        		if(msg.what==0x123){
        			String getMessage=null;
        			getMessage=msg.obj.toString();
        			
        			if(getMessage.equals("xintiaobao")){//if get message of"xintiaobao"
        				wifistatei=wifistatei+1;
        				if(wifistatei==6){wifistatei=1;}
        				if(wifistatei==1){wifistate.setText(" WifiState:");}
        				if(wifistatei==2){wifistate.setText(" WifiState:I");}
        				if(wifistatei==3){wifistate.setText(" WifiState:I I");}
        				if(wifistatei==4){wifistate.setText(" WifiState:I I I");}
        				if(wifistatei==5){wifistate.setText(" WifiState:I I I I");}
        			}
 
        			
        			if(!(getMessage.equals("xintiaobao"))){
        				
        				if(getMessage.equals("NotfoudFile")){show.setText("\r\n"+"File Operation Error");}
        				
        				if(getMessage.equals("UPLOAD")){
        					     Log.w("state","数据上传成功");
        	                     data_flag=data_flag+1;
        	                     if(data_flag==6){data_flag=1;}
    	        				 if(data_flag==1){datastate.setText(" DateState:");}
    	        				if(data_flag==2){datastate.setText(" DateState:I");}
    	        				if(data_flag==3){datastate.setText(" DateState:I I");}
    	        				if(data_flag==4){datastate.setText(" DateState:I I I");}
    	        				if(data_flag==5){datastate.setText(" DateState:I I I I");}
        				}
        	
        				if(getMessage.equals("ERROR")){
        				      
        			}		
        		}
          }
       }
        	/*private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder) {
    			return builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener()
    			{
    				@Override
    				public void onClick(DialogInterface arg0, int arg1) {
    					// TODO Auto-generated method stub
    					mPlayer.stop();
    					vibrator.cancel();
    					Intent i = getBaseContext().getPackageManager() 
    							.getLaunchIntentForPackage(getBaseContext().getPackageName()); 
    							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
    							startActivity(i);
    				}
    				
    			});*/
    			
    		//}
        };
        clientThread=new ClientThread(handler);
        new Thread(clientThread).start();
		////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////
        //GPSLocation
		locManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		Location location=locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		updataView(location);
		
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,8,new LocationListener(){
			@Override
			public void onLocationChanged(Location location){
				updataView(location);
			}
			
			@Override
			public void onProviderDisabled(String provider){
				updataView(null);
			}
			
			@Override
			public void onProviderEnabled(String provider){
				updataView(locManager.getLastKnownLocation(provider));
			}
			
			@Override
			public void onStatusChanged(String provider,int status,Bundle extras){
				
			}
		
		});
}  
    protected Builder setPositiveButton(Builder builder) {
		// TODO Auto-generated method stub
		return null;
	}
	public static boolean checkNetworkConnection(Context context)   
    {   
        final ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);   
   
        final android.net.NetworkInfo wifi =connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);   
        final android.net.NetworkInfo mobile =connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);   
   
        if(wifi.isAvailable())   
            return true;   
        else  
            return false;   
    }  
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d("blue", "Connect request result=" + result);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
 //       unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }
    //EndClickLintener
    class EndClickLintener implements OnClickListener{
    	@Override
    	public void onClick(View v){
    		 myBluetoothGattService=mBluetoothLeService.mBluetoothGatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
             mBluetoothGattCharacteristic=myBluetoothGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
             mBluetoothLeService.sendMessage("1");
             Log.e("step1","向单片机发送了1");
             mBluetoothLeService.readCharacteristic(mBluetoothGattCharacteristic);
             mBluetoothLeService.setCharacteristicNotification(mBluetoothGattCharacteristic, true); 
    	}
    }
/*    class  ReconnectionClickLintener implements OnClickListener{
    	@Override
    	public void onClick(View v){
                 mBluetoothLeService.connect("00:15:83:00:02:C5");
            }
    }
    */
    //SaveIPClickListener
    class SaveIPClickListener implements OnClickListener{
    	public void onClick(View v){
    		IP=InputIP.getText().toString();
    		editor.putString("IP",IP);
    		editor.commit();
    	}
    }
    
    //UserNameClickListener
    class UserNameClickListener implements OnClickListener{
    	public void onClick(View v){
    		UserNameText=InputUserName.getText().toString();
    		editor.putString("UserNameText",UserNameText);
    		editor.commit();
    	}
    }
///////////////////////////////////////////////////////////////////////////////////////////////////   
 //Method
    //getIP()
    public static String getIP(){
    	String getip=null;
    	getip=IP;
    	return getip;
    }
    
    

   //UserDatatoAchat()
    public static String UserDatatoAchar(){
    	String UDA=null;
    	UDA=UserData;
    	return UDA;
    }

    
 
    //getMyName
    public static String getMyName(){
    	String gmn=null;
    	String[]ga=UserNameText.split("&");
    	gmn=ga[0];
    	return gmn;
    }
    
    public static String getYourName(){
    	String gyn=null;
    	String[]gb=UserNameText.split("&");
    	gyn=gb[1];
    	return gyn;
    }
    
    public static String getGPSLocation(){
    	String gpslocation_1=null;
    	gpslocation_1=GPSLocation;
    	return gpslocation_1;
    }
    
    
    
    
 ///////////////////////////////////////////////////////////////////////////////////////////////////   
  
    
    ///////////////////////////////////
    //This method is about getting gps location
	public void updataView(Location newlocation){
		if(newlocation!=null){

			
			Longitude=newlocation.getLongitude()+"";
			Latitude=newlocation.getLatitude()+"";
		//	Speed=newlocation.getSpeed()+"";
		//	show.setText("\r\n"+Longitude+"  "+Latitude);
			
		}
		else{
			Longitude="113.93719409";
			Latitude="22.533330803";
		//	Speed="0";
		}
		
//		Longitude_Show.setText("Lon:"+Longitude);
//		Latitude_Show.setText("Lat:"+Latitude);
//		Speed_Show.setText("Speed:"+Speed);
		
	}
    
    
	
	//////////////////////////////
	//method for Separating data 
	
	public String Data_Separated_intoAandB(String sepdata)
	{
		if(sepdata.contains("@")){
			String[]sepdataArray=sepdata.split("@");
			return sepdataArray[1];
		}
		else{
			return "Data Error";
		}
	}
	
	
	
	///////////////////////////////////////////////
	//The method of getting MACAddress
	public String getLocalMacAddress(){
		WifiManager wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
		WifiInfo info=wifi.getConnectionInfo();
		return info.getMacAddress();
	}
	
	
	
	/////////////////////////////////////////////
	//The method of getting the phone's time and date
	public String getTime(){
		Time  time =new Time("GMT+8");
		time.setToNow();
		int hour=time.hour;
		int minute=time.minute;
		int sec=time.second;
		String h=hour+"";
		String m=minute+"";
		String s=sec+"";
		String gettiem=h+":"+m+":"+s;
		return gettiem;
	}
	
	public String getDate(){
		Time time=new Time("GMT+8");
		time.setToNow();
		int year=time.year;
		int month=time.month;
		int day=time.monthDay;
		String y=year+"";
		String m=month+"";
		String d=day+"";
		String getdate=y+m+d;
		return getdate;
	}
	public  static void setID(String ID)
	{
		BluetoothID=ID;
	}
	 private static IntentFilter makeGattUpdateIntentFilter() {
	        final IntentFilter intentFilter = new IntentFilter();
	        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
	        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
	        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
	        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
	        return intentFilter;
	    }
  
}  
