package com.videocraft.pro;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrimActivity extends AppCompatActivity {

    private int currentLang = Translations.LANG_AR;
    private void loadLang() {
        currentLang = getSharedPreferences("app_prefs", MODE_PRIVATE).getInt("lang", Translations.LANG_AR);
    }
    public String tr(String ar) {
        return Translations.tr(currentLang, ar);
    }

    public static final String EXTRA_VIDEO_PATH = "video_path";
    public static final String EXTRA_RESULT_START = "result_start";
    public static final String EXTRA_RESULT_END = "result_end";

    private VideoView videoView;
    private FrameLayout timelineContainer;
    private View leftHandle, rightHandle, dimLeft, dimRight;
    private TextView tvStart, tvEnd, tvCurrent;
    private int durationMs = 0;
    private float startSec = 0, endSec = 0;
    private float timelineWidth = 0;
    private int handleWidthPx = 0;
    private boolean draggingLeft = false, draggingRight = false;
    private float dragStartX = 0, dragStartPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLang();
        String videoPath = getIntent().getStringExtra(EXTRA_VIDEO_PATH);

        List<Bitmap> frames = new ArrayList<Bitmap>();
        try {
            MediaMetadataRetriever r = new MediaMetadataRetriever();
            r.setDataSource(videoPath);
            String d = r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (d != null) durationMs = Integer.parseInt(d);
            int numFrames = 10;
            for (int i = 0; i < numFrames; i++) {
                long timeUs = (long)((durationMs * 1000L) * i / numFrames);
                Bitmap b = r.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                if (b != null) frames.add(b);
            }
            r.release();
        } catch (Exception e) { }
        endSec = durationMs / 1000f;

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF1A1A1A);

        videoView = new VideoView(this);
        LinearLayout.LayoutParams vlp = new LinearLayout.LayoutParams(-1, 0, 1f);
        videoView.setLayoutParams(vlp);
        videoView.setVideoPath(videoPath);
        root.addView(videoView);

        tvCurrent = new TextView(this);
        tvCurrent.setTextColor(0xFFFFFFFF);
        tvCurrent.setTextSize(14);
        tvCurrent.setGravity(Gravity.CENTER);
        tvCurrent.setPadding(20, 8, 20, 8);
        tvCurrent.setText("00:00.0 / " + formatTime(durationMs));
        root.addView(tvCurrent);

        timelineContainer = new FrameLayout(this);
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(-1, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics()));
        tlp.setMargins(0, 10, 0, 10);
        timelineContainer.setLayoutParams(tlp);
        timelineContainer.setBackgroundColor(0xFF000000);

        LinearLayout filmstripLayout = new LinearLayout(this);
        filmstripLayout.setOrientation(LinearLayout.HORIZONTAL);
        filmstripLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        for (Bitmap b : frames) {
            ImageView iv = new ImageView(this);
            iv.setImageBitmap(b);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(0, -1, 1f);
            iv.setLayoutParams(ilp);
            filmstripLayout.addView(iv);
        }
        timelineContainer.addView(filmstripLayout);

        dimLeft = new View(this);
        dimLeft.setBackgroundColor(0xAA000000);
        timelineContainer.addView(dimLeft, new FrameLayout.LayoutParams(0, -1));

        dimRight = new View(this);
        dimRight.setBackgroundColor(0xAA000000);
        FrameLayout.LayoutParams drp = new FrameLayout.LayoutParams(0, -1);
        drp.gravity = Gravity.END;
        timelineContainer.addView(dimRight, drp);

        handleWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

        leftHandle = new View(this);
        leftHandle.setBackgroundColor(0xFFFFEB3B);
        timelineContainer.addView(leftHandle, new FrameLayout.LayoutParams(handleWidthPx, -1));

        rightHandle = new View(this);
        rightHandle.setBackgroundColor(0xFFFFEB3B);
        timelineContainer.addView(rightHandle, new FrameLayout.LayoutParams(handleWidthPx, -1));

        root.addView(timelineContainer);

        LinearLayout timeRow = new LinearLayout(this);
        timeRow.setOrientation(LinearLayout.HORIZONTAL);
        timeRow.setPadding(30, 5, 30, 5);
        tvStart = new TextView(this);
        tvStart.setTextColor(0xFF4CAF50);
        tvStart.setTextSize(16);
        tvStart.setText("0.0s");
        timeRow.addView(tvStart, new LinearLayout.LayoutParams(0, -2, 1f));
        tvEnd = new TextView(this);
        tvEnd.setTextColor(0xFFF44336);
        tvEnd.setTextSize(16);
        tvEnd.setGravity(Gravity.END);
        tvEnd.setText((durationMs/1000f) + "s");
        timeRow.addView(tvEnd, new LinearLayout.LayoutParams(0, -2, 1f));
        root.addView(timeRow);

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setPadding(20, 10, 20, 20);

        Button btnPlay = new Button(this);
        btnPlay.setText(tr("تشغيل"));
        btnPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                videoView.seekTo((int)(startSec * 1000));
                videoView.start();
            }
        });
        btnRow.addView(btnPlay, new LinearLayout.LayoutParams(0, -2, 1f));

        Button btnApply = new Button(this);
        btnApply.setText("قص");
        btnApply.setBackgroundResource(R.drawable.button_click_effect);
        btnApply.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
        btnApply.setTextColor(0xFFFFFFFF);
        btnApply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra(EXTRA_RESULT_START, startSec);
                result.putExtra(EXTRA_RESULT_END, endSec);
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        });
        btnRow.addView(btnApply, new LinearLayout.LayoutParams(0, -2, 2f));

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

        root.addView(btnRow);
        setContentView(root);

        timelineContainer.post(new Runnable() {
            public void run() {
                timelineWidth = timelineContainer.getWidth() - handleWidthPx * 2;
                updateHandles();
            }
        });

        leftHandle.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    draggingLeft = true;
                    dragStartX = e.getRawX();
                    dragStartPos = startSec;
                    return true;
                } else if (e.getActionMasked() == MotionEvent.ACTION_MOVE && draggingLeft) {
                    float dx = e.getRawX() - dragStartX;
                    float dt = (dx / timelineWidth) * (durationMs / 1000f);
                    startSec = Math.max(0, Math.min(endSec - 0.3f, dragStartPos + dt));
                    videoView.seekTo((int)(startSec * 1000));
                    updateHandles();
                    return true;
                } else if (e.getActionMasked() == MotionEvent.ACTION_UP) {
                    draggingLeft = false;
                    return true;
                }
                return false;
            }
        });

        rightHandle.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    draggingRight = true;
                    dragStartX = e.getRawX();
                    dragStartPos = endSec;
                    return true;
                } else if (e.getActionMasked() == MotionEvent.ACTION_MOVE && draggingRight) {
                    float dx = e.getRawX() - dragStartX;
                    float dt = (dx / timelineWidth) * (durationMs / 1000f);
                    endSec = Math.max(startSec + 0.3f, Math.min(durationMs / 1000f, dragStartPos + dt));
                    videoView.seekTo((int)(endSec * 1000));
                    updateHandles();
                    return true;
                } else if (e.getActionMasked() == MotionEvent.ACTION_UP) {
                    draggingRight = false;
                    return true;
                }
                return false;
            }
        });
    }

    private void updateHandles() {
        float total = durationMs / 1000f;
        int lx = (int)((startSec / total) * timelineWidth);
        int rx = (int)((endSec / total) * timelineWidth);

        FrameLayout.LayoutParams llp = (FrameLayout.LayoutParams) leftHandle.getLayoutParams();
        llp.leftMargin = lx;
        leftHandle.setLayoutParams(llp);

        FrameLayout.LayoutParams rlp = (FrameLayout.LayoutParams) rightHandle.getLayoutParams();
        rlp.leftMargin = rx + handleWidthPx;
        rightHandle.setLayoutParams(rlp);

        FrameLayout.LayoutParams dlp = (FrameLayout.LayoutParams) dimLeft.getLayoutParams();
        dlp.width = lx;
        dimLeft.setLayoutParams(dlp);

        FrameLayout.LayoutParams drp = (FrameLayout.LayoutParams) dimRight.getLayoutParams();
        drp.width = timelineContainer.getWidth() - (rx + handleWidthPx * 2);
        dimRight.setLayoutParams(drp);

        tvStart.setText(String.format(Locale.US, "%.1fs", startSec));
        tvEnd.setText(String.format(Locale.US, "%.1fs", endSec));
    }

    private String formatTime(int ms) {
        int s = ms / 1000;
        int m = s / 60;
        s = s % 60;
        return String.format(Locale.US, "%02d:%02d", m, s);
    }
}
