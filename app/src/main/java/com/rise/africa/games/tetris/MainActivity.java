package com.rise.africa.games.tetris;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class MainActivity extends Activity {
    TetrisCtrl mTetrisCtrl;
    Point mScreenSize = new Point(0, 0);
    Point mMousePos = new Point(-1, -1);
    int mCellSize = 0;
    boolean mIsTouchMove = false;
    Button btnBottom, btnLeft, btnRight;

    private AdView mAdViewBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        btnBottom = (Button) findViewById(R.id.btnBottom);

        btnBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTetrisCtrl.block2Bottom();
            }
        });


        btnLeft = (Button) findViewById(R.id.btnLeft);

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTetrisCtrl.block2Left();
            }
        });


        btnRight = (Button) findViewById(R.id.btnRight);

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTetrisCtrl.block2Right();
            }
        });

        DisplayMetrics dm = this.getApplicationContext().getResources().getDisplayMetrics();
        mScreenSize.x = dm.widthPixels;
        mScreenSize.y = dm.heightPixels;
        mCellSize = (int)(mScreenSize.x / 8);

        initTetrisCtrl();


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdViewBottom = findViewById(R.id.adViewBottom);
        AdRequest adRequestBot = new AdRequest.Builder().build();
        mAdViewBottom.loadAd(adRequestBot);

    }

    void initTetrisCtrl() {
        mTetrisCtrl = new TetrisCtrl(this, MainActivity.this);
        for(int i=0; i <= 7; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cell0 + i);
            mTetrisCtrl.addCellImage(i, bitmap);
        }
        RelativeLayout layoutCanvas = findViewById(R.id.layoutCanvas);
        layoutCanvas.addView(mTetrisCtrl);
    }

//    void onClick(View v) {
//        switch( v.getId() ) {
//            case R.id.btnLeft :
//                mTetrisCtrl.block2Left();
//                break;
//            case R.id.btnRight :
//                mTetrisCtrl.block2Right();
//                break;
//            case R.id.btnBottom :
//                mTetrisCtrl.block2Bottom();
//                break;
//            /*case R.id.btnRotate :
//                mTetrisCtrl.block2Rotate();
//                break;*/
//        }
//    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch( event.getAction() ) {
            case MotionEvent.ACTION_DOWN :
                mIsTouchMove = false;
                if( event.getY() < (int)(mScreenSize.y * 0.75)) {
                    mMousePos.x = (int) event.getX();
                    mMousePos.y = (int) event.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE :
                if( mMousePos.x < 0 )
                    break;
                if( (event.getX() - mMousePos.x) > mCellSize ) {
                    mTetrisCtrl.block2Right();
                    mMousePos.x = (int) event.getX();
                    mMousePos.y = (int) event.getY();
                    mIsTouchMove = true;
                } else if( (mMousePos.x - event.getX()) > mCellSize ) {
                    mTetrisCtrl.block2Left();
                    mMousePos.x = (int) event.getX();
                    mMousePos.y = (int) event.getY();
                    mIsTouchMove = true;
                }
                break;
            case MotionEvent.ACTION_UP :
                if( mIsTouchMove == false && mMousePos.x > 0 )
                    mTetrisCtrl.block2Rotate();
                mMousePos.set(-1, -1);
                break;
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTetrisCtrl.pauseGame();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mTetrisCtrl.restartGame();
    }
}
