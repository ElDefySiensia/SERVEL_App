package com.example.servel_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    //atributos
    private EditText rutLogin, claveLogin;
    private Button botonIngresar, botonRegistrate;

    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        //ajuste de márgenes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //instancias de los atributos
        rutLogin = findViewById(R.id.rut_login);
        claveLogin = findViewById(R.id.clave_login);
        botonIngresar = findViewById(R.id.boton_ingresar);
        botonRegistrate = findViewById(R.id.boton_registrate);

        //metodo para el boton ingresar (usa la BD)
        botonIngresar.setOnClickListener(v ->{
            //definimos variables
            String rut = rutLogin.getText().toString();
            String clave = claveLogin.getText().toString();

            //validacion de datos
            if(rut.isEmpty()){
                rutLogin.setError("Ingrese su Rut"); //esto es un feature que me gusta tener
                Toast.makeText(this, "Por favor ingrese su Rut", Toast.LENGTH_SHORT).show();
                return; //el return evita que se pueda continuar si hay error
            }
            if(clave.isEmpty()){
                claveLogin.setError("Ingrese su Clave");
                Toast.makeText(this, "Por favor ingrese su Clave", Toast.LENGTH_SHORT).show();
                return;
            }

            //BORRAR ESTO AL COLOCAR BD
            if(rut.equals("11.111.111-1") && clave.equals("123")){
                //un Intent para dirigirnos al portal
                Intent loginExitoso = new Intent(LoginActivity.this, PortalActivity.class);
                //llevamos los datos para su uso posterior
                loginExitoso.putExtra("rutUsuario", rut);
                startActivity(loginExitoso);
                finish(); //esto especificamente evita volver al login sin reiniciar la app o usando la app como tal
            } else {
                Toast.makeText(this, "Rut o Clave incorrectos, vuelva a intentar", Toast.LENGTH_SHORT).show();
                rutLogin.setError("Rut Incorrectos");
                claveLogin.setError("Clave Incorrectos");
            }
        });

        //metodo para el boton registrate
        botonRegistrate.setOnClickListener(v -> {

        });
    }
}