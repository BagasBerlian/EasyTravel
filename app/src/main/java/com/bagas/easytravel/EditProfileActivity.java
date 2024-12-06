package com.bagas.easytravel;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private TextView namaUser;
    private EditText edtEditNamaUser;
    private ImageButton backButton;
    private TextView doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        initBackBtn();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        namaUser = findViewById(R.id.namaUser);
        edtEditNamaUser = findViewById(R.id.edtEditNamaUser);
        backButton = findViewById(R.id.backButton);
        doneButton = findViewById(R.id.saveEdit);

        if (user != null) {
            String email = user.getEmail();
            fetchUserData(email);
        }

        doneButton.setOnClickListener(view -> {
            String updatedName = edtEditNamaUser.getText().toString().trim();
            if (!updatedName.isEmpty()) {
                updateUserData(updatedName);
            } else {
                Toast.makeText(EditProfileActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateUserData(String updatedName) {
        db.collection("users")
                .whereEqualTo("email", user.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String userId = document.getId();

                        // Update the name in Firestore
                        db.collection("users")
                                .document(userId)
                                .update("nama", updatedName)
                                .addOnSuccessListener(aVoid -> {
                                    namaUser.setText(updatedName);
                                    edtEditNamaUser.setText(updatedName);
                                    Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(EditProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Error finding user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchUserData(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String userName = document.getString("nama");
                        if (userName != null) {
                            // Display the name in both TextView and EditText
                            namaUser.setText(userName);
                            edtEditNamaUser.setText(userName);
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initBackBtn() {
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}