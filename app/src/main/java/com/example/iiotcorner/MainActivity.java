package com.example.iiotcorner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    EditText Email, Password, Email_recuperar, Nombre, Cargo, Email_new, Pass_new;
    private FirebaseAuth mAuth;
    ProgressDialog pdvalidando;
    LinearLayout ly_inicial, ly_olvido, ly_registro;
    Switch swbREG;

    FirebaseDatabase database;
    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Email = findViewById(R.id.textoEmail);       Password = findViewById(R.id.textoPass);
        ly_inicial = findViewById(R.id.lyinicial);        ly_olvido = findViewById(R.id.lyolvido);
        ly_registro = findViewById(R.id.lyregistro);
        Email_recuperar = findViewById(R.id.textoEmail4);
        swbREG = findViewById(R.id.switch1);
        Email_new  = findViewById(R.id.textoEmail2);       Pass_new = findViewById(R.id.textoPass2);
        Nombre = findViewById(R.id.textonombre);       Cargo = findViewById(R.id.textocargo);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        pdvalidando=new ProgressDialog(this);

        // CUANDO EL USUARIO QUEDA ACTIVO
        FirebaseUser fiberusuario = mAuth.getCurrentUser();
        if (fiberusuario != null ){
            String email = fiberusuario.getEmail().toString();
            String emailcorto;
            int pos = email.indexOf("@"); //ubicando la posicion de la @ en el email.
            emailcorto = email.substring(0,pos); // recortando email.
            int punto = emailcorto.indexOf("."); //verificando si hay . en el email.
            if (punto!=-1){
                emailcorto = email.substring(0,punto)+"_"+email.substring(punto+1,pos); // recortando email.
            }
            System.out.println("EMAIL RECORTADO: "+emailcorto);
            startActivity(new Intent(MainActivity.this, MenuPpal.class).putExtra("emailcorto", emailcorto));
            Toast.makeText(MainActivity.this, "BIENVENIDO:\n"+emailcorto, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void onInicio(View view) {

        //tomando datos ingresados
        final String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        // verificando datos validos;
        if (TextUtils.isEmpty(email)){
            Email.setError("Email Incorrecto... Intente nuevamente");
            Toast.makeText(MainActivity.this, "Email Incorrecto, Verifique",Toast.LENGTH_LONG).show();
            return;
        }
        pdvalidando.setMessage("Verificando... espere");
        pdvalidando.show();
        // Accediendo
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "INGRESO EXITOSO\n"+email, Toast.LENGTH_SHORT).show();
                            String emailcorto;
                            int pos = email.indexOf("@"); //ubicando la posicion de la @ en el email.
                            emailcorto = email.substring(0,pos); // recortando email.
                            int punto = emailcorto.indexOf("."); //verificando si hay . en el email.
                            if (punto!=-1){
                                emailcorto = email.substring(0,punto)+"_"+email.substring(punto+1,pos); // recortando email.
                            }
                            System.out.println("EMAIL RECORTADO: "+emailcorto);
                            startActivity(new Intent(MainActivity.this, MenuPpal.class).putExtra("emailcorto", emailcorto));
                            //startActivity(new Intent(MainActivity.this, MenuPpal.class));

                        } else {
                            //Toast.makeText(MainActivity.this,"ERROR PARA ACCEDER\nVerifique sus datos o conexion a internet",Toast.LENGTH_LONG).show();
                            Toast toast = Toast.makeText(MainActivity.this, "ERROR PARA ACCEDER\nVerifique sus datos o conexion a internet", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                        pdvalidando.dismiss();
                    }
                });
    }

    public void OLVIDO(View view) {
        ly_inicial.setVisibility(View.GONE); ly_olvido.setVisibility(View.VISIBLE);
    }
    public void RECORDAR(View view) {
        final String email = Email_recuperar.getText().toString().trim();
        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "EMAIL INVALIDO\nIntente nuevamente", Toast.LENGTH_LONG).show();
            return;
        }

        pdvalidando.setMessage("Validando datos... espere");
        pdvalidando.show();
        mAuth.setLanguageCode("es"); // definiendo idioma de Firebase
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    ly_inicial.setVisibility(View.VISIBLE); ly_olvido.setVisibility(View.GONE);
                    pdvalidando.dismiss();
                    //Toast.makeText(MainActivity.this, "Se ha enviado un email para restablecer Su contrasena", Toast.LENGTH_LONG).show();
                    Toast toast = Toast.makeText(MainActivity.this, "Se ha enviado un email a:\n"+email+"\npara restablecer su contraseña", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }else {
                    pdvalidando.dismiss();
                    Toast.makeText(MainActivity.this, "Falla de resgistro, verifique nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void SW_REGISTRO(View view) {
        if (view.getId()==R.id.switch1){
            if (swbREG.isChecked()){
                ly_registro.setVisibility(View.VISIBLE); ly_inicial.setVisibility(View.GONE);
            }else {
                ly_registro.setVisibility(View.GONE); ly_inicial.setVisibility(View.VISIBLE);
            }
        }
    }
    public void NUEVO_REG(View view) {
        final String email = Email_new.getText().toString().trim();
        final String NOMBRE = Nombre.getText().toString().trim();
        final String CARGO = Cargo.getText().toString().trim();
        String password = Pass_new.getText().toString().trim();
        // verificando datos validos;
        if (TextUtils.isEmpty(NOMBRE)){
            Email_new.setError("Defina un nombre valido");
            return;
        }
        if (TextUtils.isEmpty(email)){
            Email_new.setError("INVALIDO... Intente nuevamente");
            return;
        }
        if (password.length()<6){
            Pass_new.setError("La contrasena debe ser de al menos 6 digitos");
            return;
        }
        pdvalidando.setMessage("VALIDANDO... ESPERE");
        pdvalidando.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,"INGRESO EXITOSO\n"+Email_new.getText(),
                                    Toast.LENGTH_SHORT).show();
                            String emailcorto;
                            int pos = email.indexOf("@"); //ubicando la posicion de la @ en el email.
                            emailcorto = email.substring(0,pos); // recortando email.
                            int punto = emailcorto.indexOf("."); //verificando si hay . en el email.
                            if (punto!=-1){
                                emailcorto = email.substring(0,punto)+"_"+email.substring(punto+1,pos); // recortando email.
                            }
                            System.out.println("EMAIL RECORTADO: "+emailcorto);
                            USUARIO_NUEVO(emailcorto,email,NOMBRE,CARGO);
                            startActivity(new Intent(MainActivity.this, MenuPpal.class).putExtra("emailcorto", emailcorto));
                            Toast.makeText(MainActivity.this, "BIENVENIDO:\n"+emailcorto, Toast.LENGTH_LONG).show();

                        } else {
                            //updateUI(null);
                            //revisando que el usuario no este ya registrado
                            if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                //Toast.makeText(MainActivity.this,"EL EMAIL YA ESTABA REGISTRADO\nSolicite recodar contraseñA",Toast.LENGTH_LONG).show();
                                Toast toast = Toast.makeText(MainActivity.this, "ESTE EMAIL YA ESTA REGISTRADO\nSolicite recodar la contraseña para\n"+email, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            } else {
                                Toast.makeText(MainActivity.this,"...ERROR...",Toast.LENGTH_LONG).show();
                            }}
                        pdvalidando.dismiss();
                    }
                });
    }

    private void USUARIO_NUEVO(String emailcorto, String email, String NOMBRE, String CARGO) {
        //CREANDO NUEVO USUARIO:
        myRef = database.getReference("USUARIOS/"+emailcorto+"/NOMBRE");
        myRef.setValue(NOMBRE);
        myRef = database.getReference("USUARIOS/"+emailcorto+"/EMAIL");
        myRef.setValue(email);
        myRef = database.getReference("USUARIOS/"+emailcorto+"/CARGO");
        myRef.setValue(CARGO);
    }
}
