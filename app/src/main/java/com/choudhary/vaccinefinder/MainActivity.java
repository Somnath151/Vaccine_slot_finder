package com.choudhary.vaccinefinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

     RecyclerView recyclerView;
     Adapter adapter;
     EditText editText;
     Button button;
     ProgressBar progressBar;

     ArrayList<Model> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);

        recyclerView = findViewById(R.id.recylerview);
        editText = findViewById(R.id.pinedit);
        button = findViewById(R.id.button);
        arrayList = new ArrayList<>();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editText.getText().toString())){
                    editText.setError("Enter pincode");
                    return;
                }
                if (editText.getText().toString().length()<6){
                    editText.setError("Invalid Pincode");
                    return;
                }


                     progressBar.setVisibility(View.VISIBLE);
                String pincode  = editText.getText().toString().trim();

                arrayList.clear();

                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH)+1;

               // Toast.makeText(MainActivity.this,"Today's Date: " + currentDay+"-" + currentMonth+"-"+ currentYear, Toast.LENGTH_SHORT).show();
                 String date = currentDay+"-"+currentMonth+"-"+currentYear;
                Toast.makeText(MainActivity.this, date, Toast.LENGTH_SHORT).show();

                String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByPin?pincode="+pincode+"&date="+date;

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    JSONArray jsonArray = response.getJSONArray("sessions");
                                    Log.v("DAta", jsonArray.toString());

                                    for (int i = 0 ; i <jsonArray.length();i++ ){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        String center_id = jsonObject.getString("center_id");
                                        String name = jsonObject.getString("name");
                                        String address = jsonObject.getString("address");
                                        String state_name = jsonObject.getString("state_name");
                                        String district_name = jsonObject.getString("district_name");
                                        String pincode = jsonObject.getString("pincode");
                                        String available_capacity = jsonObject.getString("available_capacity");
                                        String fee_type = jsonObject.getString("fee_type");
                                        String vaccine = jsonObject.getString("vaccine");
                                        String date = jsonObject.getString("date");

                                        Model model = new Model(center_id,name,address,state_name,district_name,pincode,available_capacity,fee_type,vaccine,date);
                                        arrayList.add(model);


                                    }

                                    adapter = new Adapter(MainActivity.this,arrayList);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.INVISIBLE);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, "Error" +error.getLocalizedMessage() , Toast.LENGTH_SHORT).show();
                            }
                        });

// Access the RequestQueue through your singleton class.
                MySingleton.getInstance(MainActivity.this).addToRequestQueue(jsonObjectRequest);


            }
        });




    }
}