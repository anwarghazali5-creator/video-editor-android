package com.videocraft.pro;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EditorActivity extends AppCompatActivity {

    private int currentLang = Translations.LANG_AR;
    private void loadLang() {
        currentLang = getSharedPreferences("app_prefs", MODE_PRIVATE).getInt("lang", Translations.LANG_AR);
    }
    public String tr(String ar) {
        return Translations.tr(currentLang, ar);
    }

    public static final String EXTRA_VIDEO_PATH = "video_path";
    public static final String EXTRA_TOOL = "tool";

    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLang();
        videoPath = getIntent().getStringExtra(EXTRA_VIDEO_PATH);
        if (videoPath == null) { finish(); return; }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF0F0F0F);

        // ========== الشريط العلوي ==========
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setPadding(15, 15, 15, 15);
        header.setBackgroundColor(0xFF1A1A1A);
        header.setGravity(Gravity.CENTER_VERTICAL);

        Button btnBack = new Button(this);
        btnBack.setText(tr("← رجوع"));
        btnBack.setTextColor(0xFFFFFFFF);
        btnBack.setTextSize(12);
        btnBack.setBackgroundResource(R.drawable.button_click_effect);
        btnBack.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
        btnBack.setPadding(20, 10, 20, 10);
        btnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { finish(); }
        });
        header.addView(btnBack);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(tr("✂️ التحرير"));
        tvTitle.setTextColor(0xFFFFFFFF);
        tvTitle.setTextSize(16);
        tvTitle.setPadding(20, 0, 0, 0);
        header.addView(tvTitle);

        root.addView(header);

        // ========== معاينة الفيديو ==========
        ImageView imgPreview = new ImageView(this);
        imgPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgPreview.setBackgroundColor(0xFF000000);
        LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(-1, 0, 1f);
        imgPreview.setLayoutParams(imgLp);
        try {
            MediaMetadataRetriever r = new MediaMetadataRetriever();
            r.setDataSource(videoPath);
            Bitmap bmp = r.getFrameAtTime(1000000);
            r.release();
            if (bmp != null) imgPreview.setImageBitmap(bmp);
        } catch (Exception e) { }
        root.addView(imgPreview);

        // ========== حاوية الأدوات (في الأسفل) ==========
        LinearLayout bottomBar = new LinearLayout(this);
        bottomBar.setOrientation(LinearLayout.VERTICAL);
        bottomBar.setBackgroundColor(0xFF1E1E1E);

        // ========== عنوان الأدوات ==========
        TextView tvToolsTitle = new TextView(this);
        tvToolsTitle.setText(tr("🛠️ اختر أداة"));
        tvToolsTitle.setTextColor(0xFFFFFFFF);
        tvToolsTitle.setTextSize(13);
        tvToolsTitle.setPadding(20, 12, 20, 6);
        tvToolsTitle.setBackgroundColor(0xFF1E1E1E);
        bottomBar.addView(tvToolsTitle);

        // ========== شريط الأدوات الأفقي (قابل للتمرير) ==========
        HorizontalScrollView hsv = new HorizontalScrollView(this);
        hsv.setBackgroundColor(0xFF1E1E1E);
        hsv.setHorizontalScrollBarEnabled(false);

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setPadding(10, 10, 10, 20);
        hsv.addView(toolbar);

        // قائمة الأدوات: [الإيموجي, الاسم, المعرّف]
        String[][] tools = {
            {"✂️", tr("قص"), "cut"},
            {"✂️", tr("قص دقيق"), "precise_cut"},
            {"📝", tr("نص"), "text"},
            {"✍️", tr("نص بظل"), "shadow_text"},
            {"🎵", tr("موسيقى"), "music"},
            {"⏱️", tr("سرعة"), "speed"},
            {"🚀", tr("سرعة متدرجة"), "speed_ramp"},
            {"🔄", tr("تدوير"), "rotate"},
            {"📦", tr("ضغط"), "compress"},
            {"🎵", tr("أدوات الصوت"), "audio"},
            {"🎤", tr("تعزيز الصوت"), "audio_boost"},
            {"🎼", tr("تلاشي الصوت"), "audio_fade"},
            {"🎙️", tr("خفض الموسيقى"), "audio_duck"},
            {"🎬", tr("أدوات الفيديو"), "video_tools"},
            {"🖼️", tr("علامة مائية"), "watermark"},
            {"✨", tr("تأثيرات بصرية"), "effects"},
            {"🎬", tr("فلاتر سينمائية"), "cinematic"},
            {"📂", tr("المعرض"), "gallery"},
            {"🖼️", tr("صور إلى فيديو"), "slideshow"},
            {"🎬", tr("جودة التصدير"), "quality"},
            {"🔁", tr("تكرار"), "repeat"},
            {"🎵", tr("استبدال موسيقى"), "replace_music"},
            {"📊", tr("معلومات"), "info"},
            {"🔊", tr("إزالة ضوضاء"), "denoise"},
            {"📸", tr("لقطة"), "screenshot"},
            {"🌙", tr("وضع ليلي"), "theme"},
            {"🎨", tr("ملصقات"), "stickers"},
            {"🎨", tr("ضبط الألوان"), "color_adjust"},
            {"🖼️", tr("إطار"), "frame"},
            {"📱", tr("أبعاد السوشيال"), "social_size"},
            {"🎥", tr("فيديو داخل فيديو"), "pip"},
            {"🔄", tr("تبديل الصوت"), "swap_audio"},
            {"✂️", tr("تقسيم"), "split"},
            {"🎨", tr("تلوين الفيديو"), "color_tint"},
            {"✂️", tr("قطع صورة"), "cutout_image"},
            {"🎞️", tr("تحويل GIF"), "gif"},
            {"🎬", tr("كولاج"), "collage"},
            {"📐", tr("تعديل الأبعاد"), "manual_resize"},
            {"🔇", tr("حذف الصمت"), "remove_silence"}
        };

        int btnSize = (int)(getResources().getDisplayMetrics().density * 90);

        for (String[] tool : tools) {
            Button btn = new Button(this);
            btn.setText(tool[0] + "\n" + tool[1]);
            btn.setTextSize(10);
            btn.setTextColor(0xFFFFFFFF);
            btn.setBackgroundColor(0xFF455A64);
            btn.setPadding(4, 4, 4, 4);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(btnSize, btnSize);
            lp.setMargins(5, 0, 5, 0);
            btn.setLayoutParams(lp);
            final String toolId = tool[2];
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent result = new Intent();
                    result.putExtra(EXTRA_TOOL, toolId);
                    setResult(Activity.RESULT_OK, result);
                    finish();
                }
            });
            toolbar.addView(btn);
        }

        int toolbarHeight = (int)(getResources().getDisplayMetrics().density * 130);
        bottomBar.addView(hsv, new LinearLayout.LayoutParams(-1, toolbarHeight));
        root.addView(bottomBar, new LinearLayout.LayoutParams(-1, -2));

        setContentView(root);
    }
}
