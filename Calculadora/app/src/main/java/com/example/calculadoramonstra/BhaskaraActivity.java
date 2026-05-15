package com.example.calculadoramonstra;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class BhaskaraActivity extends AppCompatActivity {

    private TextInputEditText etA, etB, etC;
    private TextView tvDelta, tvRoots;
    private MaterialCardView cardResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bhaskara);

        // Configurar botões de voltar
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_back_bottom).setOnClickListener(v -> finish());

        etA = findViewById(R.id.et_a);
        etB = findViewById(R.id.et_b);
        etC = findViewById(R.id.et_c);
        tvDelta = findViewById(R.id.tv_delta);
        tvRoots = findViewById(R.id.tv_roots);
        cardResult = findViewById(R.id.card_result);

        View mainView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_calculate).setOnClickListener(v -> calculateBhaskara());
    }

    private void calculateBhaskara() {
        try {
            String sA = etA.getText().toString();
            String sB = etB.getText().toString();
            String sC = etC.getText().toString();

            if (sA.isEmpty() || sB.isEmpty() || sC.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double a = Double.parseDouble(sA);
            double b = Double.parseDouble(sB);
            double c = Double.parseDouble(sC);

            if (a == 0) {
                Toast.makeText(this, "'a' não pode ser zero", Toast.LENGTH_SHORT).show();
                return;
            }

            double delta = (b * b) - (4 * a * c);
            tvDelta.setText(String.format(Locale.getDefault(), "Δ = %s", formatDouble(delta)));

            if (delta < 0) {
                tvRoots.setText("Sem raízes reais (Δ < 0)");
            } else {
                double x1 = (-b + Math.sqrt(delta)) / (2 * a);
                double x2 = (-b - Math.sqrt(delta)) / (2 * a);
                tvRoots.setText(String.format(Locale.getDefault(), "x1 = %s\nx2 = %s", formatDouble(x1), formatDouble(x2)));
            }
            
            cardResult.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Toast.makeText(this, "Erro no cálculo", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatDouble(double d) {
        if (d == (long) d)
            return String.format(Locale.getDefault(), "%d", (long) d);
        else
            return String.format(Locale.getDefault(), "%.4f", d).replaceAll("0*$", "").replaceAll("\\.$", "");
    }
}
