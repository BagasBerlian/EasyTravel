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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
                                Log.w("auth_error", "Registrasi error lek", task.getException());
                            }
                        }
                    });
        } else {
            Toast.makeText(this,"Password dan Konfirmasi tidak sama!", Toast.LENGTH_LONG).show();
        }
    }

    private void saveUserToFirestore(FirebaseUser user, String nama, String email, String password) {
        if (user != null){
            Map<String, Object> userData = new HashMap<>();
            userData.put("uuid", user.getUid());
            userData.put("nama", nama);
            userData.put("email", email);
            userData.put("password", password);
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

    public void toLogin(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}