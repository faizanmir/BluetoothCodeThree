package avishkaar.com.bluetoothcodethree;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    EditText email,password;
    Button signUpButton;
    String emailAddress,userPassword;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            Intent intent = new Intent(SignUpActivity.this,DeviceListActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);
        mAuth = FirebaseAuth.getInstance();
        signUpButton = findViewById(R.id.signUpbutton);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailAddress = email.getText().toString();
                userPassword =  password.getText().toString();
                sendToFirebase(emailAddress,userPassword);
            }
        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String email =  firebaseUser.getEmail();
            Log.e(TAG, "onCreate: " + email + "Logged in" );
            Intent intent = new Intent(SignUpActivity.this,DeviceListActivity.class);
            startActivity(intent);
        }


    }

    private void sendToFirebase(final String emailAddress, final String userPassword) {

        mAuth.createUserWithEmailAndPassword(emailAddress,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                   startListActivity();
                }
                else
                {
                    mAuth.signInWithEmailAndPassword(emailAddress,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.e(TAG, "onComplete: " + FirebaseAuth.getInstance().getCurrentUser().getEmail() );
                            startListActivity();
                        }
                    });
                    {

                    }
                }
            }
        });
    }


    void startListActivity()
    {
        Intent intent = new Intent(SignUpActivity.this,DeviceListActivity.class);
        startActivity(intent);
    }
}
