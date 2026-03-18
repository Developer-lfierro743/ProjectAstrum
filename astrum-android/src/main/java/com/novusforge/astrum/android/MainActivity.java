package com.novusforge.astrum.android;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
    private SurfaceView surfaceView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        FrameLayout layout = new FrameLayout(this);
        surfaceView = new SurfaceView(this);
        
        layout.addView(surfaceView);
        setContentView(layout);
        
        initializeEngine();
    }
    
    private void initializeEngine() {
        new Thread(() -> {
            System.out.println("Astrum Android: Initializing engine...");
            
            com.novusforge.astrum.engine.RenderBackend backend = 
                com.novusforge.astrum.engine.EngineFactory.createBackend();
            
            backend.initialize();
            
            while (!isFinishing()) {
                backend.render();
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    break;
                }
            }
            
            backend.shutdown();
        }).start();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
