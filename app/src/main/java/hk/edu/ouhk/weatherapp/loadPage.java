package hk.edu.ouhk.weatherapp;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class loadPage extends AppCompatActivity {
    ImageView animImage;
    AnimationDrawable originalAnim;
    private Handler ha=new Handler();
    Intent home = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_page);
        animImage = findViewById(R.id.an);
        originalAnim = (AnimationDrawable) ResourcesCompat.getDrawable(getResources(),R.drawable.load,null);
        originalAnim.setOneShot(true);
        originalAnim.setVisible(true,true);
        animImage.setImageDrawable(originalAnim);
        originalAnim.start();
        originalAnim.setVisible(true,true);
        home.setClass( this, MainActivity.class );
        ha.postDelayed(delayrun,5000);

    }
    private  Runnable delayrun=new Runnable() {
        @Override
        public void run() {
            startActivity( home );
            close();
        }
    };

    private  void  close (){
        this.finish();

    }
}

