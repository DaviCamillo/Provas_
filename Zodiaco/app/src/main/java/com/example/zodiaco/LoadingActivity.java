package com.example.zodiaco;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        ImageView ivLoadingOrbit = findViewById(R.id.ivLoadingOrbit);

        // Animação de rotação para o ícone
        RotateAnimation rotate = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(3000);
        rotate.setRepeatCount(Animation.INFINITE);
        ivLoadingOrbit.startAnimation(rotate);

        // Pegar os dados da Intent
        String zodiac = getIntent().getStringExtra("zodiac");
        String element = getIntent().getStringExtra("element");
        String result = getIntent().getStringExtra("result");

        // Delay de 3 segundos
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LoadingActivity.this, ResultActivity.class);
            intent.putExtra("zodiac", zodiac);
            intent.putExtra("element", element);
            intent.putExtra("result", result);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 3000);
    }
}
