package com.videocraft.pro;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CropActivity extends AppCompatActivity {

    private int currentLang = Translations.LANG_AR;
    private void loadLang() {
        currentLang = getSharedPreferences("app_prefs", MODE_PRIVATE).getInt("lang", Translations.LANG_AR);
    }
    public String tr(String ar) {
        return Translations.tr(currentLang, ar);
    }

    public static final String EXTRA_VIDEO_PATH = "video_path";
    public static final String EXTRA_RESULT_X = "result_x";
    public static final String EXTRA_RESULT_Y = "result_y";
    public static final String EXTRA_RESULT_W = "result_w";
    public static final String EXTRA_RESULT_H = "result_h";

    private ImageView imgVideo;
    private FrameLayout container;
    private View cropBox;
    private View handleTL, handleTR, handleBL, handleBR;
    private View dimTop, dimBottom, dimLeft, dimRight;
    private int videoW = 1080, videoH = 1920;
    private int dispW = 0, dispH = 0;
    private float offX = 0, offY = 0;
    private float scale = 1f;
    private float cropL = 0, cropT = 0, cropR = 0, cropB = 0;
    private int handleSize = 100;
    private int controlsHeight = 220;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLang();
        String videoPath = getIntent().getStringExtra(EXTRA_VIDEO_PATH);

        container = new FrameLayout(this);
        container.setBackgroundColor(0xFF000000);

        imgVideo = new ImageView(this);
        imgVideo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgVideo.setClickable(false);
        imgVideo.setFocusable(false);
        try {
            MediaMetadataRetriever r = new MediaMetadataRetriever();
            r.setDataSource(videoPath);
            Bitmap bmp = r.getFrameAtTime(500000);
            String w = r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String h = r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            if (w != null) videoW = Integer.parseInt(w);
            if (h != null) videoH = Integer.parseInt(h);
            r.release();
            if (bmp != null) imgVideo.setImageBitmap(bmp);
        } catch (Exception e) { }
        container.addView(imgVideo, new FrameLayout.LayoutParams(-1, -1));

        // المناطق المعتمة
        dimTop = new View(this); dimTop.setBackgroundColor(0xAA000000);
        dimBottom = new View(this); dimBottom.setBackgroundColor(0xAA000000);
        dimLeft = new View(this); dimLeft.setBackgroundColor(0xAA000000);
        dimRight = new View(this); dimRight.setBackgroundColor(0xAA000000);
        container.addView(dimTop);
        container.addView(dimBottom);
        container.addView(dimLeft);
        container.addView(dimRight);

        // مربع القص
        cropBox = new View(this);
        GradientDrawable border = new GradientDrawable();
        border.setColor(0x00000000);
        border.setStroke(8, 0xFFFFFFFF);
        cropBox.setBackground(border);
        cropBox.setClickable(false);
        container.addView(cropBox);

        // المقابض - حجم أكبر
        handleTL = makeHandle();
        handleTR = makeHandle();
        handleBL = makeHandle();
        handleBR = makeHandle();
        container.addView(handleTL);
        container.addView(handleTR);
        container.addView(handleBL);
        container.addView(handleBR);

        // الأزرار
        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.VERTICAL);
        controls.setBackgroundColor(0xDD222222);
        controls.setPadding(30, 20, 30, 20);

        TextView tvInfo = new TextView(this);
        tvInfo.setTextColor(0xFFFFFFFF);
        tvInfo.setTextSize(14);
        tvInfo.setText(tr("اسحب الدوائر البيضاء لتحديد منطقة القص"));
        tvInfo.setPadding(0, 0, 0, 15);
        controls.addView(tvInfo);

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setPadding(0, 10, 0, 0);

        Button btnReset = new Button(this);
        btnReset.setText(tr("إعادة ضبط"));
        btnReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { resetCrop(); }
        });
        btnRow.addView(btnReset, new LinearLayout.LayoutParams(0, -2, 1f));

        Button btnApply = new Button(this);
        btnApply.setText(tr("تطبيق القص"));
        btnApply.setBackgroundResource(R.drawable.button_click_effect);
        btnApply.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
        btnApply.setTextColor(0xFFFFFFFF);
        btnApply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { applyCrop(); }
        });
        btnRow.addView(btnApply, new LinearLayout.LayoutParams(0, -2, 1f));

        Button btnCancel = new Button(this);
        btnCancel.setText(tr("إلغاء"));
        btnCancel.setBackgroundResource(R.drawable.button_click_effect);
        btnCancel.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
        btnCancel.setTextColor(0xFFFFFFFF);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        btnRow.addView(btnCancel, new LinearLayout.LayoutParams(0, -2, 1f));

        controls.addView(btnRow);

        FrameLayout.LayoutParams clp = new FrameLayout.LayoutParams(-1, controlsHeight);
        clp.gravity = Gravity.BOTTOM;
        container.addView(controls, clp);

        setContentView(container);

        // ربط السحب
        handleTL.setOnTouchListener(new DragListener("TL"));
        handleTR.setOnTouchListener(new DragListener("TR"));
        handleBL.setOnTouchListener(new DragListener("BL"));
        handleBR.setOnTouchListener(new DragListener("BR"));

        container.post(new Runnable() {
            public void run() {
                int cw = container.getWidth();
                int ch = container.getHeight();
                int videoAreaH = ch - controlsHeight;
                scale = Math.min((float)cw / videoW, (float)videoAreaH / videoH);
                dispW = (int)(videoW * scale);
                dispH = (int)(videoH * scale);
                offX = (cw - dispW) / 2f;
                offY = (videoAreaH - dispH) / 2f;
                resetCrop();
            }
        });
    }

    private View makeHandle() {
        View h = new View(this);
        GradientDrawable g = new GradientDrawable();
        g.setShape(GradientDrawable.OVAL);
        g.setColor(0xFFFFFFFF);
        g.setStroke(6, 0xFF000000);
        h.setBackground(g);
        h.setClickable(true);
        h.setFocusable(true);
        return h;
    }

    private void resetCrop() {
        int margin = 60;  // مسافة داخلية لضمان رؤية المقابض
        cropL = offX + margin;
        cropT = offY + margin;
        cropR = offX + dispW - margin;
        cropB = offY + dispH - margin;
        updateCropUI();
    }

    private class DragListener implements View.OnTouchListener {
        String corner;
        float lastX, lastY;
        DragListener(String c) { corner = c; }
        public boolean onTouch(View v, MotionEvent e) {
            if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                lastX = e.getRawX();
                lastY = e.getRawY();
                return true;
            } else if (e.getActionMasked() == MotionEvent.ACTION_MOVE) {
                float dx = e.getRawX() - lastX;
                float dy = e.getRawY() - lastY;
                lastX = e.getRawX();
                lastY = e.getRawY();
                float minSize = 150;
                int maxY = container.getHeight() - controlsHeight;
                if (corner.equals("TL")) {
                    cropL += dx; cropT += dy;
                    if (cropL > cropR - minSize) cropL = cropR - minSize;
                    if (cropT > cropB - minSize) cropT = cropB - minSize;
                    if (cropL < 0) cropL = 0;
                    if (cropT < 0) cropT = 0;
                } else if (corner.equals("TR")) {
                    cropR += dx; cropT += dy;
                    if (cropR < cropL + minSize) cropR = cropL + minSize;
                    if (cropT > cropB - minSize) cropT = cropB - minSize;
                    if (cropR > container.getWidth()) cropR = container.getWidth();
                    if (cropT < 0) cropT = 0;
                } else if (corner.equals("BL")) {
                    cropL += dx; cropB += dy;
                    if (cropL > cropR - minSize) cropL = cropR - minSize;
                    if (cropB < cropT + minSize) cropB = cropT + minSize;
                    if (cropL < 0) cropL = 0;
                    if (cropB > maxY) cropB = maxY;
                } else {
                    cropR += dx; cropB += dy;
                    if (cropR < cropL + minSize) cropR = cropL + minSize;
                    if (cropB < cropT + minSize) cropB = cropT + minSize;
                    if (cropR > container.getWidth()) cropR = container.getWidth();
                    if (cropB > maxY) cropB = maxY;
                }
                updateCropUI();
                return true;
            }
            return false;
        }
    }

    private void updateCropUI() {
        // مربع القص
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) cropBox.getLayoutParams();
        lp.leftMargin = (int) cropL;
        lp.topMargin = (int) cropT;
        lp.width = (int)(cropR - cropL);
        lp.height = (int)(cropB - cropT);
        cropBox.setLayoutParams(lp);

        // المناطق المعتمة
        setLayout(dimTop, 0, 0, container.getWidth(), (int) cropT);
        setLayout(dimBottom, 0, (int) cropB, container.getWidth(), container.getHeight() - (int) cropB);
        setLayout(dimLeft, 0, (int) cropT, (int) cropL, (int)(cropB - cropT));
        setLayout(dimRight, (int) cropR, (int) cropT, container.getWidth() - (int) cropR, (int)(cropB - cropT));

        // المقابض - نستخدم margin سالب لتوسيع مساحة اللمس
        setHandle(handleTL, cropL, cropT);
        setHandle(handleTR, cropR, cropT);
        setHandle(handleBL, cropL, cropB);
        setHandle(handleBR, cropR, cropB);
    }

    private void setLayout(View v, int x, int y, int w, int h) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v.getLayoutParams();
        lp.leftMargin = x;
        lp.topMargin = y;
        lp.width = w < 0 ? 0 : w;
        lp.height = h < 0 ? 0 : h;
        v.setLayoutParams(lp);
    }

    private void setHandle(View h, float x, float y) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) h.getLayoutParams();
        lp.leftMargin = (int)(x - handleSize / 2);
        lp.topMargin = (int)(y - handleSize / 2);
        lp.width = handleSize;
        lp.height = handleSize;
        h.setLayoutParams(lp);
        h.bringToFront();
    }

    private void applyCrop() {
        float relL = (cropL - offX) / scale;
        float relT = (cropT - offY) / scale;
        float relR = (cropR - offX) / scale;
        float relB = (cropB - offY) / scale;

        int x = (int) relL;
        int y = (int) relT;
        int w = (int)(relR - relL);
        int h = (int)(relB - relT);

        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + w > videoW) w = videoW - x;
        if (y + h > videoH) h = videoH - y;
        if (w < 50) w = 50;
        if (h < 50) h = 50;
        if (w % 2 != 0) w--;
        if (h % 2 != 0) h--;
        if (x % 2 != 0) x--;
        if (y % 2 != 0) y--;

        Intent result = new Intent();
        result.putExtra(EXTRA_RESULT_X, x);
        result.putExtra(EXTRA_RESULT_Y, y);
        result.putExtra(EXTRA_RESULT_W, w);
        result.putExtra(EXTRA_RESULT_H, h);
        setResult(Activity.RESULT_OK, result);
        finish();
    }
}
