package com.example.myapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class Conexao {
    static BufferedReader bufferedReader = null;
    public static String pegarJson(String endereco){
        try{
            URL url = new URL("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"+endereco+"?unitGroup=metric&include=hours%2Cdays&lang=pt&key=46XJXBY3TA59RPEHB37JL4U7C&contentType=json");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            StringBuilder stringBuilder = new StringBuilder();
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String linha;
            while ((linha = bufferedReader.readLine()) != null){
                stringBuilder.append(linha);
            }
            return stringBuilder.toString();
        }catch (Exception e){
        e.printStackTrace();
        return "Ocorreu um erro";
        }finally {
            if(bufferedReader!=null){
                try{
                    bufferedReader.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
