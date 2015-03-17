package com.example.psanchez.mainproject;

/**
 * Created by psanchez on 2/19/2015.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {


    public AdminSQLiteOpenHelper(Context context, String nombre, CursorFactory factory, int version) {
        super(context, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
  // db.execSQL("drop table if exists datos");
       db.execSQL("create table datos(cedula text primary key, nombre text, fecha text, nromesa integer,sexo text,asistencia text,fechahora text,boleta_eligio text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists datos");
       db.execSQL("create table datos(cedula text primary key, nombre text, fecha text, nromesa integer,sexo text,asistencia text,fechahora text,boleta_eligio text)");
    }
}
