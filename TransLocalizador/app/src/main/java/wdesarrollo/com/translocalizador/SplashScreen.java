package wdesarrollo.com.translocalizador;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends Activity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener fireAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        fireAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user!=null){
                    //Manda al mapa del Usuario para ver a los conductores
                    final String tipoUsuario = user.getUid();

                    DatabaseReference refTransporte = FirebaseDatabase.getInstance().getReference().child("UsuariosTransporte").child("Transportes").child(tipoUsuario);
                    DatabaseReference refUsuario = FirebaseDatabase.getInstance().getReference().child("UsuariosTransporte").child("Usuarios").child(tipoUsuario);


                    refTransporte.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){

                                if(dataSnapshot.getValue()!=null){
                                    //Toast.makeText(SplashScreen.this,"DataSnapshot.getValue != null Transporte",Toast.LENGTH_LONG).show();
                                    if (dataSnapshot.child("correo").getValue()!=null) {
                                        if (dataSnapshot.child("correo").getValue().toString().equals(user.getEmail())){
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent intent = new Intent(SplashScreen.this,ConductorMapActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            },4000);

                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    refUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){

                                if(dataSnapshot.getValue()!=null){

                                    if (dataSnapshot.child("correo").getValue()!=null) {

                                        if (dataSnapshot.child("correo").getValue().toString().equals(user.getEmail())){

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //Toast.makeText(SplashScreen.this,"dataSnapshot.child(correo).getValue()equals Usuario",Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(SplashScreen.this,UsuarioMapActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            },3000);

                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(SplashScreen.this,"Else No Logeado",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    },3000);

                }

            }
        };

    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(fireAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(fireAuthStateListener);
    }
}
