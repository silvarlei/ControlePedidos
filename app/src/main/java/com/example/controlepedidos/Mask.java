package com.example.controlepedidos;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

public abstract class Mask {
    public static String unmask(String s) {
        return s.replaceAll("[.]", "").replaceAll("[-]", "")
                .replaceAll("[/]", "").replaceAll("[(]", "")
                .replaceAll("[)]", "");
    }

    public static TextWatcher insert(final String mask, final EditText ediTxt) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String str = Mask.unmask(s.toString());
                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if (m != '#' && str.length() > old.length()) {
                        mascara += m;
                        continue;
                    }
                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                ediTxt.setText(mascara);
                ediTxt.setSelection(mascara.length());
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };
    }

    public static TextWatcher monetario(final EditText ediTxt ,final TextView TxtTroco,final TextView TxtValorTotal) {
        return new TextWatcher() {
            // Mascara monetaria para o preço do produto
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){
                    TxtTroco.setText("");

                    ediTxt.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[R$,.]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    current = formatted.replaceAll("[R$]", "");
                    ediTxt.setText(current);
                    ediTxt.setSelection(current.length());

                    ediTxt.addTextChangedListener(this);


                    String valor = ediTxt.getText().toString().replace("R$","").replace(",",".");
                    String valor1 = TxtValorTotal.getText().toString().replace("R$","").replace(",",".");


                    Double valorSomado = (Double.parseDouble( valor))  - ( Double.parseDouble(valor1));

                    if(valorSomado < 0)
                        TxtTroco.setTextColor(Color.RED);
                    else
                        TxtTroco.setTextColor(Color.parseColor("#000000"));

                    Locale ptBr = new Locale("pt", "BR") ;
                    String valorString = NumberFormat.getCurrencyInstance(ptBr).format(valorSomado);
                    TxtTroco.setText(valorString.toString());
                }
            }

        };
    }
}