package com.videocraft.pro;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class WatermarkPreviewActivity extends AppCompatActivity {

    private int currentLang = Translations.LANG_AR;
    private void loadLang() {
        currentLang = getSharedPreferences("app_prefs", MODE_PRIVATE).getInt("lang", Translations.LANG_AR);
    }
    public String tr(String ar) {
        return Translations.tr(currentLang, ar);
    }

    public static final String EXTRA_VIDEO_PATH = "video_path";
    public static final String EXTRA_IMAGE_PATH = "image_path";
    public static final String EXTRA_BG_IS_IMAGE = "bg_is_image";
    public static final String EXTRA_IS_VIDEO = "is_video";
    public static final String EXTRA_RESULT_X = "result_x";
    public static final String EXTRA_RESULT_Y = "result_y";
    public static final String EXTRA_RESULT_W = "result_w";
    public static final String EXTRA_RESULT_H = "result_h";

    private ImageView imgVideo, imgWatermark;
    private FrameLayout container;
    private float wmX = 100, wmY = 200, wmW = 300, wmH = 300;
    private float lastX, lastY;
    private ScaleGestureDetector scaleDetector;
    private int videoW = 1080, videoH = 1920;
    private float dispW = 0, dispH = 0, offX = 0, offY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        loadLang();
        String videoPath = getIntent().getStringExtra(EXTRA_VIDEO_PATH);
        String imagePath = getIntent().getStringExtra(EXTRA_IMAGE_PATH);

        container = new FrameLayout(this);
        container.setBackgroundColor(0xFF505050);

        imgVideo = new ImageView(this);
        imgVideo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        boolean bgIsImage = getIntent().getBooleanExtra(EXTRA_BG_IS_IMAGE, false);
        if (bgIsImage) {
            try {
                Bitmap bmp = android.graphics.BitmapFactory.decodeFile(videoPath);
                if (bmp != null) {
                    videoW = bmp.getWidth();
                    videoH = bmp.getHeight();
                    imgVideo.setImageBitmap(bmp);
                }
            } catch (Throwable t) { }
        } else {
        try {
            MediaMetadataRetriever r = new MediaMetadataRetriever();
            r.setDataSource(videoPath);
            Bitmap bmp = r.getFrameAtTime(500000);
            String w = r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String h = r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String rot = r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            if (w != null) videoW = Integer.parseInt(w);
            if (h != null) videoH = Integer.parseInt(h);
            int rotation = 0;
            try { if (rot != null) rotation = Integer.parseInt(rot); } catch (Exception ex) {}
            if (rotation == 90 || rotation == 270) {
                int tmp = videoW;
                videoW = videoH;
                videoH = tmp;
            }
            r.release();
            if (bmp != null) imgVideo.setImageBitmap(bmp);
        } catch (Throwable t) { }
        }
        container.addView(imgVideo, new FrameLayout.LayoutParams(-1, -1));

        boolean isVideo = getIntent().getBooleanExtra(EXTRA_IS_VIDEO, false);
        imgWatermark = new ImageView(this);
        imgWatermark.setScaleType(ImageView.ScaleType.FIT_XY);
        try {
            Bitmap wm = null;
            if (isVideo) {
                MediaMetadataRetriever wmr = new MediaMetadataRetriever();
                wmr.setDataSource(imagePath);
                wm = wmr.getFrameAtTime(1000000);
                wmr.release();
            } else {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
                wm = BitmapFactory.decodeFile(imagePath, opts);
            }
if (wm != null) {
                    wm.setHasAlpha(true);
                    imgWatermark.setImageBitmap(wm);
                }
        } catch (Throwable t) { }
        FrameLayout.LayoutParams wlp = new FrameLayout.LayoutParams(300, 300);
        wlp.leftMargin = 100;
        wlp.topMargin = 200;
        container.addView(imgWatermark, wlp);

        // زر X لحذف الملصق
        final Button btnRemoveSticker = new Button(WatermarkPreviewActivity.this);
        btnRemoveSticker.setText("X");
        btnRemoveSticker.setTextSize(14);
        btnRemoveSticker.setBackgroundResource(R.drawable.button_click_effect);
        btnRemoveSticker.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
        btnRemoveSticker.setTextColor(0xFFFFFFFF);
        btnRemoveSticker.setPadding(15, 0, 15, 0);
        FrameLayout.LayoutParams xlp = new FrameLayout.LayoutParams(
            (int)(getResources().getDisplayMetrics().density * 45),
            (int)(getResources().getDisplayMetrics().density * 45));
        container.addView(btnRemoveSticker, xlp);
        btnRemoveSticker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imgWatermark.setVisibility(View.GONE);
                btnRemoveSticker.setVisibility(View.GONE);
            }
        });

        // تحديث موضع زر X عند تحريك الملصق
        final Runnable updateX = new Runnable() {
            public void run() {
                if (imgWatermark.getVisibility() != View.VISIBLE) return;
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) btnRemoveSticker.getLayoutParams();
                lp.leftMargin = (int) wmX + (int) wmW - 25;
                lp.topMargin = (int) wmY - 25;
                btnRemoveSticker.setLayoutParams(lp);
                btnRemoveSticker.postDelayed(this, 50);
            }
        };
        btnRemoveSticker.post(updateX);

        imgWatermark.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                scaleDetector.onTouchEvent(event);
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE && event.getPointerCount() == 1) {
                    wmX += event.getRawX() - lastX;
                    wmY += event.getRawY() - lastY;
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    updatePos();
                }
                return true;
            }
        });

        scaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            public boolean onScale(ScaleGestureDetector d) {
                wmW *= d.getScaleFactor();
                wmH *= d.getScaleFactor();
                if (wmW < 50) wmW = 50;
                if (wmH < 50) wmH = 50;
                if (wmW > 1500) wmW = 1500;
                if (wmH > 1500) wmH = 1500;
                updatePos();
                return true;
            }
        });

        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setBackgroundColor(0xDD222222);

        Button btnSquare = new Button(this); btnSquare.setText(tr("مربع"));
        btnSquare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { wmW = 300; wmH = 300; updatePos(); }
        });
        Button btnHoriz = new Button(this); btnHoriz.setText(tr("أفقي"));
        btnHoriz.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { wmW = 450; wmH = 180; updatePos(); }
        });
        Button btnVert = new Button(this); btnVert.setText(tr("عمودي"));
        btnVert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { wmW = 180; wmH = 450; updatePos(); }
        });
        Button btnRect = new Button(this); btnRect.setText(tr("مستطيل"));
        btnRect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { wmW = 400; wmH = 200; updatePos(); }
        });
        Button btnHeart = new Button(this); btnHeart.setText(tr("قلب"));
        btnHeart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { wmW = 280; wmH = 260; updatePos(); }
        });
        Button btnCircle = new Button(this); btnCircle.setText(tr("دائري"));
        btnCircle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { wmW = 300; wmH = 300; updatePos(); }
        });
        Button btnOval = new Button(this); btnOval.setText(tr("بيضاوي"));
        btnOval.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { wmW = 380; wmH = 260; updatePos(); }
        });

        topBar.addView(btnSquare, new LinearLayout.LayoutParams(0, -2, 1f));
        topBar.addView(btnHoriz, new LinearLayout.LayoutParams(0, -2, 1f));
        topBar.addView(btnVert, new LinearLayout.LayoutParams(0, -2, 1f));
        topBar.addView(btnRect, new LinearLayout.LayoutParams(0, -2, 1f));
        topBar.addView(btnHeart, new LinearLayout.LayoutParams(0, -2, 1f));
        topBar.addView(btnCircle, new LinearLayout.LayoutParams(0, -2, 1f));
        topBar.addView(btnOval, new LinearLayout.LayoutParams(0, -2, 1f));

        FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(-1, -2);
        tlp.gravity = Gravity.TOP;
        container.addView(topBar, tlp);

        LinearLayout bottom = new LinearLayout(this);
        bottom.setOrientation(LinearLayout.HORIZONTAL);
        bottom.setBackgroundColor(0xCC000000);
        Button btnCancel = new Button(this);
        btnCancel.setText(tr("X إلغاء"));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { setResult(Activity.RESULT_CANCELED); finish(); }
        });
        Button btnApply = new Button(this);
        btnApply.setText(tr("✔ تطبيق"));
        btnApply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int cw = container.getWidth();
                int ch = container.getHeight();
                if (cw == 0 || ch == 0) { finish(); return; }

                // حساب المنطقة المرئية الفعلية للفيديو داخل الـ container
                float scale = Math.min((float)cw / videoW, (float)ch / videoH);
                float dispW = videoW * scale;
                float dispH = videoH * scale;
                float offX = (cw - dispW) / 2f;
                float offY = (ch - dispH) / 2f;

                int rx = (int)((wmX - offX) / scale);
                int ry = (int)((wmY - offY) / scale);
                int rw = (int)(wmW / scale);
                int rh = (int)(wmH / scale);

                if (rx < 0) rx = 0;
                if (ry < 0) ry = 0;
                if (rx + rw > videoW) rw = videoW - rx;
                if (ry + rh > videoH) rh = videoH - ry;
                if (rw < 10) rw = 10;
                if (rh < 10) rh = 10;

                Intent result = new Intent();
                result.putExtra(EXTRA_RESULT_X, rx);
                result.putExtra(EXTRA_RESULT_Y, ry);
                result.putExtra(EXTRA_RESULT_W, rw);
                result.putExtra(EXTRA_RESULT_H, rh);
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });
        bottom.addView(btnCancel, new LinearLayout.LayoutParams(0, -2, 1f));
        bottom.addView(btnApply, new LinearLayout.LayoutParams(0, -2, 1f));
        FrameLayout.LayoutParams blp = new FrameLayout.LayoutParams(-1, -2);
        blp.gravity = Gravity.BOTTOM;
        container.addView(bottom, blp);

        setContentView(container);
    }

    private void updatePos() {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) imgWatermark.getLayoutParams();
        lp.leftMargin = (int) wmX;
        lp.topMargin = (int) wmY;
        lp.width = (int) wmW;
        lp.height = (int) wmH;
        imgWatermark.setLayoutParams(lp);
    }
}
