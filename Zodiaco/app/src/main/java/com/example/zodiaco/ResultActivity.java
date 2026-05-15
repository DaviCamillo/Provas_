package com.example.zodiaco;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ImageView ivZodiacIcon = findViewById(R.id.ivZodiacIcon);
        TextView tvZodiacName = findViewById(R.id.tvZodiacName);
        TextView tvElement = findViewById(R.id.tvElement);
        TextView tvFullResult = findViewById(R.id.tvFullResult);
        MaterialButton btnBack = findViewById(R.id.btnBack);

        String zodiac = getIntent().getStringExtra("zodiac");
        String element = getIntent().getStringExtra("element");
        String result = getIntent().getStringExtra("result");

        tvZodiacName.setText(zodiac);
        tvElement.setText(getString(R.string.element_label, element));
        tvFullResult.setText(result);

        // Definir ícone baseado no signo
        int iconRes = getZodiacIcon(zodiac);
        ivZodiacIcon.setImageResource(iconRes);

        btnBack.setOnClickListener(v -> finish());
    }

    private int getZodiacIcon(String zodiac) {
        if (zodiac == null) return android.R.drawable.star_big_on;
        switch (zodiac) {
            case "Áries": return R.drawable.ic_aries;
            case "Touro": return R.drawable.ic_tauro;
            case "Gêmeos": return R.drawable.ic_geminis;
            case "Câncer": return R.drawable.ic_cancer;
            case "Leão": return R.drawable.ic_leo;
            case "Virgem": return R.drawable.ic_virgo;
            case "Libra": return R.drawable.ic_libra;
            case "Escorpião": return R.drawable.ic_scorpio;
            case "Sagitário": return R.drawable.ic_sagittarius;
            case "Capricórnio": return R.drawable.ic_capricorn;
            case "Aquário": return R.drawable.ic_aquarius;
            case "Peixes": return R.drawable.ic_pisces;
            default: return android.R.drawable.star_big_on;
        }
    }
}
