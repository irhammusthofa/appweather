package id.co.kamil.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText edtNama;
    private EditText edtKodePos;
    private Button btnLanjutkan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtNama = (EditText) findViewById(R.id.edtNamaLengkap);
        edtKodePos = (EditText) findViewById(R.id.edtKodePos);
        btnLanjutkan = (Button) findViewById(R.id.btnLanjutkan);

        btnLanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edtNama.getText())){
                    edtNama.setError(getString(R.string.required_field));
                    edtNama.requestFocus();
                }else if(TextUtils.isEmpty(edtKodePos.getText())){
                    edtKodePos.setError(getString(R.string.required_field));
                    edtKodePos.requestFocus();
                }else{
                    Intent intent = new Intent(MainActivity.this,ResultActivity.class);
                    intent.putExtra("name",edtNama.getText().toString());
                    intent.putExtra("kodepos",edtKodePos.getText().toString());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
