package wdesarrollo.com.translocalizador;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText mUsuario,mContraseña;
    private Button mInicio,mRegistro;

    //FireBase
    //FireBase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener fireAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FireBase Auth
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        fireAuthStateListener = new FirebaseAuth.AuthStateListener() {
            FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(user!=null){
                    //Manda al mapa del Usuario para ver a los conductores
                    Intent intent = new Intent(MainActivity.this,UsuarioMapActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        };

        //EditText
        mUsuario = findViewById(R.id.edtCorreo);
        mContraseña = findViewById(R.id.edtContraseña);

        //Button
        mInicio = findViewById(R.id.btnIniciar);
        mRegistro = findViewById(R.id.btnRegistro);


        //Acciones de los Botones

        mInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
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
