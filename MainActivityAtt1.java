package com.example.climating;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    boolean Interface_Criada = false;
    int quant_view = 0;
    private TextView MostrarCidade;
    private ConstraintLayout menu;
    private ImageView iconImage;
    private TextView SetTemperatura;
    private TextView min_max;
    private EditText cidade;
    private Button button;
    private LinearLayout mContent;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        MostrarCidade = (TextView) findViewById(R.id.cidadeMostra);
        iconImage = (ImageView) findViewById(R.id.iconImage);
        mContent = (LinearLayout) findViewById(R.id.linear);
        SetTemperatura = (TextView) findViewById(R.id.temp);
        min_max = (TextView) findViewById(R.id.min_max);
        /*Mostrarcidade = (TextView) findViewById(R.id.endereco);

        textView =(TextView) findViewById(R.id.resul);*/
        menu = (ConstraintLayout) findViewById(R.id.back);
        cidade = (EditText) findViewById(R.id.cidade);


        RecuperaLocal();
        cidade.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {

                    try {
                        String endereco = String.valueOf(cidade.getText());
                        CompletableFuture<String> future = Conexao.pegarJson(endereco);

                        retira_teclado();
                        if(Interface_Criada==false){
                            cria_interface();
                            Interface_Criada=true;
                        }

                        String retorno = future.get();

                        TratarDados(retorno);

                    } catch(ExecutionException e){
                        throw new RuntimeException(e);
                    } catch(InterruptedException e){
                        throw new RuntimeException(e);
                    } catch(JSONException e){
                        throw new RuntimeException(e);
                    }
                }
                return false;
            }

        });
    }

    //Faz a request da API e mostra os dados na tela.
    public void TratarDados(String retorno) throws JSONException {
        System.out.println("Passou por tratar dados");
            String Mostrar = "";
            //Faz a chamada pra api e retorna
            JSONObject my_obj = new JSONObject(retorno);
            //cidade.setText(my_obj.getString(""));

            String local = my_obj.getString("resolvedAddress");
            String[] localSplit = local.split(",");

            cidade.setText("");
            MostrarCidade.setText(localSplit[0]+","+localSplit[1]);
            SalvaLocal(my_obj.getString("resolvedAddress"));

            //Faz o tratamento para chegar aos dados necessários
            JSONArray daysArray = my_obj.getJSONArray("days");
            JSONObject firstDay = daysArray.getJSONObject(0);
            int temperatura = firstDay.getInt("temp");
            int min = firstDay.getInt("tempmin");
            int max = firstDay.getInt("tempmax");

            //Chama a funcao que modifica o icone grande
            modifica_icon_geral(firstDay.getString("icon"));

            JSONArray hoursArray = firstDay.getJSONArray("hours");
            int j = 0;

            //Estrutura de repetição para percorrer todas as posições do array
            //Serve para adicionar a temperatura em todos os horários
            for (int i = 0; i < hoursArray.length(); i++) {
                JSONObject hourObject = hoursArray.getJSONObject(i);
                String datetime = hourObject.getString("datetime");

                //faz o tratamento das horas para aparecer apenas horas e minutos
                String[] dateSeparado = datetime.split(":");
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                String[] dispositivohora = currentTime.split(":");
                int dispositivo = Integer.valueOf(dispositivohora[0]);
                int hora_dia = Integer.valueOf(dateSeparado[0]);
                if (hora_dia < dispositivo) {

                } else if (hora_dia == dispositivo) {
                    SetTemperatura.setText(new Integer(temperatura).toString() + "°");
                    min_max.setText("Max:" + new Integer(max).toString() + "° Min:" + new Integer(min).toString() + "°");
                } else {
                    int temp = hourObject.getInt("temp");
                    int chuvaProb = hourObject.getInt("precipprob");

                    TextView chuva = findViewById(90+j);
                    TextView temphora = findViewById(60 + j);
                    TextView horario = findViewById(30 + j);
                    ImageView image = findViewById(j);

                    j++;
                    chuva.setText(chuvaProb+"%");
                    temphora.setText(temp + "°C");
                    horario.setText(dateSeparado[0] + ":" + dateSeparado[1]);

                    if (hourObject.getString("icon").equals("rain")) {
                        image.setBackground(getResources().getDrawable(R.drawable.chuva));
                    } else if (hourObject.getString("icon").equals("partly-cloudy-day")) {
                        image.setBackground(getResources().getDrawable(R.drawable.parcial_nubl));
                    } else if (hourObject.getString("icon").equals("cloudy")) {
                        image.setBackground(getResources().getDrawable(R.drawable.nublado));
                    } else if (hourObject.getString("icon").equals("clear-day")) {
                        image.setBackground(getResources().getDrawable(R.drawable.sun));
                    } else if (hourObject.getString("icon").equals("partly-cloudy-night")) {
                        image.setBackground(getResources().getDrawable(R.drawable.cloudy_night));
                    } else if (hourObject.getString("icon").equals("clear-night")) {
                        image.setBackground(getResources().getDrawable(R.drawable.clear_night));
                    } else if (hourObject.getString("icon").equals("wind")) {
                        image.setBackground(getResources().getDrawable(R.drawable.windy));
                    }
                }
            }
        }

    //função que reinicia o app
    /*public void reset() {
        finish();
        startActivity(getIntent());
        overridePendingTransition(2, 0);
    }*/
    public void retira_teclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }

    public void modifica_icon_geral(String icon) {
        if (icon.equals("rain")) {
            iconImage.setBackground(getResources().getDrawable(R.drawable.chuva));
        } else if (icon.equals("partly-cloudy-day")) {
            iconImage.setBackground(getResources().getDrawable(R.drawable.parcial_nubl));
        } else if (icon.equals("cloudy")) {
            iconImage.setBackground(getResources().getDrawable(R.drawable.nublado));
        } else if (icon.equals("clear-day")) {
            iconImage.setBackground(getResources().getDrawable(R.drawable.sun));
        } else if (icon.equals("partly-cloudy-night")) {
            iconImage.setBackground(getResources().getDrawable(R.drawable.cloudy_night));
        } else if (icon.equals("clear-night")) {
            iconImage.setBackground(getResources().getDrawable(R.drawable.clear_night));
        } else if (icon.equals("wind")) {
            iconImage.setBackground(getResources().getDrawable(R.drawable.windy));
        }
    }

    public void cria_interface() {
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String[] dispositivohora = currentTime.split(":");
        int dispositivo = Integer.valueOf(dispositivohora[0]);

        quant_view = 23 - dispositivo;

        for (int i = 0; i < quant_view; i++) {
            TextView horario = new TextView(this);
            TextView temphora = new TextView(this);
            TextView chuva = new TextView(this);
            ImageView image = new ImageView(this);
            LinearLayout linear = new LinearLayout(this);

            //muda a cor de fundo
            temphora.setBackgroundColor(getResources().getColor(R.color.hora, getResources().newTheme()));
            chuva.setBackgroundColor(getResources().getColor(R.color.hora, getResources().newTheme()));
            //muda a cor do texto
            temphora.setTextColor(getResources().getColor(R.color.white, getResources().newTheme()));
            chuva.setTextColor(getResources().getColor(R.color.white, getResources().newTheme()));
            //tamanho do texto
            temphora.setTextSize(20);
            chuva.setTextSize(20);
            //adiciona parametros de margen
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //params.setMargins(10,10,10,10);
            params.setMarginEnd(20);
            //----------------------------horario------------------
            //adiciona os parametros no txtItem.
            horario.setLayoutParams(params);
            horario.setPadding(10, 0, 5, 20);

            // Informamos um id
            image.setId(i);
            horario.setId(30 + i);
            temphora.setId(60 + i);
            chuva.setId(90+i);

            //muda a cor de fundo
            horario.setBackgroundColor(getResources().getColor(R.color.hora, getResources().newTheme()));

            //muda a cor do texto
            horario.setTextColor(getResources().getColor(R.color.white, getResources().newTheme()));

            //tamanho do texto
            horario.setTextSize(20);

            //adiciona parametros de margen
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //params.setMargins(10,10,10,10);
            //params.setMarginEnd(20);
            params.setMargins(10, 10, 10, 10);

            //adiciona os parametros no txtItem.
            horario.setLayoutParams(params);
            horario.setPadding(10, 0, 5, 20);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(50, 50);
            image.setLayoutParams(layoutParams);

            // Adiciona no Linear Layout
            linear.setOrientation(LinearLayout.VERTICAL);

            temphora.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            horario.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            chuva.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            layoutParams = new LinearLayout.LayoutParams(50, 50);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(0, 15, 0, 15);

            image.setLayoutParams(layoutParams);

            //adiciona os parametros no txtItem.
            System.out.println("Gerou os Views");
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);



            linear.addView(temphora);
            linear.addView(image);
            linear.addView(chuva);
            linear.addView(horario);

            mContent.addView(linear);
        }
    }
    public void SalvaLocal(String localizacao) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.cidade), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("localizacao", localizacao);
        editor.apply();
    }
    public void RecuperaLocal() {
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.cidade), Context.MODE_PRIVATE);
        String local = sharedPref.getString("localizacao", "");
        if (local.equals("")) {
            SalvaLocal("New York");
            PossuiSalvo(local);
        } else {
            PossuiSalvo(local);
        }
    }
    public void PossuiSalvo(String local) {
        try {
            CompletableFuture<String> future = Conexao.pegarJson(local);

            retira_teclado();
            cria_interface();

            String retorno = future.get();
            TratarDados(retorno);
            System.out.println("Passou possui salvo");
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
