package com.example.psanchez.mainproject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import PRTAndroidSDK.PRTAndroidPrint;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PRTSDKApp extends Activity {
private  Bitmap bmp;
	private Spinner spnPrinterList=null;
	private Button btnBT=null;
	private Button btnDisplayRemainingPower=null;
	private Button btnWiFi=null;
	private Button btnUSB=null;
	private EditText edtIP = null;
	private Button btnPrint=null;
	private TextView txtTips=null;
	private TextView txtRemainingPower=null;
	private EditText edtPrintText = null;
	private Spinner spinnerLanguage     = null;



	
	private String ConnectType="";
	private Context thisCon=null;
	private ArrayAdapter arrPrinterList; 
	private static PRTAndroidPrint PRT=null;
	private BluetoothAdapter mBluetoothAdapter;
	private String strBTAddress="";

	private UsbManager mUsbManager=null;	
	private UsbDevice device=null;
	private static final String ACTION_USB_PERMISSION = "com.example.psanchez.mainproject.PRTSDKApp";
	private PendingIntent mPermissionIntent=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        Log.d("ONCREATE", "ONCREATE");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prtsdkapp);
		
		spnPrinterList = (Spinner) findViewById(R.id.spn_printer_list);		
		btnBT = (Button) findViewById(R.id.btnBT);
		btnDisplayRemainingPower = (Button) findViewById(R.id.btnDisplayRemainingPower);
		txtRemainingPower = (TextView) findViewById(R.id.txtRemainingPower);
		btnWiFi = (Button) findViewById(R.id.btnWiFi);
		btnUSB = (Button) findViewById(R.id.btnUSB);		
		edtIP=(EditText)findViewById(R.id.txtIPAddress);
		edtIP.setText("192.168.0.33");
		btnPrint = (Button) findViewById(R.id.btnPrint);
		txtTips = (TextView) findViewById(R.id.txtTips);
		edtPrintText =(EditText)findViewById(R.id.edtPrintText);
		
		ArrayAdapter<String> languageItem=getLangguageInfo();
		spinnerLanguage=(Spinner) findViewById(R.id.spn_language);
		languageItem.setDropDownViewResource(android.R.layout.simple_spinner_item);
		spinnerLanguage.setAdapter(languageItem);

		//Enable Bluetooth
		EnableBluetooth();	
		
		thisCon=this.getApplicationContext();
		
		//Add Printer List		
		InitCombox();
				
		mPermissionIntent = PendingIntent.getBroadcast(thisCon, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        thisCon.registerReceiver(mUsbReceiver, filter);
		
		btnBT.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{  									
				if(PRT!=null)
				{					
					PRT.CloseProt();					
				}
				
				Intent serverIntent = new Intent(PRTSDKApp.this,DeviceListActivity.class);				
				startActivityForResult(serverIntent, 10);
                ConnectType="Bluetooth";
				return;				
	        }
        });
		
		btnDisplayRemainingPower.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{  								
				if(PRT==null)
				{
					Toast.makeText(thisCon, R.string.no_printer_object, Toast.LENGTH_LONG).show();
					return;
				}
				
				if(!PRT.IsOpen())
				{
                Toast.makeText(thisCon, R.string.warmingconnect, Toast.LENGTH_LONG).show();
                return;
            }
				txtRemainingPower.setText(PRT.PRTGetRemainingPower());
				return;
	        }
        });
		
		btnWiFi.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{  								
				if(PRT!=null)
				{
					PRT.CloseProt();
				}
				String strIP="";
				strIP=edtIP.getText().toString().trim();
				ConnectType="WiFi";
				if(strIP.length()==0)
				{
					Toast.makeText(thisCon, R.string.err_noIP, Toast.LENGTH_SHORT).show();
                	return;
				}
				
				PRT=new PRTAndroidPrint(thisCon,"WiFi",arrPrinterList.getItem(spnPrinterList.getSelectedItemPosition()).toString());
				//PRT=new PRTAndroidPrint(thisCon,"WiFi");
				//PRT.OpenPort("192.168.0.33,9100")
				if(!PRT.OpenPort(strIP+",9100"))
				{
					PRT=null;
					txtTips.setText(thisCon.getString(R.string.connecterr));
					Toast.makeText(thisCon, R.string.connecterr, Toast.LENGTH_SHORT).show();
                	return;
				}
				else
				{					
					txtTips.setText("Printer:"+spnPrinterList.getSelectedItem().toString().trim());
					Toast.makeText(thisCon, R.string.connected, Toast.LENGTH_SHORT).show();
                	return;
				}					
	        }
        });
		
		btnUSB.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{  						
				if(PRT!=null)
				{
					PRT.CloseProt();
				}
				
				ConnectType="USB";							
				PRT=new PRTAndroidPrint(thisCon,"USB",arrPrinterList.getItem(spnPrinterList.getSelectedItemPosition()).toString());					
				//USB not need call "iniPort"				
				mUsbManager = (UsbManager) thisCon.getSystemService(Context.USB_SERVICE);				
		  		HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();  		
		  		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		  		
		  		boolean HavePrinter=false;		  
		  		while(deviceIterator.hasNext())
		  		{
		  		    device = deviceIterator.next();
		  		    int count = device.getInterfaceCount();
		  		    for (int i = 0; i < count; i++) 
		  	        {
		  		    	UsbInterface intf = device.getInterface(i); 
		  		    	//Class IDΪ7��ʾ��USB�豸Ϊ��ӡ���豸
		  	            if (intf.getInterfaceClass() == 7) 
		  	            {
		  	            	HavePrinter=true;
		  	            	mUsbManager.requestPermission(device, mPermissionIntent);		  	            	
		  	            }
		  	        }
		  		}
		  		if(!HavePrinter)
		  			Toast.makeText(thisCon, R.string.connect_usb_printer, Toast.LENGTH_LONG).show();
	        }
        });
						
		btnPrint.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				if(PRT==null)
				{
					Toast.makeText(thisCon, R.string.no_printer_object, Toast.LENGTH_LONG).show();
					return;
				}
				if(!PRT.IsOpen())
				{
					 Toast.makeText(thisCon, R.string.warmingconnect, Toast.LENGTH_LONG).show();
					 return;
				}
				try
				{
					PRT.PRTReset();
					if( PRT.PRTGetCurrentStatus()==3)
					{
						Toast.makeText(thisCon, R.string.PrintIsNotReady, Toast.LENGTH_SHORT).show();
						return;
					}
																				
					try
					{
						Thread.sleep(200);
					}
					catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
																
					int intLanguageNum=23;							
					String codeL="utf-8";	//chinese charset
					
					String codeLanguage=spinnerLanguage.getSelectedItem().toString().trim();
					HashMap<String, Integer> map=getMapLanguage();
					HashMap<String,String> codeMap=getCodeLanguage();					 						
					if(codeMap.containsKey(codeLanguage))					
						 codeL=codeMap.get(codeLanguage);					
					if(map.containsKey(codeLanguage))					
						intLanguageNum= map.get(codeLanguage);										
					PRT.Language=codeL;

                    Drawable myDrawable = getResources().getDrawable(R.drawable.boleta1);
                    Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();

                    PRT.PRTPrintBitmap(myLogo);
                    PRT.PRTFeedLines(20);
                    PRT.PRTReset();

//PRT.PRTPrintBitmap(Bitmap.createBitmap(R.drawable.boleta1,30,30,30,30))
					/*PRT.PRTSetPagePrintModel();							
					PRT.PRTSetPagePrintArea(0,0,576,352);
					PRT.WriteData("Page mode test begin.\n".getBytes(codeL));
					PRT.PRTSetPageModeAbsolutePosition(0,160);
					PRT.PRTPrintBarcode(PRTAndroidPrint.BC_UPCA,3,80,2,"075678164125");
					PRT.SetHorizontalAbsolutePosition(0);
					PRT.PRTSetPageModeRelativeTopMargin(80);
					PRT.WriteData("Page mode test end.".getBytes(codeL));
					byte ClearCMD[] = new byte[1];
					ClearCMD[0]=0x0C;				
					PRT.WriteData(ClearCMD);*/						
										
					//print string



                    ////////////////////*********************************///////////////////////////

				/*
							if(!edtPrintText.getText().toString().equals(""))
					{																	
						if(!PRT.PRTSendString(codeLanguage+":\n"))
							return;
						
						byte data[] = null;
						byte sendText[]=new byte[3];
						
						sendText[0]=0x1B;
						sendText[1]=0x74;
						sendText[2]=(byte)intLanguageNum;
						if(PRT.WriteData(sendText, sendText.length)==-1)
							return;
						String strPrintText=edtPrintText.getText().toString();
						if(codeLanguage.contains("WPC1256"))							
							strPrintText=new StringBuffer(strPrintText).reverse().toString();
						
						data = (thisCon.getString(R.string.originalsize) + strPrintText+"\n").getBytes(codeL);
						if(PRT.WriteData(data, data.length)==-1)
							return;
						if(!PRT.PRTFeedLines(20))
							return;
						if(!PRT.PRTReset())
							return;
																							
						//width��height��bold��underline��minifont
						if(!PRT.PRTFormatString(false,true,false,false,false))
							return;						
						data = (thisCon.getString(R.string.heightsize) + strPrintText+"\n").getBytes(codeL);
						if(PRT.WriteData(data, data.length)==-1)
							return;
						if(!PRT.PRTFeedLines(20))
							return;
						if(!PRT.PRTReset())
							return;
												
						PRT.PRTFormatString(true,false,false,false,false);
						data = (thisCon.getString(R.string.widthsize) + strPrintText+"\n").getBytes(codeL);
						PRT.WriteData(data, data.length);
						PRT.PRTFeedLines(20);
						PRT.PRTReset();
						
						PRT.PRTFormatString(true,true,false,false,false);
						data = (thisCon.getString(R.string.heightwidthsize) + strPrintText+"\n").getBytes(codeL);
						PRT.WriteData(data, data.length);
						PRT.PRTFeedLines(20);
						PRT.PRTReset();
						
						PRT.PRTFormatString(false,false,true,false,false);
						data = (thisCon.getString(R.string.bold) + strPrintText+"\n").getBytes(codeL);
					PRT.WriteData(data, data.length);
						PRT.PRTFeedLines(20);
						PRT.PRTReset();
						
						PRT.PRTFormatString(false,false,false,true,false);
						data = (thisCon.getString(R.string.underline) + strPrintText+"\n").getBytes(codeL);
						PRT.WriteData(data, data.length);
						PRT.PRTFeedLines(20);
						PRT.PRTReset();	
						
						if(PRT.PRTCapPrintMiniFont())
						{
							PRT.PRTFormatString(false,false,false,false,true);
							data = (thisCon.getString(R.string.minifront) + strPrintText+"\n").getBytes(codeL);							
							PRT.WriteData(data, data.length);
							PRT.PRTFeedLines(20);
						}						
					}
					PRT.PRTReset();
					//print barcode
					if(PRT.PRTCapPrintBarcode())
					{
						if(!Barcode_BC_UPCA())
							return;
						Barcode_BC_UPCE();
						Barcode_BC_EAN8();							
						Barcode_BC_EAN13();
						Barcode_BC_CODE93();
						Barcode_BC_CODE39();
						Barcode_BC_CODEBAR();		
						Barcode_BC_ITF();
						Barcode_BC_CODE128();
					}
					
					PRT.PRTSendString("\n");					
					if(PRT.PRTCapPrintBarcode2())
					{
						PRT.PRTSendString("BarCode2:0123456789abcdef0123456789abcdef\n");					
						//��ӡ��ά��
						//Align:0:Left,1:Center,2:Right
						//LeftMargin:0mm~4112mm
						//Model:49,50
						//Size:1~16
						//ErrLevel:48,49,50,51
						//pData:Code String
						PRT.PRTPrintBarcode2(0,5,49,8,48,"0123456789abcdef0123456789abcdef");										
						PRT.PRTFeedLines(240);	
						PRT.PRTFeedLines(240);
					}
					if (PRT.PRTCapPaperCut())
					{										
						PRT.PRTPaperCut(true);	//true:half cut
					}	*/
				} 
				catch (Exception e) 
				{					
					Toast.makeText(thisCon, e.getMessage(), Toast.LENGTH_LONG).show();
				}				
			}	        
        });
	}

	private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() 
	{
	    public void onReceive(Context context, Intent intent) 
	    {
	        String action = intent.getAction();	       
	        //Toast.makeText(thisCon, "now:"+System.currentTimeMillis(), Toast.LENGTH_LONG).show();
	        //��ȡ����USB�豸Ȩ��
	        if (ACTION_USB_PERMISSION.equals(action))
	        {
		        synchronized (this) 
		        {		        	
		            device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
			        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
			        {			        	
			        	if(!PRT.OpenPort("",device))
						{					
			        		PRT=null;
							txtTips.setText(thisCon.getString(R.string.connecterr));
							Toast.makeText(thisCon, thisCon.getString(R.string.connecterr)+PRT.GetPrinterName(), Toast.LENGTH_SHORT).show();					
		                	return;
						}
						else
						{
							txtTips.setText("Printer:"+spnPrinterList.getSelectedItem().toString().trim());
							Toast.makeText(thisCon, thisCon.getString(R.string.connected), Toast.LENGTH_SHORT).show();
		                	return;
						}	
			        }		
			        else
			        {			        	
			        	return;
			        }
		        }
		    }
	        //�Ͽ�����
	        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) 
	        {
	            device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
	            if (device != null) 
	            {	                
	            	PRT.ClosePort();
	            }
	        }
	    }
	};
	
	private boolean Barcode_BC_UPCA()
	{		
		PRT.PRTSendString("BC_UPCA:\n");		
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_UPCA,
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_HRIBELOW, 
		 		 "075678164125");
	}
	
	private boolean Barcode_BC_UPCE()
	{		
		PRT.PRTSendString("BC_UPCE:\n");		
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_UPCE,
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_HRIBELOW, 
		 		 "01227000009");//04252614 
	}
	
	private boolean Barcode_BC_EAN8()
	{		
		PRT.PRTSendString("BC_EAN8:\n");		
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_EAN8,
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_HRIBELOW, 
		 		 "04210009");
	}
	
	private boolean Barcode_BC_EAN13()
	{		
		PRT.PRTSendString("BC_EAN13:\n");		
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_EAN13,
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_HRIBELOW, 
		 		 "6901028075831");
	}
	
	private boolean Barcode_BC_CODE93()
	{		
		PRT.PRTSendString("BC_CODE93:\n");		
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_CODE93,
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_HRIBELOW, 
		 		 "TEST93");
	}
	
	private boolean Barcode_BC_CODE39()
	{		
		PRT.PRTSendString("BC_CODE39:\n");		
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_CODE39,
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_HRIBELOW, 
		 		 "123456789");
	}
	
	private boolean Barcode_BC_CODEBAR()
	{		
		PRT.PRTSendString("BC_CODEBAR:\n");		
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_CODEBAR,
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_HRIBELOW, 
		 		 "A40156B");
	}
	
	private boolean Barcode_BC_ITF()
	{		
		PRT.PRTSendString("BC_ITF:\n");		
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_ITF,
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_HRIBELOW, 
 		 		 "123456789012");
	}
	
	private boolean Barcode_BC_CODE128()
	{		
		PRT.PRTSendString("BC_CODE128:\n");		
		return PRT.PRTPrintBarcode(PRTAndroidPrint.BC_CODE128,
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_DEFAULT, 
				 PRTAndroidPrint.BC_HRIBELOW, 
		 		 "{BS/N:{C\014\042\070\116{A3");	// decimal 1234 = octonary 1442
	}
	
	//create menu 
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.prtsdkapp, menu);
	    return true;
	}
	
	//back key
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{ 		
		if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
		{
			new AlertDialog.Builder(this)
			.setIcon(R.drawable.ic_launcher)
			.setMessage(R.string.sureExit).setTitle(R.string.warmTips)
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog, int which) 
					{
						if(PRT!=null)
						{
							PRT.CloseProt();
						}
						finish();							
					}
				}
			).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() 
				{								
					public void onClick(DialogInterface dialog,	int which)
					{
						
					}
				}
			) .show();			
		} 
		return false;
	}
	
	//menu click
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{	      			
	       	case R.id.Quit:				
	       		if(PRT!=null)
				{					
					PRT.CloseProt();					
				}
	       		finish();
		}			
		return false; 
	}
	
	//call back by scan bluetooth printer
	@Override  
  	protected void onActivityResult(int requestCode, int resultCode, Intent data)  
  	{  
  		try
  		{  		   
	  		switch(resultCode)
	  		{
	  			case 10:			  	        	
	  				String strIsConnected=data.getExtras().getString("is_connected");
	  	        	if (strIsConnected.equals("NO"))
	  	        	{
	  	        		txtTips.setText(thisCon.getString(R.string.scan_error));
	  	        		Toast.makeText(thisCon, R.string.connecterr, Toast.LENGTH_SHORT).show();
  	                	return;
	  	        	}
	  	        	else
	  	        	{	  	        		
	  	        		strBTAddress=data.getExtras().getString("BTAddress");
	  	        		if(strBTAddress==null)
	  					{
	  						return;
	  					}
	  					else if(!strBTAddress.contains(":"))
	  					{
	  						return;
	  					}
	  					else if(strBTAddress.length()!=17)
	  					{
	  						return;
	  					}
	  					
	  					PRT=new PRTAndroidPrint(thisCon,"Bluetooth",spnPrinterList.getSelectedItem().toString().trim());						
	  					//PRT=new PRTAndroidPrint(thisCon,"Bluetooth");
	  					PRT.InitPort();	
	  					
	  					if(!PRT.OpenPort(strBTAddress))
	  					{
	  						Toast.makeText(thisCon, R.string.connecterr, Toast.LENGTH_SHORT).show();
	  						txtTips.setText(thisCon.getString(R.string.scan_error));
	  	                	return;
	  					}
	  					else
	  					{
	  						Toast.makeText(thisCon, R.string.connected, Toast.LENGTH_SHORT).show();
	  						txtTips.setText(thisCon.getString(R.string.scan_success));
	  	                	return;
	  					}
	  	        	}	  	            		
  			}
  		}
  		catch(Exception e)
  		{
  			e.printStackTrace();
  		}
        super.onActivityResult(requestCode, resultCode, data);  
  	} 
	
	//add printer list
	private void InitCombox()
	{
		arrPrinterList = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
		arrPrinterList=ArrayAdapter.createFromResource(this, R.array.printer_list, android.R.layout.simple_spinner_item);		
		arrPrinterList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//set adapter to spinner
		spnPrinterList.setAdapter(arrPrinterList);		
	}
	
	//EnableBluetooth
	private boolean EnableBluetooth()
    {
        boolean bRet = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter != null)
        {
            if(mBluetoothAdapter.isEnabled())
                return true;
            mBluetoothAdapter.enable();
            try 
    		{
    			Thread.sleep(500);
    		} 
    		catch (InterruptedException e) 
    		{			
    			e.printStackTrace();
    		}
            if(!mBluetoothAdapter.isEnabled())
            {
                bRet = true;
                Log.d("PRTLIB", "BTO_EnableBluetooth --> Open OK");
            }
        } 
        else
        {
            Log.d("PRTLIB", "BTO_EnableBluetooth --> mBluetoothAdapter is null");
        }
        return bRet;
    }
				
	private ArrayAdapter<String> getLangguageInfo()
    {
    	ArrayAdapter<String> languageItem = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
    	languageItem.add("TEST MODIFICADO COPY SOLUTION");//��������
    	languageItem.add("Chinese Traditional");//��������
        languageItem.add("PC437 [U.S.A. Standard Europe]");//CP437 [����ŷ�ޱ�׼]  
        languageItem.add("KataKana");//KataKana [Ƭ����]   JIS_X_0201   
		languageItem.add("PC850 [Multilingual]");//CP850 [������]  iso8859-3
		languageItem.add("PC860 [Portuguese]");//CP860 [������]    iso8859-1
		languageItem.add("PC863 [Canadian-French]");//CP863 [���ô�-����]   iso8859-1
		languageItem.add("PC865 [Nordic]");//CP865 [��ŷ]  nls iso8859-1
		languageItem.add("PC857 [Turkish]");//CP857[��������] iso8859-3 
		languageItem.add("PC737 [Greek]");//CP737 [ϣ��]  iso8859-7
		languageItem.add("ISO-8859-7 [Greek]");//ISO-8859-7[ϣ����]
		languageItem.add("WCP1252");//��CP1252 [������ 1] iso8859-1 
		languageItem.add("PC866 [Cyrillic #2]");//CP866 ˹����2  iso8859-5
		languageItem.add("PC852 [Latin 2]");//CP852 [������ 2] iso8859-2
		languageItem.add("PC858 [Euro]");//CP858 [�������������� 1+ŷԪ��]  iso8859-15
		languageItem.add("KU42 [Thai]");
		languageItem.add("TIS11 [Thai]");//̩�ģ���TM-88 Thai character code 14ͬ�� gb2312
        languageItem.add("TIS18 [Thai]");//[̩��2] gb2312
		languageItem.add("PC720");//CP720[��������] iso8859-6  
		languageItem.add("WPC775");//���޵ĺ�  iso8859-1          
		languageItem.add("PC855 [Cyrillic]");//�������  iso8859-5
		languageItem.add("PC862 [Hebrew]");//CP862  [ϣ����] iso8859-8        
        languageItem.add("PC864 [Arabic]");//CP864 [��������] iso8859-6     
		languageItem.add("ISO-8859-2 [Latin 2]");//ISO-8859-2[������2]
		languageItem.add("ISO-8859-15 [Latin 9]");//ISO-8859-15[������9]
     	languageItem.add("WPC1250");//WCP1250[��ŷ] iso8859-2
        languageItem.add("WPC1251 [Cyrillic]");//WCP1251 [˹������] iso8859-5        
        languageItem.add("WPC1253");//WCP1253 [ϣ��] iso8859-7 
        languageItem.add("WPC1254");//WCP1254��������] iso8859-3
        languageItem.add("WPC1255");//WCP1255[ϣ������] iso8859-8
        languageItem.add("WPC1256");//WCP1256[��������] iso8859-6
        languageItem.add("WPC1257");//WCP1257 [���޵ĺ�] iso8859-1 ������ŷ��ϵ
        languageItem.add("WPC1258");//WCP1258[Խ����] bg2312
        // ����Codepage EPSON û�йʰ�˳������
        languageItem.add("MIK [Cyrillic/Bulgarian]");              
        languageItem.add("PC755 [Eastern Europe, Latvia 2]");//MIK[˹����/��������]  iso8859-5  
        languageItem.add("Iran");//[���ʣ���˹]  iso8859-6
        languageItem.add("Iran ��");//���ʢ�[��˹��]  iso8859-6 ���ð�������
        languageItem.add("Latvia");//����ά�� iso8859-4
        languageItem.add("ISO-8859-1 [West Europe]");//ISO-8859-1 [��ŷ]  iso8859-1         
        languageItem.add("ISO-8859-3 [Latin 3]");//ISO-8859-3[������3]
        languageItem.add("ISO-8859-4 [Baltic]");//ISO-8859-4[���޵���]
        languageItem.add("ISO-8859-5 [Cyrillic]");//ISO-8859-5[˹������]
        languageItem.add("ISO-8859-6 [Arabic]");//ISO-8859-6[��������]        
        languageItem.add("ISO-8859-8 [Hebrew]");//ISO-8859-8[ϣ������]
        languageItem.add("ISO-8859-9 [Turkish]");//ISO-8859-9[��������]
        languageItem.add("PC856");//[ϣ������] iso8859-8  
        languageItem.add("ABICOIM");        
        return languageItem;
    }
	
	private HashMap<String,String> getCodeLanguage()
    {
    	 HashMap<String,String> codeMap = new HashMap<String,String>();
    	 codeMap.put("TEST MODIFICADO COPY SOLUTIO", 			"gb2312");//gb2312
    	 codeMap.put("Chinese Traditional", 		"big5");	//big5
    	 codeMap.put("PC437 [U.S.A. Standard Europe]","iso8859-1");//
    	 codeMap.put("KataKana", 					"Shift_JIS");//
    	 codeMap.put("PC850 [Multilingual]", 		"iso8859-3");//
    	 codeMap.put("PC860 [Portuguese]", 			"iso8859-6");//
    	 codeMap.put("PC863 [Canadian-French]", 	"iso8859-1");
    	 codeMap.put("PC865 [Nordic]", 				"iso8859-1");
    	 codeMap.put("PC857 [Turkish]", 			"iso8859-3");
    	 codeMap.put("PC737 [Greek]", 				"iso8859-7");
    	 codeMap.put("ISO-8859-7 [Greek]", 			"iso8859-7");
    	 codeMap.put("WCP1252", 					"iso8859-1");
    	 codeMap.put("PC866 [Cyrillic #2]", 		"iso8859-5");
    	 codeMap.put("PC852 [Latin 2]", 			"iso8859-2");
    	 codeMap.put("PC858 [Euro]", 				"iso8859-15");
    	 codeMap.put("KU42 [Thai]", 				"ISO8859-11");
    	 codeMap.put("TIS11 [Thai]", 				"ISO8859-11");
    	 codeMap.put("TIS18 [Thai]", 				"ISO8859-11");
    	 codeMap.put("PC720", 						"iso8859-6");
    	 codeMap.put("WPC775", 						"iso8859-1");
    	 codeMap.put("PC855 [Cyrillic]", 			"iso8859-5");    	 
    	 codeMap.put("PC862 [Hebrew]", 				"iso8859-8");
    	 codeMap.put("PC864 [Arabic]", 				"iso8859-6");
    	 codeMap.put("ISO8859-2 [Latin 2]", 		"iso8859-2");
    	 codeMap.put("ISO8859-15 [Latin 9]", 		"iso8859-15");
    	 codeMap.put("WPC1250", 					"iso8859-2");
    	 codeMap.put("WPC1251 [Cyrillic]", 			"iso8859-5");
    	 codeMap.put("WPC1253", 					"iso8859-7");
    	 codeMap.put("WPC1254", 					"iso8859-3");
    	 codeMap.put("WPC1255", 					"iso8859-8");
    	 codeMap.put("WPC1256", 					"iso8859-6");
    	 codeMap.put("WPC1257", 					"iso8859-1");
    	 codeMap.put("WPC1258", 					"CP1258");
    	 // ����Codepage EPSON û�йʰ�˳������
    	 codeMap.put("MIK [Cyrillic/Bulgarian]", 	"iso8859-15");
    	 codeMap.put("PC755 [East Europe, Latvian 2]", "iso8859-5");
    	 codeMap.put("Iran", 						"iso8859-6");
    	 codeMap.put("Iran II", 					"iso8859-6");
    	 codeMap.put("Latvian", 					"iso8859-4");
    	 codeMap.put("ISO-8859-1 [West Europe]", 	"iso8859-1");
    	 codeMap.put("ISO-8859-3 [Latin 3]", 		"iso8859-3");
    	 codeMap.put("ISO-8859-4 [Baltic]", 		"iso8859-4");
    	 codeMap.put("ISO-8859-5 [Cyrillic]", 		"iso8859-5");
    	 codeMap.put("ISO-8859-6 [Arabic]", 		"iso8859-6");
    	 codeMap.put("ISO-8859-8 [Hebrew]", 		"iso8859-8");
    	 codeMap.put("ISO-8859-9 [Turkish]", 		"iso8859-9");
    	 codeMap.put("PC856",		 				"iso8859-8");
    	 codeMap.put("ABICOIM", 					"iso8859-15");    	 
    	 return codeMap;
    }
	
	private HashMap<String,Integer> getMapLanguage()
    {
		HashMap<String,Integer> map = new HashMap<String,Integer>();
    	map.put("TEST MODIFICADO COPY SOLUTIO", 		0);//gb2312
    	map.put("Chinese Traditional", 		0);
    	map.put("PC437 [U.S.A. Standard Europe]",0);
     	map.put("KataKana", 				1);
     	map.put("PC850 [Multilingual]", 	2);
     	map.put("PC860 [Portuguese]", 		3);
     	map.put("PC863 [Canadian-French]", 	4);
     	map.put("PC865 [Nordic]", 			5);     	
     	map.put("PC857 [Turkish]", 			13);
     	map.put("PC737 [Greek]", 			14);
     	map.put("ISO-8859-7 [Greek]", 		15);
     	map.put("WCP1252", 					16);
     	map.put("PC866 [Cyrillic #2]", 		17);
     	map.put("PC852 [Latin 2]", 			18);
     	map.put("PC858 [Euro]", 			19);
     	map.put("KU42 [Thai]", 				20);
     	map.put("TIS11 [Thai]", 			21);     	
     	map.put("TIS18 [Thai]", 			26);     	
     	map.put("PC720", 					32);
     	map.put("WPC775", 					33);
     	map.put("PC855 [Cyrillic]", 		33);       	
     	map.put("PC862 [Hebrew]", 			36);     	
     	map.put("PC864 [Arabic]", 			37);     	
     	map.put("ISO8859-2 [Latin 2]", 		39);    	
     	map.put("ISO8859-15 [Latin 9]", 	40);     	
     	map.put("WPC1250", 					45);
     	map.put("WPC1251 [Cyrillic]", 		46);
     	map.put("WPC1253", 					47);
     	map.put("WPC1254", 					48);
     	map.put("WPC1255", 					49);
     	map.put("WPC1256", 					50);
     	map.put("WPC1257", 					51);
     	map.put("WPC1258", 					52);
     	// ����Codepage EPSON û�йʰ�˳������
     	map.put("MIK [Cyrillic/Bulgarian]", 54);
     	map.put("PC755 [East Europe, Latvian 2]", 55);
     	map.put("Iran", 					56);
     	map.put("Iran II", 					57);
     	map.put("Latvian", 					58);
     	map.put("ISO-8859-1 [West Europe]", 59);
     	map.put("ISO-8859-3 [Latin 3]", 	60);
     	map.put("ISO-8859-4 [Baltic]", 		61);
     	map.put("ISO-8859-5 [Cyrillic]", 	62);
     	map.put("ISO-8859-6 [Arabic]", 		63);
     	map.put("ISO-8859-8 [Hebrew]", 		64);
     	map.put("ISO-8859-9 [Turkish]", 	65);
     	map.put("PC856", 					66);
     	map.put("ABICOIM", 					67);
     	    	            		       		    
     	return map;
    }	
}

