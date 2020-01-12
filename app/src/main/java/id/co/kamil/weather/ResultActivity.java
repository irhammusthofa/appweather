package id.co.kamil.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private static final String TAG = ResultActivity.class.getSimpleName();

    private List<ItemList> list = new ArrayList<>();
    private GridView listView;
    private TextView txtWelcome, txtTemp, txtDeskripsi, txtHari;
    private ImageView iconTemp;
    private String name;
    private String kodepos;
    private Calendar currentCalender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        currentCalender = Calendar.getInstance();

        name = getIntent().getStringExtra("name");
        kodepos = getIntent().getStringExtra("kodepos");

        txtWelcome = findViewById(R.id.txtWelcome);
        txtTemp = findViewById(R.id.txtTemp);
        txtDeskripsi = findViewById(R.id.txtDeskripsi);
        txtHari = findViewById(R.id.txtHari);

        String myFormat = "EEEE dd MMMM, HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        txtWelcome.setText("Selamat Sore, " + name);
        txtHari.setText(sdf.format(currentCalender.getTime()));

        listView = findViewById(R.id.listView);
        loadCuaca(kodepos);
    }

    private void loadCuaca(String pos){
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = Uri.parse("http://api.openweathermap.org/data/2.5/forecast")
                .buildUpon()
                .appendQueryParameter("zip",pos)
                .toString();
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.dismiss();
                try {
                    String statusCode = response.getString("cod");
                    String message = response.getString("message");
                    if (statusCode.equals("200")){
                        JSONArray array = response.getJSONArray("list");
                        for (int i=0;i<array.length();i++){
                            JSONObject data = array.getJSONObject(i);
                            JSONObject main = data.getJSONObject("main");
                            JSONArray weather = data.getJSONArray("weather");
                            JSONObject objWeather = weather.getJSONObject(0);

                            String dtTxt = data.getString("dt_txt");
                            String temp = main.getString("temp");
                            String icon = objWeather.getString("icon");
                            String deskripsi = objWeather.getString("description");

                            ItemList item = new ItemList();
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dtTxt);
                                String hari = new SimpleDateFormat("EEEE").format(date);
                                String jam = new SimpleDateFormat("HH:mm").format(date);
                                String tgl = hari + ", " + jam;

                                item.setHari(tgl);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                            item.setTemp(temp);
                            item.setIcon(icon);
                            item.setDeskripsi(deskripsi);

                            list.add(item);
                        }
                        listView.setAdapter(new ListAdapter(list, ResultActivity.this));
                    }else {
                        Toast.makeText(ResultActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i(TAG,"Error Response : " + e.getMessage());
                    Toast.makeText(ResultActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Log.i(TAG,"Volley Error : " + error.getMessage());
                if (error instanceof NoConnectionError) {
                    Toast.makeText(ResultActivity.this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
                }else if (error instanceof NetworkError) {
                    Toast.makeText(ResultActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                }else if (error instanceof ServerError) {
                    Toast.makeText(ResultActivity.this, "Server sedang gangguan, ulangi beberapa saat lagi", Toast.LENGTH_SHORT).show();
                }else if (error instanceof TimeoutError) {
                    Toast.makeText(ResultActivity.this, "Timeout, silahkan ulangi lagi", Toast.LENGTH_SHORT).show();
                }else if (error instanceof AuthFailureError) {
                    Toast.makeText(ResultActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                }else if (error instanceof ParseError) {
                    Toast.makeText(ResultActivity.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("x-api-key", "b9e6a3d43264c4432929050d00f9faf1");
                return map;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        requestQueue.add(jsonObjectRequest);
    }

    private class ItemList{
        String hari, deskripsi, temp, icon;

        public ItemList() {
        }

        public String getHari() {
            return hari;
        }

        public void setHari(String hari) {
            this.hari = hari;
        }

        public String getDeskripsi() {
            return deskripsi;
        }

        public void setDeskripsi(String deskripsi) {
            this.deskripsi = deskripsi;
        }

        public String getTemp() {
            return temp;
        }

        public void setTemp(String temp) {
            this.temp = temp;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    private class ListAdapter extends BaseAdapter {
        private List<ItemList> list;
        private Context context;

        public ListAdapter(List<ItemList> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);

            TextView txtHari = view.findViewById(R.id.txtHari);
            TextView txtDeskripsi = view.findViewById(R.id.txtDeskripsi);
            TextView txtTemp = view.findViewById(R.id.txtTemp);
            ImageView iconTemp = view.findViewById(R.id.iconTemp);

            txtHari.setText(list.get(position).getHari());
            txtDeskripsi.setText(list.get(position).getDeskripsi());
            txtTemp.setText(list.get(position).getTemp());


            Picasso.with(context).load("http://openweathermap.org/img/wn/" + list.get(position).getIcon() + ".png").into(iconTemp);
            return view;
        }
    }
}
