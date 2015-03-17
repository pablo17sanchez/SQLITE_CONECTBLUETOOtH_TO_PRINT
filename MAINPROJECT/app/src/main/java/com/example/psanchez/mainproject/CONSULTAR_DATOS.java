package com.example.psanchez.mainproject;

import android.content.ContentValues;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import java.text.DateFormat;
import java.util.Calendar;
import  java.text.SimpleDateFormat;
 import  android.app.AlertDialog;
import android.content.DialogInterface;
import android.bluetooth.BluetoothAdapter;


public class CONSULTAR_DATOS extends ActionBarActivity {
private EditText consulta_texto;
private Button nextbutton,consultar;
private TextView nombrelabel,cedulalabel,fechalabel,mesalabel,sexolabel,texto;
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
   // private ImageView CEDULAIMAGE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar__datosv7);
        nombrelabel = (TextView) findViewById(R.id.LABELNOMBRE);
        cedulalabel = (TextView) findViewById(R.id.LABELCEDULA);
        fechalabel = (TextView) findViewById(R.id.LABELMESA);
        sexolabel = (TextView) findViewById(R.id.LABELSEXO);
        mesalabel = (TextView) findViewById(R.id.LABELMESA);
        nextbutton=(Button)findViewById(R.id.NEXTPAGEBUTON);
        consultar=(Button)findViewById(R.id.button3);








       // CEDULAIMAGE=(ImageView)findViewById(R.id.IMAGENCEDULA);

        consulta_texto = (EditText) findViewById(R.id.txtcedulaconsulta);

    }
@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_consultar__dato, menu);
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

    public void consultarTalbal(View v){


        Calendar instanciadeCALENDAR = Calendar.getInstance();

        int HORA = instanciadeCALENDAR.get(Calendar.HOUR_OF_DAY);


        if (HORA>17) {



            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("HORA");
            alertDialog.setMessage("La hora excede la establecida para el cierre : "+dateFormat.format(instanciadeCALENDAR.getTime()).toString());
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                     consultar.setEnabled(false);

// here you can add functions
                }
            });

            alertDialog.show();

        } else {


        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "administracion", null, 4);
        SQLiteDatabase bd = admin.getWritableDatabase();

        String cedula = consulta_texto.getText().toString();

        Cursor filaresult = bd.rawQuery(  //devuelve 0 o 1 fila //es una consulta
                "select asistencia,fechahora from datos where cedula=" + cedula, null);
        String evaluar = "";
        String fechahora="";

       if (filaresult.moveToFirst()){

             evaluar = filaresult.getString(0);
        fechahora = filaresult.getString(1);



        }



        if (evaluar!=null && evaluar!=""){



          //  Toast.makeText(this, "La persona ya se registro el "+fechahora+" y la cantidad "+evaluar,
            //        Toast.LENGTH_SHORT).show();

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("REGISTRO");
            alertDialog.setMessage("La persona ya se registro el "+fechahora+" y la cantidad "+evaluar);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
// here you can add functions
                }
            });


            alertDialog.show();




        } else {


            Cursor fila = bd.rawQuery(  //devuelve 0 o 1 fila //es una consulta
                    "select nombre,cedula,fecha,nromesa,sexo  from datos where cedula=" + cedula, null);

            if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)
                nombrelabel.setText(fila.getString(0));
                cedulalabel.setText(fila.getString(1));
                fechalabel.setText(fila.getString(2));
                mesalabel.setText(fila.getString(3));
                sexolabel.setText(fila.getString(4));


            } else {
                Toast.makeText(this, "No existe una persona con dicha cedula",
                        Toast.LENGTH_SHORT).show();

            }
            if (cedulalabel.getText().equals("")) {

                nextbutton.setEnabled(false);


            } else {

                nextbutton.setEnabled(true);
                //  CEDULAIMAGE.setImageResource(R.drawable.cedula);


            }
            bd.close();
        }}

    }






    public void modificar(View view){

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "administracion", null, 4);
        Calendar c = Calendar.getInstance();

        SQLiteDatabase bd = admin.getWritableDatabase();
        String cedula = cedulalabel.getText().toString();
        ContentValues registro = new ContentValues();
        registro.put("asistencia", "1");
        registro.put("fechahora",dateFormat.format(c.getTime()).toString() );
        int cant = bd.update("datos", registro, "cedula=" + cedula, null);
        bd.close();
        if (cant == 1){
            Toast.makeText(this, "REGISTRADO", Toast.LENGTH_SHORT)
                    .show();}
        else{
            Toast.makeText(this, "no existe una persona con dicho documento",
                    Toast.LENGTH_SHORT).show();}





        try {
            Intent ventana_sellecion = new Intent(this,SELECCION.class);
            ventana_sellecion.putExtra("cedula", cedulalabel.getText().toString());
            startActivity(ventana_sellecion);
        }
        catch (Exception ex){

            Toast.makeText(this, "Error" + ex.getMessage(),
                    Toast.LENGTH_SHORT).show();


        }




        nombrelabel.setText("");
        cedulalabel.setText("");
        fechalabel.setText("");
        mesalabel.setText("");
        sexolabel.setText("");
        nextbutton.setEnabled(false);

    }


}
