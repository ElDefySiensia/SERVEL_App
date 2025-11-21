package com.example.servel_app.repositorio;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseRepositorio {

    private static final String TAG = "FirebaseRepositorio";
    private FirebaseAuth mAuth;

    public FirebaseRepositorio() {
        // Inicializa la instancia de Auth al crear el Repositorio
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Interface para manejar los resultados asíncronos de Firebase.
     */
    public interface AuthCallback {
        void onSuccess(String rut);
        void onFailure(String errorMessage);
    }

    /**
     * Registra un nuevo usuario en Firebase Auth.
     */
    public void registrarUsuario(String rut, String password, final AuthCallback callback) {
        // Convertir RUT a un formato de email para Firebase
        String email = rut.trim() + "@servel.cl";

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Task<AuthResult> task) -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Registro exitoso para: " + rut);
                        callback.onSuccess(rut);
                    } else {
                        // Extraemos y traducimos el error
                        String error = getErrorMessage(task.getException(), true);
                        Log.w(TAG, "Registro fallido", task.getException());
                        callback.onFailure(error);
                    }
                });
    }

    /**
     * Inicia sesión en Firebase Auth.
     */
    public void iniciarSesion(String rut, String password, final AuthCallback callback) {
        String email = rut.trim() + "@servel.cl";

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Task<AuthResult> task) -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Inicio de sesión exitoso para: " + rut);
                        callback.onSuccess(rut);
                    } else {
                        // Extraemos y traducimos el error
                        String error = getErrorMessage(task.getException(), false);
                        Log.w(TAG, "Inicio de sesión fallido", task.getException());
                        callback.onFailure(error);
                    }
                });
    }

    /**
     * Cierra la sesión activa de Firebase.
     */
    public void cerrarSesion() {
        mAuth.signOut();
    }

    /**
     * Obtiene el RUT del usuario actualmente logueado.
     */
    public String getRUTUsuarioActual() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Revertir el email a RUT. Ejemplo: "11222333-4@servel.cl" -> "11222333-4"
            String email = user.getEmail();
            if (email != null && email.endsWith("@servel.cl")) {
                return email.substring(0, email.indexOf("@servel.cl"));
            }
        }
        return null;
    }

    /**
     * Traduce los códigos de error de Firebase Authentication a un mensaje amigable en español.
     * @param exception La excepción obtenida de la tarea de Firebase.
     * @param isRegistration Indica si la operación es de registro o de login.
     * @return Mensaje de error traducido.
     */
    private String getErrorMessage(Exception exception, boolean isRegistration) {
        if (exception instanceof FirebaseAuthException) {
            String errorCode = ((FirebaseAuthException) exception).getErrorCode();

            switch (errorCode) {
                // Errores de LOGIN y REGISTRO
                case "ERROR_INVALID_EMAIL":
                    return "El formato del RUT o Correo no es válido.";
                case "ERROR_WRONG_PASSWORD":
                case "ERROR_USER_NOT_FOUND":
                case "ERROR_INVALID_CUSTOM_TOKEN":
                case "ERROR_USER_DISABLED":
                    // Para el login, agrupamos la mayoría de errores de credenciales
                    return "RUT o Clave incorrectos. Vuelva a intentar.";

                // Errores específicos de REGISTRO
                case "ERROR_EMAIL_ALREADY_IN_USE":
                    return "Este RUT ya se encuentra registrado en el sistema.";
                case "ERROR_WEAK_PASSWORD":
                    return "La Clave debe tener al menos 6 caracteres.";

                // Errores de LOGIN
                case "ERROR_USER_LOCKED_OUT":
                    return "El acceso del usuario ha sido bloqueado temporalmente.";

                default:
                    return isRegistration ? "Error desconocido al registrar." : "Error desconocido al iniciar sesión.";
            }
        }

        if (exception != null) {
            // Error genérico (ej. problemas de conexión, network)
            if (exception.getMessage().contains("network")) {
                return "Error de conexión. Revise su conexión a internet.";
            }
            return exception.getMessage();
        }

        return "Ha ocurrido un error inesperado.";
    }
}