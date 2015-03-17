package com.example.psanchez.mainproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.database.sqlite.SQLiteException;


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


public class SELECCION extends ActionBarActivity {
private ImageView img;
  //  private Spinner spnPrinterList=null;
    private  TextView texto,SELECCIONBOLETA;
    private Button btnBT=null,FINAL;
    private static PRTAndroidPrint PRT=null;
    private String ConnectType="";
    private Context thisCon=null;
    private BluetoothAdapter mBluetoothAdapter;
    private PendingIntent mPermissionIntent=null;
    private UsbDevice device=null;
    private  TextView txttips;
    private String strBTAddress="";
    private static final String ACTION_USB_PERMISSION = "com.example.psanchez.mainproject.SELECCION";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion);
       img=(ImageView)findViewById(R.id.imageView);
     btnBT=(Button)findViewById(R.id.bluet);
        FINAL=(Button)findViewById(R.id.FINALIZARBTN);
        texto=(TextView)findViewById(R.id.IMAGEN);
        EnableBluetooth();
        thisCon=this.getApplicationContext();
txttips=(TextView)findViewById(R.id.TXTTIPS);
        SELECCIONBOLETA=(TextView)findViewById(R.id.OTRO);
        Bundle bundle = getIntent().getExtras();
        texto.setText(bundle.getString("cedula"));
        mPermissionIntent = PendingIntent.getBroadcast(thisCon, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        thisCon.registerReceiver(mUsbReceiver, filter);
        btnBT.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(PRT!=null)
                {
                    PRT.CloseProt();
                }

                Intent serverIntent = new Intent(SELECCION.this,DeviceListActivity.class);
                startActivityForResult(serverIntent, 10);
                ConnectType="Bluetooth";
                return;
            }
        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_seleccion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                            txttips.setText(thisCon.getString(R.string.connecterr));
                            Toast.makeText(thisCon, thisCon.getString(R.string.connecterr)+PRT.GetPrinterName(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else
                        {
                            //txttips.setText("Printer:"+spnPrinterList.getSelectedItem().toString().trim());

                            txttips.setText("Printer:"+"PRUEBA ESTO ES LO QUE TRAE EL SPAINNER");
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


    public void lanzar_1(View view){
img.setImageResource(R.drawable.boleta1);
        SELECCIONBOLETA.setText("1");

FINAL.setEnabled(true);




    }
    public void lanzar_2(View view){
        img.setImageResource(R.drawable.boleta2);
        SELECCIONBOLETA.setText("2");


        FINAL.setEnabled(true);

    }
    public void lanzar_3(View view){

        img.setImageResource(R.drawable.boleta3);
        SELECCIONBOLETA.setText("3");
        FINAL.setEnabled(true);



    }



    public  void PRINT (View view){


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

            PRT.PRTReset();
            if( PRT.PRTGetCurrentStatus()==3)
            {
                Toast.makeText(thisCon, R.string.PrintIsNotReady, Toast.LENGTH_SHORT).show();
                return;
            }
        try {


            PRT.PRTReset();
            if (PRT.PRTGetCurrentStatus() == 3) {
                Toast.makeText(thisCon, R.string.PrintIsNotReady, Toast.LENGTH_SHORT).show();
                return;
            }


            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (texto.getText().toString() == "Texto1") {
                Drawable myDrawable = getResources().getDrawable(R.drawable.boleta1);
                Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();

                PRT.PRTPrintBitmap(myLogo);
                PRT.PRTFeedLines(20);
                PRT.PRTReset();

            } else if (texto.getText().toString() == "Texto2") {


                Drawable myDrawable = getResources().getDrawable(R.drawable.boleta2);
                Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();

                PRT.PRTPrintBitmap(myLogo);
                PRT.PRTFeedLines(20);
                PRT.PRTReset();


            } else if (texto.getText().toString() == "Texto2") {


                Drawable myDrawable = getResources().getDrawable(R.drawable.boleta3);
                Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();

                PRT.PRTPrintBitmap(myLogo);
                PRT.PRTFeedLines(20);
                PRT.PRTReset();


            } else if (texto.getText().toString() == "Texto2") {

                Toast.makeText(thisCon, "Debe seleccionar una Boleta", Toast.LENGTH_LONG).show();

            }
        }catch (Exception e)
        {
            Toast.makeText(thisCon, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


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
                        txttips.setText(thisCon.getString(R.string.scan_error));
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

                        PRT=new PRTAndroidPrint(thisCon,"Bluetooth","MPT8");
                        //PRT=new PRTAndroidPrint(thisCon,"Bluetooth");
                        PRT.InitPort();

                        if(!PRT.OpenPort(strBTAddress))
                        {
                            Toast.makeText(thisCon, R.string.connecterr, Toast.LENGTH_SHORT).show();
                            txttips.setText(thisCon.getString(R.string.scan_error));
                            return;
                        }
                        else
                        {
                            Toast.makeText(thisCon, R.string.connected, Toast.LENGTH_SHORT).show();
                            txttips.setText(thisCon.getString(R.string.scan_success));
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

    public void btnfinalizar(View view){


       // int cant;
        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                    "administracion", null, 4);
            SQLiteDatabase bd = admin.getWritableDatabase();
            String cedula = texto.getText().toString();
String  boleta = SELECCIONBOLETA.getText().toString();
            ContentValues registro = new ContentValues();
            registro.put("boleta_eligio",boleta );

            int cant = bd.update("datos", registro, "cedula=" + cedula, null);
            bd.close();


FINAL.setEnabled(false);

        if (cant == 1){
            Toast.makeText(this, "FINALIZO", Toast.LENGTH_SHORT)
                    .show();}
        else{
            Toast.makeText(this, "NO FINALIZO",
                    Toast.LENGTH_SHORT).show();}

        } catch (SQLiteException ex) {


            Toast.makeText(this, "ERROR CON SQLITE"+ex.getMessage().toString(),
                    Toast.LENGTH_SHORT).show();


        }
        try {
            Intent ventana_sellecion = new Intent(this,CONSULTAR_DATOS.class);

            startActivity(ventana_sellecion);
        }

        catch (Exception ex){
            Toast.makeText(this, "ERROR AL PASAR DE VENTANA", Toast.LENGTH_SHORT)
                    .show();

        }


    }
}
