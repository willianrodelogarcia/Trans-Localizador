package wdesarrollo.com.translocalizador;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        setContentView(R.layout.activity_main);
        //FireBase Auth


        mAuth = FirebaseAuth.getInstance();

        fireAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user!=null){
                    //Manda al mapa del Usuario para ver a los conductores
                    String tipoUsuario = user.getUid();
                    DatabaseReference refTransporte = FirebaseDatabase.getInstance().getReference().child("Transportes").child(tipoUsuario);
                    //DatabaseReference refUsuario = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(tipoUsuario);

                    //Toast.makeText(MainActivity.this,"REF "+refConductor.getKey()+ " REFU "+refUsuario.getKey(),Toast.LENGTH_LONG).show();

                    if(tipoUsuario.equals(refTransporte.getKey())){

                        Toast.makeText(MainActivity.this,"Es un conductor",Toast.LENGTH_LONG).show();
                       /* Intent intent = new Intent(MainActivity.this,ConductorMapActivity.class);
                        startActivity(intent);
                        finish();*/
                    }else{
                        Toast.makeText(MainActivity.this,"Es un Usuario",Toast.LENGTH_LONG).show();
                    }

                        /*Intent intent = new Intent(MainActivity.this,UsuarioMapActivity.class);
                        startActivity(intent);
                        finish();*/





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
                final String correo = mUsuario.getText().toString();
                final String password = mContraseña.getText().toString();

                mAuth.signInWithEmailAndPassword(correo,password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"Error al Iniciar Sesion",Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
