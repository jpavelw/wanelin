package com.iste776.jpavelw.wanelin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FileNameActivity extends AppCompatActivity {

    private final String MY_PREFERENCES = "MY_PREF_LOGIN";
    public static final String FILE_NAME_KEY = "USERNAME_KEY";
    private SharedPreferences sp;
    private EditText txtFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_name);

        this.txtFileName = (EditText) findViewById(R.id.txt_file_name);
        Button btnSave = (Button) findViewById(R.id.btn_save_fname);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = txtFileName.getText().toString().trim();
                if(!fileName.equals("")){
                    fileName += ".csv";
                    sp = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(FILE_NAME_KEY, fileName);
                    editor.apply();
                    Intent intent = new Intent(FileNameActivity.this, MainActivity.class);
                    intent.putExtra(FILE_NAME_KEY, fileName);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_saving_file), Toast.LENGTH_LONG).show();
                }
            }
        });

        if(ContextCompat.checkSelfPermission(FileNameActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(FileNameActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            sp = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
            String fileName = sp.getString(FILE_NAME_KEY, null);
            if(fileName != null){
                Intent intent = new Intent(FileNameActivity.this, MainActivity.class);
                intent.putExtra(FILE_NAME_KEY, fileName);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length == 1){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                finish();
            }
        }
    }
}
