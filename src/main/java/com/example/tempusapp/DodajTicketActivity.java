package com.example.tempusapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class DodajTicketActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText ime;
    private EditText opis;
    private Spinner spinner_stanje;
    private String stanjeTicketa;
    private EditText idUporabnika;
    private EditText idProjekta;
    private TextView status;

    private RequestQueue requestQueue;
    private String url = "https://tempus-is.azurewebsites.net/api/users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_ticket);

        ime = (EditText) findViewById(R.id.teIme);
        opis = (EditText) findViewById(R.id.teOpis);

        spinner_stanje = findViewById(R.id.spinner_stanje);
        ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this, R.array.stanja, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_stanje.setAdapter(adapter);

        idUporabnika = (EditText) findViewById(R.id.teIdUporabnika);
        idProjekta = (EditText) findViewById(R.id.teIdProjekta);
        status = (TextView) findViewById(R.id.stanje);

        requestQueue = Volley.newRequestQueue(getApplicationContext());


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectStanje = parent.getItemAtPosition(position).toString();
        stanjeTicketa = selectStanje;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void addTicket(View view){
        this.status.setText("Posting to " + url);
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("ime", ime.getText());
            jsonBody.put("opis", opis.getText());
            jsonBody.put("stanje", stanjeTicketa);
            jsonBody.put("idUporabnika", idUporabnika.getText());
            jsonBody.put("idProjekta", idProjekta.getText());

            final String mRequestBody = jsonBody.toString();

            status.setText(mRequestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG_VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        status.setText(responseString);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }

                @Override
                public Map<String,String> getHeaders() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ApiKey", "SecretKey");
                    params.put("Content-Type", "application/json");
                    return params;
                }
            };

            requestQueue.add(stringRequest);;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}