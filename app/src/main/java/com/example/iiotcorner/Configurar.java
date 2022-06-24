package com.example.iiotcorner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Configurar extends AppCompatActivity {
    TextView TextPasos, DI1, DI2, DI3, DI4, DI1_B, DI2_B, DI3_B, DI4_B, A01, A02, A01_B, A02_B;
    EditText nombrar, email, contenido, tNveces, kWh;
    ConstraintLayout Paso1, Paso2, Paso4;
    Spinner Paso3; LinearLayout Paso3lay, Paso5;
    int pos_spin = 0, Nveces=0;
    double kWh_double = 0.0d;
    String puerto, AB, nombre1, nombre2, nombre3, nombre4,  accionA, accionB;
    Double cuandoB, cuandoA;
    LinearLayout lyemail;
    Button btn_p3;
    String Nombre_var;

    Datos Senal01, Senal02, Senal03, Senal04, Analoga01, Analoga02;

    FirebaseDatabase database;
    DatabaseReference myRef, myRef2, myRef3, myRef4;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurar);
        DI1 = findViewById(R.id.textViewdi1); DI1_B = findViewById(R.id.textViewdi);
        DI2 = findViewById(R.id.textViewdi2); DI2_B = findViewById(R.id.textViewdi5);
        DI3 = findViewById(R.id.textViewdi3); DI3_B = findViewById(R.id.textViewdi6);
        DI4 = findViewById(R.id.textViewdi4); DI4_B = findViewById(R.id.textViewdi7);
        A01 = findViewById(R.id.textViewdi8); A01_B = findViewById(R.id.textViewdi9);
        A02 = findViewById(R.id.textViewdi10); A02_B = findViewById(R.id.textViewdi11);

        TextPasos = findViewById(R.id.textView2);
        TextPasos.setText("PASO 1:\nSeleccione el Puerto físico a configurar\n...para borrar, mantenga oprimido...");

        progressDialog = new ProgressDialog(this);

        Paso1 = findViewById(R.id.layoutpaso1); Paso1.setVisibility(View.VISIBLE);
        Paso2 = findViewById(R.id.layoutpaso2); Paso2.setVisibility(View.GONE);
        Paso3 = findViewById(R.id.listpaso3); nombrar = findViewById(R.id.editTextnombre); Paso3lay = findViewById(R.id.layoutpaso3);
        Paso4 = findViewById(R.id.layoutpaso4); lyemail = findViewById(R.id.layoutemail);
        email = findViewById(R.id.editTextTextEmailAddress2); contenido = findViewById(R.id.editTextTextMultiLine2);
        tNveces = findViewById(R.id.editTextNumber2); tNveces.setVisibility(View.GONE);
        kWh = findViewById(R.id.editTextkwh);
        btn_p3 = findViewById(R.id.button19);

        Paso5 = findViewById(R.id.layoutfinal);
        database = FirebaseDatabase.getInstance();
        progressDialog.setMessage("Obteniendo la actual configuracion\nPor favor espere...");
        progressDialog.show();

        // PENDIENTE PRE CARGAR LAS NVECES Y NVECESB Y OPCIONAL EL TIPO DE ACTIVO
        Senal01 = new Datos("00","",0.0,"","","",0.0,"","");
        Senal02 = new Datos("00","",0.0,"","","",0.0,"","");
        Senal03 = new Datos("00","",0.0,"","","",0.0,"","");
        Senal04 = new Datos("00","",0.0,"","","",0.0,"","");

        Analoga01 = new Datos("00","",0.0,"","","",0.0,"","");
        Analoga02 = new Datos("00","",0.0,"","","",0.0,"","");

        ANALOGICA("analog1");
        ANALOGICA2("analog2");
        ONLINE();

        // Preparando la info de la senal. 01:
        myRef = database.getReference("in1");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("nombre").getValue().toString().equals("00")){
                    String accion = snapshot.child("accion").getValue().toString();
                    String elemail , contenido;
                    if (accion.equals("Email")){
                        elemail = snapshot.child("email").getValue().toString();
                        contenido = snapshot.child("contenido").getValue().toString();
                    }else{ elemail = ""; contenido ="";}
                    String accionB, elemailB, contenidoB;
                    if (snapshot.child("accionB").exists()){
                        accionB = snapshot.child("accionB").getValue().toString();
                        if (accionB.equals("Email")){
                            elemailB = snapshot.child("emailB").getValue().toString();
                            contenidoB = snapshot.child("contenidoB").getValue().toString();
                        } else {elemailB = ""; contenidoB ="";}
                    } else {accionB= ""; elemailB = ""; contenidoB ="";}

                    Senal01.setNombre(snapshot.child("nombre").getValue().toString()); Senal01.setAccion(accion);
                    Senal01.setActivador(snapshot.child("cuando").getValue(Double.class)); Senal01.setEmails(elemail); Senal01.setContenido(contenido);
                    Senal01.setAccionB(accionB); Senal01.setActivadorB(snapshot.child("cuandoB").getValue(Double.class));
                    Senal01.setEmailB(elemailB); Senal01.setContenidoB(contenidoB);
                    //DI1.setWidth(230);
                    String str = (Senal01.getNombre() +"\nActivador: "+ (int)Senal01.getActivador() +"\nAccion: "+ Senal01.getAccion());
                    DI1.setText(str);
                    if (accionB.equals("")){
                        DI1_B.setVisibility(View.VISIBLE); DI1_B.setText("   +   "); DI1_B.setWidth(60);
                    }else {
                        //DI1_B.setWidth(230);
                        String str2 = ("2da funcion\nActivador: " + (int)Senal01.getActivadorB() + "\nAccion: " + Senal01.getAccionB());
                        DI1_B.setText(str2);
                    }
                } else {
                    DI1.setText("   +   "); DI1_B.setVisibility(View.INVISIBLE); DI1.setWidth(60);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error Base de datos: "+ error);
                progressDialog.dismiss();
            }
        });

        // Senal 02:
        myRef = database.getReference("in2");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //System.out.println("INFO ENTRADA 1 COMPLETA: \n" + snapshot);
                if (!snapshot.child("nombre").getValue().toString().equals("00")){
                    String accion = snapshot.child("accion").getValue().toString();
                    String elemail , contenido;
                    if (accion.equals("Email")){
                        elemail = snapshot.child("email").getValue().toString();
                        contenido = snapshot.child("contenido").getValue().toString();
                    }else{ elemail = ""; contenido ="";}
                    String accionB, elemailB, contenidoB;
                    if (snapshot.child("accionB").exists()){
                        accionB = snapshot.child("accionB").getValue().toString();
                        if (accionB.equals("Email")){
                            elemailB = snapshot.child("emailB").getValue().toString();
                            contenidoB = snapshot.child("contenidoB").getValue().toString();
                        } else {elemailB = ""; contenidoB ="";}
                    } else {accionB= ""; elemailB = ""; contenidoB ="";}

                    //Datos datos = new Datos();
                    Senal02.setNombre(snapshot.child("nombre").getValue().toString()); Senal02.setAccion(accion);
                    Senal02.setActivador(snapshot.child("cuando").getValue(Double.class)); Senal02.setEmails(elemail); Senal02.setContenido(contenido);
                    Senal02.setAccionB(accionB); Senal02.setActivadorB(snapshot.child("cuandoB").getValue(Double.class));
                    Senal02.setEmailB(elemailB); Senal02.setContenidoB(contenidoB);

                    DI2.setWidth(230);
                    String str = (Senal02.getNombre() +"\nActivador: "+ (int)Senal02.getActivador() +"\nAccion: "+ Senal02.getAccion());
                    DI2.setText(str);
                    if (accionB.equals("")){
                        DI2_B.setVisibility(View.VISIBLE); DI2_B.setText("   +   ");DI2_B.setWidth(60);
                    }else {
                        DI2_B.setWidth(230);
                        String str2 = ("2da funcion\nActivador: " + (int)Senal02.getActivadorB() + "\nAccion: " + Senal02.getAccionB());
                        DI2_B.setText(str2);
                    }
                } else {
                    DI2.setText("   +   "); DI2_B.setVisibility(View.INVISIBLE); DI2.setWidth(60);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error Base de datos: "+ error);
                progressDialog.dismiss();
            }
        });

        // Senal 03:
        myRef = database.getReference("in3");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //System.out.println("INFO ENTRADA 1 COMPLETA: \n" + snapshot);
                if (!snapshot.child("nombre").getValue().toString().equals("00")){
                    String accion = snapshot.child("accion").getValue().toString();
                    String elemail , contenido;
                    if (accion.equals("Email")){
                        elemail = snapshot.child("email").getValue().toString();
                        contenido = snapshot.child("contenido").getValue().toString();
                    }else{ elemail = ""; contenido ="";}
                    String accionB, elemailB, contenidoB;
                    if (snapshot.child("accionB").exists()){
                        accionB = snapshot.child("accionB").getValue().toString();
                        if (accionB.equals("Email")){
                            elemailB = snapshot.child("emailB").getValue().toString();
                            contenidoB = snapshot.child("contenidoB").getValue().toString();
                        } else {elemailB = ""; contenidoB ="";}
                    } else {accionB= ""; elemailB = ""; contenidoB ="";}

                    //Datos datos = new Datos();
                    Senal03.setNombre(snapshot.child("nombre").getValue().toString()); Senal03.setAccion(accion);
                    Senal03.setActivador(snapshot.child("cuando").getValue(Double.class)); Senal03.setEmails(elemail); Senal03.setContenido(contenido);
                    Senal03.setAccionB(accionB); Senal03.setActivadorB(snapshot.child("cuandoB").getValue(Double.class));
                    Senal03.setEmailB(elemailB); Senal03.setContenidoB(contenidoB);

                    DI3.setWidth(230);
                    String str = (Senal03.getNombre() +"\nActivador: "+ (int)Senal03.getActivador() +"\nAccion: "+ Senal03.getAccion());
                    DI3.setText(str);
                    if (accionB.equals("")){
                        DI3_B.setVisibility(View.VISIBLE); DI3_B.setText("   +   ");DI3_B.setWidth(60);
                    }else {
                        DI3_B.setWidth(230);
                        String str2 = ("2da funcion\nActivador: " + (int)Senal03.getActivadorB() + "\nAccion: " + Senal03.getAccionB());
                        DI3_B.setText(str2);
                    }
                } else {
                    DI3.setText("   +   "); DI3_B.setVisibility(View.INVISIBLE);DI3.setWidth(60);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error Base de datos: "+ error);
                progressDialog.dismiss();
            }
        });

        // Señal 04:
        myRef = database.getReference("in4");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //System.out.println("INFO ENTRADA 1 COMPLETA: \n" + snapshot);
                if (!snapshot.child("nombre").getValue().toString().equals("00")){
                    String accion = snapshot.child("accion").getValue().toString();
                    String elemail , contenido;
                    if (accion.equals("Email")){
                        elemail = snapshot.child("email").getValue().toString();
                        contenido = snapshot.child("contenido").getValue().toString();
                    }else{ elemail = ""; contenido ="";}
                    String accionB, elemailB, contenidoB;
                    if (snapshot.child("accionB").exists()){
                        accionB = snapshot.child("accionB").getValue().toString();
                        if (accionB.equals("Email")){
                            elemailB = snapshot.child("emailB").getValue().toString();
                            contenidoB = snapshot.child("contenidoB").getValue().toString();
                        } else {elemailB = ""; contenidoB ="";}
                    } else {accionB= ""; elemailB = ""; contenidoB ="";}

                    //Datos datos = new Datos();
                    Senal04.setNombre(snapshot.child("nombre").getValue().toString()); Senal04.setAccion(accion);
                    Senal04.setActivador(snapshot.child("cuando").getValue(Double.class)); Senal04.setEmails(elemail); Senal04.setContenido(contenido);
                    Senal04.setAccionB(accionB); Senal04.setActivadorB(snapshot.child("cuandoB").getValue(Double.class));
                    Senal04.setEmailB(elemailB); Senal04.setContenidoB(contenidoB);

                    DI4.setWidth(230);
                    String str = (Senal04.getNombre() +"\nActivador: "+ (int)Senal04.getActivador() +"\nAccion: "+ Senal04.getAccion());
                    DI4.setText(str);
                    if (accionB.equals("")){
                        DI4_B.setVisibility(View.VISIBLE); DI4_B.setText("   +   ");DI4_B.setWidth(60);
                    }else {
                        DI4_B.setWidth(230);
                        String str2 = ("2da funcion\nActivador: " + (int)Senal04.getActivadorB() + "\nAccion: " + Senal04.getAccionB());
                        DI4_B.setText(str2);
                    }
                } else {
                    DI4.setText("   +   "); DI4_B.setVisibility(View.INVISIBLE);DI4.setWidth(60);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error Base de datos: "+ error);
                progressDialog.dismiss();
            }
        });

        //PASO 3:
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Lista_Paso3, android.R.layout.simple_spinner_item);
        Paso3.setAdapter(adapter);
        Paso3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos_spin = position;
                if (position == 4){
                    tNveces.setVisibility(View.VISIBLE);
                } else {tNveces.setVisibility(View.GONE);}
                if (position != 0){
                    btn_p3.setVisibility(View.VISIBLE);
                } else {btn_p3.setVisibility(View.GONE);}

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        DI1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                INICIAR_BORRADO("1", Senal01);
                return false;
            }
        });
        DI2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                INICIAR_BORRADO("2", Senal02);
                return false;
            }
        });
        DI3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                INICIAR_BORRADO("3", Senal03);
                return false;
            }
        });
        DI4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                INICIAR_BORRADO("4", Senal04);
                return false;
            }
        });

        DI1_B.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                INICIAR_BORRADO_SIMPLE("1", Senal01);
                return false;
            }
        });
        DI2_B.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                INICIAR_BORRADO_SIMPLE("2", Senal02);
                return false;
            }
        });
        DI3_B.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                INICIAR_BORRADO_SIMPLE("3", Senal03);
                return false;
            }
        });
        DI4_B.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                INICIAR_BORRADO_SIMPLE("4", Senal04);
                return false;
            }
        });
        A01.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                BORRADO_SIMPLE_ANALOG("analog1", Analoga01);
                return false;
            }
        });
        A01_B.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                BORRADO_SIMPLE_ANALOG("analog1", Analoga01);
                return false;
            }
        });
        A02.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                BORRADO_SIMPLE_ANALOG("analog2", Analoga02);
                return false;
            }
        });
        A02_B.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                BORRADO_SIMPLE_ANALOG("analog2", Analoga02);
                return false;
            }
        });
    }

    private void ONLINE() {

        Snackbar.make(Paso1, "AGREGAR ALGO...", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();

        myRef = database.getReference("Status");
        myRef.setValue(true);

        myRef = database.getReference("OnlineState");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String estado = dataSnapshot.getValue().toString();
                    if (estado.equals("1")){
                        System.out.println("ESTA EN LINEA");
                        Snackbar.make(Paso1, "SISTEMA EN LINEA", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    } else {
                        System.out.println("NOOO ESTA EN LINEA");
                        Snackbar.make(Paso1, "OFF LINE. La configuracion se completara cuando vuelta a estar en linea", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("ERROR FIREBASE "+error);
            }
        });

    }

    private void INICIAR_BORRADO_SIMPLE(final String in, final Datos senal) {
        if (senal.getActivadorB()!=0.0){
            AlertDialog.Builder listo = new AlertDialog.Builder(Configurar.this);
            listo.setMessage("Esta seguro que desea eliminar.\nesta configuracion de la entrada #"+in+"?").setCancelable(true).
                    setPositiveButton("SI.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference ref5 = database.getReference("in"+in+"/cuandoB"); ref5.setValue(0.0);
                            DatabaseReference ref6 = database.getReference("in"+in+"/accionB"); ref6.setValue("");
                            DatabaseReference ref7 = database.getReference("in"+in+"/emailB"); ref7.setValue("");
                            DatabaseReference ref8 = database.getReference("in"+in+"/contenidoB"); ref8.setValue("");

                        }
                    });
            AlertDialog titulo = listo.create();
            titulo.setTitle("ELIMINAR");
            titulo.show();
        }
    }

    private void INICIAR_BORRADO(final String in, final Datos senal) {
        AlertDialog.Builder listo = new AlertDialog.Builder(Configurar.this);
        listo.setMessage("Esta seguro que desea eliminar.\nesta configuracion de la entrada #"+in+"?").setCancelable(true).
                setPositiveButton("SI.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (senal.getActivadorB()==0.0){
                            Datos datos = new Datos("00","",0.0,"","","",0.0,"","");

                            DatabaseReference ref  = database.getReference("in"+in+"/nombre"); ref.setValue(datos.getNombre());
                            DatabaseReference ref1 = database.getReference("in"+in+"/accion"); ref1.setValue(datos.getAccion());
                            DatabaseReference ref2 = database.getReference("in"+in+"/cuando"); ref2.setValue(datos.getActivador());
                            DatabaseReference ref3 = database.getReference("in"+in+"/email"); ref3.setValue(datos.getEmails());
                            DatabaseReference ref4 = database.getReference("in"+in+"/contenido"); ref4.setValue(datos.getContenido());
                            DatabaseReference ref5 = database.getReference("in"+in+"/cuandoB"); ref5.setValue(datos.getActivadorB());
                            DatabaseReference ref6 = database.getReference("in"+in+"/accionB"); ref6.setValue(datos.getAccionB());
                            DatabaseReference ref7 = database.getReference("in"+in+"/emailB"); ref7.setValue(datos.getEmailB());
                            DatabaseReference ref8 = database.getReference("in"+in+"/contenidoB"); ref8.setValue(datos.getContenidoB());
                            DatabaseReference ref9 = database.getReference("in"+in+"/kwh"); ref8.setValue(0.0);
                            BORRAR_TODO(in);
                        } else {
                            Datos datos = new Datos(senal.getNombre(),senal.getAccionB(),senal.getActivadorB(),
                                    senal.getEmailB(),senal.getContenidoB(),
                                    "",0.0,"","");

                            DatabaseReference ref1 = database.getReference("in"+in+"/accion"); ref1.setValue(datos.getAccion());
                            DatabaseReference ref2 = database.getReference("in"+in+"/cuando"); ref2.setValue(datos.getActivador());
                            DatabaseReference ref3 = database.getReference("in"+in+"/email"); ref3.setValue(datos.getEmails());
                            DatabaseReference ref4 = database.getReference("in"+in+"/contenido"); ref4.setValue(datos.getContenido());
                            DatabaseReference ref5 = database.getReference("in"+in+"/cuandoB"); ref5.setValue(datos.getActivadorB());
                            DatabaseReference ref6 = database.getReference("in"+in+"/accionB"); ref6.setValue(datos.getAccionB());
                            DatabaseReference ref7 = database.getReference("in"+in+"/emailB"); ref7.setValue(datos.getEmailB());
                            DatabaseReference ref8 = database.getReference("in"+in+"/contenidoB"); ref8.setValue(datos.getContenidoB());
                        }
                    }
                });
        AlertDialog titulo = listo.create();
        titulo.setTitle("ELIMINAR");
        titulo.show();
    }

    private void BORRAR_TODO(final String in) {
        AlertDialog.Builder listo = new AlertDialog.Builder(Configurar.this);
        listo.setMessage("Desea tambien borrar todos los datos guardados de esta señal?\nRESETAR TODA LA INFORMACION:").setCancelable(false).
                setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference ref1 = database.getReference("in"+in+"/cambio");
                        ref1.removeValue();
                        Toast.makeText(Configurar.this, "SE HA BORRADO LA INFORMACION DE ESTA SENAL DE LA BASE DE DATOS", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Configurar.this, "EL HISTORIAL DE DATOS SE MANTIENE", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog titulo = listo.create();
        titulo.setTitle("BORRAR TODOS LOS DATOS");
        titulo.show();
    }

    private void BORRADO_SIMPLE_ANALOG(final String in, final Datos senal) {
        AlertDialog.Builder listo = new AlertDialog.Builder(Configurar.this);
        listo.setMessage("Esta seguro que desea eliminar la configuracion de:\n"+senal.getNombre()+"?").setCancelable(true).
                setPositiveButton("SI.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Datos datos = new Datos("00","",0.0,"","",
                                "",0.0,"","");
                        DatabaseReference ref  = database.getReference(in+"/nombre"); ref.setValue(datos.getNombre());
                        DatabaseReference ref1 = database.getReference(in+"/accion"); ref1.setValue(datos.getAccion());
                        DatabaseReference ref2 = database.getReference(in+"/cuando"); ref2.setValue(datos.getActivador());
                        DatabaseReference ref3 = database.getReference(in+"/email"); ref3.setValue(datos.getEmails());
                        DatabaseReference ref4 = database.getReference(in+"/contenido"); ref4.setValue(datos.getContenido());
                        DatabaseReference ref5 = database.getReference(in+"/cuandoB"); ref5.setValue(datos.getActivadorB());
                        DatabaseReference ref6 = database.getReference(in+"/accionB"); ref6.setValue(datos.getAccionB());
                        DatabaseReference ref7 = database.getReference(in+"/emailB"); ref7.setValue(datos.getEmailB());
                        DatabaseReference ref8 = database.getReference(in+"/contenidoB"); ref8.setValue(datos.getContenidoB());
                        if (in.equals("analog1")){
                            A01.setText("+");A01_B.setText("");
                        } else {
                            A02.setText("+");A02_B.setText("");
                        }
                        BORRAR_TODO_ANALOG(in);

                    }
                });
        AlertDialog titulo = listo.create();
        titulo.setTitle("ELIMINAR");
        titulo.show();
    }
    private void BORRAR_TODO_ANALOG(final String in) {
        AlertDialog.Builder listo = new AlertDialog.Builder(Configurar.this);
        listo.setMessage("Desea tambien borrar todos los datos guardados de esta señal?\nRESETAR TODA LA INFORMACION:").setCancelable(false).
                setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference ref1 = database.getReference(in+"/historico");
                        ref1.removeValue();
                        //Toast.makeText(Configurar.this, "SE HA BORRADO LA INFORMACION DE ESTA SENAL DE LA BASE DE DATOS", Toast.LENGTH_LONG).show();
                        Toast toast = Toast.makeText(Configurar.this, "SE HA BORRADO LA INFORMACION DE ESTA SEÑAL DE LA BASE DE DATOS", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Configurar.this, "EL HISTORIAL DE DATOS SE MANTIENE", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog titulo = listo.create();
        titulo.setTitle("BORRAR TODOS LOS DATOS");
        titulo.show();
    }

    public void Digital1(View view) {
        String mensaje;
        if (Senal01.getNombre().equals("00")){
            mensaje = "A continuacion podra configurar una accion para la señal #1.\nSiga los pasos indicados arriba";
        } else {
            //mensaje = "Esta seguro que desea modificar\nlos parametros de la entrada #1?";
            String activador = QUE_ACCION(Senal01.getActivador());
            String accion = Senal01.getAccion();
            if (accion.equals("Email")){
                accion = "Email a: "+Senal01.getEmails();
            }
            mensaje = "Nombre: "+Senal01.getNombre()+"\nAccion: "+activador+"\nFuncion: "+accion;
        }

        AlertDialog.Builder listo = new AlertDialog.Builder(Configurar.this);
        listo.setMessage(mensaje).setCancelable(true).
                setPositiveButton("Modificar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Paso1.setVisibility(View.GONE);
                        Paso2.setVisibility(View.VISIBLE); AB = "";
                        puerto = "1"; TextPasos.setText("PASO 2: Que tipo de elemento tiene conectado en el PUERTO 1:");
                        nombrar.setText(Senal01.getNombre());
                        pos_spin = (int) Senal01.getActivador();
                        System.out.println("POSICION DEL SPIN:  " + pos_spin + "Activador en Datos: "+ Senal01.getActivador());
                        Paso3.setSelection(pos_spin);
                    }
                });
        AlertDialog titulo = listo.create();
        titulo.setTitle("ENTRADA DIGITAL 1");
        titulo.show();
    }

    public void Digital2(View view) {
        String mensaje;
        if (Senal02.getNombre().equals("00")){
            mensaje = "A continuacion podra configurar una accion para la señal #2.\nSiga los pasos indicados arriba.";
        } else {
            String activador = QUE_ACCION(Senal02.getActivador());
            String accion = Senal02.getAccion();
            if (accion.equals("Email")){
                accion = "Email a: "+Senal02.getEmails();
            }
            mensaje = "Nombre: "+Senal02.getNombre()+"\nAccion: "+activador+"\nFuncion: "+accion;
        }

        AlertDialog.Builder listo = new AlertDialog.Builder(Configurar.this);
        listo.setMessage(mensaje).setCancelable(true).
                setPositiveButton("MODIFICAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Paso1.setVisibility(View.GONE);
                        Paso2.setVisibility(View.VISIBLE); AB = "";
                        puerto = "2"; TextPasos.setText("PASO 2: Que tipo de elemento tiene conectado en el PUERTO 2:");
                        nombrar.setText(Senal02.getNombre());
                        pos_spin = (int) Senal02.getActivador();
                        System.out.println("POSICION DEL SPIN:  " + pos_spin + "Activador en Datos: "+ Senal02.getActivador());
                        Paso3.setSelection(pos_spin);
                    }
                });
        AlertDialog titulo = listo.create();
        titulo.setTitle("ENTRADA DIGITAL 2");
        titulo.show();
    }
    public void Digital3(View view) {
        String mensaje;
        if (Senal03.getNombre().equals("00")){
            mensaje = "A continuacion podra configurar una accion para la señal #3.\nSiga los pasos indicados arriba.";
        } else {
            String activador = QUE_ACCION(Senal03.getActivador());
            String accion = Senal03.getAccion();
            if (accion.equals("Email")){
                accion = "Email a: "+Senal03.getEmails();
            }
            mensaje = "Nombre: "+Senal03.getNombre()+"\nAccion: "+activador+"\nFuncion: "+accion;
        }

        AlertDialog.Builder listo = new AlertDialog.Builder(Configurar.this);
        listo.setMessage(mensaje).setCancelable(true).
                setPositiveButton("MODIFICAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Paso1.setVisibility(View.GONE);
                        Paso2.setVisibility(View.VISIBLE); AB = "";
                        puerto = "3"; TextPasos.setText("PASO 2: Que tipo de elemento tiene conectado en el PUERTO 3:");
                        nombrar.setText(Senal03.getNombre());
                        pos_spin = (int) Senal03.getActivador();
                        System.out.println("POSICION DEL SPIN:  " + pos_spin + "Activador en Datos: "+ Senal03.getActivador());
                        Paso3.setSelection(pos_spin);
                    }
                });
        AlertDialog titulo = listo.create();
        titulo.setTitle("ENTRADA DIGITAL 3");
        titulo.show();
    }
    public void Digital4(View view) {
        String mensaje;
        if (Senal04.getNombre().equals("00")){
            mensaje = "A continuacion podra configurar una accion para la señal #4.\nSiga los pasos indicados arriba.";
        } else {
            String activador = QUE_ACCION(Senal04.getActivador());
            String accion = Senal04.getAccion();
            if (accion.equals("Email")){
                accion = "Email a: "+Senal04.getEmails();
            }
            mensaje = "Nombre: "+Senal04.getNombre()+"\nAccion: "+activador+"\nFuncion: "+accion;
        }

        AlertDialog.Builder listo = new AlertDialog.Builder(Configurar.this);
        listo.setMessage(mensaje).setCancelable(true).
                setPositiveButton("MODIFICAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Paso1.setVisibility(View.GONE);
                        Paso2.setVisibility(View.VISIBLE); AB = "";
                        puerto = "4"; TextPasos.setText("PASO 2: Que tipo de elemento tiene conectado en el PUERTO 4:");
                        nombrar.setText(Senal04.getNombre());
                        pos_spin = (int) Senal04.getActivador();
                        System.out.println("POSICION DEL SPIN:  " + pos_spin + "Activador en Datos: "+ Senal04.getActivador());
                        Paso3.setSelection(pos_spin);
                    }
                });
        AlertDialog titulo = listo.create();
        titulo.setTitle("ENTRADA DIGITAL 4");
        titulo.show();
    }

    public void Digital1B(View view) {
        String mensaje;
        if (Senal01.getAccionB().equals("")){
            mensaje = "Desea agregar una segunda funcion a:\n"+Senal01.getNombre();
        } else {
            String activador = QUE_ACCION(Senal01.getActivadorB());
            String accion = Senal01.getAccionB();
            if (accion.equals("Email")){
                accion = "Email a: "+Senal01.getEmailB();
            }
            mensaje = "Nombre: "+Senal01.getNombre()+"\nAccion: "+activador+"\nFuncion: "+accion;
        }
        AlertDialog.Builder listo = new AlertDialog.Builder(Configurar.this);
        listo.setMessage(mensaje).setCancelable(true).
                setPositiveButton("MODIFICAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Paso1.setVisibility(View.GONE);
                        Paso3.setVisibility(View.VISIBLE); Paso3lay.setVisibility(View.VISIBLE);
                        puerto = "1"; AB = "B"; TextPasos.setText("PASO 3-B: Indicada el desencadenador de la nueva accion: ");
                        nombrar.setText(Senal01.getNombre()); pos_spin = (int) Senal01.getActivadorB();
                        System.out.println("POSICION DEL SPIN:  " + pos_spin + "Activador en Datos: "+ Senal01.getActivadorB());
                        Paso3.setSelection(pos_spin);
                    }
                });
        AlertDialog titulo = listo.create();
        titulo.setTitle("2DA FUNCION, IN 01");
        titulo.show();
    }
    public void Digital2B(View view) {
        String mensaje;
        if (Senal02.getAccionB().equals("")){
            mensaje = "Desea agregar una segunda funcion a:\n"+Senal02.getNombre();
        } else {
            String activador = QUE_ACCION(Senal02.getActivadorB());
            String accion = Senal02.getAccionB();
            if (accion.equals("Email")){
                accion = "Email a: "+Senal02.getEmailB();
            }
            mensaje = "Nombre: "+Senal02.getNombre()+"\nAccion: "+activador+"\nFuncion: "+accion;
        }
        AlertDialog.Builder listo = new AlertDialog.Builder(Configurar.this);
        listo.setMessage(mensaje).setCancelable(true).
                setPositiveButton("MODIFICAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Paso1.setVisibility(View.GONE);
                        Paso3.setVisibility(View.VISIBLE); Paso3lay.setVisibility(View.VISIBLE);
                        puerto = "2"; AB = "B"; TextPasos.setText("PASO 3-B: Indicada el desencadenador de la nueva accion: ");
                        nombrar.setText(Senal02.getNombre()); pos_spin = (int) Senal02.getActivadorB();
                        System.out.println("POSICION DEL SPIN:  " + pos_spin + "Activador en Datos: "+ Senal02.getActivadorB());
                        Paso3.setSelection(pos_spin);
                    }
                });
        AlertDialog titulo = listo.create();
        titulo.setTitle("2DA FUNCION, IN 02");
        titulo.show();
    }
    public void Digital3B(View view) {
        String mensaje;
        if (Senal02.getAccionB().equals("")){
            mensaje = "Desea agregar una segunda funcion a:\n"+Senal03.getNombre();
        } else {
            String activador = QUE_ACCION(Senal03.getActivadorB());
            String accion = Senal03.getAccionB();
            if (accion.equals("Email")){
                accion = "Email a: "+Senal03.getEmailB();
            }
            mensaje = "Nombre: "+Senal03.getNombre()+"\nAccion: "+activador+"\nFuncion: "+accion;
        }
        AlertDialog.Builder listo = new AlertDialog.Builder(Configurar.this);
        listo.setMessage(mensaje).setCancelable(true).
                setPositiveButton("MODIFICAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Paso1.setVisibility(View.GONE);
                        Paso3.setVisibility(View.VISIBLE); Paso3lay.setVisibility(View.VISIBLE);
                        puerto = "3"; AB = "B"; TextPasos.setText("PASO 3-B: Indicada el desencadenador de la nueva accion: ");
                        nombrar.setText(Senal03.getNombre()); pos_spin = (int) Senal03.getActivadorB();
                        System.out.println("POSICION DEL SPIN:  " + pos_spin + "Activador en Datos: "+ Senal03.getActivadorB());
                        Paso3.setSelection(pos_spin);
                    }
                });
        AlertDialog titulo = listo.create();
        titulo.setTitle("2DA FUNCION, IN 03");
        titulo.show();
    }
    public void Digital4B(View view) {
        String mensaje;
        if (Senal04.getAccionB().equals("")){
            mensaje = "Desea agregar una segunda funcion a:\n"+Senal04.getNombre();
        } else {
            String activador = QUE_ACCION(Senal04.getActivadorB());
            String accion = Senal04.getAccionB();
            if (accion.equals("Email")){
                accion = "Email a: "+Senal04.getEmailB();
            }
            mensaje = "Nombre: "+Senal04.getNombre()+"\nAccion: "+activador+"\nFuncion: "+accion;
        }
        AlertDialog.Builder listo = new AlertDialog.Builder(Configurar.this);
        listo.setMessage(mensaje).setCancelable(true).
                setPositiveButton("MODIFICAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Paso1.setVisibility(View.GONE);
                        Paso3.setVisibility(View.VISIBLE); Paso3lay.setVisibility(View.VISIBLE);
                        puerto = "4"; AB = "B"; TextPasos.setText("PASO 3-B: Indicada el desencadenador de la nueva accion: ");
                        nombrar.setText(Senal04.getNombre()); pos_spin = (int) Senal04.getActivadorB();
                        System.out.println("POSICION DEL SPIN:  " + pos_spin + "Activador en Datos: "+ Senal03.getActivadorB());
                    }
                });
        AlertDialog titulo = listo.create();
        titulo.setTitle("2DA FUNCION, IN 04");
        titulo.show();
    }

    private String QUE_ACCION(double activador) {
        String accion="";
        if (activador==2){
            accion = "Al pasar a ON";
        }
        if (activador==3){
            accion = "Al pasar a OFF";
        }
        if (activador==1){
            accion = "Al cambiar de estado";
        }
        if (activador==4){
            accion = "Luego de N veces";
        }

        return accion;
    }

    public void Electroval(View view) {
        Paso3.setVisibility(View.VISIBLE); Paso3lay.setVisibility(View.VISIBLE);
        Paso2.setVisibility(View.GONE); TextPasos.setText("PASO 3: Definele un nombre a este INTERRUPTOR" + "\ne indique el desencadenador de la accion final:");
        //nombrar.setText("electrovalvula");
    }
    public void Reed(View view) {
        Paso3.setVisibility(View.VISIBLE); Paso3lay.setVisibility(View.VISIBLE);
        Paso2.setVisibility(View.GONE); TextPasos.setText("PASO 3: Definele un nombre a este PRESOSTATO" + "\ne indique el desencadenador de la accion final:");
        //nombrar.setText("reed");
    }
    public void Presostato(View view) {
        Paso3.setVisibility(View.VISIBLE); Paso3lay.setVisibility(View.VISIBLE);
        Paso2.setVisibility(View.GONE); TextPasos.setText("PASO 3: Definele un nombre a este SENSOR" + "\ne indique el desencadenador de la accion final:");
        //nombrar.setText("presostato");
    }
    public void Valvetop(View view) {
        Paso3.setVisibility(View.VISIBLE); Paso3lay.setVisibility(View.VISIBLE);
        Paso2.setVisibility(View.GONE); TextPasos.setText("PASO 3: Definele un nombre a este ACTUADOR" + "\ne indique el desencadenador de la accion final:");
        //nombrar.setText("valve top");
    }
    public void GOswitch(View view) {
        Paso3.setVisibility(View.VISIBLE); Paso3lay.setVisibility(View.VISIBLE);
        Paso2.setVisibility(View.GONE); TextPasos.setText("PASO 3: Definele un nombre a este TERMOSTATO" + "\ne indique el desencadenador de la accion final:");
        //nombrar.setText("go swtich");
    }
    public void SolenoidValve(View view) {
        Paso3.setVisibility(View.VISIBLE); Paso3lay.setVisibility(View.VISIBLE);
        Paso2.setVisibility(View.GONE); TextPasos.setText("PASO 3: Definele un nombre a este ACTIVO" + "\ne indique el desencadenador de la accion final:");
        //nombrar.setText("val solenoide");
    }
    public void Otrotipo(View view) {
        Paso3.setVisibility(View.VISIBLE); Paso3lay.setVisibility(View.VISIBLE);
        Paso2.setVisibility(View.GONE); TextPasos.setText("PASO 3: Definele un nombre a este ACTIVO" + "\ne indique el desencadenador de la accion final:");
        //nombrar.setText("otro");
    }
    public void Notificacion(View view) {
        TextPasos.setText("DEFINE LA NOTIFICACION: Digite el email y redacte el contenido del correo:");
        String path = "in"+puerto+"/accion";
        switch (puerto){
            case "1":
                if (AB.equals("B")){
                    email.setText(Senal01.getEmailB()); contenido.setText(Senal01.getContenidoB());
                }else {email.setText(Senal01.getEmails()); contenido.setText(Senal01.getContenido());}
                break;
            case "2":
                if (AB.equals("B")){
                    email.setText(Senal02.getEmailB()); contenido.setText(Senal02.getContenidoB());
                }else {email.setText(Senal02.getEmails()); contenido.setText(Senal02.getContenido());}
                break;
            case "3":
                if (AB.equals("B")){
                    email.setText(Senal03.getEmailB()); contenido.setText(Senal03.getContenidoB());
                }else {email.setText(Senal03.getEmails()); contenido.setText(Senal03.getContenido());}
                break;
            case "4":
                if (AB.equals("B")){
                    email.setText(Senal04.getEmailB()); contenido.setText(Senal04.getContenidoB());
                }else {email.setText(Senal04.getEmails()); contenido.setText(Senal04.getContenido());}
                break;
        }
        Paso4.setVisibility(View.GONE); lyemail.setVisibility(View.VISIBLE);
    }

    public void elemail(View view ) {
        String mail = email.getText().toString();
        if (TextUtils.isEmpty(mail)){
            Toast.makeText(Configurar.this, "Email Incorrecto, Verifique",Toast.LENGTH_LONG).show();
            return;
        }
        if (contenido.getText().toString().isEmpty()){
            Toast.makeText(Configurar.this, "El contenido del E-mail no puede estar vacio",Toast.LENGTH_LONG).show();
            return;
        }
        String path;
        if (AB.equals("B")){
            String path0 = "in"+puerto+"/accionB";    myRef4 = database.getReference(path0);
            path = "in" + puerto + "/emailB";
            myRef2 = database.getReference(path);     myRef2.setValue(mail);
            String path2 = "in"+puerto+"/contenidoB"; myRef3 = database.getReference(path2);
        }else{
            String path0 = "in"+puerto+"/accion";     myRef4 = database.getReference(path0);
            path = "in" + puerto + "/email";
            myRef2 = database.getReference(path);     myRef2.setValue(mail);
            String path2 = "in"+puerto+"/contenido";  myRef3 = database.getReference(path2);
        }
        myRef4.setValue("Email");
        myRef3.setValue(contenido.getText().toString());
        Paso5.setVisibility(View.VISIBLE);
        lyemail.setVisibility(View.GONE); TextPasos.setText("LISTO: Puedes seguir configurando los demas activos o regresar al menu principal:");
    }

    public void Historial(View view) {
        Paso5.setVisibility(View.VISIBLE);
        Paso4.setVisibility(View.GONE);TextPasos.setText("LISTO: Puedes seguir configurando los demas activos o regresar al menu principal:");

        String path = "in"+puerto+"/accion"+AB;
        myRef = database.getReference(path);
        myRef.setValue("Historial");
    }
    public void Act_Alarma(View view) {
        Paso5.setVisibility(View.VISIBLE);
        Paso4.setVisibility(View.GONE);TextPasos.setText("LISTO: Puedes seguir configurando los demas activos o regresar al menu principal:");

        String path = "in"+puerto+"/accion"+AB;
        myRef = database.getReference(path);
        myRef.setValue("Alarma");
    }
    public void Act_Electroval(View view) {
        Paso5.setVisibility(View.VISIBLE);
        Paso4.setVisibility(View.GONE);TextPasos.setText("LISTO: Puedes seguir configurando los demas activos o regresar al menu principal:");

        String path = "in"+puerto+"/accion"+AB;
        myRef = database.getReference(path);
        myRef.setValue("Electrovalvula");
    }
    public void Seguir(View view) {
        Paso5.setVisibility(View.GONE); Paso1.setVisibility(View.VISIBLE);
        TextPasos.setText("PASO 1:\nSelecciona el Puerto físico a configurar");

    }
    public void FIN(View view) {
        startActivity(new Intent(Configurar.this, MenuPpal.class));
    }


    public void Alpaso4(View view) {
        Nombre_var = nombrar.getText().toString();
        String path = "in"+puerto+"/nombre"; String path2 = "in"+puerto+"/cuando"+AB;
        String path3 = "in"+puerto+"/nveces"+AB; String path4 = "in"+puerto+"/kwh";
        myRef = database.getReference(path);
        myRef.setValue(Nombre_var);
        myRef = database.getReference(path2);
        myRef.setValue(pos_spin);
        Paso3.setVisibility(View.GONE); Paso3lay.setVisibility(View.GONE);
        Paso4.setVisibility(View.VISIBLE);
        TextPasos.setText("PASO 4: Define la accion final:");

        //  SUBIR A LA NUBE LAS NVECES :
        if (pos_spin==4){
            Nveces  = Integer.parseInt(tNveces.getText().toString());
            myRef = database.getReference(path3);
            myRef.setValue(Nveces);
        }else {
            Nveces  = 0;
            myRef = database.getReference(path3);
            myRef.setValue(Nveces);
        }
        //  SUBIR A LA NUBE LOS kWh:
        kWh_double  = Double.parseDouble(kWh.getText().toString());
        if (!(kWh_double > 0)) {
            kWh_double = 0;
        }
        myRef = database.getReference(path4);
        myRef.setValue(kWh_double);

    }

    private void ANALOGICA(String analog) {
        myRef = database.getReference(analog);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("nombre").getValue().toString().equals("00")){
                    String accion = snapshot.child("accion").getValue().toString();
                    String elemail , contenido;
                    if (accion.equals("Email")){
                        elemail = snapshot.child("email").getValue().toString();
                        contenido = snapshot.child("contenido").getValue().toString();
                    }else{ elemail = ""; contenido ="";}
                    String accionB, elemailB, contenidoB;
                    if (snapshot.child("accionB").exists()){
                        accionB = snapshot.child("accionB").getValue().toString();
                        if (accionB.equals("Email")){
                            elemailB = snapshot.child("emailB").getValue().toString();
                            contenidoB = snapshot.child("contenidoB").getValue().toString();
                        } else {elemailB = ""; contenidoB ="";}
                    } else {accionB= ""; elemailB = ""; contenidoB ="";}

                    Analoga01.setNombre(snapshot.child("nombre").getValue().toString()); Analoga01.setAccion(accion);
                    Analoga01.setActivador(snapshot.child("cuando").getValue(Double.class)); Analoga01.setEmails(elemail);
                    Analoga01.setContenido(contenido);
                    Analoga01.setAccionB(accionB); Analoga01.setActivadorB(snapshot.child("cuandoB").getValue(Double.class));
                    Analoga01.setEmailB(elemailB); Analoga01.setContenidoB(contenidoB);

                    String str = (Analoga01.getNombre() +"\nMIN: "+ (int)Analoga01.getActivador() +"%\nAccion: "+ Analoga01.getAccion());
                    A01.setText(str);
                    if (accionB.equals("")){
                        A01_B.setVisibility(View.VISIBLE); A01_B.setText("   +   "); A01_B.setWidth(60);
                    }else {
                        A01_B.setWidth(230);
                        String str2 = ("MAX: " + (int)Analoga01.getActivadorB() + "%\nAccion: " + Analoga01.getAccionB());
                        A01_B.setText(str2);
                    }
                } else {
                    A01.setText("   +   "); A01_B.setVisibility(View.INVISIBLE); A01.setWidth(60);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error Base de datos: "+ error);
                progressDialog.dismiss();
            }
        });

    }

    private void ANALOGICA2(String analog) {
        myRef = database.getReference(analog);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("nombre").getValue().toString().equals("00")){
                    String accion = snapshot.child("accion").getValue().toString();
                    String elemail , contenido;
                    if (accion.equals("Email")){
                        elemail = snapshot.child("email").getValue().toString();
                        contenido = snapshot.child("contenido").getValue().toString();
                    }else{ elemail = ""; contenido ="";}
                    String accionB, elemailB, contenidoB;
                    if (snapshot.child("accionB").exists()){
                        accionB = snapshot.child("accionB").getValue().toString();
                        if (accionB.equals("Email")){
                            elemailB = snapshot.child("emailB").getValue().toString();
                            contenidoB = snapshot.child("contenidoB").getValue().toString();
                        } else {elemailB = ""; contenidoB ="";}
                    } else {accionB= ""; elemailB = ""; contenidoB ="";}

                    Analoga02.setNombre(snapshot.child("nombre").getValue().toString()); Analoga02.setAccion(accion);
                    Analoga02.setActivador(snapshot.child("cuando").getValue(Double.class)); Analoga02.setEmails(elemail);
                    Analoga02.setContenido(contenido);
                    Analoga02.setAccionB(accionB); Analoga02.setActivadorB(snapshot.child("cuandoB").getValue(Double.class));
                    Analoga02.setEmailB(elemailB); Analoga02.setContenidoB(contenidoB);

                    String str = (Analoga02.getNombre() +"\nMIN: "+ (int)Analoga02.getActivador() +"%\nAccion: "+ Analoga02.getAccion());
                    A02.setText(str);
                    if (accionB.equals("")){
                        A02_B.setVisibility(View.VISIBLE); A02_B.setText("   +   "); A02_B.setWidth(60);
                    }else {
                        A02_B.setWidth(230);
                        String str2 = ("MAX: " + (int)Analoga02.getActivadorB() + "%\nAccion: " + Analoga02.getAccionB());
                        A02_B.setText(str2);
                    }
                } else {
                    A02.setText("   +   "); A02_B.setVisibility(View.INVISIBLE); A02.setWidth(60);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error Base de datos: "+ error);
                progressDialog.dismiss();
            }
        });

    }

    public void Analogica1(View view) {
        Intent intent = new Intent(Configurar.this, Analogicas.class);
        intent.putExtra("path", "analog1");
        startActivity(intent);
    }

    public void Analogica2(View view) {
        Intent intent = new Intent(Configurar.this, Analogicas.class);
        intent.putExtra("path", "analog2");
        startActivity(intent);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK){
            myRef = database.getReference("Status");
            myRef.setValue(false);
            myRef = database.getReference("OnlineState");
            myRef.setValue(false);
            startActivity(new Intent(Configurar.this, MenuPpal.class));
        }
        return super.onKeyDown(keyCode, event);
    }
}