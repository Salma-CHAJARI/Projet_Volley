package com.example.projetws;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashView extends AppCompatActivity {

    private Animation rotateAnimation;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_view);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.primaryColor));
        Button button1 = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);
        imageView = findViewById(R.id.imageView);


        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.anim);


        imageView.startAnimation(rotateAnimation);


        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAnimation();

                if (view.getId() == R.id.button) {

                    Intent intent = new Intent(SplashView.this, MainActivity.class);
                    startActivity(intent);
                } else if (view.getId() == R.id.button2) {
                    // Action pour le bouton 2
                    Intent intent = new Intent(SplashView.this, addEtudiant.class);
                    startActivity(intent);
                }
            }
        };


        button1.setOnClickListener(buttonClickListener);
        button2.setOnClickListener(buttonClickListener);
    }

    private void stopAnimation() {

        imageView.clearAnimation();
    }
}
