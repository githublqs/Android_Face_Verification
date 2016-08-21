package com.tec.jca.faceverification.domain;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.ivsign.android.IDCReader.IDCReaderSDK;
import com.tec.jca.faceverification.GloabalVar;
import com.tec.jca.faceverification.R;
import com.tec.jca.faceverification.entity.CardInfo;
import com.tec.jca.faceverification.ui.activity.MainActivity;

import FaceRec.SesFaceRec;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
public class CardService extends Service{
	
	private boolean bExit;
	String[] decodeInfo = new String[10];
	String DEVICE_NAME1 = "CVR-100B";
    String DEVICE_NAME2 = "IDCReader";
    String DEVICE_NAME3 = "COM2";
    String DEVICE_NAME4 = "BOLUTEK";
    private CardThread cardThread;
    private CardInfo cardInfo=new CardInfo();
    int Readflage = -99;
	byte[] recData = new byte[1500];
    byte[] cmd_SAM = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x12, (byte) 0xFF, (byte) 0xEE  };
    byte[] cmd_find  = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x01, 0x22  };
	byte[] cmd_selt  = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x02, 0x21  };
	byte[] cmd_read  = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x30, 0x01, 0x32 };
	byte[] cmd_sleep  = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x02, 0x00, 0x02};
	byte[] cmd_weak  = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x02, 0x01, 0x03 };
    static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static final  String BROADCAST = "com.tec.jca.faceverification.domain.CardService";
	public static final int CMD_PAUSE_READ = 1;
	public static final int CMD_RESUME_READ = 2;
	//private boolean readFlag;//CMD_PAUSE_READ��ʱ��readFlag=false��CMD_RESUME_READ readFlag=true
	private boolean preCardConnected;//��һ�ο��Ƿ�������
	private void connectCard(){
		
		myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); 
		if(myBluetoothAdapter!=null){
			/*//����isEnabled()�����жϵ�ǰ�����豸�Ƿ����
			if(!myBluetoothAdapter.isEnabled())
			{     
				//��������豸�����õĻ�,����һ��intent����,�ö�����������һ��Activity,��ʾ�û���������������
				Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivity(intent);
			}*/
			Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
			if (pairedDevices.size() > 0)
			{
				for (Iterator<BluetoothDevice> iterator = pairedDevices.iterator(); iterator.hasNext();)
				{
					BluetoothDevice device = (BluetoothDevice)iterator.next();
					if (DEVICE_NAME1.equals(device.getName())||DEVICE_NAME2.equals(device.getName())||DEVICE_NAME3.equals(device.getName())||DEVICE_NAME4.equals(device.getName()))
					{        					
				        try 
				        {
				            myBluetoothAdapter.enable();
				           /* Intent discoverableIntent = new Intent( 
				                        BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);//ʹ���������ڿɷ���ģʽ������ʱ��150s 
				            discoverableIntent.putExtra( 
				                        BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 150); */
				            //mBTHSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
				            
				            int sdk = Integer.parseInt(Build.VERSION.SDK);
				            if (sdk >= 10) {
				            	mBTHSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
				            } else {
				            	mBTHSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
				            }
				            if(mBThServer==null){
				            	mBThServer = myBluetoothAdapter.listenUsingRfcommWithServiceRecord("myServerSocket",MY_UUID);
				            }
				            mBTHSocket.connect();
				            
				        	mmInStream = mBTHSocket.getInputStream();
				            mmOutStream = mBTHSocket.getOutputStream();
				            
			            	
			        	
				        } catch (IOException e)
				        {
				        	e.printStackTrace();
				        	
				        }
						break;
					}
				}
			}
			
		}
		if(!preCardConnected){
    		if(mmInStream!=null&&mmOutStream!=null){
    			preCardConnected=true;
    			//���Ϳ��Ѿ�����
        		sendCardConnectNotification(true);
    		}
    		
    	}else{
    		if(mmInStream==null||mmOutStream==null){
    			preCardConnected=false;
    			//���Ϳ��Ѿ�����
        		sendCardConnectNotification(false);
    		}
    	}

	}

	private void sendCardConnectNotification(boolean connected) {
		/*String content=connected?"���������֤�Ķ���":"δ�������֤�Ķ���";
		 Uri soundpath = Uri.parse("android.resource://com.tec.jca.faceverification/" + R.raw.qzb);
			//or Uri path = Uri.parse("android.resource://com.androidbook.samplevideo/raw/myvideo");
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setContentTitle("���֤�Ķ���")//����֪ͨ������
        .setContentText(content) //����֪ͨ����ʾ����
        .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //����֪ͨ�������ͼ
        //  .setNumber(number) //����֪ͨ���ϵ�����
        .setTicker(content) //֪ͨ�״γ�����֪ͨ��������������Ч����
        .setWhen(System.currentTimeMillis())//֪ͨ������ʱ�䣬����֪ͨ��Ϣ����ʾ��һ����ϵͳ��ȡ����ʱ��
        .setPriority(Notification.PRIORITY_DEFAULT) //���ø�֪ͨ���ȼ�
        //  .setAutoCancel(true)//���������־���û��������Ϳ�����֪ͨ���Զ�ȡ��
        .setOngoing(false)//ture��������Ϊһ�����ڽ��е�֪ͨ������ͨ����������ʾһ����̨����,�û���������(�粥������)����ĳ�ַ�ʽ���ڵȴ�,���ռ���豸(��һ���ļ�����,ͬ������,������������)
        //.setDefaults(Notification.DEFAULT_SOUND)//��֪ͨ������������ƺ���Ч������򵥡���һ�µķ�ʽ��ʹ�õ�ǰ���û�Ĭ�����ã�ʹ��defaults���ԣ��������
       .setSound(soundpath)
        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND ������� // requires VIBRATE permission
        .setSmallIcon(R.drawable.ic_launcher);//����֪ͨСICON
		mNotificationManager.notify(123, mBuilder.build());  
		mNotificationManager.cancel(123); */ 
		 sendBroadcast(new Intent().setAction(MainActivity.BROADCAST)
					.putExtra("command", 2)
					.putExtra("connected", connected));
	}
	public PendingIntent getDefalutIntent(int flags){
	      PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
	      return pendingIntent;
	}
	final class CardThread extends Thread{
		

		@Override
		public void run() {
			super.run();
			while (!bExit) {
				if(SesFaceRec.loadSuccess&&MainActivity.handleBroadCastFlag/*&&readFlag*/){
					Log.i("CardThread", "readCard...........");
					//if(sleepCmdSendFlag){
					sendWeakCmd();
					//}
					readCard();
				}/*else if(!readFlag&&!sleepCmdSendFlag){
					sendSleepCmd();
				}*/
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(bExit){
				closeConnectCard();
			}
		}
	}
	//private boolean sleepCmdSendFlag;
	//������������
	private void sendSleepCmd(){
		//sleepCmdSendFlag=true;
		if((mmInStream == null)||(mmOutStream == null))
        {
        	return;
        }
		try {
			mmOutStream.write(cmd_sleep);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//���ͻ�������
	private void sendWeakCmd(){
		//sleepCmdSendFlag=false;
		try {
			if((mmInStream == null)||(mmOutStream == null))
	        {
	        	return;
	        }
			mmOutStream.write(cmd_weak);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private BluetoothAdapter myBluetoothAdapter;
	private BluetoothSocket  mBTHSocket;
	private BluetoothServerSocket mBThServer;
	private OutputStream mmOutStream;
	private InputStream mmInStream;
	//private CardServiceReceiver mCardServiceReceiver;
	private Timer mConnectCardTimer;
	private TimerTask mConnectCardTimerTask;
	@Override
	public void onCreate() {
		super.onCreate();
		//mCardServiceReceiver = new CardServiceReceiver();
		IntentFilter filter = new IntentFilter();// ����IntentFilter����
		// ע��һ���㲥�����ڽ���Activity���͹������������Service����Ϊ���磺�������ݣ�ֹͣ�����
		filter.addAction("com.tec.jca.faceverification.domain.CardService");
		// ע��Broadcast Receiver
		//registerReceiver(mCardServiceReceiver, filter);
		
		
		 mConnectCardTimer = new Timer();
	     mConnectCardTimerTask = new TimerTask() {
	
				@Override
				public void run() {
					
					if(mmInStream==null||mmOutStream==null){
						closeConnectCard();
						connectCard();
					}
				}
	     };
	     if(mConnectCardTimerTask!=null&&mConnectCardTimer!=null){
	    	 mConnectCardTimer.schedule(mConnectCardTimerTask, 1000, 5000);
	     }
		cardThread=new CardThread();
		cardThread.start();

	
	

	}
	@Override
	public void onDestroy() {
		bExit=true;
		//unregisterReceiver(mCardServiceReceiver);
		try {
			if(mConnectCardTimer!=null){
				mConnectCardTimer.cancel();
			}
			if(mConnectCardTimerTask!=null){
				mConnectCardTimerTask.cancel();	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
		
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void readCard() {
		 
		int readcount = 15;
		try 
		{ 
			while(readcount > 1)
			{
				ReadCard();
				readcount = readcount - 1;
				
				if(Readflage > 0)
				{
					readcount = 0;
					
					notifyReadCard(Readflage);     					
				}     				
				Thread.sleep(100);	
			}
			notifyReadCard(Readflage);
			
			
		} catch (InterruptedException e) {
			
			notifyReadCard(-100);
		} 
		
    
		
	}
	private void ReadCard()
	    {    	
			try 
			{        			
				if((mmInStream == null)||(mmOutStream == null))
		        {	        	
					Readflage = -2;//�����쳣
					
					return;
		        }			
				mmOutStream.write(cmd_find);
				Thread.sleep(200);					
				int datalen = mmInStream.read(recData);
				if(recData[9] == -97)
				{						
					mmOutStream.write(cmd_selt);
					Thread.sleep(200);					
					datalen = mmInStream.read(recData);
					if(recData[9] == -112)
					{
						mmOutStream.write(cmd_read);
						Thread.sleep(1000);	
						byte[] tempData = new byte[1500];
						if(mmInStream.available()>0)
						{
							datalen = mmInStream.read(tempData);
						}
						else
						{
							Thread.sleep(500);	
							if(mmInStream.available()>0)
							{
								datalen = mmInStream.read(tempData);
							}
						}
						int flag = 0;
						if(datalen <1294)
						{
							for(int i = 0;i<datalen ;i++,flag++)
							{									
								recData[flag] = tempData[i];
							}								
							Thread.sleep(1000);
							if(mmInStream.available()>0)
							{
								datalen = mmInStream.read(tempData);
							}
							else
							{
								Thread.sleep(500);
								if(mmInStream.available()>0)
								{
									datalen = mmInStream.read(tempData);
								}									
							}
							for(int i = 0;i<datalen ;i++,flag++)
							{									
								recData[flag] = tempData[i];
							}
							
						}
						else
						{
							for(int i = 0;i<datalen ;i++,flag++)
							{									
								recData[flag] = tempData[i];
							}	
						}
						tempData = null;
						if(flag == 1295)
						{
							if(recData[9] == -112)
							{		
								byte[] dataBuf = new byte[256];								
								for(int i = 0; i < 256; i++)
								{
									dataBuf[i] = recData[14 + i];
								}
								decodeCard(dataBuf);
							}
							else
							{								
								Readflage = -5;//����ʧ�ܣ�
							}
						}
						else
						{
							Readflage = -5;//����ʧ��
						}					
					}
					else
					{							
						Readflage = -4;//ѡ��ʧ��
					}
				}
				else
				{						
					Readflage = -3;//Ѱ��ʧ��
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Readflage = -99;//��ȡ�����쳣
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Readflage = -99;//��ȡ�����쳣
			}
	    }
	private void decodeCard(byte[] dataBuf) throws UnsupportedEncodingException{
		 String TmpStr = new String(dataBuf, "UTF-16-LE");
			TmpStr = new String(TmpStr.getBytes("UTF-8"));
			decodeInfo[0] = TmpStr.substring(0, 15);
			cardInfo.setName(decodeInfo[0]);
			decodeInfo[1] = TmpStr.substring(15, 16);
			decodeInfo[2] = TmpStr.substring(16, 18);
			
			decodeInfo[3] = TmpStr.substring(18, 26);
			cardInfo.setBirthday(decodeInfo[3]);
			
			decodeInfo[4] = TmpStr.substring(26, 61);
			cardInfo.setAddress(decodeInfo[4]);
			decodeInfo[5] = TmpStr.substring(61, 79);
			cardInfo.setCard_code(decodeInfo[5]);
			decodeInfo[6] = TmpStr.substring(79, 94);
			cardInfo.setFzjg(decodeInfo[6]);
			decodeInfo[7] = TmpStr.substring(94, 102);
			cardInfo.setYrqx_begin(decodeInfo[7]);
			decodeInfo[8] = TmpStr.substring(102, 110);
			cardInfo.setYrqx_end(decodeInfo[8]);
			decodeInfo[9] = TmpStr.substring(110, 128);
			if (decodeInfo[1].equals("1")){
				decodeInfo[1] = "��";
				cardInfo.setSex(1);
			}
			else{
				decodeInfo[1] = "Ů";
				cardInfo.setSex(0);
			}
			try
			{
				int code = Integer.parseInt(decodeInfo[2].toString());
				cardInfo.setNation(code);
				
			}
			catch (Exception e)
			{
				decodeInfo[2] = "";
			}
			
			decodePhoto();
			
											
			
	 }
	 private void decodePhoto(){
		//��Ƭ����									
			try
			{	
				int ret = IDCReaderSDK.Init(GloabalVar.wltlibDir);
				if (ret == 0)
				{	
					byte[] datawlt = new byte[1384];
					byte[] byLicData = {(byte)0x05,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x5B,(byte)0x03,(byte)0x33,(byte)0x01,(byte)0x5A,(byte)0xB3,(byte)0x1E,(byte)0x00};
					for(int i = 0; i < 1295; i++)
					{
					 	datawlt[i] = recData[i];
					}
					int t = IDCReaderSDK.unpack(datawlt,byLicData);
					if(t == 1)
					{
						cardInfo.setPicPath(GloabalVar.wltlibDir+"/zp.bmp");
						Readflage = 1;//�����ɹ�
					}
					else
					{
						Readflage = 6;//��Ƭ�����쳣
					}											
				}
				else
				{
					Readflage = 6;//��Ƭ�����쳣
				}										
			}
			catch(Exception e)
			{								
				Readflage = 6;//��Ƭ�����쳣
			}
	 }
		
	 private void notifyReadCard(final int code){
		 	if(MainActivity.handleBroadCastFlag){
		 		if(code==1){
		 			Log.i("CardThread", "notifyReadCard...........");
		 			sendBroadcast(new Intent().setAction(MainActivity.BROADCAST)
							.putExtra("command", 1)
							.putExtra("code", code)
							.putExtra("cardInfo",cardInfo));
					//sendSleepCmd();
		 		}
		 	}
	 }
	 private void closeConnectCard(){
		
 		try {
 		
 			if(mmOutStream!=null){
 				mmOutStream.close();
 				mmOutStream=null;
 			}
 			if(mmInStream!=null){
 				mmInStream.close();
 				mmInStream=null;
 			}
 			
 			
 			if(mBTHSocket!=null){
 				mBTHSocket.close();
 				mBTHSocket=null;
 			}
 			
 			if(mBThServer!=null){
				mBThServer.close();
				mBThServer=null;
 			}
 			
			} catch (IOException e) {
				e.printStackTrace();
			}
	 }
	/* private class CardServiceReceiver extends BroadcastReceiver {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(BROADCAST)) {
					Bundle bundle = intent.getExtras();
					int cmd = bundle.getInt("command");// ��ȡExtra��Ϣ
					switch (cmd) {
					case CMD_PAUSE_READ:
						readFlag=false;
						try {
							if(mConnectCardTimer!=null){
								mConnectCardTimer.cancel();
								mConnectCardTimer=null;
							}
							
							
						} catch (Exception e) {
						}
						try {
							if(mConnectCardTimerTask!=null){
								mConnectCardTimerTask.cancel();
								mConnectCardTimerTask=null;
							}
						} catch (Exception e) {
							//e.printStackTrace();
						}
						cardTimerScheduled=false;
						break;
					case CMD_RESUME_READ:
						readFlag=true;
						if(mConnectCardTimer!=null&&mConnectCardTimerTask!=null){
							if(!cardTimerScheduled){
								mConnectCardTimer.schedule(mConnectCardTimerTask, 1000, 5000);
								cardTimerScheduled=true;
							}
						}
						break;
					}
				}
			}
	 }*/
		
}
