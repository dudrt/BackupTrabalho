package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static TextView textView;
    private static TextView Mostrarcidade;
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

        mContent = (LinearLayout) findViewById(R.id.linear);
        /*Mostrarcidade = (TextView) findViewById(R.id.endereco);

        textView =(TextView) findViewById(R.id.resul);*/
        button = (Button) findViewById(R.id.button);
        cidade = (EditText) findViewById(R.id.cidade);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String endereco = String.valueOf(cidade.getText());
                String retorno = Conexao.pegarJson(endereco);
                try {
                    TratarDados(retorno);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });

    }

    //Faz a request da API e mostra os dados na tela.
    public void TratarDados(String retorno) throws JSONException {
        String Mostrar = "";
        JSONObject my_obj = new JSONObject(retorno);



        //Faz o tratamento para chegar aos dados necessários
        JSONArray daysArray = my_obj.getJSONArray("days");
        JSONObject firstDay = daysArray.getJSONObject(0);
        JSONArray hoursArray = firstDay.getJSONArray("hours");
        //Mostrarcidade.setText(my_obj.getString("resolvedAddress"));

        //Estrutura de repetição para percorrer todas as posições do array
        //Serve para adicionar a temperatura em todos os horários
        for (int i = 0; i < hoursArray.length(); i++) {

            //cria uma nova textView
            TextView txtItem = new TextView(this);

            //muda a cor de fundo
            txtItem.setBackgroundColor(getResources().getColor(R.color.fundo_tempo, getResources().newTheme()));

            //muda a cor do texto
            txtItem.setTextColor(getResources().getColor(R.color.white, getResources().newTheme()));

            //tamanho do texto
            txtItem.setTextSize(40);

            //adiciona parametros de margen
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //params.setMargins(10,10,10,10);
            params.setMarginEnd(20);

            //adiciona os parametros no txtItem.
            txtItem.setLayoutParams(params);
            txtItem.setPadding(10,0,5,20);

            // Informamos um id
            txtItem.setId( i );
            JSONObject hourObject = hoursArray.getJSONObject(i);
            String datetime = hourObject.getString("datetime");
            int temp = hourObject.getInt("temp");

            //faz o tratamento das horas para aparecer apenas horas e minutos
            String[] dateSeparado =datetime.split(":");

            //adiciona o texto no textview
            txtItem.setText(dateSeparado[0]+":"+dateSeparado[1]+"\n"+temp);

            // Adiciona no Linear Layout
            mContent.addView(txtItem);




            //JSONObject hourObject = hoursArray.getJSONObject(i);
            //String datetime = hourObject.getString("datetime");
            //int temp = hourObject.getInt("temp");
            //System.out.println(datetime+":"+temp);
            //Mostrar = Mostrar + datetime +" Temperatura-"+temp+"\n";
        }




    }
    //função que reinicia o app
    public void reset(){
        finish();
        startActivity(getIntent());
        overridePendingTransition(2, 0);
    }
}




