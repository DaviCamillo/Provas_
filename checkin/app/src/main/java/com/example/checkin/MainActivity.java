package com.example.checkin;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private String passengerName;
    private String passengerCpf;
    private String travelDate;
    private String selectedSeat = "";
    private final List<MaterialButton> seatButtons = new ArrayList<>();
    private static final List<CheckinModel> myCheckins = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showMenu();
    }

    private void showMenu() {
        setContentView(R.layout.layout_menu);
        findViewById(R.id.btn_new_checkin).setOnClickListener(v -> {
            resetData();
            showRegistration();
        });
        findViewById(R.id.btn_view_checkins).setOnClickListener(v -> showMyCheckins());
    }

    private void resetData() {
        passengerName = null;
        passengerCpf = null;
        travelDate = null;
        selectedSeat = "";
    }

    private void showRegistration() {
        setContentView(R.layout.layout_register);
        EditText etName = findViewById(R.id.et_name);
        EditText etCpf = findViewById(R.id.et_cpf);
        EditText etDate = findViewById(R.id.et_date);
        Button btnNext = findViewById(R.id.btn_next_to_seats);
        View btnBack = findViewById(R.id.btn_back_to_menu_from_reg);

        if (passengerName != null) etName.setText(passengerName);
        if (passengerCpf != null) etCpf.setText(passengerCpf);
        if (travelDate != null) etDate.setText(travelDate);

        etCpf.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) { isUpdating = false; return; }
                String str = s.toString().replaceAll("\\D", "");
                if (str.length() > 11) str = str.substring(0, 11);
                StringBuilder mask = new StringBuilder();
                if (!str.isEmpty()) mask.append(str.substring(0, Math.min(3, str.length())));
                if (str.length() > 3) mask.append(".").append(str.substring(3, Math.min(6, str.length())));
                if (str.length() > 6) mask.append(".").append(str.substring(6, Math.min(9, str.length())));
                if (str.length() > 9) mask.append("-").append(str.substring(9, str.length()));
                isUpdating = true;
                etCpf.setText(mask.toString());
                etCpf.setSelection(mask.length());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String date = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, (month + 1), year);
                etDate.setText(date);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnNext.setOnClickListener(v -> {
            passengerName = etName.getText().toString().trim();
            passengerCpf = etCpf.getText().toString().trim();
            travelDate = etDate.getText().toString().trim();
            
            if (passengerName.isEmpty() || passengerCpf.isEmpty() || travelDate.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar CPF Único
            for (CheckinModel c : myCheckins) {
                if (c.getCpf().equals(passengerCpf)) {
                    Toast.makeText(this, "Este CPF já possui um check-in realizado!", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            showSeatSelection();
        });

        if (btnBack != null) btnBack.setOnClickListener(v -> showMenu());
    }

    private void showSeatSelection() {
        setContentView(R.layout.layout_seats);
        GridLayout gridSeats = findViewById(R.id.grid_seats);
        TextView tvSelectedSeat = findViewById(R.id.tv_selected_seat);
        Button btnNext = findViewById(R.id.btn_next_to_review);
        View btnBack = findViewById(R.id.btn_back_to_register);

        // Avião de 20 lugares (5 fileiras x 4 assentos)
        String[] columns = {"A", "B", "", "C", "D"};
        int rows = 5;
        seatButtons.clear();
        gridSeats.removeAllViews();
        gridSeats.setColumnCount(columns.length);

        float scale = getResources().getDisplayMetrics().density;
        int buttonSize = (int) (60 * scale + 0.5f); // Botões maiores para 20 lugares
        int margin = (int) (8 * scale + 0.5f);

        for (int r = 1; r <= rows; r++) {
            for (int c = 0; c < columns.length; c++) {
                if (columns[c].isEmpty()) {
                    // Corredor com número da fileira
                    TextView tvRow = new TextView(this);
                    tvRow.setText(String.valueOf(r));
                    tvRow.setGravity(Gravity.CENTER);
                    tvRow.setTextColor(ContextCompat.getColor(this, R.color.grey_text));
                    tvRow.setTextSize(14);
                    tvRow.setTypeface(null, android.graphics.Typeface.BOLD);
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = (int)(30 * scale);
                    params.height = buttonSize;
                    tvRow.setLayoutParams(params);
                    gridSeats.addView(tvRow);
                    continue;
                }

                final String seatLabel = r + columns[c];
                MaterialButton seatButton = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonStyle);
                seatButton.setText(seatLabel);
                seatButton.setTextSize(12);
                seatButton.setPadding(0, 0, 0, 0);
                seatButton.setInsetTop(0);
                seatButton.setInsetBottom(0);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = buttonSize;
                params.height = buttonSize;
                params.setMargins(margin, margin, margin, margin);
                seatButton.setLayoutParams(params);

                // Verificar se o assento já está ocupado
                boolean isOccupied = false;
                for (CheckinModel checkin : myCheckins) {
                    if (checkin.getSeat().equals(seatLabel)) {
                        isOccupied = true;
                        break;
                    }
                }

                if (isOccupied) {
                    seatButton.setEnabled(false);
                    seatButton.setBackgroundColor(ContextCompat.getColor(this, R.color.seat_occupied));
                    seatButton.setTextColor(ContextCompat.getColor(this, R.color.grey_text));
                    seatButton.setAlpha(0.6f);
                } else {
                    updateSeatButtonStyle(seatButton, seatLabel.equals(selectedSeat));
                    seatButton.setOnClickListener(v -> {
                        selectedSeat = seatLabel;
                        tvSelectedSeat.setText(selectedSeat);
                        btnNext.setEnabled(true);
                        for (MaterialButton b : seatButtons) {
                            String bLabel = b.getText().toString();
                            boolean bOccupied = false;
                            for (CheckinModel ci : myCheckins) {
                                if (ci.getSeat().equals(bLabel)) {
                                    bOccupied = true;
                                    break;
                                }
                            }
                            if (!bOccupied) {
                                updateSeatButtonStyle(b, bLabel.equals(selectedSeat));
                            }
                        }
                    });
                }
                seatButtons.add(seatButton);
                gridSeats.addView(seatButton);
            }
        }
        btnNext.setEnabled(!selectedSeat.isEmpty());
        if (!selectedSeat.isEmpty()) tvSelectedSeat.setText(selectedSeat);

        btnNext.setOnClickListener(v -> showReview());
        btnBack.setOnClickListener(v -> showRegistration());
    }

    private void updateSeatButtonStyle(MaterialButton button, boolean isSelected) {
        if (isSelected) {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.seat_selected));
            button.setTextColor(Color.WHITE);
            button.setStrokeWidth(0);
        } else {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.primary)); // Laranja para livre
            button.setTextColor(Color.WHITE);
            button.setStrokeWidth(0);
        }
    }

    private void showReview() {
        setContentView(R.layout.layout_review);
        ((TextView) findViewById(R.id.tv_review_name)).setText(passengerName);
        ((TextView) findViewById(R.id.tv_review_cpf)).setText(passengerCpf);
        ((TextView) findViewById(R.id.tv_review_date)).setText(travelDate);
        ((TextView) findViewById(R.id.tv_review_seat)).setText(selectedSeat);
        findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            myCheckins.add(new CheckinModel(passengerName, passengerCpf, travelDate, selectedSeat));
            showSuccess();
        });
        findViewById(R.id.btn_back_to_seats).setOnClickListener(v -> showSeatSelection());
    }

    private void showSuccess() {
        setContentView(R.layout.layout_success);
        ((TextView) findViewById(R.id.tv_success_seat)).setText(selectedSeat);
        findViewById(R.id.btn_back_to_menu_from_success).setOnClickListener(v -> showMenu());
    }

    private void showMyCheckins() {
        setContentView(R.layout.layout_my_checkins);
        RecyclerView rv = findViewById(R.id.rv_checkins);
        LinearLayout tvEmpty = findViewById(R.id.tv_empty_list);
        if (myCheckins.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setAdapter(new CheckinAdapter(myCheckins));
        }
        findViewById(R.id.btn_back_from_list).setOnClickListener(v -> showMenu());
    }

    private static class CheckinAdapter extends RecyclerView.Adapter<CheckinAdapter.ViewHolder> {
        private final List<CheckinModel> items;
        CheckinAdapter(List<CheckinModel> items) { this.items = items; }
        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkin, parent, false);
            return new ViewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CheckinModel item = items.get(position);
            holder.textName.setText(item.getName());
            holder.textDetails.setText("CPF: " + item.getCpf() + " | " + item.getDate());
            holder.textSeat.setText(item.getSeat());
        }
        @Override public int getItemCount() { return items.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textName, textDetails, textSeat;
            ViewHolder(View v) {
                super(v);
                textName = v.findViewById(R.id.tv_item_name);
                textDetails = v.findViewById(R.id.tv_item_details);
                textSeat = v.findViewById(R.id.tv_item_seat);
            }
        }
    }
}