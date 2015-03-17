package com.example.psanchez.mainproject;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;


public class MAINPROJECT extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainproject);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mainproject, menu);
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

    public void llamar_ventana_insertar(View view){

try{
        Intent ventana_insertar = new Intent(this,INSERTAR_DATOS.class);
        startActivity(ventana_insertar);}
        catch (Exception ex){

    Toast.makeText(this, "Error" + ex.getMessage(),
            Toast.LENGTH_SHORT).show();


}


    }

    public  void  llamar_venta_consultar(View view){

     try {
         Intent ventana_consultar = new Intent(this,CONSULTAR_DATOS.class);
         startActivity(ventana_consultar);
     }
     catch (Exception ex){

         Toast.makeText(this, "Error" + ex.getMessage(),
                 Toast.LENGTH_SHORT).show();


}




}

    public  void llamar_venta_impresion(View view){


        try {
            Intent ventana_consultar = new Intent(this,PRTSDKApp.class);
            startActivity(ventana_consultar);
        }
        catch (Exception ex){

            Toast.makeText(this, "Error" + ex.getMessage(),
                    Toast.LENGTH_SHORT).show();


        }

    }

}
