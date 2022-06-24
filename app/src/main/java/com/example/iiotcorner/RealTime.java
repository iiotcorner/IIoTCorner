package com.example.iiotcorner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RealTime extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef, Ref2, Ref3, Ref4, newRef;
    ProgressDialog progressDialog, progressDialog2;

    TextView Senal1, Senal2, Senal3, Senal4, Name1, Name2, Name3, Name4;
    TextView Alarma, Evalvula, EstadoA1, EstadoA2, Online;
    String Relativo_online, Relativo1="...", Relativo2="...", Relativo3="...", Relativo4="...";

    ArrayList<String> Tiempos = new ArrayList<>();
    List<String> Relativos = new ArrayList<>();
    boolean Alarm, Evalve;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime);

        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog2 = new ProgressDialog(this);


        NOMBRE_ESTADO("1"); NOMBRE_ESTADO("2"); NOMBRE_ESTADO("3"); NOMBRE_ESTADO("4");
        CAMBIOS ("1"); CAMBIOS ("2"); CAMBIOS ("3"); CAMBIOS ("4");
        Relativos.add("...Sin datos..."); Relativos.add("...Sin datos..."); Relativos.add("...Sin datos..."); Relativos.add("...Sin datos...");

        Senal1 = findViewById(R.id.textViewdi1); Senal2 = findViewById(R.id.textViewdi2);
        Senal3 = findViewById(R.id.textViewdi3); Senal4 = findViewById(R.id.textViewdi4);
        Name1 = findViewById(R.id.textViewdi); Name2 = findViewById(R.id.textViewdi5);
        Name3 = findViewById(R.id.textViewdi6); Name4 = findViewById(R.id.textViewdi7);

        Alarma = findViewById(R.id.textViewalarm); Evalvula = findViewById(R.id.textViewvalve);
        EstadoA1 = findViewById(R.id.textViewa1); EstadoA2 = findViewById(R.id.textViewa2);
        Online = findViewById(R.id.textViewonline);
        ALARMA(); EVALVULA();

        newRef = database.getReference("in1/cambio");
        progressDialog2.setMessage("Conectandose \nPor favor espere...");
        progressDialog2.show();
        newRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                if (dataSnapshot.exists()){
                    Long newPost = dataSnapshot.getValue(Long.class);
                    System.out.println("lo que CAMBIO:"+ dataSnapshot);
                    Relativo1 = DateUtils.getRelativeTimeSpanString(newPost,
                            System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_ALL).toString();
                    System.out.println("ULTIMO CAMBIO:  " + Relativo1);
                }
                progressDialog2.dismiss();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        myRef = database.getReference("analog1/estado");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long porcentaje = (long) snapshot.getValue();
                    String texto = porcentaje + " %";
                    EstadoA1.setText(texto);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        myRef = database.getReference("analog2/estado");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long porcentaje = (long) snapshot.getValue();
                    String texto = porcentaje + " %";
                    EstadoA2.setText(texto);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        myRef = database.getReference("Status");
        myRef.setValue(true);

        myRef = database.getReference("OnlineState2");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long tiempo = (long) snapshot.getValue();
                    Relativo_online = DateUtils.getRelativeTimeSpanString(tiempo,
                            System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_ALL).toString();
                    System.out.println("APAGADO DESDE: "+ Relativo_online);
                    Online.setText("EL SISTEMA NO ESTA EN LINEA\nDesde: "+Relativo_online);
                } else {
                    Relativo_online = "sin registro de tiempo";
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        myRef = database.getReference("OnlineState");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String estado = dataSnapshot.getValue().toString();
                    if (estado.equals("1")){
                        System.out.println("ESTA EN LINEA");
                        Online.setText("EL SISTEMA ESTA EN LINEA");
                        Online.setBackgroundResource(R.color.colorPrimary);
                    } else {
                        System.out.println("NOOO ESTA EN LINEA");
                        Online.setText("EL SISTEMA NO ESTA EN LINEA\nDesde: "+Relativo_online);
                        Online.setBackgroundResource(R.color.colorAccent);
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

    private void ALARMA() {
        myRef = database.getReference("alarma"); Ref4 = database.getReference("reset_alarma");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long estado = (long) snapshot.getValue();
                    int alarma = (int) estado;
                    switch (alarma){
                        case 1:
                            Alarma.setText("ALARMA ACTIVADA\nclick aqui para desactivarla remotamente");
                            Alarma.setBackgroundResource(R.color.activado);
                            Alarm = true;
                            break;
                        case 0:
                            Alarma.setText("");
                            Alarma.setBackgroundResource(R.color.release);
                            Ref4.setValue(false);
                            Alarm = false;
                            break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error Firabase: " + error);
            }
        });
    }
    private void EVALVULA() {
        myRef = database.getReference("solenoide"); Ref3 = database.getReference("reset_solenoide");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long estado = (long) snapshot.getValue();
                    int solenoide = (int) estado;
                    switch (solenoide){
                        case 1:
                            Evalvula.setText("SOLENOIDE ACTIVA\nclick aqui para desactivarla remotamente");
                            Evalvula.setBackgroundResource(R.color.activado);
                            Evalve = true;
                            break;
                        case 0:
                            Evalvula.setText("");
                            Evalvula.setBackgroundResource(R.color.release);
                            Ref3.setValue(false);
                            Evalve = false;
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error Firabase: " + error);
            }
        });
    }


    private void CAMBIOS (final String in) {
        final int i = Integer.parseInt(in)-1;
        progressDialog.setMessage("Conectandose al equipo\nPor favor espere...");
        progressDialog.show();
        String path = ("in"+in+"/cambio"); myRef = database.getReference(path);
        final ArrayList<String> Tiempo = new ArrayList<>();
        Tiempos.clear();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds: snapshot.getChildren()){
                        Tiempo.add(ds.getValue().toString());
                        if (Tiempo.size()>=snapshot.getChildrenCount()){
                            Collections.sort(Tiempo);
                            String relativo = DateUtils.getRelativeTimeSpanString(Long.parseLong(Tiempo.get(Tiempo.size()-1)),
                                    System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_ALL).toString();
                            Relativos.set(i, relativo);
                            switch (in){
                                case "1":
                                    Relativo1 = relativo; System.out.println("TIEMPO RELATIVO 1: " + Relativo1);
                                    break;
                                case "2":
                                    Relativo2 = relativo; System.out.println("TIEMPO RELATIVO 2: " + Relativo2);
                                    break;
                                case "3":
                                    Relativo3 = relativo;System.out.println("TIEMPO RELATIVO 3: " + Relativo3);
                                    break;
                                case "4":
                                    Relativo4 = relativo;System.out.println("TIEMPO RELATIVO 4: " + Relativo4);
                                    break;
                            }
                        }
                    }
                } else {
                    Relativos.set(i, "Sin datos");
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error Firabase: " + error);
                progressDialog.dismiss();
            }
        });
    }

    private void NOMBRE_ESTADO(final String in) {
        progressDialog.setMessage("Obteniendo Datos\nPor favor espere...");
        progressDialog.show();
        String path = ("in"+in+"/estado"); myRef = database.getReference(path);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                if (dataSnapshot.exists()){
                    long estado = (long) dataSnapshot.getValue();
                    ESTADO (in, estado);
                    progressDialog.dismiss();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();

            }
        });
    }

    private void ESTADO(final String in, final long estado) {
        String path2 = ("in"+in+"/nombre");
        Ref2 = database.getReference(path2);
        Ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nimble = snapshot.getValue(String.class);
                PUBLICAR (in, estado, nimble);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("The read failed: " + error.getCode());
            }
        });

    }

    private void PUBLICAR(String entrada, Long estado, String nombre) {

        final int i = Integer.parseInt(entrada)-1;

        switch (entrada) {
            case "1":
                if (estado == 0){
                    Senal1.setBackgroundResource(R.drawable.redondeoff); Senal1.setText(" OFF  ");
                } else {
                    Senal1.setBackgroundResource(R.drawable.redondeon); Senal1.setText(" ON  ");
                }
                Name1.setText(nombre.toUpperCase() + "\n\nUltimo cambio:\n"+ Relativo1);
                break;
            case "2":
                if (estado == 0){
                    Senal2.setBackgroundResource(R.drawable.redondeoff); Senal2.setText(" OFF  ");
                } else {
                    Senal2.setBackgroundResource(R.drawable.redondeon); Senal2.setText(" ON  ");
                }
                Name2.setText(nombre.toUpperCase() + "\n\nUltimo cambio:\n"+ Relativo2);
                break;
            case "3":
                if (estado == 0){
                    Senal3.setBackgroundResource(R.drawable.redondeoff); Senal3.setText(" OFF  ");
                } else {
                    Senal3.setBackgroundResource(R.drawable.redondeon); Senal3.setText(" ON  ");
                }
                Name3.setText(nombre.toUpperCase() + "\n\nUltimo cambio:\n"+ Relativo3);
                break;
            case "4":
                if (estado == 0){
                    Senal4.setBackgroundResource(R.drawable.redondeoff); Senal4.setText(" OFF  ");
                } else {
                    Senal4.setBackgroundResource(R.drawable.redondeon); Senal4.setText(" ON  ");
                }
                Name4.setText(nombre.toUpperCase() + "\n\nUltimo cambio:\n"+ Relativo4);
                break;
        }
    }
    public void RESET_SOL(View view) {
        //boolean SR = true;
        if (Evalve){
            myRef = database.getReference("reset_solenoide");
            myRef.setValue(true);
            Evalvula.setText("... SOLENOIDE. Esperando confirmacion ...");

        }else {Toast.makeText(this, "LA SOLENOIDE ESTA APAGADA.", Toast.LENGTH_SHORT).show();}

    }
    public void RESET_ALARM(View view) {
        if (Alarm){
            myRef = database.getReference("reset_alarma");
            myRef.setValue(true);
            Alarma.setText("... ALARMA. Esperando confirmacion ...");
        }else {Toast.makeText(this, "LA ALARMA ESTA APAGADA.", Toast.LENGTH_SHORT).show();}

    }
    public void Digital1(View view) {
    }
    public void Digital2(View view) {
    }
    public void Digital3(View view) {
    }
    public void Digital4(View view) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK){
            startActivity(new Intent(RealTime.this, MenuPpal.class));
            myRef = database.getReference("Status");
            myRef.setValue(false);
            myRef = database.getReference("OnlineState");
            myRef.setValue(false);
        }
        return super.onKeyDown(keyCode, event);
    }
}