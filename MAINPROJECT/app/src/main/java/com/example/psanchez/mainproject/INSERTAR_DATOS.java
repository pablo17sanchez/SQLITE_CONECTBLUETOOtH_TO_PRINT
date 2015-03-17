package com.example.psanchez.mainproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.view.View;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import android.content.ContentValues;
import android.content.Intent;
import com.example.psanchez.mainproject.MaskedWatcher;
import android.content.pm.ActivityInfo;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.View.OnFocusChangeListener;
public class INSERTAR_DATOS extends ActionBarActivity {

    private EditText TextNombre,Textcedla,Textfecha,Textnumeromesa;
    private RadioButton RadioM,RadioF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar__datos);

        TextNombre=(EditText)findViewById(R.id.TXTNOMBRE);
        Textcedla=(EditText)findViewById(R.id.TXTCEDULA);
        Textfecha=(EditText)findViewById(R.id.TXTFECHA);
        Textnumeromesa=(EditText)findViewById(R.id.TXTNUMEROMESA);
        RadioM=(RadioButton)findViewById(R.id.rdmasculino);
        RadioF=(RadioButton)findViewById(R.id.rdfemenino);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      /* int maxLength = 14; Textcedla.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
      //  final MaskedWatcher cepWatcher = new MaskedWatcher(Textcedla, "###-#######-##");
     //   cepWatcher.setAcceptOnlyNumbers(true);


        Textcedla.addTextChangedListener(new TextWatcher() {

    public void afterTextChanged(Editable s) {

    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    public void onTextChanged(CharSequence s, int start, int before, int count) {



    }

        }



        );


*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_insertar__dato, menu);
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


    public void insertar(View view) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 4);

        SQLiteDatabase bd = admin.getWritableDatabase();

        String validarcedula = Textcedla.getText().toString();

        Cursor filaresult = bd.rawQuery(  //devuelve 0 o 1 fila //es una consulta
                "select cedula from datos where cedula=" + validarcedula, null);
        if (filaresult.moveToFirst()){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("EXISTENCIA");
            alertDialog.setMessage("Esta cedula ya existe en el sistema ");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
// here you can add functions
                }
            });
            alertDialog.show();


        }else {


            String cedula = Textcedla.getText().toString();
            String nombre = TextNombre.getText().toString();
            String fecha = Textfecha.getText().toString();
            String numeromesa = Textnumeromesa.getText().toString();


            try {


                if (RadioM.isChecked() == true) {


                    ContentValues registro = new ContentValues();
                    registro.put("cedula", cedula);
                    registro.put("nombre", nombre);
                    registro.put("fecha", fecha);
                    registro.put("nromesa", numeromesa);
                    registro.put("sexo", "M");
                    bd.insert("datos", null, registro);
                    bd.close();
                    //   registro.put("asistencia", "1");
                } else if (RadioF.isChecked() == true) {
                    ContentValues registro = new ContentValues();
                    registro.put("cedula", cedula);
                    registro.put("nombre", nombre);
                    registro.put("fecha", fecha);
                    registro.put("nromesa", numeromesa);
                    registro.put("sexo", "M");
                    bd.insert("datos", null, registro);
                    bd.close();
                    //registro.put("asistencia", "1");
                } else if (RadioF.isChecked() == false && RadioM.isChecked() == false) {

                    Toast.makeText(this, "Debe seleccionar el Sexo de la persona",
                            Toast.LENGTH_SHORT).show();


                }
            } catch (Exception ex) {

                Toast.makeText(this, "Error" + ex.getMessage(),
                        Toast.LENGTH_SHORT).show();


            } finally {

                TextNombre.setText("");
                Textcedla.setText("");
                Textfecha.setText("");
                Textnumeromesa.setText("");
                RadioF.setChecked(false);
                RadioM.setChecked(false);

                Toast.makeText(this, "Los datos se registraron con exito",
                        Toast.LENGTH_SHORT).show();

            }


        }

    }

        public  void  llamar_venta_consultar_desdeinsertar(View view){

            try {
                Intent ventana_consultar = new Intent(this,CONSULTAR_DATOS.class);
                startActivity(ventana_consultar);
            }
            catch (Exception ex){

                Toast.makeText(this, "Error" + ex.getMessage(),
                        Toast.LENGTH_SHORT).show();


            }




        }



    }

