package com.bagas.easytravel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText edtRegEmail, edtRegPassword, edtRegNama, edtRegConfirm;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        edtRegEmail = findViewById(R.id.edtRegEmail);
        edtRegNama = findViewById(R.id.edtRegNama);
        edtRegPassword = findViewById(R.id.edtRegPassword);
        edtRegConfirm = findViewById(R.id.edtRegConfirm);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void register(View view) {
        String email, nama, password, confirm;
        email = edtRegEmail.getText().toString();
        nama = edtRegNama.getText().toString();
        password = edtRegPassword.getText().toString();
        confirm = edtRegConfirm.getText().toString();

        if(password.equals(confirm)){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                saveUserToFirestore(user, nama, email, password);
                                startActivity(new Intent(getApplicationContext(), DashboardActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            } else {
                                // Handle Firebase Authentication errors
                                String errorMessage = "Registrasi gagal. Silakan coba lagi.";
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    errorMessage = "Password terlalu lemah, gunakan lebih dari 6 karakter.";
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    errorMessage = "Email atau password tidak valid.";
                                } catch (FirebaseAuthUserCollisionException e) {
                                    errorMessage = "Email sudah terdaftar.";
                                } catch (Exception e) {
                                    Log.w("auth_error", "Registrasi error lek", e);
                                }
                                
                                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this,"Password dan Konfirmasi tidak sama!", Toast.LENGTH_LONG).show();
        }
    }

    private void saveUserToFirestore(FirebaseUser user, String nama, String email, String password) {
        if (user != null){

            String hashedPassword = hashPassword(password);
            
            Map<String, Object> userData = new HashMap<>();
            userData.put("uuid", user.getUid());
            userData.put("nama", nama);
            userData.put("email", email);
            userData.put("password", hashedPassword);
            userData.put("created_at", System.currentTimeMillis());
            userData.put("updated_at", null);

            db.collection("users")
                    .document(user.getUid())
                    .set(userData)
                    .addOnSuccessListener(aVoid ->{
                       Log.d("Firestore", "Data pengguna berhasil disimpan");
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error menyimpan data pengguna", e);
                    });
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("HashError", "Password hashing failed", e);
            return null;
        }
    }

    public void toLogin(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}