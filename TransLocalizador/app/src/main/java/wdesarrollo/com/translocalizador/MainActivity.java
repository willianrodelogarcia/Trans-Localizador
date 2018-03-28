package wdesarrollo.com.translocalizador;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText mUsuario,mContraseña;
    private Button mInicio,mRegistro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //EditText
        mUsuario = findViewById(R.id.edtUsuario);
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
                Intent intent = new Intent();
                
            }
        });
    }
}
