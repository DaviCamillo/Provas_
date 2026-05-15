package com.example.calculadoramonstra;

import android.content.Intent;
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

import android.widget.PopupMenu;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {

    private TextView tvFormula, tvResult;
    private StringBuilder currentInput = new StringBuilder();
    private double firstOperand = Double.NaN;
    private String currentOperator = "";
    private boolean isNewOperation = true;
    private static final String PREFS_NAME = "CalcPrefs";
    private static final String HISTORY_KEY = "history";

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

        tvFormula = findViewById(R.id.tv_formula);
        tvResult = findViewById(R.id.tv_result);

        findViewById(R.id.btn_menu).setOnClickListener(this::showMenu);

        setupNumericButtons();
        setupOperatorButtons();

        findViewById(R.id.btn_clear).setOnClickListener(v -> clear());
        findViewById(R.id.btn_del).setOnClickListener(v -> deleteLastCharacter());
        findViewById(R.id.btn_equal).setOnClickListener(v -> calculate());
    }

    private void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_bhaskara) {
                startActivity(new Intent(this, BhaskaraActivity.class));
                return true;
            } else if (id == R.id.menu_history) {
                showHistoryDialog();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void saveToHistory(String calculation) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String history = prefs.getString(HISTORY_KEY, "");
        history = calculation + "\n" + history;
        
        // Limitar histórico para as últimas 20 entradas
        String[] lines = history.split("\n");
        if (lines.length > 20) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 20; i++) {
                sb.append(lines[i]).append("\n");
            }
            history = sb.toString();
        }
        
        prefs.edit().putString(HISTORY_KEY, history).apply();
    }

    private void showHistoryDialog() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String history = prefs.getString(HISTORY_KEY, "Nenhum histórico disponível.");

        new MaterialAlertDialogBuilder(this)
                .setTitle("Histórico de Cálculos")
                .setMessage(history)
                .setPositiveButton("Fechar", null)
                .setNeutralButton("Limpar", (dialog, which) -> {
                    prefs.edit().remove(HISTORY_KEY).apply();
                    Toast.makeText(this, "Histórico limpo", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void setupNumericButtons() {
        int[] numericIds = {
                R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
                R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9, R.id.btn_dot
        };

        View.OnClickListener listener = v -> {
            Button b = (Button) v;
            if (isNewOperation) {
                currentInput.setLength(0);
                isNewOperation = false;
            }

            String text = b.getText().toString();
            if (text.equals(".") && currentInput.toString().contains(".")) {
                return;
            }

            currentInput.append(text);
            tvResult.setText(currentInput.toString());
        };

        for (int id : numericIds) {
            View btn = findViewById(id);
            if (btn != null) btn.setOnClickListener(listener);
        }
    }

    private void setupOperatorButtons() {
        int[] operatorIds = {
                R.id.btn_add, R.id.btn_sub, R.id.btn_mul, R.id.btn_div, R.id.btn_power, R.id.btn_root
        };

        View.OnClickListener listener = v -> {
            String op = ((Button) v).getText().toString();

            // Lógica Especial para Raiz Quadrada (Sempre Raiz Quadrada do número atual)
            if (op.equals("√")) {
                double valueToRoot = 0;
                String displayValue = tvResult.getText().toString().replace(',', '.');
                
                try {
                    // Tenta pegar o valor que já está no visor (resultado de um cálculo anterior ou número digitado)
                    if (!displayValue.equals("Erro")) {
                        valueToRoot = Double.parseDouble(displayValue);
                    } else {
                        return;
                    }
                } catch (NumberFormatException e) {
                    return;
                }

                if (valueToRoot < 0) {
                    tvResult.setText("Erro");
                    Toast.makeText(this, "Raiz negativa inválida", Toast.LENGTH_SHORT).show();
                } else {
                    double result = Math.sqrt(valueToRoot);
                    // Formatação: o símbolo √ aparece à direita/envolvendo o número que já estava lá
                    String formula = String.format(Locale.US, "√(%s) =", formatDouble(valueToRoot));
                    tvFormula.setText(formula);
                    tvResult.setText(formatDouble(result));
                    
                    saveToHistory(formula + " " + formatDouble(result));
                    
                    // Prepara para a próxima operação usando o resultado
                    currentInput.setLength(0);
                    currentInput.append(formatDouble(result));
                    firstOperand = result;
                    isNewOperation = true;
                }
                return;
            }

            if (currentInput.length() > 0 || !Double.isNaN(firstOperand)) {
                if (currentInput.length() > 0) {
                    try {
                        firstOperand = Double.parseDouble(currentInput.toString().replace(',', '.'));
                    } catch (NumberFormatException e) {
                        return;
                    }
                }
                currentOperator = op;
                tvFormula.setText(String.format(Locale.US, "%s %s", formatDouble(firstOperand), currentOperator));
                isNewOperation = true;
            }
        };

        for (int id : operatorIds) {
            View btn = findViewById(id);
            if (btn != null) btn.setOnClickListener(listener);
        }
    }

    private void calculate() {
        if (Double.isNaN(firstOperand) || currentInput.length() == 0) return;

        double secondOperand;
        try {
            secondOperand = Double.parseDouble(currentInput.toString().replace(',', '.'));
        } catch (NumberFormatException e) {
            return;
        }
        
        double result = 0;
        boolean error = false;

        switch (currentOperator) {
            case "+": result = firstOperand + secondOperand; break;
            case "-": result = firstOperand - secondOperand; break;
            case "*": result = firstOperand * secondOperand; break;
            case "/":
                if (secondOperand != 0) result = firstOperand / secondOperand;
                else error = true;
                break;
            case "^": result = Math.pow(firstOperand, secondOperand); break;
            case "√":
                // Lógica ajustada para ser mais intuitiva:
                // Se o usuário apertou √ primeiro (auto-preencheu 2), ele agora digita o número (secondOperand)
                // Se ele digitou o índice antes, funciona como Raiz de Índice N.
                // Ordem: ÍNDICE √ VALOR. (Ex: 2 √ 9 = 3)
                if (firstOperand != 0) {
                    if (secondOperand < 0) {
                        if (firstOperand == (long) firstOperand && Math.abs((long) firstOperand) % 2 != 0) {
                            result = -Math.pow(Math.abs(secondOperand), 1.0 / firstOperand);
                        } else {
                            error = true;
                        }
                    } else {
                        result = Math.pow(secondOperand, 1.0 / firstOperand);
                    }
                } else {
                    error = true;
                }
                break;
        }

        if (error) {
            tvResult.setText("Erro");
            Toast.makeText(this, "Operação inválida", Toast.LENGTH_SHORT).show();
        } else {
            // Se for raiz quadrada padrão (índice 2), mostra de forma mais bonita
            String formula;
            if (currentOperator.equals("√") && firstOperand == 2.0) {
                formula = String.format(Locale.US, "√(%s) =", formatDouble(secondOperand));
            } else {
                formula = String.format(Locale.US, "%s %s %s =", formatDouble(firstOperand), currentOperator, formatDouble(secondOperand));
            }
            
            tvFormula.setText(formula);
            tvResult.setText(formatDouble(result));
            
            saveToHistory(formula + " " + formatDouble(result));

            firstOperand = result;
            currentInput.setLength(0);
            currentInput.append(formatDouble(result));
        }
        isNewOperation = true;
    }

    private void deleteLastCharacter() {
        if (currentInput.length() > 0 && !isNewOperation) {
            // Caso 1: Apagando dígitos de um número que está sendo digitado agora
            currentInput.deleteCharAt(currentInput.length() - 1);
            tvResult.setText(currentInput.length() == 0 ? "0" : currentInput.toString());
        } else if (!currentOperator.isEmpty()) {
            // Caso 2: Já inseriu o primeiro número e o operador, mas ainda não o segundo.
            // Ao apagar aqui, removemos o operador e voltamos a editar o primeiro número.
            currentOperator = "";
            tvFormula.setText("");
            isNewOperation = false;
            // O currentInput já contém o primeiro operando (convertido em string)
            tvResult.setText(currentInput.length() == 0 ? "0" : currentInput.toString());
        } else if (currentInput.length() > 0 && isNewOperation) {
            // Caso 3: Acabou de calcular um resultado (ou raiz) e quer apagar o último dígito dele
            currentInput.deleteCharAt(currentInput.length() - 1);
            isNewOperation = false;
            String newValue = currentInput.toString();
            tvResult.setText(newValue.isEmpty() ? "0" : newValue);
            
            // Atualiza o operando interno também para permitir continuar cálculos
            try {
                firstOperand = newValue.isEmpty() ? 0 : Double.parseDouble(newValue);
            } catch (NumberFormatException e) {
                firstOperand = Double.NaN;
            }
        }
    }

    private void clear() {
        currentInput.setLength(0);
        firstOperand = Double.NaN;
        currentOperator = "";
        tvFormula.setText("");
        tvResult.setText("0");
        isNewOperation = true;
    }

    private String formatDouble(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) return "Erro";
        if (d == (long) d)
            return String.format(Locale.US, "%d", (long) d);
        else
            return String.format(Locale.US, "%.4f", d).replaceAll("0*$", "").replaceAll("\\.$", "");
    }
}
