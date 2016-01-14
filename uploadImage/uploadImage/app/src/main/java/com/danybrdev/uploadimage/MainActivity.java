package com.danybrdev.uploadimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasFile;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String UPLOAD_URL = "http://simplifiedcoding.16mb.com/ImageUpload/upload.php";
    public static final String UPLOAD_KEY = "image";
    public static final String TAG = "MY MESSAGE";

    private int PICK_IMAGE_REQUEST = 1;

    private Button buttonChoose;
    private Button buttonUpload;
    private Button buttonView;
    private ImageView imageView;

    private Bitmap bitmap;

    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        buttonView = (Button) findViewById(R.id.buttonViewImage);

        imageView = (ImageView) findViewById(R.id.imageView);

        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        BaasBox.builder(this).setAuthentication(BaasBox.Config.AuthType.SESSION_TOKEN)
                .setApiDomain("geeft.no-ip.org")
                .setPort(9000)
                .setAppCode("1234567890")
                .init();


        BaasUser user = BaasUser.withUserName("admin").setPassword("admin");
        user.login(new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> result) {
                if (result.isSuccess()) {
                    Log.d("LOG", "The user is currently logged in: " + result.value());
                } else {
                    Log.e("LOG", "Show error", result.error());
                }
            }
        });

    }

    private void showFileChooser() { //dal tutorial
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            Log.d("log","Uri is: " + filePath.toString());
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    public void uploadToBB(){
        //in = new FileInputStream(filePath.toString());
        //buf = new BufferedInputStream(in);
        //Bitmap bMap = BitmapFactory.decodeStream(buf);
        //InputStream data = (InputStream) new URL(url).getContent();; // input stream to upload
        //InputStream data = getContentResolver().openInputStream(filePath);// input stream to upload
        byte[] stream = getBytesFromBitmap(bitmap);
        Log.d("log","creato stream byte");
        BaasFile file = new BaasFile();
        file.upload(stream, new BaasHandler<BaasFile>() {
            @Override
            public void handle(BaasResult<BaasFile> baasResult) {
                if( baasResult.isSuccess() ) {
                    Log.d("LOG","File uploaded with permissions");
                    Toast.makeText(getApplicationContext(), "Image Uploaded!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("LOG","Deal with error",baasResult.error());
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == buttonChoose) {
            showFileChooser();
        }
        if(v == buttonUpload){
            uploadToBB();
        }

    }
    /*
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }*/


}