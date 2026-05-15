package com.example.zodiaco;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etName, etBirthDate, etBirthTime, etBirthPlace;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Inicializar componentes
        etName = findViewById(R.id.etName);
        etBirthDate = findViewById(R.id.etBirthDate);
        etBirthTime = findViewById(R.id.etBirthTime);
        etBirthPlace = findViewById(R.id.etBirthPlace);
        MaterialButton btnCalculate = findViewById(R.id.btnCalculate);

        // Configurar seletores de Data e Hora
        etBirthDate.setOnClickListener(v -> showDatePicker());
        etBirthTime.setOnClickListener(v -> showTimePicker());

        // Limpar erros ao digitar
        setupErrorClearing(etName, findViewById(R.id.tilName));
        setupErrorClearing(etBirthDate, findViewById(R.id.tilBirthDate));
        setupErrorClearing(etBirthTime, findViewById(R.id.tilBirthTime));
        setupErrorClearing(etBirthPlace, findViewById(R.id.tilBirthPlace));

        btnCalculate.setOnClickListener(v -> calculateDestiny());
    }

    private void setupErrorClearing(android.widget.EditText editText, com.google.android.material.textfield.TextInputLayout layout) {
        editText.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                layout.setError(null);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            etBirthDate.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            etBirthTime.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void calculateDestiny() {
        com.google.android.material.textfield.TextInputLayout tilName = findViewById(R.id.tilName);
        com.google.android.material.textfield.TextInputLayout tilBirthDate = findViewById(R.id.tilBirthDate);
        com.google.android.material.textfield.TextInputLayout tilBirthTime = findViewById(R.id.tilBirthTime);
        com.google.android.material.textfield.TextInputLayout tilBirthPlace = findViewById(R.id.tilBirthPlace);

        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String dateStr = etBirthDate.getText() != null ? etBirthDate.getText().toString().trim() : "";
        String timeStr = etBirthTime.getText() != null ? etBirthTime.getText().toString().trim() : "";
        String place = etBirthPlace.getText() != null ? etBirthPlace.getText().toString().trim() : "";

        // Resetar erros
        tilName.setError(null);
        tilBirthDate.setError(null);
        tilBirthTime.setError(null);
        tilBirthPlace.setError(null);

        boolean hasError = false;

        if (name.isEmpty()) {
            tilName.setError(getString(R.string.error_name_empty));
            hasError = true;
        }

        if (dateStr.isEmpty()) {
            tilBirthDate.setError(getString(R.string.error_date_empty));
            hasError = true;
        }

        if (timeStr.isEmpty()) {
            tilBirthTime.setError(getString(R.string.error_time_empty));
            hasError = true;
        }

        if (hasError) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date birthDate = sdf.parse(dateStr);
            if (birthDate == null) return;

            Calendar cal = Calendar.getInstance();
            cal.setTime(birthDate);

            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);

            // 1. Signo Ocidental
            String zodiac = getZodiacSign(day, month);
            
            // 2. Signo Chinês
            String chinese = getChineseSign(year);
            
            // 3. Elemento
            String element = getElement(zodiac);
            
            // 4. Número da Sorte
            int luckyNumber = (name.length() + day + month) % 9 + 1;
            
            // 5. Numerologia
            int numerology = calculateNumerology(dateStr);
            
            // 6. Ascendente (Simplificado por hora)
            String ascendant = getAscendant(zodiac, timeStr);
            
            // 7. Sorte do Dia
            String luck = getDailyLuck(zodiac);

            StringBuilder result = new StringBuilder();
            result.append(getString(R.string.result_greeting, name)).append("\n");
            if (!place.isEmpty()) {
                result.append(getString(R.string.result_birth_place, place)).append("\n");
            }
            result.append("\n").append(getString(R.string.result_chinese, chinese));
            result.append("\n").append(getString(R.string.result_ascendant, ascendant));
            result.append("\n").append(getString(R.string.result_lucky_number, luckyNumber));
            result.append("\n").append(getString(R.string.result_numerology, numerology));
            result.append("\n").append(getString(R.string.result_luck_header)).append(luck);

            android.content.Intent intent = new android.content.Intent(this, LoadingActivity.class);
            intent.putExtra("zodiac", zodiac);
            intent.putExtra("element", element);
            intent.putExtra("result", result.toString());
            startActivity(intent);

        } catch (ParseException e) {
            Toast.makeText(this, R.string.error_invalid_date, Toast.LENGTH_SHORT).show();
        }
    }

    private String getZodiacSign(int day, int month) {
        if (month == 3) return (day >= 21) ? "Áries" : "Peixes";
        if (month == 4) return (day >= 20) ? "Touro" : "Áries";
        if (month == 5) return (day >= 21) ? "Gêmeos" : "Touro";
        if (month == 6) return (day >= 21) ? "Câncer" : "Gêmeos";
        if (month == 7) return (day >= 23) ? "Leão" : "Câncer";
        if (month == 8) return (day >= 23) ? "Virgem" : "Leão";
        if (month == 9) return (day >= 23) ? "Libra" : "Virgem";
        if (month == 10) return (day >= 23) ? "Escorpião" : "Libra";
        if (month == 11) return (day >= 22) ? "Sagitário" : "Escorpião";
        if (month == 12) return (day >= 22) ? "Capricórnio" : "Sagitário";
        if (month == 1) return (day >= 20) ? "Aquário" : "Capricórnio";
        if (month == 2) return (day >= 19) ? "Peixes" : "Aquário";
        return "Desconhecido";
    }

    private String getChineseSign(int year) {
        String[] signs = {"Macaco", "Galo", "Cão", "Porco", "Rato", "Boi", "Tigre", "Coelho", "Dragão", "Serpente", "Cavalo", "Cabra"};
        return signs[year % 12];
    }

    private String getElement(String zodiac) {
        switch (zodiac) {
            case "Áries": case "Leão": case "Sagitário": return "Fogo";
            case "Touro": case "Virgem": case "Capricórnio": return "Terra";
            case "Gêmeos": case "Libra": case "Aquário": return "Ar";
            default: return "Água";
        }
    }

    private int calculateNumerology(String date) {
        int sum = 0;
        for (char c : date.toCharArray()) {
            if (Character.isDigit(c)) sum += Character.getNumericValue(c);
        }
        while (sum > 9 && sum != 11 && sum != 22) {
            int temp = 0;
            while (sum > 0) {
                temp += sum % 10;
                sum /= 10;
            }
            sum = temp;
        }
        return sum;
    }

    private String getAscendant(String sunSign, String timeStr) {
        if (timeStr.isEmpty()) return "Incalculável (hora necessária)";
        try {
            int hour = Integer.parseInt(timeStr.split(":")[0]);
            String[] signs = {"Áries", "Touro", "Gêmeos", "Câncer", "Leão", "Virgem", "Libra", "Escorpião", "Sagitário", "Capricórnio", "Aquário", "Peixes"};
            int index = 0;
            for (int i = 0; i < signs.length; i++) {
                if (signs[i].equals(sunSign)) { index = i; break; }
            }
            int offset = (hour < 6) ? (hour + 24 - 6) / 2 : (hour - 6) / 2;
            return signs[(index + offset) % 12];
        } catch (Exception e) {
            return "Indefinido";
        }
    }

    private String getDailyLuck(String zodiac) {
        String[] lucks = getResources().getStringArray(R.array.daily_lucks);
        return lucks[Math.abs(zodiac.hashCode()) % lucks.length];
    }
}
