package com.example.iiotcorner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Analogicas extends AppCompatActivity {

    Datos Analogica;
    TextView MIN, MAX;
    SeekBar minimo, maximo;
    Spinner sp_min, sp_max;
    int pos1, pos2;
    LinearLayout ly_email1, ly_email2;
    EditText nombre, email1, contenido1, email2, contenido2;

    FirebaseDatabase database;
    DatabaseReference myRef;
    String path, spiner1, spiner2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analogicas);

        path = getIntent().getStringExtra("path");

        Analogica = new Datos("00","",0.0,"","","",0.0,"","");
        MIN = findViewById(R.id.textViewmin); MAX = findViewById(R.id.textViewmax);
        minimo = findViewById(R.id.seekBar_min); maximo = findViewById(R.id.seekBar_max);
        sp_min = findViewById(R.id.spinner_min); sp_max = findViewById(R.id.spinner_max);
        ly_email1 = findViewById(R.id.ly_emailmin); ly_email2 = findViewById(R.id.ly_emailmax);
        nombre = findViewById(R.id.editTextTextname);
        email1 = findViewById(R.id.editTextEmail2); contenido1 = findViewById(R.id.editTextTextMultiLine2);
        email2 = findViewById(R.id.editTextEmail3); contenido2 = findViewById(R.id.editTextTextMultiLine3);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(path);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("nombre").getValue().toString().equals("00")){
                    nombre.setText(snapshot.child("nombre").getValue().toString());
                    Double valor_min = snapshot.child("cuando").getValue(Double.class);
                    MIN.setText("VALOR MINIMO ACTUAL: "+ valor_min + " %");
                    String accion = snapshot.child("accion").getValue().toString();
                    String elemail , contenido;
                    if (accion.equals("Email")){
                        elemail = snapshot.child("email").getValue().toString();
                        contenido = snapshot.child("contenido").getValue().toString();
                        ly_email1.setVisibility(View.VISIBLE);
                        email1.setText(elemail); contenido1.setText(contenido);
                    }else{ elemail = ""; contenido ="";}

                    String accionB, elemailB, contenidoB;
                    accionB = snapshot.child("accionB").getValue().toString();
                    Double valor_max = snapshot.child("cuandoB").getValue(Double.class);
                    MAX.setText("VALOR MAXIMO ACTUAL: "+ valor_max + " %");
                    if (accionB.equals("Email")){
                        elemailB = snapshot.child("emailB").getValue().toString();
                        contenidoB = snapshot.child("contenidoB").getValue().toString();
                        ly_email2.setVisibility(View.VISIBLE);
                        email2.setText(elemailB); contenido2.setText(contenidoB);
                    } else {elemailB = ""; contenidoB ="";}


                    Analogica.setNombre(snapshot.child("nombre").getValue().toString()); Analogica.setAccion(accion);
                    Analogica.setActivador(snapshot.child("cuando").getValue(Double.class)); Analogica.setEmails(elemail);
                    Analogica.setContenido(contenido);
                    Analogica.setAccionB(accionB); Analogica.setActivadorB(snapshot.child("cuandoB").getValue(Double.class));
                    Analogica.setEmailB(elemailB); Analogica.setContenidoB(contenidoB);

                } else {
                    nombre.setHint("DEFINE UN NOMBRE PARA ESTA SEÑAL");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        minimo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MIN.setText("VALOR MINIMO: "+ minimo.getProgress() + " %");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast.makeText(Analogicas.this, "Definiendo valor minimo", Toast.LENGTH_SHORT).show();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                maximo.setMin(minimo.getProgress()+1);
                /*if (minimo.getProgress()==0){
                    sp_min.setVisibility(View.INVISIBLE);
                }else { sp_min.setVisibility(View.VISIBLE); }*/

            }
        });


        maximo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MAX.setText("VALOR MAXIMO: "+ maximo.getProgress() + " %");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast.makeText(Analogicas.this, "Definiendo valor maximo", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                minimo.setMax(maximo.getProgress()-1);
                /*if (maximo.getProgress()==100){
                    sp_max.setVisibility(View.INVISIBLE);
                }else { sp_max.setVisibility(View.VISIBLE); }*/
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Lista_Paso2, android.R.layout.simple_spinner_item);
        sp_min.setAdapter(adapter);
        sp_min.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos1=position;
                if (position==2){
                    ly_email1.setVisibility(View.VISIBLE);
                }else {ly_email1.setVisibility(View.GONE);}
                switch (position){
                    case 1:
                        spiner1="Alarma";
                        break;
                    case 2:
                        spiner1="Email";
                        break;
                    case 3:
                        spiner1="Electrovalvula";
                        break;
                    case 4:
                    case 0:
                        spiner1="Historial";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.Lista_Paso2, android.R.layout.simple_spinner_item);
        sp_max.setAdapter(adapter2);
        sp_max.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos2=position;
                if (position==2){
                    ly_email2.setVisibility(View.VISIBLE);
                }else {ly_email2.setVisibility(View.GONE);}
                switch (position){
                    case 1:
                        spiner2="Alarma";
                        break;
                    case 2:
                        spiner2="Email";
                        break;
                    case 3:
                        spiner2="Electrovalvula";
                        break;
                    case 4:
                    case 0:
                        spiner2="Historial";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void ACEPTAR(View view) {
        if (TextUtils.isEmpty(nombre.getText())){
            nombre.setError("Nombre Invalido");
            Toast.makeText(this, "SE DEBE DEFINIR UN NOMBRE VALIDO", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "INFORMACION RECIBIDA", Toast.LENGTH_SHORT).show();
            String path1 = path+"/nombre"; String path2 = path+"/cuando"; String path3 = path+"/accion";
            String path4 = path+"/email"; String path5 = path+"/contenido";
            String path6 = path+"/cuandoB"; String path7 = path+"/accionB";
            String path8 = path+"/emailB"; String path9 = path+"/contenidoB";
            myRef = database.getReference(path1);
            myRef.setValue(nombre.getText().toString());
            myRef = database.getReference(path2);
            myRef.setValue(minimo.getProgress());
            myRef = database.getReference(path3);
            myRef.setValue(spiner1);
            myRef = database.getReference(path4);
            myRef.setValue(email1.getText().toString());
            myRef = database.getReference(path5);
            myRef.setValue(contenido1.getText().toString());
            myRef = database.getReference(path6);
            myRef.setValue(maximo.getProgress());
            myRef = database.getReference(path7);
            myRef.setValue(spiner2);
            myRef = database.getReference(path8);
            myRef.setValue(email2.getText().toString());
            myRef = database.getReference(path9);
            myRef.setValue(contenido2.getText().toString());

            startActivity(new Intent(Analogicas.this, Configurar.class));
            finish();

        }
    }
}