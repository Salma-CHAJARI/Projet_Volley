package com.example.projetws;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class addEtudiant extends AppCompatActivity {

    private EditText nom, prenom;
    private Spinner ville;
    private RadioGroup sexeGroup;
    private RadioButton homme, femme;
    private ImageView imageView;
    private Button add, chooseImage;
    private Bitmap bitmap;
    private RequestQueue requestQueue;

    private static final int PICK_IMAGE_REQUEST = 1;
    private String insertUrl = "http://10.0.2.2/php-mobile2/ws/createEtudiant.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_etudiant);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.purple));
        MaterialToolbar matriel = findViewById(R.id.materialToolbar3);
        setSupportActionBar(matriel);
        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        ville = findViewById(R.id.ville);
        sexeGroup = findViewById(R.id.sexe);
        homme = findViewById(R.id.homme);
        femme = findViewById(R.id.femme);
        imageView = findViewById(R.id.imageView);
        add = findViewById(R.id.add);
        chooseImage = findViewById(R.id.choose_image);

        requestQueue = Volley.newRequestQueue(this);

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEtudiant();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addEtudiant() {
        if (bitmap == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nom.getText().toString().isEmpty() || prenom.getText().toString().isEmpty() || ville.getSelectedItem() == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, insertUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Server Response", response);
                        Toast.makeText(addEtudiant.this, "Étudiant ajouté", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    Log.e("Volley Error", "Status Code: " + error.networkResponse.statusCode);
                }
                Log.e("Volley Error", error.getMessage());
                Toast.makeText(addEtudiant.this, "Error adding student: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                String sexe = sexeGroup.getCheckedRadioButtonId() == R.id.homme ? "Homme" : "Femme";
                String encodedImage = encodeImageToBase64(bitmap);
                Log.d("Image Base64 Length", String.valueOf(encodedImage.length()));

                Map<String, String> params = new HashMap<>();
                params.put("nom", nom.getText().toString());
                params.put("prenom", prenom.getText().toString());
                params.put("ville", ville.getSelectedItem().toString());
                params.put("sexe", sexe);
                params.put("image", encodedImage); // Use "image" to match the database
                return params;
            }
        };
        requestQueue.add(request);
    }

    private Bitmap getScaledBitmap(Bitmap bitmap) {
        int newWidth = 200;
        int newHeight = (bitmap.getHeight() * newWidth) / bitmap.getWidth();
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        bitmap = getScaledBitmap(bitmap);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.liste) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }else if(item.getItemId() == R.id.share){
            String txt = "Share the list of students or the new student";
            String mimeType = "text/plain";
            ShareCompat.IntentBuilder
                    .from(this)
                    .setType(mimeType)
                    .setChooserTitle("Students")
                    .setText(txt)
                    .startChooser();
        }

        return super.onOptionsItemSelected(item);
    }
}

