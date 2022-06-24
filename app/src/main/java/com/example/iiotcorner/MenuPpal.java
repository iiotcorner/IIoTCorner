package com.example.iiotcorner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuPpal extends AppCompatActivity {

    private static final String CHANNEL_ID = "channelId";
    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView usuario;
    String emailcorto, USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menuppal);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            emailcorto = bundle.getString("emailcorto");
        }
        usuario = findViewById(R.id.textViewname);
        database = FirebaseDatabase.getInstance();
        String path = "USUARIOS/"+emailcorto+"/NOMBRE";
        myRef = database.getReference(path);

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                USER = dataSnapshot.getValue(String.class);
                usuario.setText(USER);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                usuario.setText("No name: "+emailcorto);
            }
        });
        myRef = database.getReference("Status");
        myRef.setValue(false);

        myRef = database.getReference("alarma");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long estado = (long) snapshot.getValue();
                    int noti = (int) estado;
                    if (noti==1){
                        NOTIFICACIONES("ALARMA ACTIVADA");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error Firabase " + error);
            }
        });

        myRef = database.getReference("solenoide");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long estado = (long) snapshot.getValue();
                    int noti = (int) estado;
                    if (noti==1){
                        NOTIFICACIONES("ELECTROVALVULA ACTIVADA");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error Firabase " + error);
            }
        });

    }

    private void NOTIFICACIONES(String titulo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Notificacion";
            NotificationChannel notificationChannel = new NotificationChannel(Canal_id, name, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Canal_id);
        builder.setAutoCancel(true);

        builder.setSmallIcon(R.drawable.ic_action_alarm).setTicker("Nueva Notificacion").
                setContentTitle(titulo).setContentText("Se ha activado. Click aqui para ver la app en tiempo real").
                setColor(Color.BLUE).setDefaults(Notification.DEFAULT_SOUND).setDefaults(Notification.DEFAULT_VIBRATE);

        Intent intent = new Intent(this, RealTime.class);
        //intent.putExtra("Toque", 1);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(notifiacion_id, builder.build());
    }

    private PendingIntent pendingIntent;
    private  final static int notifiacion_id = 1;
    private  final static String Canal_id = "NOTIFICACION";


    public void Configurar(View view) {
        startActivity(new Intent(MenuPpal.this, Configurar.class).putExtra("user", USER));
    }

    public void Historial(View view) {
        startActivity(new Intent(MenuPpal.this, Estadistico.class));
    }

    public void RealTime(View view) {
        startActivity(new Intent(MenuPpal.this, RealTime.class));
    }

    public void SALIR(View view) {
        AlertDialog.Builder listo = new AlertDialog.Builder(MenuPpal.this);
        listo.setMessage("Esta seguro que desea salir?").setCancelable(true).
                setPositiveButton("LOG OUT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent salidasegura = new Intent(MenuPpal.this, MainActivity.class);
                        salidasegura.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        salidasegura.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(salidasegura);
                    }
                });
        AlertDialog titulo = listo.create();
        titulo.setTitle("SALIDA SEGURA");
        titulo.show();

    }
}