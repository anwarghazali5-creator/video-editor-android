package com.videocraft.pro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.Gravity;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.ReturnCode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import androidx.annotation.NonNull;


public class MainActivity extends AppCompatActivity {
    // ===== AdMob Fields =====
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;
    private static final String BANNER_AD_UNIT_ID = "ca-app-pub-8730424933785076/4075267878";
    private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-8730424933785076/7548618182";
    private static final String REWARDED_AD_UNIT_ID = "ca-app-pub-8730424933785076/8039572773";


    // ===== Language Support =====
    private int currentLang = Translations.LANG_AR;

    private void loadLang() {
        android.content.SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
        currentLang = sp.getInt("lang", Translations.LANG_AR);
    }

    private void saveLang(int lang) {
        currentLang = lang;
        getSharedPreferences("app_prefs", MODE_PRIVATE).edit().putInt("lang", lang).apply();
    }

    public String tr(String ar) {
        return Translations.tr(currentLang, ar);
    }

    private void showLangDialog() {
        final String[] names = {"العربية", "English", "Français"};
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names) {
            @Override
            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.view.View v = super.getView(position, convertView, parent);
                android.widget.TextView tv = (android.widget.TextView) v.findViewById(android.R.id.text1);
                if (tv != null) tv.setTextColor(0xFFFFFFFF);
                return v;
            }
        };
        new androidx.appcompat.app.AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle("Choose Language")
            .setAdapter(adapter, (d, which) -> {
                saveLang(which);
                recreate();
            })
            .show();
    }

    private void applyLang() {
        Button b;
        b = findViewById(R.id.btnAddText); if (b != null) b.setText(tr("إضافة نص"));
        b = findViewById(R.id.btnExportFinal); if (b != null) b.setText(tr("تصدير الكل"));
        b = findViewById(R.id.btnTrimSecs); if (b != null) b.setText(tr("قص بالثواني"));
            
                        if (b != null) {
                if (currentLang == 1) b.setText("Export All");
                else if (currentLang == 2) b.setText("Exporter tout");
                else b.setText("تصدير الكل");
            }
                        if (b != null) {
                if (currentLang == 1) b.setText("Trim by Seconds");
                else if (currentLang == 2) b.setText("Couper par secondes");
                else b.setText("قص بالثواني");
            }
        if (b != null) {
            if (currentLang == 1) b.setText("Export All");
            else if (currentLang == 2) b.setText("Exporter tout");
            else b.setText("تصدير الكل");
        }
        if (b != null) {
            if (currentLang == 1) b.setText("Trim by Seconds");
            else if (currentLang == 2) b.setText("Couper par secondes");
            else b.setText("قص بالثواني");
        }

        b = findViewById(R.id.btnVideoOverImage); if (b != null) b.setText(tr("فيديو فوق صورة"));
        b = findViewById(R.id.btnAddMusic); if (b != null) b.setText(tr("إضافة موسيقى"));
        b = findViewById(R.id.btnSpeed); if (b != null) b.setText(tr("تغيير السرعة"));
        b = findViewById(R.id.btnRotate); if (b != null) b.setText(tr("تدوير الفيديو"));
        b = findViewById(R.id.btnCompress); if (b != null) b.setText(tr("ضغط الفيديو"));
        b = findViewById(R.id.btnAudio); if (b != null) b.setText(tr("أدوات الصوت"));
        b = findViewById(R.id.btnVideoTools); if (b != null) b.setText(tr("أدوات الفيديو"));
        b = findViewById(R.id.btnWatermark); if (b != null) b.setText(tr("علامة مائية"));
        b = findViewById(R.id.btnEffects); if (b != null) b.setText(tr("تأثيرات بصرية"));
        b = findViewById(R.id.btnGallery); if (b != null) b.setText(tr("المعرض"));
        b = findViewById(R.id.btnLangAr); if (b != null) b.setAlpha(currentLang == 0 ? 1.0f : 0.5f);
        b = findViewById(R.id.btnLangEn); if (b != null) b.setAlpha(currentLang == 1 ? 1.0f : 0.5f);
        b = findViewById(R.id.btnLangFr); if (b != null) b.setAlpha(currentLang == 2 ? 1.0f : 0.5f);
        b = findViewById(R.id.btnSlideshow); if (b != null) b.setText(tr("صور إلى فيديو"));
        b = findViewById(R.id.btnQuality); if (b != null) b.setText(tr("جودة التصدير"));
        b = findViewById(R.id.btnRepeat); if (b != null) b.setText(tr("تكرار الفيديو"));
        b = findViewById(R.id.btnReplaceMusic); if (b != null) b.setText(tr("استبدال الموسيقى"));
        b = findViewById(R.id.btnDenoise); if (b != null) b.setText(tr("إزالة الضوضاء"));
        b = findViewById(R.id.btnScreenshot); if (b != null) b.setText(tr("لقطة من الفيديو"));
        b = findViewById(R.id.btnTheme); if (b != null) b.setText(tr("وضع ليلي/نهاري"));
        b = findViewById(R.id.btnStickers); if (b != null) b.setText(tr("ملصقات"));
        b = findViewById(R.id.btnPreciseCut); if (b != null) b.setText(tr("قص دقيق"));
        b = findViewById(R.id.btnColorAdjust); if (b != null) b.setText(tr("ضبط الألوان"));
        b = findViewById(R.id.btnFrame); if (b != null) b.setText(tr("إطار"));
        b = findViewById(R.id.btnSocialSize); if (b != null) b.setText(tr("أبعاد السوشيال"));
        b = findViewById(R.id.btnPiP); if (b != null) b.setText(tr("فيديو داخل فيديو"));
        b = findViewById(R.id.btnSwapAudio); if (b != null) b.setText(tr("تبديل الصوت"));
        b = findViewById(R.id.btnSplit); if (b != null) b.setText(tr("تقسيم الفيديو"));
        b = findViewById(R.id.btnShadowText); if (b != null) b.setText(tr("نص بظل"));
        b = findViewById(R.id.btnVideoInfo); if (b != null) b.setText(tr("معلومات الفيديو"));
        b = findViewById(R.id.btnExtraEffects); if (b != null) b.setText(tr("تأثيرات سينمائية"));
        b = findViewById(R.id.btnManualResize); if (b != null) b.setText(tr("تعديل الأبعاد"));
        b = findViewById(R.id.btnToGif); if (b != null) b.setText(tr("تحويل GIF"));
        b = findViewById(R.id.btnCollage); if (b != null) b.setText(tr("كولاج"));
        b = findViewById(R.id.btnCutoutImage_old_NEW); if (b != null) b.setText(tr("قطع صورة"));
        b = findViewById(R.id.btnAudioFade); if (b != null) b.setText(tr("تلاشي الصوت"));
        b = findViewById(R.id.btnAudioDuck); if (b != null) b.setText(tr("خفض الموسيقى"));
        b = findViewById(R.id.btnSpeedRamp); if (b != null) b.setText(tr("سرعة متدرجة"));
        b = findViewById(R.id.btnAudioBoost); if (b != null) b.setText(tr("تعزيز الصوت"));
        b = findViewById(R.id.btnRemoveSilence); if (b != null) b.setVisibility(android.view.View.GONE);
        
        android.widget.TextView tvt = findViewById(R.id.tvPickVideoTitle);
        if (tvt != null) tvt.setText(tr("اختيار فيديو"));
        android.widget.TextView tvInfo = findViewById(R.id.tvVideoInfo);
        if (tvInfo != null && videoPath == null) tvInfo.setText(tr("لم يتم اختيار فيديو"));
        if (b != null) {
            if (currentLang == 1) b.setText("Export All");
            else if (currentLang == 2) b.setText("Exporter tout");
            else b.setText("تصدير الكل");
        }
        if (b != null) {
            if (currentLang == 1) b.setText("Trim by Seconds");
            else if (currentLang == 2) b.setText("Couper par secondes");
            else b.setText("قص بالثواني");
        }
        if (b != null) {
            if (currentLang == 1) b.setText("💾\nExport All");
            else if (currentLang == 2) b.setText("💾\nExporter tout");
            else b.setText("💾\nتصدير الكل");
        }
        if (b != null) {
            if (currentLang == 1) b.setText("✂️\nTrim by Seconds");
            else if (currentLang == 2) b.setText("✂️\nCouper par secondes");
            else b.setText("✂️\nقص بالثواني");
        }
        if (b != null) {
            if (currentLang == 1) b.setText("💾\nExport All");
            else if (currentLang == 2) b.setText("💾\nExporter tout");
            else b.setText("💾\nتصدير الكل");
        }
        if (b != null) {
            if (currentLang == 1) b.setText("✂️\nTrim by Seconds");
            else if (currentLang == 2) b.setText("✂️\nCouper par secondes");
            else b.setText("✂️\nقص بالثواني");
        }

        android.widget.Button btnExpF = findViewById(R.id.btnExportFinal);
        android.widget.Button btnTrS = findViewById(R.id.btnTrimSecs);
        if (btnExpF != null) {
            if (currentLang == 0) btnExpF.setText("💾\nتصدير الكل");
            else if (currentLang == 2) btnExpF.setText("💾\nExporter tout");
            else btnExpF.setText("💾\nExport All");
        }
        if (btnTrS != null) {
            if (currentLang == 0) btnTrS.setText("✂️\nقص بالثواني");
            else if (currentLang == 2) btnTrS.setText("✂️\nCouper par secondes");
            else btnTrS.setText("✂️\nTrim by Seconds");
        }
}

    private AlertDialog showWhite(AlertDialog dialog) {
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFFFFFFFF);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFFFFFFFF);
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xFFFFFFFF);
            int titleId = getResources().getIdentifier("alertTitle", "id", "android");
            android.widget.TextView titleTv = dialog.findViewById(titleId);
            if (titleTv != null) titleTv.setTextColor(0xFFFFFFFF);
            android.widget.TextView msgTv = dialog.findViewById(android.R.id.message);
            if (msgTv != null) msgTv.setTextColor(0xFFFFFFFF);
        });
        dialog.show();
        return dialog;
    }

    // ===== End Language Support =====



    private static final int PICK_VIDEO = 1001;
    private static final int PICK_CROP_INTERACTIVE = 1018;
    private static final int PICK_COLLAGE_VIDEOS = 1020;
    private static final int PICK_CUTOUT_IMAGE = 1022;
    private static final int PICK_EDITOR = 1023;
    private static final int PICK_DUCK_MUSIC = 1021;
    private static final int PICK_MUSIC = 1002;
    private static final int PICK_VIDEO_2 = 1003;
    private static final int PICK_IMAGE = 1004;
    private static final int PICK_WATERMARK_PREVIEW = 1005;
    private static final int PICK_TRIM = 1006;
    private static final int PICK_IMAGES = 1007;
    private static final int PICK_REPLACE_MUSIC = 1008;
    private static final int PICK_STICKER_PREVIEW = 1009;
private static final int PICK_PIP_VIDEO = 1011;
    private static final int PICK_VOI = 1040;
    private String voiVideoPath;
    private boolean isPipFlow = false;
    private boolean isCutoutFlow = false;
    private String pendingCutoutPath = null;
    private String voiImagePath;
    private static final int PICK_PIP_PREVIEW = 1012;
    private static final int PICK_SWAP_AUDIO = 1013;
    private static final int PICK_SHADOWTEXT_PREVIEW = 1016;

    private TextView tvVideoInfo, tvStatus;
    private ProgressBar progressBar;
    private Button btnCancelOp;
    private FFmpegSession currentSession;
    public static String videoPath;
    private String musicPath;
    private String imagePath;
    private String stickerPath;
    private String pipVideoPath;
    private String swapAudioPath;
    private String shadowTextPath;
    private String chromaBgPath;
    private long videoDurationMs = 0;
    private File outputDir;
    private List<String> imagePaths = new ArrayList<String>();
    private List<String> multiVideoPaths = new ArrayList<String>();
    private String exportQuality = "1080";
    private String collageMode = "vertical";
    private boolean isDarkMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLang();
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            requestPermissions(new String[]{
                android.Manifest.permission.READ_MEDIA_VIDEO,
                android.Manifest.permission.READ_MEDIA_AUDIO,
                android.Manifest.permission.READ_MEDIA_IMAGES
            }, 100);
        } else {
            requestPermissions(new String[]{
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
        setContentView(R.layout.activity_main);

        // ===== AdMob Initialization =====
        MobileAds.initialize(this, initializationStatus -> {
            android.util.Log.d("AdMob", "SDK initialized");
        });
        loadInterstitialAd();
        loadRewardedAd();
        setupBannerAd();

        applyLang();

        tvVideoInfo = findViewById(R.id.tvVideoInfo);
        tvStatus = findViewById(R.id.tvStatus);
        progressBar = findViewById(R.id.progressBar);
        btnCancelOp = findViewById(R.id.btnCancelOp);
        btnCancelOp.setOnClickListener(v -> {
            if (currentSession != null) {
                FFmpegKit.cancel(currentSession.getSessionId());
                currentSession = null;
                toast("تم إلغاء العملية");
            }
            progressBar.setVisibility(android.view.View.INVISIBLE);
            progressBar.setIndeterminate(false);
            btnCancelOp.setVisibility(android.view.View.GONE);
            tvStatus.setText("تم إلغاء العملية");
            tvStatus.setTextColor(0xFFFF9800);
        });

        outputDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "VideoEditorPro");
        if (!outputDir.exists()) outputDir.mkdirs();

        findViewById(R.id.btnSelectVideoCard).setOnClickListener(v -> {
            // حركة ضغط (تصغير ثم تكبير)
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(80).start();
            }).start();
            // فتح معرض الفيديو بعد 80ms
            v.postDelayed(() -> pickVideo(), 100);
        });
        findViewById(R.id.btnVideoOverImage).setOnClickListener(v -> pickVideoOverImage());
        findViewById(R.id.btnTrimSecs).setOnClickListener(v -> showTrimBySecondsDialog());
        findViewById(R.id.btnExportFinal).setOnClickListener(v -> exportFinalVideo());
        findViewById(R.id.btnAddText).setOnClickListener(v -> showTextDialog());
        findViewById(R.id.btnAddMusic).setOnClickListener(v -> pickMusic());
        findViewById(R.id.btnSpeed).setOnClickListener(v -> showSpeedDialog());
        findViewById(R.id.btnRotate).setOnClickListener(v -> showRotateDialog());
        findViewById(R.id.btnCompress).setOnClickListener(v -> compressVideo());
        findViewById(R.id.btnAudio).setOnClickListener(v -> showAudioDialog());
        findViewById(R.id.btnVideoTools).setOnClickListener(v -> showVideoToolsDialog());
        findViewById(R.id.btnWatermark).setOnClickListener(v -> showWatermarkDialog());
        findViewById(R.id.btnEffects).setOnClickListener(v -> showVisualEffectsDialog());
        findViewById(R.id.btnLangAr).setOnClickListener(v -> { saveLang(0); recreate(); });
        findViewById(R.id.btnLangEn).setOnClickListener(v -> { saveLang(1); recreate(); });
        findViewById(R.id.btnLangFr).setOnClickListener(v -> { saveLang(2); recreate(); });
        findViewById(R.id.btnGallery).setOnClickListener(v -> startActivity(new Intent(this, GalleryActivity.class)));
        findViewById(R.id.btnSlideshow).setOnClickListener(v -> pickImages());
        findViewById(R.id.btnQuality).setOnClickListener(v -> showQualityDialog());
        findViewById(R.id.btnRepeat).setOnClickListener(v -> showRepeatDialog());
        findViewById(R.id.btnReplaceMusic).setOnClickListener(v -> pickReplaceMusic());
        findViewById(R.id.btnVideoInfo).setOnClickListener(v -> showVideoInfo());
        findViewById(R.id.btnExtraEffects).setOnClickListener(v -> showExtraEffectsDialog());
        findViewById(R.id.btnManualResize).setOnClickListener(v -> showManualResizeDialog());
        findViewById(R.id.btnToGif).setOnClickListener(v -> showGifDialog());
        findViewById(R.id.btnCollage).setOnClickListener(v -> showCollageDialog());
        findViewById(R.id.btnDenoise).setOnClickListener(v -> denoiseAudio());
        findViewById(R.id.btnScreenshot).setOnClickListener(v -> captureScreenshot());
        findViewById(R.id.btnTheme).setOnClickListener(v -> toggleTheme());
        findViewById(R.id.btnStickers).setOnClickListener(v -> showStickersDialog());
        
        findViewById(R.id.btnAudioFade).setOnClickListener(v -> showAudioFadeDialog());
        findViewById(R.id.btnAudioDuck).setOnClickListener(v -> showAudioDuckDialog());
        findViewById(R.id.btnSpeedRamp).setOnClickListener(v -> showSpeedRampDialog());
        findViewById(R.id.btnAudioBoost).setOnClickListener(v -> showAudioBoostDialog());
        findViewById(R.id.btnRemoveSilence).setOnClickListener(v -> showRemoveSilenceDialog());
        findViewById(R.id.btnCutoutImage_old_NEW).setOnClickListener(v -> pickCutoutImage());
        findViewById(R.id.btnColorTint).setOnClickListener(v -> showColorTintDialog());

        
        findViewById(R.id.btnPreciseCut).setOnClickListener(v -> showPreciseCutDialog());
        findViewById(R.id.btnColorAdjust).setOnClickListener(v -> showColorAdjustDialog());
        findViewById(R.id.btnFrame).setOnClickListener(v -> showFrameDialog());
        findViewById(R.id.btnSocialSize).setOnClickListener(v -> showSocialSizeDialog());
        findViewById(R.id.btnPiP).setOnClickListener(v -> pickPipVideo());
        findViewById(R.id.btnSwapAudio).setOnClickListener(v -> pickSwapAudioVideo());
        findViewById(R.id.btnShadowText).setOnClickListener(v -> showShadowTextDialog());
        findViewById(R.id.btnSplit).setOnClickListener(v -> showSplitDialog());

        String appLangCode = "en";
        try {
            if (android.os.Build.VERSION.SDK_INT >= 24) {
                appLangCode = getResources().getConfiguration().getLocales().get(0).getLanguage();
            } else {
                appLangCode = getResources().getConfiguration().locale.getLanguage();
            }
        } catch (Exception e) {}

        loadTheme();
    }

    private void pickVideo() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("video/*");
        startActivityForResult(i, PICK_VIDEO);
    }

    private void pickMusic() {
        if (videoPath == null) { toast(tr("\u0627\u062e\u062a\u0631 \u0641\u064a\u062f\u064a\u0648 \u0623\u0648\u0644\u0627\u064b")); return; }
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("audio/*");
        startActivityForResult(i, PICK_MUSIC);
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (res != RESULT_OK || data == null) return;
        Uri uri = data.getData();
        if (req == PICK_VIDEO) {
            if (isCutoutFlow) {
                if (data == null || data.getData() == null) { isCutoutFlow = false; return; }
                final android.net.Uri cutoutVidUri = data.getData();
                tvStatus.setText("جاري تجهيز الفيديو...");
                tvStatus.setTextColor(0xFFFFC107);
                new Thread(() -> {
                    final String result = copyToCache(cutoutVidUri, "video_cutout_" + System.currentTimeMillis() + ".mp4");
                    runOnUiThread(() -> {
                        if (result != null) {
                            videoPath = result;
                            ProjectState.originalVideoPath = result;
                            isCutoutFlow = false;
                            if (pendingCutoutPath != null) {
                                applyCutoutImage(pendingCutoutPath);
                                pendingCutoutPath = null;
                            }
                        } else {
                            isCutoutFlow = false;
                            toast("فشل نسخ الفيديو");
                        }
                    });
                }).start();
                return;
            }
            if (isPipFlow) {
                tvStatus.setText("جاري نسخ الفيديو الرئيسي...");
                new Thread(() -> {
                    final String result = copyToCache(uri, "video.mp4");
                    runOnUiThread(() -> {
                        if (result != null) {
                            videoPath = result;
                            isPipFlow = false;
                            pickPipVideo(); // الآن سيفتح الفيديو الثاني
                        } else {
                            isPipFlow = false;
                            toast("فشل نسخ الفيديو الرئيسي");
                        }
                    });
                }).start();
                return;
            }
            ProjectState.reset();
            videoPath = copyToCache(uri, "video.mp4");
            ProjectState.originalVideoPath = videoPath;
            if (videoPath != null) {
                Intent ed = new Intent(this, EditorActivity.class);
                ed.putExtra(EditorActivity.EXTRA_VIDEO_PATH, videoPath);
                startActivityForResult(ed, PICK_EDITOR);
            }
            String name = getFileName(uri); tvVideoInfo.setText(tr("\u200F\u062a\u0645 \u0627\u062e\u062a\u064a\u0627\u0631: \u202A") + name + "\u202C");
            try {
                android.media.MediaMetadataRetriever r = new android.media.MediaMetadataRetriever();
                r.setDataSource(videoPath);
                String d = r.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
                if (d != null) videoDurationMs = Long.parseLong(d);
                r.release();
            } catch (Exception e) { videoDurationMs = 0; }
        } else if (req == PICK_DUCK_MUSIC) {
            String mPath = copyToCache(data.getData(), "duck_music.mp3");
            if (mPath != null) applyAudioDuck(mPath);
        } else if (req == PICK_EDITOR) {
            String tool = data.getStringExtra(EditorActivity.EXTRA_TOOL);
            if (tool != null) handleEditorTool(tool);
        } else if (req == PICK_CUTOUT_IMAGE) {
            if (data.getData() != null) {
                String imgPath = copyToCache(data.getData(), "source_image.png");
                if (imgPath != null) {
                    Intent cut = new Intent(this, CutoutActivity.class);
                    cut.putExtra(CutoutActivity.EXTRA_IMAGE_PATH, imgPath);
                    startActivityForResult(cut, 2002);
                }
            }
        } else if (req == 2002) {
            String resultPath = data.getStringExtra(CutoutActivity.EXTRA_RESULT_PATH);
            if (resultPath != null) {
                if (videoPath == null || ProjectState.originalVideoPath == null) {
                    // لا يوجد فيديو مختار - نطلب من المستخدم اختيار الفيديو أولاً
                    pendingCutoutPath = resultPath;
                    isCutoutFlow = true;
                    //
                    Intent vidIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    vidIntent.setType("video/*");
                    startActivityForResult(vidIntent, PICK_VIDEO);
                } else {
                    applyCutoutImage(resultPath);
                }
            }
        } else if (req == PICK_COLLAGE_VIDEOS) {
            try {
                multiVideoPaths.clear();
                if (data.getClipData() != null) {
                    int cnt = data.getClipData().getItemCount();
                    for (int i = 0; i < cnt; i++) {
                        String pth = copyToCache(data.getClipData().getItemAt(i).getUri(), "col_" + i + "_" + System.currentTimeMillis() + ".mp4");
                        if (pth != null) { multiVideoPaths.add(pth); }
                    }
                } else if (data.getData() != null) {
                    String pth = copyToCache(data.getData(), "col_0_" + System.currentTimeMillis() + ".mp4");
                    if (pth != null) { multiVideoPaths.add(pth); }
                }
                
                if (multiVideoPaths.size() >= 2) {
                    videoPath = multiVideoPaths.get(0);
                    ProjectState.originalVideoPath = videoPath;
                    multiVideoPaths.remove(0);
                    applyCollage();
                } else {
                    toast("الرجاء اختيار فيديوهين على الأقل");
                }
            } catch (Exception e) {
                toast("خطأ: " + e.getMessage());
            }
        } else if (req == PICK_VIDEO_2) {
            multiVideoPaths.clear();
            if (data.getClipData() != null) {
                int cnt = data.getClipData().getItemCount();
                for (int i = 0; i < cnt; i++) {
                    String pth = copyToCache(data.getClipData().getItemAt(i).getUri(), "merge_" + i + "_" + System.currentTimeMillis() + ".mp4");
                    if (pth != null) multiVideoPaths.add(pth);
                }
            } else if (data.getData() != null) {
                String pth = copyToCache(data.getData(), "merge_0_" + System.currentTimeMillis() + ".mp4");
                if (pth != null) multiVideoPaths.add(pth);
            }
            
            // إذا لم يكن هناك فيديو أساسي، نأخذ أول فيديو من القائمة كفيديو أساسي
            if (videoPath == null && !multiVideoPaths.isEmpty()) {
                videoPath = multiVideoPaths.get(0);
                ProjectState.originalVideoPath = videoPath;
                multiVideoPaths.remove(0);
            }
            
            if (!multiVideoPaths.isEmpty()) mergeVideos();
        } else if (req == PICK_MUSIC) {
            musicPath = copyToCache(uri, "music.mp3");
            if (musicPath != null) {
                String[] options = {tr("الموسيقى فقط"), tr("تكرار الموسيقى"), tr("الأطول"), tr("دمج الصوت")};
                showPurpleDialog(tr("طريقة الدمج"), options, which -> {
                    ProjectState.musicPath = musicPath;
                    ProjectState.musicMergeType = which;
                    ProjectState.pendingCount = ProjectState.getPendingCount();
                    tvStatus.setText("تم إضافة الموسيقى (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                    tvStatus.setTextColor(0xFFFFC107);
                    toast("سيتم تطبيق الدمج عند التصدير");
                });
            }
        } else if (req == PICK_IMAGE) {
            imagePath = copyToCache(uri, "watermark.png");
            applyWatermark();
        } else if (req == PICK_WATERMARK_PREVIEW) {
            int x = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_X, 0);
            int y = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_Y, 0);
            int w = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_W, 200);
            int h = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_H, 200);
            applyWatermarkFinal(x, y, w, h);
        } else if (req == PICK_TRIM) {
            if (data != null) {
                float start = data.getFloatExtra(TrimActivity.EXTRA_RESULT_START, 0);
                float end = data.getFloatExtra(TrimActivity.EXTRA_RESULT_END, 0);
                
                float total = videoDurationMs / 1000f;
                if (total <= 0) {
                    try {
                        android.media.MediaMetadataRetriever r = new android.media.MediaMetadataRetriever();
                        r.setDataSource(videoPath);
                        total = Long.parseLong(r.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000f;
                        r.release();
                    } catch (Exception e) {}
                }
                
                float cutStart = start;
                float cutEnd = total - end; // مدة القص من النهاية
                if (cutEnd < 0) cutEnd = 0;
                
                ProjectState.trimStart = cutStart;
                ProjectState.trimEnd = cutEnd;
                ProjectState.pendingCount = ProjectState.getPendingCount();
                
                tvStatus.setText("تم تحديد القص (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                tvStatus.setTextColor(0xFFFFC107);
                toast("سيتم التطبيق عند التصدير");
            }
        } else if (req == PICK_IMAGES) {
            imagePaths.clear();
            if (data.getClipData() != null) {
                int cnt = data.getClipData().getItemCount();
                for (int i = 0; i < cnt; i++) {
                    String pth = copyToCache(data.getClipData().getItemAt(i).getUri(), "slide_" + i + ".jpg");
                    if (pth != null) imagePaths.add(pth);
                }
            } else if (data.getData() != null) {
                String pth = copyToCache(data.getData(), "slide_0.jpg");
                if (pth != null) imagePaths.add(pth);
            }
            if (imagePaths.size() >= 2) askForSlideshowMusic();
        } else if (req == 2001) {
            String mPath = copyToCache(data.getData(), "slides_music.mp3");
            createSlideshow(mPath);
        } else if (req == PICK_REPLACE_MUSIC) {
            String mPath = copyToCache(data.getData(), "replace_music.mp3");
            if (mPath != null) {
                ProjectState.musicPath = mPath;
                ProjectState.musicMergeType = 0; // 0 يعني استبدال الموسيقى (الموسيقى فقط)
                ProjectState.muteAudio = false; // التأكد من عدم كتم الصوت الجديد
                ProjectState.audioFilter = null; // إزالة أي فلاتر صوتية قديمة
                
                ProjectState.pendingCount = ProjectState.getPendingCount();
                tvStatus.setText("تم استبدال الموسيقى (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                tvStatus.setTextColor(0xFFFFC107);
                toast("سيتم استبدال الموسيقى عند التصدير");
            }
        } else if (req == PICK_STICKER_PREVIEW) {
            int x = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_X, 0);
            int y = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_Y, 0);
            int w = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_W, 200);
            int h = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_H, 200);
            applyStickerFinal(x, y, w, h);
        } else if (req == PICK_VOI) {
            if (data == null || data.getData() == null) { toast("لم يتم اختيار صورة"); return; }
            final android.net.Uri voiImgUri = data.getData();
            tvStatus.setText("جاري نسخ الصورة...");
            tvStatus.setTextColor(0xFFFFC107);
            progressBar.setVisibility(android.view.View.VISIBLE);
            progressBar.setIndeterminate(true);
            new Thread(() -> {
                final String result = copyToCache(voiImgUri, "voi_image.jpg");
                runOnUiThread(() -> {
                    progressBar.setVisibility(android.view.View.INVISIBLE);
                    progressBar.setIndeterminate(false);
                    if (result == null) { toast("فشل نسخ الصورة"); return; }
                    voiImagePath = result;
//                     toast("تم اختيار الصورة");
                    pickVoiVideo();
                });
            }).start();
        } else if (req == PICK_VOI + 1) {
            if (data == null || data.getData() == null) { toast("لم يتم اختيار فيديو"); return; }
            final android.net.Uri voiVidUri = data.getData();
            tvStatus.setText("جاري نسخ الفيديو...");
            tvStatus.setTextColor(0xFFFFC107);
            progressBar.setVisibility(android.view.View.VISIBLE);
            progressBar.setIndeterminate(true);
            new Thread(() -> {
                final String result = copyToCache(voiVidUri, "voi_video.mp4");
                runOnUiThread(() -> {
                    progressBar.setVisibility(android.view.View.INVISIBLE);
                    progressBar.setIndeterminate(false);
                    if (result == null) { toast("فشل نسخ الفيديو"); return; }
                    voiVideoPath = result;
//                     toast("تم اختيار الفيديو");
                    openVoiPreviewFromPaths();
                });
            }).start();
        } else if (req == PICK_VOI + 2) {
            if (data == null) return;
            int x = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_X, 0);
            int y = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_Y, 0);
            int w = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_W, 200);
            int h = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_H, 200);
            applyVideoOverImage(x, y, w, h);
        } else if (req == PICK_PIP_VIDEO) {
            if (data == null || data.getData() == null) return;
            final android.net.Uri pipUri = data.getData();
            progressBar.setVisibility(android.view.View.VISIBLE);
            progressBar.setIndeterminate(true);
            new Thread(() -> {
                final String result = copyToCache(pipUri, "pip_video_temp.mp4");
                runOnUiThread(() -> {
                    progressBar.setVisibility(android.view.View.INVISIBLE);
                    progressBar.setIndeterminate(false);
                    if (result != null) {
                        pipVideoPath = result;
                        askPipPosition();
                    }
                });
            }).start();
        } else if (req == PICK_CROP_INTERACTIVE) {
            if (data != null) {
                int x = data.getIntExtra(CropActivity.EXTRA_RESULT_X, 0);
                int y = data.getIntExtra(CropActivity.EXTRA_RESULT_Y, 0);
                int w = data.getIntExtra(CropActivity.EXTRA_RESULT_W, 200);
                int h = data.getIntExtra(CropActivity.EXTRA_RESULT_H, 200);
                applyCropInteractive(x, y, w, h);
            }
        } else if (req == PICK_PIP_PREVIEW) {
            
            int x = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_X, 0);
            int y = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_Y, 0);
            int w = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_W, 200);
            int h = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_H, 200);
            applyPiPFinal(x, y, w, h);
        } else if (req == PICK_SWAP_AUDIO) {
            swapAudioPath = copyToCache(data.getData(), "swap_audio.mp4");
            if (swapAudioPath != null) applySwapAudio();
        } else if (req == PICK_SHADOWTEXT_PREVIEW) {
            int x = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_X, 0);
            int y = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_Y, 0);
            int w = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_W, 200);
            int h = data.getIntExtra(WatermarkPreviewActivity.EXTRA_RESULT_H, 200);
            applyShadowTextFinal(x, y, w, h);
        }
    }
    
    private String copyToCache(Uri uri, String name) {
        if (uri == null) {
            toast("خطأ: لا يوجد ملف");
            return null;
        }
        try {
            File out = new File(getCacheDir(), name);
            java.io.InputStream is = getContentResolver().openInputStream(uri);
            if (is == null) {
                toast("تعذر فتح الملف");
                return null;
            }
            java.io.FileOutputStream os = new java.io.FileOutputStream(out);
            byte[] buf = new byte[8192];
            int len;
            long total = 0;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
                total += len;
            }
            is.close();
            os.close();
            os.flush();
            // Verify size
            if (out.length() < 100) {
                toast("الملف صغير جداً - فشل النسخ");
                return null;
            }
            return out.getAbsolutePath();
        } catch (Exception e) {
            toast("خطأ: " + e.getMessage());
            return null;
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (idx >= 0) result = cursor.getString(idx);
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }

    private String newOut(String suffix) {
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        return new File(outputDir, "video_" + suffix + "_" + ts + ".mp4").getAbsolutePath();
    }

    private LinearLayout makeVertical() {
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(50, 20, 50, 20);
        return ll;
    }

    private void toast(String m) {
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
    }

    private String findLastOutputPath() {
        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MOVIES), "VideoEditorPro");
            if (!dir.exists()) return null;
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) return null;
            File latest = null;
            for (File f : files) {
                if (latest == null || f.lastModified() > latest.lastModified()) latest = f;
            }
            return latest != null ? latest.getAbsolutePath() : null;
        } catch (Exception e) { return null; }
    }
    private void runFFmpeg(String[] args, String label) { runFFmpeg(args, label, null); }
    private void runFFmpeg(String[] args, String label, Runnable onSuccess) {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        tvStatus.setText(tr("جاري ") + label);
        tvStatus.setTextColor(0xFFFFC107);
        progressBar.setVisibility(android.view.View.VISIBLE);
        progressBar.setProgress(0);
        btnCancelOp.setVisibility(android.view.View.VISIBLE);
        StringBuilder sb = new StringBuilder();
        for (String s : args) sb.append(s).append(" ");
        final String cmd = sb.toString().trim();
        currentSession = FFmpegKit.executeAsync(cmd, s -> runOnUiThread(() -> {
            progressBar.setVisibility(android.view.View.INVISIBLE);
            btnCancelOp.setVisibility(android.view.View.GONE);
            currentSession = null;
            if (ReturnCode.isSuccess(s.getReturnCode())) {
                tvStatus.setText(tr("تم: ") + label);
                tvStatus.setTextColor(0xFF4CAF50);
                String lastOut = findLastOutputPath();
                if (lastOut != null) notifyGallery(lastOut);
                if (onSuccess != null) onSuccess.run();
            } else if (ReturnCode.isCancel(s.getReturnCode())) {
                tvStatus.setText(tr("تم إلغاء: ") + label);
                tvStatus.setTextColor(0xFFFF9800);
            } else {
                tvStatus.setText(tr("فشل: ") + label);
                tvStatus.setTextColor(0xFFFF0000);
                String fullLog = s.getAllLogsAsString();
                String log = tr("لا تفاصيل");
                if (fullLog != null) {
                    String[] lines = fullLog.split("\n");
                    for (String line : lines) {
                        if (line.contains("Error") || line.contains("error") || line.contains("Invalid") || line.contains("Unknown") || line.contains("No such")) {
                            log = line.trim();
                            break;
                        }
                    }
                    if (log.equals(tr("لا تفاصيل")) && lines.length > 0) {
                        log = lines[lines.length - 1].trim();
                    }
                }
                showWhite(new AlertDialog.Builder(this, R.style.PurpleDialog)
                    .setTitle(tr("خطأ في FFmpeg"))
                    .setMessage(log)
                    .setPositiveButton(tr("حسناً"), null)
                    .create());
            }
        }), log -> {}, statistics -> {
            if (statistics != null && videoDurationMs > 0) {
                double timeMs = statistics.getTime();
                int percent = (int)((timeMs * 100L) / videoDurationMs);
                if (percent < 0) percent = 0;
                if (percent > 99) percent = 99;
                final int pp = percent;
                runOnUiThread(() -> {
                    progressBar.setProgress(pp);
                    tvStatus.setText(tr("جاري ") + label + ": " + pp + "%");
                });
            }
        });
// old listener removed
    }

    private void showCutDialog() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        Intent intent = new Intent(this, TrimActivity.class);
        intent.putExtra(TrimActivity.EXTRA_VIDEO_PATH, videoPath);
        startActivityForResult(intent, PICK_TRIM);
    }

        private void applyTrim(float start, float end) {
        ProjectState.trimStart = start;
        ProjectState.trimEnd = end;
        ProjectState.pendingCount++;
        tvStatus.setText("تم تحديد القص (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم التطبيق عند التصدير");
    }

    private void showPreciseCutDialog() { showCutDialog(); }



    private String wrapText(String text, int maxCharsPerLine) {
        if (text == null || text.trim().isEmpty()) return "";
        StringBuilder result = new StringBuilder();
        String[] words = text.split("\\s+");
        int currentLineLength = 0;
        for (String word : words) {
            if (currentLineLength + word.length() + 1 > maxCharsPerLine && currentLineLength > 0) {
                result.append("\n");
                currentLineLength = 0;
            }
            result.append(word).append(" ");
            currentLineLength += word.length() + 1;
        }
        return result.toString().trim();
    }

    private void showFilterDialog() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        String[] names = {tr("ابيض واسود"), tr("سيبيا"), tr("تباين عالي"), tr("سلبي"), tr("توهج دافئ")};
        String[] vals = {"hue=s=0", "colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131", "eq=contrast=1.8:saturation=1.3", "negate", "colorbalance=rs=.3:gs=.1:bs=-.1"};
        showPurpleDialog(tr("فلتر"), names, i -> {
            String out = newOut("filter");
            runFFmpeg(new String[]{"-y", "-i", videoPath, "-vf", vals[i], "-c:a", "aac", "-b:a", "96k", out}, tr("فلتر"));
        });
    }

    private void showSpeedDialog() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }

        final String[] names = {tr("1) 0.5x بطيء"), tr("2) 1.5x سريع"), tr("3) 2x سريع جداً")};
        final String[] vals = {"0.5", "1.5", "2.0"};

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(30, 30, 30, 30);
        container.setBackgroundColor(0xFF6A1B9A);

        TextView title = new TextView(this);
        title.setText(tr("سرعة الفيديو"));
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(20);
        title.setGravity(android.view.Gravity.CENTER);
        title.setPadding(0, 0, 0, 30);
        container.addView(title);

        for (int i = 0; i < names.length; i++) {
            final int idx = i;
            Button btn = new Button(this);
            btn.setText(names[i]);
            btn.setTextColor(0xFFFFFFFF);
            btn.setTextSize(16);
            btn.setBackgroundResource(R.drawable.button_click_effect);
            btn.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
            btn.setPadding(20, 25, 20, 25);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.setMargins(0, 8, 0, 8);
            btn.setLayoutParams(lp);
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                    String speedStr = vals[idx];
                    ProjectState.videoSpeed = Float.parseFloat(speedStr);
                    ProjectState.pendingCount = ProjectState.getPendingCount();
                    tvStatus.setText("تم تغيير السرعة (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                    tvStatus.setTextColor(0xFFFFC107);
                    toast("سيتم تطبيق السرعة عند التصدير");
                }
            });
            container.addView(btn);
        }

        Button cancelBtn = new Button(this);
        cancelBtn.setText(tr("إلغاء"));
        cancelBtn.setTextColor(0xFFFFFFFF);
        cancelBtn.setTextSize(14);
        cancelBtn.setBackgroundResource(R.drawable.button_click_effect);
        cancelBtn.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
        cancelBtn.setPadding(20, 20, 20, 20);
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(-1, -2);
        clp.setMargins(0, 25, 0, 0);
        cancelBtn.setLayoutParams(clp);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { dialog.dismiss(); }
        });
        container.addView(cancelBtn);

        dialog = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.PurpleDialog)
                .setView(container)
                .create();
        dialog.show();
    }

    private androidx.appcompat.app.AlertDialog dialog;

    private void showRotateDialog() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        String[] names = {tr("90 درجة يمين"), tr("90 درجة يسار"), tr("180 درجة")};
        String[] vals = {"transpose=1", "transpose=2", "transpose=1,transpose=1"};
        showPurpleDialog(tr("تدوير"), names, i -> {
            ProjectState.filters.add(vals[i]);
            ProjectState.pendingCount = ProjectState.getPendingCount();
            tvStatus.setText("تم إضافة التدوير (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
            tvStatus.setTextColor(0xFFFFC107);
            toast("سيتم التطبيق عند التصدير");
        });
    }


    private void saveMp3ToGallery(String cachePath) {
        try {
            java.io.File file = new java.io.File(cachePath);
            String ts = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US).format(new java.util.Date());
            android.content.ContentValues values = new android.content.ContentValues();
            values.put(android.provider.MediaStore.Audio.Media.DISPLAY_NAME, "video_audio_" + ts + ".mp3");
            values.put(android.provider.MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg");
            values.put(android.provider.MediaStore.Audio.Media.RELATIVE_PATH, android.os.Environment.DIRECTORY_MUSIC + "/VideoEditorPro");
            values.put(android.provider.MediaStore.Audio.Media.IS_PENDING, 1);
            android.net.Uri item = getContentResolver().insert(
                android.provider.MediaStore.Audio.Media.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL_PRIMARY),
                values);
            if (item != null) {
                java.io.OutputStream os = getContentResolver().openOutputStream(item);
                java.io.FileInputStream is = new java.io.FileInputStream(file);
                byte[] buf = new byte[8192]; int len;
                while ((len = is.read(buf)) > 0) { os.write(buf, 0, len); }
                is.close(); os.close();
                values.clear();
                values.put(android.provider.MediaStore.Audio.Media.IS_PENDING, 0);
                getContentResolver().update(item, values, null, null);
                tvStatus.setText("تم استخراج الصوت بنجاح!");
                tvStatus.setTextColor(0xFF4CAF50);
                toast("تم حفظ MP3 في مجلد Music/VideoEditorPro");
            } else {
                toast("فشل إنشاء سجل في المعرض");
            }
        } catch (Exception e) {
            toast("فشل حفظ MP3: " + e.getMessage());
        }
    }

    private void showAudioDialog() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        String[] options = {tr("استخراج الصوت MP3"), tr("حذف الصوت (كتم)"), tr("رفع الصوت (مضاعف)"), tr("خفض الصوت (نصف)")};
        showPurpleDialog(tr("أدوات الصوت"), options, which -> {
            if (which == 0) {
                // استخراج الصوت (طابور)
                ProjectState.toMp3 = true;
                ProjectState.pendingCount = ProjectState.getPendingCount();
                tvStatus.setText("تم إضافة استخراج الصوت (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                tvStatus.setTextColor(0xFFFFC107);
                toast("سيتم استخراج الصوت عند التصدير");
            } else if (which == 1) {
                // كتم الصوت (طابور)
                ProjectState.muteAudio = true;
                ProjectState.audioFilter = null; // إزالة أي فلتر سابق
                ProjectState.pendingCount = ProjectState.getPendingCount();
                tvStatus.setText("تم كتم الصوت (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                tvStatus.setTextColor(0xFFFFC107);
                toast("سيتم كتم الصوت عند التصدير");
            } else if (which == 2) {
                // رفع الصوت (طابور)
                ProjectState.audioFilter = "volume=2.0";
                ProjectState.muteAudio = false; // إلغاء الكتم
                ProjectState.pendingCount = ProjectState.getPendingCount();
                tvStatus.setText("تم رفع الصوت (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                tvStatus.setTextColor(0xFFFFC107);
                toast("سيتم رفع الصوت عند التصدير");
            } else {
                // خفض الصوت (طابور)
                ProjectState.audioFilter = "volume=0.5";
                ProjectState.muteAudio = false; // إلغاء الكتم
                ProjectState.pendingCount = ProjectState.getPendingCount();
                tvStatus.setText("تم خفض الصوت (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                tvStatus.setTextColor(0xFFFFC107);
                toast("سيتم خفض الصوت عند التصدير");
            }
        });
    }



    private void showVideoToolsDialog() {
        String[] options = {tr("دمج"), tr("عكس"), tr("مرآة أفقية"), tr("مرآة عمودية"), tr("تيك توك"), tr("انستغرام")};
        showPurpleDialog(tr("أدوات الفيديو"), options, which -> {
            if (which == 0) {
                pickVideo2();
            } else if (which == 1) {
                ProjectState.filters.add("scale=720:-2,fps=30,reverse");
                ProjectState.muteAudio = true; // كتم الصوت
                ProjectState.pendingCount = ProjectState.getPendingCount();
                tvStatus.setText("تم إضافة العكس (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                tvStatus.setTextColor(0xFFFFC107);
                toast("سيتم التطبيق عند التصدير");
            } else if (which == 2) {
                ProjectState.filters.add("hflip");
                ProjectState.pendingCount = ProjectState.getPendingCount();
                tvStatus.setText("تم إضافة المرآة الأفقية (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                tvStatus.setTextColor(0xFFFFC107);
                toast("سيتم التطبيق عند التصدير");
            } else if (which == 3) {
                ProjectState.filters.add("vflip");
                ProjectState.pendingCount = ProjectState.getPendingCount();
                tvStatus.setText("تم إضافة المرآة العمودية (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                tvStatus.setTextColor(0xFFFFC107);
                toast("سيتم التطبيق عند التصدير");
            } else if (which == 4) {
                ProjectState.filters.add("crop=ih*9/16:ih,scale=1080:1920");
                ProjectState.pendingCount = ProjectState.getPendingCount();
                tvStatus.setText("تم إضافة أبعاد تيك توك (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                tvStatus.setTextColor(0xFFFFC107);
                toast("سيتم التطبيق عند التصدير");
            } else if (which == 5) {
                ProjectState.filters.add("crop=ih:ih,scale=1080:1080");
                ProjectState.pendingCount = ProjectState.getPendingCount();
                tvStatus.setText("تم إضافة أبعاد انستغرام (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                tvStatus.setTextColor(0xFFFFC107);
                toast("سيتم التطبيق عند التصدير");
            }
        });
    }


    private void pickVideo2() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("video/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(i, tr("اختر الفيديوهات")), PICK_VIDEO_2);
    }

    private void mergeVideos() {
        if (multiVideoPaths.isEmpty()) return;
        ProjectState.isMerging = true;
        ProjectState.mergePaths = new java.util.ArrayList<>(multiVideoPaths);
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تم إضافة دمج الفيديو (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم دمج الفيديو عند التصدير");
    }


    private void applyWatermark() {
        if (videoPath == null || imagePath == null) return;
        Intent intent = new Intent(this, WatermarkPreviewActivity.class);
        intent.putExtra(WatermarkPreviewActivity.EXTRA_VIDEO_PATH, videoPath);
        intent.putExtra(WatermarkPreviewActivity.EXTRA_IMAGE_PATH, imagePath);
        startActivityForResult(intent, PICK_WATERMARK_PREVIEW);
    }

    private void applyWatermarkFinal(int x, int y, int w, int h) {
        if (x < 0) x = 0; if (y < 0) y = 0; if (w < 10) w = 10; if (h < 10) h = 10;
        ProjectState.overlays.add(new ProjectState.Overlay(imagePath, x, y, w, h));
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تم إضافة العلامة المائية (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم تطبيق العلامة المائية عند التصدير");
    }

    private void pickImages() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(i, tr("اختر الصور")), PICK_IMAGES);
    }





    private void createSlideshow(String musicFilePath) {
        ProjectState.slideshowImages.clear();
        ProjectState.slideshowImages.addAll(imagePaths);
        ProjectState.slideshowMusic = musicFilePath;
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تم إضافة عرض الشرائح (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم إنشاء عرض الشرائح عند التصدير");
    }

    private void showQualityDialog() {
        String[] names = {"1080p", "720p", "480p", "360p"};
        final String[] vals = {"1080", "720", "480", "360"};
        int current = 0;
        for (int i = 0; i < vals.length; i++) if (vals[i].equals(exportQuality)) current = i;
        showPurpleDialog(tr("الجودة: ") + exportQuality + "p", names, which -> {
            exportQuality = vals[which];
            ProjectState.targetResolution = exportQuality;
            ProjectState.qualityChanged = true;
            ProjectState.pendingCount = ProjectState.getPendingCount();
            tvStatus.setText("تم اختيار الجودة (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
            tvStatus.setTextColor(0xFFFFC107);
            toast(tr("سيتم التصدير بجودة: ") + exportQuality + "p");
        });
    }

    private void showRepeatDialog() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        String[] names = {tr("2x سريع جداً"), "3x", "5x"};
        final int[] vals = {2, 3, 5};
        showPurpleDialog(tr("تكرار"), names, which -> {
            ProjectState.repeatCount = vals[which];
            ProjectState.pendingCount = ProjectState.getPendingCount();
            tvStatus.setText("تم إضافة التكرار (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
            tvStatus.setTextColor(0xFFFFC107);
            toast("سيتم تكرار الفيديو عند التصدير");
        });
    }

    private void pickReplaceMusic() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("audio/*");
        startActivityForResult(i, PICK_REPLACE_MUSIC);
    }

    private void replaceMusic(String musicFilePath) {
        ProjectState.musicPath = musicFilePath;
        ProjectState.musicMergeType = 0;
        ProjectState.muteAudio = false;
        ProjectState.audioFilter = null;
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تم استبدال الموسيقى (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم استبدال الموسيقى عند التصدير");
    }

    private void showVideoInfo() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        try {
            android.media.MediaMetadataRetriever r = new android.media.MediaMetadataRetriever();
            r.setDataSource(videoPath);
            String w = r.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String h = r.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String dur = r.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
            r.release();
            File f = new File(videoPath);
            double sizeMB = f.length() / (1024.0 * 1024.0);
            long durMs = dur != null ? Long.parseLong(dur) : 0;
            int sec = (int)(durMs / 1000);
            int min = sec / 60; sec = sec % 60;
            String info = tr("الدقة: ") + w + "x" + h + "\nDuration: " + min + ":" + String.format("%02d", sec) + "\nSize: " + String.format("%.2f", sizeMB) + " MB";
            showWhite(new AlertDialog.Builder(this, R.style.PurpleDialog).setTitle(tr("معلومات الفيديو")).setMessage(info).setPositiveButton(tr("حسناً"), null).create());
        } catch (Exception e) { toast(tr("خطأ: ") + e.getMessage()); }
    }

    private void denoiseAudio() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        
        // إذا كان هناك فلتر صوتي سابق، نضيف إليه
        if (ProjectState.audioFilter == null) {
            ProjectState.audioFilter = "afftdn=nf=-25";
        } else {
            ProjectState.audioFilter += ",afftdn=nf=-25";
        }
        
        ProjectState.muteAudio = false; // إلغاء الكتم إن وجد
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تم إضافة إزالة الضوضاء (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم إزالة الضوضاء عند التصدير");
    }


    private void saveScreenshot(String timeSec) {
        try {
            if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
            
            float time = Float.parseFloat(timeSec);
            float maxDuration = videoDurationMs / 1000f;
            if (time > maxDuration) time = maxDuration - 0.1f; 
            if (time < 0) time = 0;
            
            long timeUs = (long)(time * 1000000);
            
            android.media.MediaMetadataRetriever r = new android.media.MediaMetadataRetriever();
            r.setDataSource(videoPath);
            
            Bitmap bmp = r.getFrameAtTime(timeUs, android.media.MediaMetadataRetriever.OPTION_CLOSEST);
            if (bmp == null) {
                bmp = r.getFrameAtTime(timeUs, android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            }
            r.release();
            
            if (bmp == null) { toast(tr("فشل استخراج الصورة (قد يكون الوقت خارج النطاق)")); return; }
            
            String fname = "screenshot_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".png";
            
            // الحفظ باستخدام MediaStore لضمان الظهور في المعرض
            android.content.ContentValues values = new android.content.ContentValues();
            values.put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, fname);
            values.put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/VideoEditorPro");
            values.put(android.provider.MediaStore.Images.Media.IS_PENDING, 1);

            android.net.Uri collection = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            android.net.Uri itemUri = getContentResolver().insert(collection, values);
            
            if (itemUri != null) {
                java.io.OutputStream out = getContentResolver().openOutputStream(itemUri);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
                values.clear();
                values.put(android.provider.MediaStore.Images.Media.IS_PENDING, 0);
                getContentResolver().update(itemUri, values, null, null);
                toast(tr("تم الحفظ في معرض الصور: ") + fname);
            } else {
                toast(tr("فشل إنشاء ملف الصورة"));
            }
            
        } catch (Exception e) { 
            toast(tr("خطأ: ") + e.getMessage()); 
        }
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        getSharedPreferences("theme", MODE_PRIVATE).edit().putBoolean("dark", isDarkMode).apply();
        applyTheme();
        toast(isDarkMode ? tr("وضع ليلي") : tr("وضع نهاري"));
    }

    private void loadTheme() {
        isDarkMode = getSharedPreferences("theme", MODE_PRIVATE).getBoolean("dark", true);
        applyTheme();
    }

    private void applyTheme() {
        android.view.View root = findViewById(R.id.mainLayout);
        android.view.View scroll = findViewById(R.id.mainScroll);
        if (isDarkMode) {
            if (scroll != null) scroll.setBackgroundColor(0xFF0F0F0F);
            if (root != null) root.setBackgroundColor(0xFF0F0F0F);
            tvVideoInfo.setTextColor(0xFFFFEB3B);
        } else {
            if (scroll != null) scroll.setBackgroundColor(0xFFF5F5F5);
            if (root != null) root.setBackgroundColor(0xFFF5F5F5);
            tvVideoInfo.setTextColor(0xFFFFEB3B);
        }
    }

    private void showStickersDialog() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        final java.util.List<String> emojisList = new java.util.ArrayList<String>();
        try {
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(getAssets().open("emojis.txt"), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) { line = line.trim(); if (!line.isEmpty()) emojisList.add(line); }
            br.close();
        } catch (Exception e) { toast(tr("خطأ: ") + e.getMessage()); return; }
        final android.widget.GridView grid = new android.widget.GridView(this);
        grid.setNumColumns(4);
        grid.setBackgroundColor(0xFF1A1A1A);
        grid.setPadding(15, 15, 15, 15);
        grid.setVerticalSpacing(10);
        grid.setHorizontalSpacing(10);
        grid.setAdapter(new android.widget.BaseAdapter() {
            public int getCount() { return emojisList.size(); }
            public Object getItem(int i) { return emojisList.get(i); }
            public long getItemId(int i) { return i; }
            public android.view.View getView(int pos, android.view.View view, android.view.ViewGroup parent) {
                android.widget.TextView tv;
                if (view == null) {
                    tv = new android.widget.TextView(MainActivity.this);
                    int sz = (int)(getResources().getDisplayMetrics().density * 80);
                    tv.setLayoutParams(new android.widget.AbsListView.LayoutParams(sz, sz));
                    tv.setTextSize(46);
                    tv.setGravity(android.view.Gravity.CENTER);
                    tv.setBackgroundColor(0xFF2A2A2A);
                } else { tv = (android.widget.TextView) view; }
                tv.setText(emojisList.get(pos));
                return tv;
            }
        });
        grid.setOnItemClickListener((parent, view, position, id) -> renderEmojiAndPreview(emojisList.get(position)));
        showWhite(new AlertDialog.Builder(this, R.style.PurpleDialog).setTitle(tr("اختر إيموجي")).setView(grid).setNegativeButton(tr("إلغاء"), null).create());
    }

    private void renderEmojiAndPreview(String emoji) {
        try {
            int size = 400;
            Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            android.graphics.Canvas canvas = new android.graphics.Canvas(bmp);
            android.graphics.Paint paint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(size * 0.85f);
            paint.setTextAlign(android.graphics.Paint.Align.CENTER);
            android.graphics.Paint.FontMetrics fm = paint.getFontMetrics();
            float cy = size/2f - (fm.ascent + fm.descent) / 2f;
            canvas.drawText(emoji, size/2f, cy, paint);
            File cacheFile = new File(getCacheDir(), "preview_sticker.png");
            FileOutputStream fos = new FileOutputStream(cacheFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            stickerPath = cacheFile.getAbsolutePath();
            Intent intent = new Intent(this, WatermarkPreviewActivity.class);
            intent.putExtra(WatermarkPreviewActivity.EXTRA_VIDEO_PATH, videoPath);
            intent.putExtra(WatermarkPreviewActivity.EXTRA_IMAGE_PATH, stickerPath);
            startActivityForResult(intent, PICK_STICKER_PREVIEW);
        } catch (Exception e) { toast(tr("خطأ: ") + e.getMessage()); }
    }

    private void applyStickerFinal(int x, int y, int w, int h) {
        ProjectState.overlays.add(new ProjectState.Overlay(stickerPath, x, y, w, h));
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تمت إضافة النص (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم التطبيق عند التصدير");
        //
    }

    private void showColorAdjustDialog() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        LinearLayout ll = makeVertical();
        final TextView tvB = new TextView(this); tvB.setText("Brightness: 0"); tvB.setTextColor(0xFFFFFFFF);
        final android.widget.SeekBar sbB = new android.widget.SeekBar(this); sbB.setMax(200); sbB.setProgress(100);
        sbB.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(android.widget.SeekBar sb, int p, boolean u) { tvB.setText(tr("السطوع: ") + (p - 100)); }
            public void onStartTrackingTouch(android.widget.SeekBar sb) {}
            public void onStopTrackingTouch(android.widget.SeekBar sb) {}
        });
        ll.addView(tvB); ll.addView(sbB);
        final TextView tvC = new TextView(this); tvC.setText("Contrast: 1.0"); tvC.setTextColor(0xFFFFFFFF);
        final android.widget.SeekBar sbC = new android.widget.SeekBar(this); sbC.setMax(200); sbC.setProgress(100);
        sbC.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(android.widget.SeekBar sb, int p, boolean u) { tvC.setText(tr("التباين: ") + String.format("%.1f", p/100f)); }
            public void onStartTrackingTouch(android.widget.SeekBar sb) {}
            public void onStopTrackingTouch(android.widget.SeekBar sb) {}
        });
        ll.addView(tvC); ll.addView(sbC);
        final TextView tvS = new TextView(this); tvS.setText("Saturation: 1.0"); tvS.setTextColor(0xFFFFFFFF);
        final android.widget.SeekBar sbS = new android.widget.SeekBar(this); sbS.setMax(300); sbS.setProgress(100);
        sbS.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(android.widget.SeekBar sb, int p, boolean u) { tvS.setText(tr("التشبع: ") + String.format("%.1f", p/100f)); }
            public void onStartTrackingTouch(android.widget.SeekBar sb) {}
            public void onStopTrackingTouch(android.widget.SeekBar sb) {}
        });
        ll.addView(tvS); ll.addView(sbS);
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.PurpleDialog).setTitle(tr("ضبط الألوان")).setView(ll)
            .setPositiveButton(tr("تطبيق"), (d, w) -> {
                float br = (sbB.getProgress() - 100) / 100f;
                float co = sbC.getProgress() / 100f;
                float sa = sbS.getProgress() / 100f;
                String filter = "eq=brightness=" + br + ":contrast=" + co + ":saturation=" + sa;
                ProjectState.filters.add(filter);
                ProjectState.pendingCount = ProjectState.getPendingCount();
                tvStatus.setText("تم إضافة ضبط الألوان (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                tvStatus.setTextColor(0xFFFFC107);
                
                    toast("سيتم التطبيق عند التصدير");})
            .setNegativeButton(tr("إلغاء"), null).create();
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFFFFFFFF);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFFFFFFFF);
            int titleId = getResources().getIdentifier("alertTitle", "id", "android");
            TextView titleTv = dialog.findViewById(titleId);
            if (titleTv != null) titleTv.setTextColor(0xFFFFFFFF);
        });
        dialog.show();
    }

    private void showFrameDialog() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        String[] names = {tr("أسود سميك (50)"), tr("أبيض سميك (50)"), tr("أحمر (30)"), tr("أزرق (30)"), tr("ذهبي (30)"), tr("رفيع (20)"), tr("رفيع (10)")};
        final int[] colors = {0x000000, 0xFFFFFF, 0xFF0000, 0x0066FF, 0xFFD700, 0x000000, 0x000000};
        final int[] sizes = {50, 50, 30, 30, 30, 20, 10};
        showPurpleDialog(tr("إطار"), names, which -> {
            String hex = String.format("%06X", colors[which] & 0xFFFFFF);
            int s = sizes[which];
            String vf = "pad=iw+" + (s*2) + ":ih+" + (s*2) + ":" + s + ":" + s + ":color=0x" + hex;
            
            ProjectState.filters.add(vf);
            ProjectState.pendingCount = ProjectState.getPendingCount();
            tvStatus.setText("تم إضافة الإطار (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
            tvStatus.setTextColor(0xFFFFC107);
            toast("سيتم تطبيق الإطار عند التصدير");
        });
    }


    private void showSocialSizeDialog() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        String[] names = {tr("تيك توك 9:16"), tr("يوتيوب 16:9"), tr("انستغرام 1:1"), tr("انستغرام ستوري"), tr("فيسبوك 4:5")};
        final String[] sizes = {"1080:1920", "1920:1080", "1080:1080", "1080:1920", "1080:1350"};
        showPurpleDialog(tr("أبعاد السوشيال ميديا"), names, which -> {
            String[] wh = sizes[which].split(":");
            int W = Integer.parseInt(wh[0]); int H = Integer.parseInt(wh[1]);
            ProjectState.hasSocialSize = true;
            ProjectState.socialWidth = W;
            ProjectState.socialHeight = H;
            ProjectState.pendingCount = ProjectState.getPendingCount();
            tvStatus.setText("تمت إضافة أبعاد السوشيال (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
            tvStatus.setTextColor(0xFFFFC107);
            toast("سيتم تطبيق الأبعاد عند التصدير");
        });
    }

    private void pickPipVideo() {
        isPipFlow = true;
        if (videoPath == null) {
            
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("video/*");
            startActivityForResult(i, PICK_VIDEO);
            return;
        }
        
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("video/*");
        startActivityForResult(i, PICK_PIP_VIDEO);
    }

    private void askPipPosition() {
        try {
            if (videoPath == null || pipVideoPath == null) {
                toast("خطأ: مسار فارغ");
                return;
            }
            Intent intent = new Intent(this, WatermarkPreviewActivity.class);
            intent.putExtra(WatermarkPreviewActivity.EXTRA_VIDEO_PATH, videoPath);
            intent.putExtra(WatermarkPreviewActivity.EXTRA_IMAGE_PATH, pipVideoPath);
            intent.putExtra(WatermarkPreviewActivity.EXTRA_IS_VIDEO, true);
            startActivityForResult(intent, PICK_PIP_PREVIEW);
        } catch (Throwable e) {
            toast("خطأ في فتح المعاينة: " + e.getMessage());
        }
    }

    private void applyPiPFinal(int x, int y, int w, int h) {
        
        if (pipVideoPath == null) { toast("خطأ: مسار الفيديو فارغ"); return; }
        if (x < 0) x = 0; if (y < 0) y = 0; if (w < 20) w = 20; if (h < 20) h = 20;
        final int fx = x, fy = y, fw = w, fh = h;
        
        String[] audioOpts = {tr("صوت الفيديو الكبير"), tr("صوت الفيديو الصغير"), tr("دمج الصوتين"), tr("بدون صوت")};
        showPurpleDialog(tr("أدوات الصوت"), audioOpts, which -> {
            ProjectState.pipVideoPath = pipVideoPath;
            ProjectState.pipX = fx;
            ProjectState.pipY = fy;
            ProjectState.pipW = fw;
            ProjectState.pipH = fh;
            ProjectState.pipAudioType = which;
            ProjectState.pendingCount = ProjectState.getPendingCount();
            tvStatus.setText("تم إضافة فيديو داخل فيديو (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
            tvStatus.setTextColor(0xFFFFC107);
            toast("سيتم تطبيق الدمج عند التصدير");
        });
    }

    private void pickVideoOverImage() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, PICK_VOI);
    }

    private void pickVoiVideo() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("video/*");
        startActivityForResult(i, PICK_VOI + 1);
    }

    private void openVoiPreviewFromPaths() {
        Intent intent = new Intent(this, WatermarkPreviewActivity.class);
        intent.putExtra(WatermarkPreviewActivity.EXTRA_VIDEO_PATH, voiImagePath);
        intent.putExtra(WatermarkPreviewActivity.EXTRA_IMAGE_PATH, voiVideoPath);
        intent.putExtra(WatermarkPreviewActivity.EXTRA_IS_VIDEO, true);
        intent.putExtra(WatermarkPreviewActivity.EXTRA_BG_IS_IMAGE, true);
        startActivityForResult(intent, PICK_VOI + 2);
    }

    private String downscaleImage(String srcPath, int maxSize) {
        try {
            android.graphics.BitmapFactory.Options opts = new android.graphics.BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            android.graphics.BitmapFactory.decodeFile(srcPath, opts);
            int origW = opts.outWidth;
            int origH = opts.outHeight;
            android.util.Log.d("VOI", "Original image: " + origW + "x" + origH);

            // Coarse downscale with inSampleSize
            int sample = 1;
            while ((origW / sample) > maxSize * 2 || (origH / sample) > maxSize * 2) {
                sample *= 2;
            }
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = sample;
            android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeFile(srcPath, opts);
            if (bmp == null) return null;

            // Precise scale to exactly maxSize
            int curW = bmp.getWidth();
            int curH = bmp.getHeight();
            if (curW > maxSize || curH > maxSize) {
                float scale = Math.min((float) maxSize / curW, (float) maxSize / curH);
                int targetW = (int) (curW * scale);
                int targetH = (int) (curH * scale);
                android.graphics.Bitmap scaled = android.graphics.Bitmap.createScaledBitmap(bmp, targetW, targetH, true);
                bmp.recycle();
                bmp = scaled;
            }

            // Force even dimensions
            int newW = (bmp.getWidth() / 2) * 2;
            int newH = (bmp.getHeight() / 2) * 2;
            if (newW != bmp.getWidth() || newH != bmp.getHeight()) {
                android.graphics.Bitmap cropped = android.graphics.Bitmap.createBitmap(bmp, 0, 0, newW, newH);
                bmp.recycle();
                bmp = cropped;
            }

            android.util.Log.d("VOI", "Final image: " + bmp.getWidth() + "x" + bmp.getHeight());

            File out = new File(getCacheDir(), "voi_bg_small.jpg");
            if (out.exists()) out.delete();
            java.io.FileOutputStream fos = new java.io.FileOutputStream(out);
            bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 95, fos);
            fos.close();
            bmp.recycle();
            return out.getAbsolutePath();
        } catch (Exception e) {
            android.util.Log.e("VOI", "downscale error", e);
            return null;
        }
    }



    private void applyVideoOverImage(int x, int y, int w, int h) {
        if (voiImagePath == null || voiVideoPath == null) { toast("الملفات غير محددة"); return; }
        if (x < 0) x = 0; if (y < 0) y = 0; if (w < 20) w = 20; if (h < 20) h = 20;
        
        android.graphics.BitmapFactory.Options origOpts = new android.graphics.BitmapFactory.Options();
        origOpts.inJustDecodeBounds = true;
        android.graphics.BitmapFactory.decodeFile(voiImagePath, origOpts);
        int origW = origOpts.outWidth; int origH = origOpts.outHeight;
        
        final String smallImage = downscaleImage(voiImagePath, 1080);
        if (smallImage == null) { toast("فشل تجهيز الصورة"); return; }
        
        android.graphics.BitmapFactory.Options newOpts = new android.graphics.BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        android.graphics.BitmapFactory.decodeFile(smallImage, newOpts);
        int newW = newOpts.outWidth; int newH = newOpts.outHeight;
        
        float sx = (float) newW / origW; float sy = (float) newH / origH;
        ProjectState.voiX = (int) (x * sx);
        ProjectState.voiY = (int) (y * sy);
        ProjectState.voiW = (int) (w * sx);
        ProjectState.voiH = (int) (h * sy);
        ProjectState.voiImagePath = smallImage;
        ProjectState.voiVideoPath = voiVideoPath;
        videoPath = voiVideoPath;
        ProjectState.originalVideoPath = voiVideoPath;
        ProjectState.isVOI = true;
        
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تم إضافة فيديو فوق صورة (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم دمج الفيديو فوق الصورة عند التصدير");
    }

    private void runFFmpegVOI(String[] args, String label) {
        tvStatus.setText("جاري " + label);
        tvStatus.setTextColor(0xFFFFC107);
        progressBar.setVisibility(android.view.View.VISIBLE);
        progressBar.setIndeterminate(false);
        progressBar.setProgress(0);
        btnCancelOp.setVisibility(android.view.View.VISIBLE);
        
        // حساب مدة الفيديو لحساب نسبة التقدم
        long totalMs = 0;
        try {
            android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
            mmr.setDataSource(voiVideoPath);
            String dur = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (dur != null) totalMs = Long.parseLong(dur);
            mmr.release();
        } catch (Exception e) {}
        final long finalTotalMs = totalMs;
        
        StringBuilder sb = new StringBuilder();
        for (String s : args) sb.append(s).append(" ");
        final String cmd = sb.toString().trim();
        currentSession = FFmpegKit.executeAsync(cmd, s -> runOnUiThread(() -> {
            progressBar.setVisibility(android.view.View.INVISIBLE);
            btnCancelOp.setVisibility(android.view.View.GONE);
            currentSession = null;
            if (ReturnCode.isSuccess(s.getReturnCode())) {
                tvStatus.setText("تم: " + label);
                tvStatus.setTextColor(0xFF4CAF50);
                String lastOut = findLastOutputPath();
                if (lastOut != null) notifyGallery(lastOut);
            } else {
                tvStatus.setText("فشل: " + label);
                tvStatus.setTextColor(0xFFFF0000);
                String log = s.getAllLogsAsString();
                if (log == null) log = "لا تفاصيل";
                if (log.length() > 500) log = log.substring(log.length() - 500);
                new AlertDialog.Builder(this, R.style.PurpleDialog)
                    .setTitle("خطأ في FFmpeg")
                    .setMessage(log)
                    .setPositiveButton("حسناً", null)
                    .show();
            }
        }), null, statistics -> {
            if (statistics != null && finalTotalMs > 0) {
                int progress = (int) ((statistics.getTime() / (float) finalTotalMs) * 100);
                if (progress >= 0 && progress <= 100) {
                    runOnUiThread(() -> {
                        progressBar.setIndeterminate(false);
                        progressBar.setVisibility(android.view.View.VISIBLE);
                        progressBar.setProgress(progress);
                        tvStatus.setText("جاري " + label + "... " + progress + "%");
                    });
                }
            }
        });
    }

    private void pickSwapAudioVideo() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        showPurpleMessageDialog(tr("تبديل الصوت"), tr("اختر الفيديو الثاني لأخذ صوته"), tr("متابعة"), () -> {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("video/*");
            startActivityForResult(i, PICK_SWAP_AUDIO);
        });
    }

    private void applySwapAudio() {
        if (videoPath == null || swapAudioPath == null) return;
        String[] audioOpts = {tr("استبدال"), tr("دمج")};
        showPurpleDialog(tr("الطريقة"), audioOpts, which -> {
            ProjectState.swapAudioPath = swapAudioPath;
            ProjectState.swapAudioType = which;
            ProjectState.pendingCount = ProjectState.getPendingCount();
            tvStatus.setText("تم إضافة تبديل الصوت (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
            tvStatus.setTextColor(0xFFFFC107);
            toast("سيتم تبديل الصوت عند التصدير");
        });
    }

    private void showShadowTextDialog() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        showPurpleInputDialog(tr("نص بظل"), tr("النص"), tr("التالي"), text -> {
            if (text.isEmpty()) return;
            try {
                File outFile = new File(getCacheDir(), "shadow_text.png");
                createShadowTextImage(text, outFile);
                shadowTextPath = outFile.getAbsolutePath();
                Intent intent = new Intent(this, WatermarkPreviewActivity.class);
                intent.putExtra(WatermarkPreviewActivity.EXTRA_VIDEO_PATH, videoPath);
                intent.putExtra(WatermarkPreviewActivity.EXTRA_IMAGE_PATH, shadowTextPath);
                startActivityForResult(intent, PICK_SHADOWTEXT_PREVIEW);
            } catch (Exception e) { toast(tr("خطأ: ") + e.getMessage()); }
        });
    }

    private void createShadowTextImage(String text, File outFile) throws Exception {
        String shaped = org.amr.arabic.ArabicUtilities.reshape(text);
        String fontPath = getCacheDir().getAbsolutePath() + "/NotoNaskhArabic-Regular.ttf";
        try {
            InputStream is = getAssets().open("fonts/NotoNaskhArabic-Regular.ttf");
            FileOutputStream fos = new FileOutputStream(fontPath);
            byte[] buf = new byte[8192];
            int len;
            while ((len = is.read(buf)) > 0) fos.write(buf, 0, len);
            is.close(); fos.close();
        } catch (Exception e) {}
        android.graphics.Paint paint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(android.graphics.Typeface.createFromFile(fontPath));
        paint.setTextSize(120);
        paint.setTextAlign(android.graphics.Paint.Align.CENTER);
        float textWidth = paint.measureText(shaped);
        int w = (int)(textWidth + 60);
        int h = 200;
        if (w < 300) w = 300;
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bmp);
        android.graphics.Paint shadowPaint = new android.graphics.Paint(paint);
        shadowPaint.setColor(0xCC000000);
        canvas.drawText(shaped, w/2f + 8, h/2f + 20, shadowPaint);
        android.graphics.Paint borderPaint = new android.graphics.Paint(paint);
        borderPaint.setStyle(android.graphics.Paint.Style.STROKE);
        borderPaint.setStrokeWidth(6);
        borderPaint.setColor(0xFF000000);
        canvas.drawText(shaped, w/2f, h/2f, borderPaint);
        paint.setColor(0xFFFFFFFF);
        canvas.drawText(shaped, w/2f, h/2f, paint);
        FileOutputStream fos = new FileOutputStream(outFile);
        bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.close();
    }

    private void applyShadowTextFinal(int x, int y, int w, int h) {
        if (x < 0) x = 0; if (y < 0) y = 0; if (w < 30) w = 30; if (h < 30) h = 30;
        
        // ننسخ الصورة لاسم فريد حتى لا يتم استبدالها إذا أضفت نصاً آخر
        String uniquePath = getCacheDir() + "/shadowtext_" + System.currentTimeMillis() + ".png";
        try {
            java.nio.file.Files.copy(new java.io.File(shadowTextPath).toPath(), new java.io.File(uniquePath).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            uniquePath = shadowTextPath;
        }
        
        ProjectState.overlays.add(new ProjectState.Overlay(uniquePath, x, y, w, h));
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تم إضافة نص بظل (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم تطبيق نص بظل عند التصدير");
    }

    private void showSplitDialog() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        LinearLayout ll = makeVertical();
        showPurpleInputDialog(tr("تقسيم الفيديو"), tr("عدد المقاطع (2-10)"), tr("تقسيم"), text -> {
            try {
                int count = Integer.parseInt(text);
                if (count < 2 || count > 10) { toast(tr("أدخل رقماً من 2 إلى 10")); return; }
                splitVideo(count);
            } catch (Exception e) { toast(tr("أدخل رقماً")); }
        });
    }

    private void splitVideo(int count) {
        if (videoDurationMs <= 0) { toast(tr("لم نتمكن من قراءة المدة")); return; }
        ProjectState.splitCount = count;
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تم إضافة تقسيم الفيديو (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم التقسيم بعد التصدير");
    }

    private void splitExportedVideo(String filePath, int count) {
        tvStatus.setText("جاري تقسيم الفيديو النهائي...");
        tvStatus.setTextColor(0xFFFFC107);
        progressBar.setVisibility(android.view.View.VISIBLE);
        progressBar.setIndeterminate(false);
        progressBar.setProgress(0);
        
        long dur = 0;
        try {
            android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
            mmr.setDataSource(filePath);
            String d = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (d != null) dur = Long.parseLong(d);
            mmr.release();
        } catch (Exception e) {}

        if (dur <= 0) {
            progressBar.setVisibility(android.view.View.INVISIBLE);
            toast("فشل قراءة مدة الفيديو");
            return;
        }

        final long totalDur = dur;
        final double segDur = (totalDur / 1000.0) / count;
        String ts = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US).format(new java.util.Date());
        
        new Thread(() -> {
            boolean success = true;
            for (int i = 0; i < count; i++) {
                double start = i * segDur;
                double durToCut = (i == count - 1) ? (totalDur / 1000.0 - start) : segDur;
                String outPath = new java.io.File(outputDir, "split_" + ts + "_part_" + (i+1) + ".mp4").getAbsolutePath();
                String cmd = "-y -ss " + start + " -i \"" + filePath + "\" -t " + durToCut + " -c copy \"" + outPath + "\"";
                
                com.arthenica.ffmpegkit.Session session = FFmpegKit.execute(cmd);
                if (!ReturnCode.isSuccess(session.getReturnCode())) {
                    success = false;
                    break;
                }
                final int progress = (int) (((i + 1) / (float) count) * 100);
                runOnUiThread(() -> progressBar.setProgress(progress));
            }
            
            final boolean finalSuccess = success;
            runOnUiThread(() -> {
                progressBar.setVisibility(android.view.View.INVISIBLE);
                if (finalSuccess) {
                    tvStatus.setText("تم التقسيم بنجاح!");
                    tvStatus.setTextColor(0xFF4CAF50);
                    for (int i = 0; i < count; i++) {
                        String partFile = new java.io.File(outputDir, "split_" + ts + "_part_" + (i+1) + ".mp4").getAbsolutePath();
                        notifyGallery(partFile);
                    }
                    toast("تم حفظ " + count + " مقاطع في المعرض!");
                    try { new java.io.File(filePath).delete(); } catch(Exception e) {}
                } else {
                    tvStatus.setText("فشل التقسيم");
                    tvStatus.setTextColor(0xFFFF0000);
                    toast("حدث خطأ أثناء التقسيم");
                }
            });
        }).start();
    }

    private void mergeMusic() {
        if (videoPath == null || musicPath == null) return;
        String[] options = {tr("الموسيقى فقط"), tr("تكرار الموسيقى"), tr("الأطول"), tr("دمج الصوت")};
        showPurpleDialog(tr("طريقة الدمج"), options, which -> {
            String out = newOut("music");
            String[] cmd;
            if (which == 0) cmd = new String[]{"-y", "-i", videoPath, "-i", musicPath, "-c:v", "copy", "-c:a", "aac", "-map", "0:v:0", "-map", "1:a:0", out};
            else if (which == 1) cmd = new String[]{"-y", "-i", videoPath, "-stream_loop", "-1", "-i", musicPath, "-c:v", "copy", "-c:a", "aac", "-shortest", "-map", "0:v:0", "-map", "1:a:0", out};
            else if (which == 2) cmd = new String[]{"-y", "-i", videoPath, "-i", musicPath, "-c:v", "copy", "-c:a", "aac", "-map", "0:v:0", "-map", "1:a:0", out};
            else cmd = new String[]{"-y", "-i", videoPath, "-i", musicPath, "-filter_complex", "[0:a][1:a]amix=inputs=2:duration=first[a]", "-map", "0:v:0", "-map", "[a]", "-c:v", "copy", "-c:a", "aac", "-shortest", out};
            runFFmpeg(cmd, tr("دمج الموسيقى"));
        });
    }
    private void showTextDialog() {
        if (videoPath == null) { toast(tr("\u0627\u062e\u062a\u0631 \u0641\u064a\u062f\u064a\u0648 \u0623\u0648\u0644\u0627\u064b")); return; }
        final String[] fontNames = {tr("خط أميري"), tr("خط النسخ")};
        final String[] fontFiles = {"Amiri-Regular.ttf", "NotoNaskhArabic-Regular.ttf"};
        showPurpleDialog(tr("اختر الخط"), fontNames, fi -> {
            final String selectedFont = fontFiles[fi];
            String[] colorNames = {tr("أبيض"), tr("أسود"), tr("أصفر"), tr("أحمر"), tr("أخضر"), tr("سماوي"), tr("وردي"), tr("برتقالي")};
            final String[] colorVals = {"white", "black", "yellow", "red", "green", "cyan", "magenta", "orange"};
            showPurpleDialog(tr("اختر اللون"), colorNames, ci -> {
                EditText et = new EditText(this);
                et.setHint(tr("اكتب النص هنا"));
                et.setTextColor(0xFFFFFFFF);
                et.setHintTextColor(0xFFFFFFFF);
                LinearLayout ll = makeVertical();
                ll.addView(et);
                AlertDialog dialog = new AlertDialog.Builder(this, R.style.PurpleDialog)
                    .setTitle(tr("إضافة نص"))
                    .setView(ll)
                    .setPositiveButton(tr("التالي"), (d3, w) -> {
                        String text = et.getText().toString().trim();
                        if (text.isEmpty()) return;
                        try {
                            File outFile = new File(getCacheDir(), "text_image.png");
                            createTextImage(text, selectedFont, colorVals[ci], outFile);
                            stickerPath = outFile.getAbsolutePath();
                            Intent intent = new Intent(this, WatermarkPreviewActivity.class);
                            intent.putExtra(WatermarkPreviewActivity.EXTRA_VIDEO_PATH, videoPath);
                            intent.putExtra(WatermarkPreviewActivity.EXTRA_IMAGE_PATH, stickerPath);
                            startActivityForResult(intent, PICK_STICKER_PREVIEW);
                        } catch (Exception e) { toast(tr("خطأ: ") + e.getMessage()); }
                    })
                    .setNegativeButton(tr("إلغاء"), null)
                    .create();
                dialog.setOnShowListener(d -> {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFFFFFFFF);
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFFFFFFFF);
                    int titleId = getResources().getIdentifier("alertTitle", "id", "android");
                    TextView titleTv = dialog.findViewById(titleId);
                    if (titleTv != null) titleTv.setTextColor(0xFFFFFFFF);
                });
                dialog.show();
            });
        });
    }

    private void createTextImage(String text, String fontFile, String color, File outFile) throws Exception {
        String shaped = org.amr.arabic.ArabicUtilities.reshape(text);
        String fontPath = getCacheDir().getAbsolutePath() + "/" + fontFile;
        try {
            InputStream is = getAssets().open("fonts/" + fontFile);
            FileOutputStream fos = new FileOutputStream(fontPath);
            byte[] buf = new byte[8192];
            int len;
            while ((len = is.read(buf)) > 0) fos.write(buf, 0, len);
            is.close(); fos.close();
        } catch (Exception e) { toast(tr("\u0641\u0634\u0644 \u062a\u062d\u0645\u064a\u0644 \u0627\u0644\u062e\u0637")); return; }

        // حساب اللون
        int textColor = 0xFFFFFFFF;
        if (color.equals("black")) textColor = 0xFF000000;
        else if (color.equals("yellow")) textColor = 0xFFFFFF00;
        else if (color.equals("red")) textColor = 0xFFFF0000;
        else if (color.equals("green")) textColor = 0xFF00FF00;
        else if (color.equals("cyan")) textColor = 0xFF00FFFF;
        else if (color.equals("magenta")) textColor = 0xFFFF00FF;
        else if (color.equals("orange")) textColor = 0xFFFFA500;

        android.graphics.Paint paint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(android.graphics.Typeface.createFromFile(fontPath));
        paint.setTextSize(120);
        paint.setTextAlign(android.graphics.Paint.Align.CENTER);
        paint.setColor(textColor);

        String[] lines = shaped.split("\\n");
        float maxWidth = 0;
        for (String line : lines) {
            float w = paint.measureText(line);
            if (w > maxWidth) maxWidth = w;
        }
        int lineHeight = 160;
        int w = (int)(maxWidth + 80);
        int h = lines.length * lineHeight + 80;
        if (w < 300) w = 300;
        if (h < 200) h = 200;

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(bmp);

        // ظل أسود للنص
        android.graphics.Paint shadowPaint = new android.graphics.Paint(paint);
        shadowPaint.setColor(0xCC000000);

        for (int i = 0; i < lines.length; i++) {
            float cy = h/2f - (lines.length - 1) * lineHeight / 2f + i * lineHeight;
            canvas.drawText(lines[i], w/2f + 6, cy + 6, shadowPaint);
            canvas.drawText(lines[i], w/2f, cy, paint);
        }

        FileOutputStream fos = new FileOutputStream(outFile);
        bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.close();
    }


    private void showExtraEffectsDialog() {
        if (videoPath == null) { toast(tr("\u0627\u062e\u062a\u0631 \u0641\u064a\u062f\u064a\u0648 \u0623\u0648\u0644\u0627\u064b")); return; }
        String[] names = {
            tr("Vignette - \u062a\u0638\u0644\u064a\u0644 \u0627\u0644\u062d\u0648\u0627\u0641"),
            tr("\u0634\u062d\u0630 \u0627\u0644\u062a\u0641\u0627\u0635\u064a\u0644"),
            tr("\u0633\u064a\u0646\u0645\u0627\u0626\u064a \u062f\u0627\u0643\u0646"),
            tr("\u062a\u0628\u0627\u064a\u0646 \u0639\u0627\u0644\u064a \u062c\u062f\u0627\u064b"),
            tr("\u062a\u0628\u0627\u064a\u0646 \u0645\u0646\u062e\u0641\u0636"),
            tr("\u0623\u0644\u0648\u0627\u0646 \u062f\u0627\u0641\u0626\u0629"),
            tr("\u0623\u0644\u0648\u0627\u0646 \u0628\u0627\u0631\u062f\u0629"),
            tr("\u0641\u064a\u0644\u0645 \u0642\u062f\u064a\u0645"),
            tr("\u062d\u062f\u064a\u062b \u0648\u0646\u0627\u0635\u0639"),
            tr("\u062a\u0631\u0643\u064a\u0632 \u0623\u062d\u0645\u0631")
        };
        final String[] filters = {
            "vignette=PI/4",
            "unsharp=5:5:1.5",
            "eq=contrast=1.2:saturation=0.9,vignette=PI/5",
            "eq=contrast=2.0:saturation=1.3",
            "eq=contrast=0.7:brightness=0.05",
            "colorbalance=rs=.2:gs=-.05:bs=-.1,eq=saturation=1.3",
            "colorbalance=rs=-.1:gs=0:bs=.2,eq=saturation=1.2",
            "curves=preset=vintage,eq=saturation=0.7:contrast=1.1",
            "eq=contrast=1.3:saturation=1.2:brightness=0.02",
            "colorchannelmixer=1.3:0:0:0:0:0.7:0:0:0:0:0.7"
        };
        showPurpleDialog(tr("\u0627\u062e\u062a\u0631 \u0627\u0644\u062a\u0623\u062b\u064a\u0631"), names, which -> {
                ProjectState.filters.add(filters[which]);
                ProjectState.pendingCount = ProjectState.getPendingCount();
                tvStatus.setText("تم إضافة التأثير (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                tvStatus.setTextColor(0xFFFFC107);
                toast("سيتم تطبيق التأثير عند التصدير");
            });
    }

    private void showVisualEffectsDialog() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        String[] names = {
            tr("خلفية ضبابية (تغبيش)"),
            tr("تلاشي في البداية"),
            tr("تلاشي في النهاية"),
            tr("زيادة السطوع (قوي)"),
            tr("زيادة التباين (قوي)"),
            tr("زيادة التشبع (قوي)")
        };
        showPurpleDialog(tr("اختر التأثير"), names, which -> {
            String filter = "";
            if (which == 0) filter = "boxblur=10:2";
            else if (which == 1) filter = "fade=t=in:st=0:d=2";
            else if (which == 2) {
                float dur = videoDurationMs / 1000f;
                float st = dur > 2 ? dur - 2 : 0;
                filter = "fade=t=out:st=" + st + ":d=2";
            }
            else if (which == 3) filter = "eq=brightness=0.3";
            else if (which == 4) filter = "eq=contrast=1.5";
            else if (which == 5) filter = "eq=saturation=2.0";

            if (!filter.isEmpty()) {
                ProjectState.filters.add(filter);
                ProjectState.pendingCount = ProjectState.getPendingCount();
                tvStatus.setText("تم إضافة التأثير (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                tvStatus.setTextColor(0xFFFFC107);
                toast("سيتم تطبيق التأثير عند التصدير");
            }
        });
    }













    private void showPresetSizes() {
        String[] names = {
            tr("16:9 - \u064a\u0648\u062a\u064a\u0648\u0628 (1920x1080)"),
            tr("9:16 - \u062a\u064a\u0643 \u062a\u0648\u0643 (1080x1920)"),
            tr("1:1 - \u0627\u0646\u0633\u062a\u063a\u0631\u0627\u0645 (1080x1080)"),
            tr("4:3 - \u0642\u062f\u064a\u0645 (1440x1080)"),
            tr("3:2 - \u0643\u0627\u0645\u064a\u0631\u0627 (1620x1080)"),
            tr("4:5 - \u0641\u064a\u0633\u0628\u0648\u0643 (1080x1350)")
        };
        final String[] sizes = {"1920:1080", "1080:1920", "1080:1080", "1440:1080", "1620:1080", "1080:1350"};
        showPurpleDialog(tr("\u0627\u062e\u062a\u0631 \u0627\u0644\u0623\u0628\u0639\u0627\u062f"), names, which -> {
                String[] wh = sizes[which].split(":");
                int W = Integer.parseInt(wh[0]);
                int H = Integer.parseInt(wh[1]);
                applyResize(W, H, "pad");
            });
    }

    private void showCustomSizeDialog() {
        LinearLayout ll = makeVertical();
        final EditText etW = new EditText(this);
        etW.setHint(tr("\u0627\u0644\u0639\u0631\u0636 \u0628\u0627\u0644\u0628\u0643\u0633\u0644 (\u0645\u062b\u0627\u0644: 1280)"));
        etW.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        final EditText etH = new EditText(this);
        etH.setHint(tr("\u0627\u0644\u0637\u0648\u0644 \u0628\u0627\u0644\u0628\u0643\u0633\u0644 (\u0645\u062b\u0627\u0644: 720)"));
        etH.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        ll.addView(etW); ll.addView(etH);
        showWhite(new AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(tr("\u0623\u0628\u0639\u0627\u062f \u0645\u062e\u0635\u0635\u0629"))
            .setView(ll)
            .setPositiveButton(tr("\u062a\u0637\u0628\u064a\u0642"), (d, w) -> {
                try {
                    int W = Integer.parseInt(etW.getText().toString().trim());
                    int H = Integer.parseInt(etH.getText().toString().trim());
                    if (W < 100 || H < 100) { toast(tr("\u0627\u0644\u062d\u062f \u0627\u0644\u0623\u062f\u0646\u0649 100")); return; }
                    if (W > 4000 || H > 4000) { toast(tr("\u0627\u0644\u062d\u062f \u0627\u0644\u0623\u0642\u0635\u0649 4000")); return; }
                    if (W % 2 != 0) W--;
                    if (H % 2 != 0) H--;
                    applyResize(W, H, "stretch");
                } catch (Exception e) { toast(tr("\u0623\u062f\u062e\u0644 \u0623\u0631\u0642\u0627\u0645\u0627\u064b")); }
            })
            .setNegativeButton(tr("\u0625\u0644\u063a\u0627\u0621"), null)
            .create());
    }

    private void showCropSidesDialog() {
        LinearLayout ll = makeVertical();
        final EditText etTop = new EditText(this);
        etTop.setHint(tr("\u0642\u0635 \u0645\u0646 \u0627\u0644\u0623\u0639\u0644\u0649 (\u0628\u0643\u0633\u0644)"));
        etTop.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etTop.setText("0");
        final EditText etBottom = new EditText(this);
        etBottom.setHint(tr("\u0642\u0635 \u0645\u0646 \u0627\u0644\u0623\u0633\u0641\u0644 (\u0628\u0643\u0633\u0644)"));
        etBottom.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etBottom.setText("0");
        final EditText etLeft = new EditText(this);
        etLeft.setHint(tr("\u0642\u0635 \u0645\u0646 \u0627\u0644\u064a\u0633\u0627\u0631 (\u0628\u0643\u0633\u0644)"));
        etLeft.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etLeft.setText("0");
        final EditText etRight = new EditText(this);
        etRight.setHint(tr("\u0642\u0635 \u0645\u0646 \u0627\u0644\u064a\u0645\u064a\u0646 (\u0628\u0643\u0633\u0644)"));
        etRight.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etRight.setText("0");
        ll.addView(etTop); ll.addView(etBottom); ll.addView(etLeft); ll.addView(etRight);
        showWhite(new AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(tr("\u0627\u0644\u0642\u0635 \u0645\u0646 \u0627\u0644\u062c\u0648\u0627\u0646\u0628"))
            .setView(ll)
            .setPositiveButton(tr("\u062a\u0637\u0628\u064a\u0642"), (d, w) -> {
                try {
                    int t = Integer.parseInt(etTop.getText().toString().trim());
                    int b = Integer.parseInt(etBottom.getText().toString().trim());
                    int l = Integer.parseInt(etLeft.getText().toString().trim());
                    int r = Integer.parseInt(etRight.getText().toString().trim());
                    applyCropSides(t, b, l, r);
                } catch (Exception e) { toast(tr("\u0623\u062f\u062e\u0644 \u0623\u0631\u0642\u0627\u0645\u0627\u064b")); }
            })
            .setNegativeButton(tr("\u0625\u0644\u063a\u0627\u0621"), null)
            .create());
    }

    private void applyResize(final int W, final int H, final String mode) {
        ProjectState.manualW = W;
        ProjectState.manualH = H;
        ProjectState.manualMode = mode;
        ProjectState.hasManualResize = true;
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تم تعديل الأبعاد (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم تطبيق الأبعاد عند التصدير");
    }

        private void applyCropSides(int top, int bottom, int left, int right) {
        int vw = 1080, vh = 1920;
        try {
            android.media.MediaMetadataRetriever r = new android.media.MediaMetadataRetriever();
            r.setDataSource(videoPath);
            String sw = r.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String sh = r.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            if (sw != null) vw = Integer.parseInt(sw);
            if (sh != null) vh = Integer.parseInt(sh);
            r.release();
        } catch (Exception e) {}
        int newW = vw - left - right;
        int newH = vh - top - bottom;
        if (newW % 2 != 0) newW--;
        if (newH % 2 != 0) newH--;
        ProjectState.cropFilter = "crop=" + newW + ":" + newH + ":" + left + ":" + top;
        ProjectState.pendingCount++;
        tvStatus.setText("تم تحديد القص (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
    }

    private void showManualResizeDialog() {
        if (videoPath == null) { toast(tr("\u0627\u062e\u062a\u0631 \u0641\u064a\u062f\u064a\u0648 \u0623\u0648\u0644\u0627\u064b")); return; }
        String[] options = {
            tr("\u0642\u0635 \u062a\u0641\u0627\u0639\u0644\u064a (\u0627\u0633\u062d\u0628 \u0627\u0644\u0632\u0648\u0627\u064a\u0627) \u2b50"),
            tr("\u0623\u0628\u0639\u0627\u062f \u062c\u0627\u0647\u0632\u0629 (16:9, 9:16, 1:1)"),
            tr("\u0623\u0628\u0639\u0627\u062f \u0645\u062e\u0635\u0635\u0629 (\u0623\u062f\u062e\u0644 \u0627\u0644\u0631\u0642\u0645)")
        };
        showPurpleDialog(tr("\u062a\u0639\u062f\u064a\u0644 \u0627\u0644\u0623\u0628\u0639\u0627\u062f"), options, which -> {
                if (which == 0) {
                    Intent intent = new Intent(this, CropActivity.class);
                    intent.putExtra(CropActivity.EXTRA_VIDEO_PATH, videoPath);
                    startActivityForResult(intent, PICK_CROP_INTERACTIVE);
                } else if (which == 1) showPresetSizes();
                else showCustomSizeDialog();
            });
    }

        private void applyCropInteractive(int x, int y, int w, int h) {
        ProjectState.cropFilter = "crop=" + w + ":" + h + ":" + x + ":" + y;
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تم تحديد القص (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم تطبيق القص عند التصدير");
    }

    private void askForSlideshowMusic() {
        showWhite(new AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(tr("\u0647\u0644 \u062a\u0631\u064a\u062f \u0625\u0636\u0627\u0641\u0629 \u0645\u0648\u0633\u064a\u0642\u0649\u061f"))
            .setPositiveButton(tr("\u0646\u0639\u0645"), (d, w) -> {
                Intent m = new Intent(Intent.ACTION_GET_CONTENT);
                m.setType("audio/*");
                startActivityForResult(m, 2001);
            })
            .setNegativeButton(tr("\u0644\u0627"), (d, w) -> createSlideshow(null))
            .create());
    }

    private void showGifDialog() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        String[] names = {tr("جودة عالية (480، 10 إطار)"), tr("جودة متوسطة (320، 8 إطار)"), tr("حجم صغير (240، 6 إطار)")};
        showPurpleDialog(tr("اختر جودة GIF"), names, which -> {
            ProjectState.toGif = true;
            ProjectState.gifPreset = which;
            ProjectState.pendingCount = ProjectState.getPendingCount();
            tvStatus.setText("تم إضافة تحويل GIF (" + ProjectState.pendingCount + ")");
            tvStatus.setTextColor(0xFFFFC107);
            toast("سيتم التحويل إلى GIF عند التصدير");
        });
    }

    private void convertVideoToGif(String mp4Path, int preset) {
        String[] vals = {"480:10", "320:8", "240:6"};
        String[] parts = vals[preset].split(":");
        int width = Integer.parseInt(parts[0]);
        int fps = Integer.parseInt(parts[1]);
        String vf = "fps=" + fps + ",scale=" + width + ":-1:flags=lanczos";
        final String tempOut = new java.io.File(getCacheDir(), "temp_gif_" + System.currentTimeMillis() + ".gif").getAbsolutePath();
        
        String cmd = "-y -t 5 -i \"" + mp4Path + "\" -an -vf \"" + vf + "\" -c:v gif -pix_fmt rgb8 -loop 0 \"" + tempOut + "\"";
        
        tvStatus.setText("جاري تحويل الفيديو إلى GIF...");
        progressBar.setVisibility(android.view.View.VISIBLE);
        progressBar.setIndeterminate(true);
        
        FFmpegKit.executeAsync(cmd, session -> runOnUiThread(() -> {
            progressBar.setVisibility(android.view.View.INVISIBLE);
            progressBar.setIndeterminate(false);
            if (ReturnCode.isSuccess(session.getReturnCode())) {
                saveGifToGallery(tempOut);
                try { new java.io.File(mp4Path).delete(); } catch(Exception e) {}
                try { new java.io.File(tempOut).delete(); } catch(Exception e) {}
            } else {
                toast("فشل إنشاء GIF");
            }
        }));
    }

    private void saveGifToGallery(String cacheGifPath) {
        try {
            java.io.File file = new java.io.File(cacheGifPath);
            String ts = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US).format(new java.util.Date());
            android.content.ContentValues values = new android.content.ContentValues();
            values.put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "video_gif_" + ts + ".gif");
            values.put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/gif");
            values.put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/VideoEditorPro");
            values.put(android.provider.MediaStore.Images.Media.IS_PENDING, 1);
            android.net.Uri item = getContentResolver().insert(android.provider.MediaStore.Images.Media.getContentUri(android.provider.MediaStore.VOLUME_EXTERNAL_PRIMARY), values);
            if (item != null) {
                java.io.OutputStream os = getContentResolver().openOutputStream(item);
                java.io.FileInputStream is = new java.io.FileInputStream(file);
                byte[] buf = new byte[8192]; int len;
                while ((len = is.read(buf)) > 0) { os.write(buf, 0, len); }
                is.close(); os.close();
                values.clear(); values.put(android.provider.MediaStore.Images.Media.IS_PENDING, 0);
                getContentResolver().update(item, values, null, null);
                tvStatus.setText("تم إنشاء GIF بنجاح!");
                tvStatus.setTextColor(0xFF4CAF50);
                toast("تم حفظ GIF في المعرض بنجاح!");
            }
        } catch (Exception e) { toast("فشل حفظ GIF: " + e.getMessage()); }
    }


    private void extractScreenshots(int count) {
        long dur = videoDurationMs;
        if (dur <= 0) { toast(tr("لم نتمكن من قراءة المدة")); return; }
        int savedCount = 0;
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        
        for (int i = 0; i < count; i++) {
            long timeMs = (long)((dur / (double)(count + 1)) * (i + 1));
            try {
                android.media.MediaMetadataRetriever r = new android.media.MediaMetadataRetriever();
                r.setDataSource(videoPath);
                Bitmap bmp = r.getFrameAtTime(timeMs * 1000, android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                r.release();
                if (bmp == null) continue;
                
                String fname = "shot_" + ts + "_" + (i + 1) + ".png";
                
                android.content.ContentValues values = new android.content.ContentValues();
                values.put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, fname);
                values.put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/VideoEditorPro");
                values.put(android.provider.MediaStore.Images.Media.IS_PENDING, 1);

                android.net.Uri collection = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                android.net.Uri itemUri = getContentResolver().insert(collection, values);
                
                if (itemUri != null) {
                    java.io.OutputStream out = getContentResolver().openOutputStream(itemUri);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                    values.clear();
                    values.put(android.provider.MediaStore.Images.Media.IS_PENDING, 0);
                    getContentResolver().update(itemUri, values, null, null);
                    savedCount++;
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
        
        if (savedCount > 0) {
            tvStatus.setText(tr("تم حفظ ") + savedCount + tr(" لقطة"));
            tvStatus.setTextColor(0xFF4CAF50);
            toast(tr("تم حفظ ") + savedCount + tr(" لقطة"));
        } else {
            toast(tr("فشل الاستخراج"));
        }
    }

    private void captureScreenshot() {
        if (videoPath == null) { toast(tr("\u0627\u062e\u062a\u0631 \u0641\u064a\u062f\u064a\u0648 \u0623\u0648\u0644\u0627\u064b")); return; }
        String[] options = {
            tr("\u0644\u0642\u0637\u0629 \u0648\u0627\u062d\u062f\u0629 (\u062a\u062d\u062f\u064a\u062f \u0627\u0644\u0648\u0642\u062a)"),
            tr("\u0639\u062f\u0629 \u0644\u0642\u0637\u0627\u062a (\u0645\u062a\u0648\u0632\u0639\u0629 \u062a\u0644\u0642\u0627\u0626\u064a\u0627\u064b)")
        };
        showPurpleDialog(tr("\u0644\u0642\u0637\u0627\u062a \u0645\u0646 \u0627\u0644\u0641\u064a\u062f\u064a\u0648"), options, which -> {
                if (which == 0) showSingleScreenshotDialog();
                else showMultiScreenshotCount();
            });
    }

    private void showSingleScreenshotDialog() {
        EditText et = new EditText(this);
        et.setHint(tr("\u0627\u0644\u0648\u0642\u062a \u0628\u0627\u0644\u062b\u0648\u0627\u0646\u064a (\u0645\u062b\u0627\u0644: 5)"));
        et.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout ll = makeVertical();
        ll.addView(et);
        showWhite(new AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(tr("\u0644\u0642\u0637\u0629 \u0648\u0627\u062d\u062f\u0629"))
            .setView(ll)
            .setPositiveButton(tr("\u062d\u0641\u0638"), (d, w) -> {
                String s = et.getText().toString().trim();
                if (!s.isEmpty()) saveScreenshot(s);
            })
            .setNegativeButton(tr("\u0625\u0644\u063a\u0627\u0621"), null)
            .create());
    }

    private void showMultiScreenshotCount() {
        EditText et = new EditText(this);
        et.setHint(tr("\u0639\u062f\u062f \u0627\u0644\u0644\u0642\u0637\u0627\u062a (2-20)"));
        et.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        et.setText("5");
        LinearLayout ll = makeVertical();
        ll.addView(et);
        showWhite(new AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(tr("\u0639\u062f\u062f \u0627\u0644\u0644\u0642\u0637\u0627\u062a"))
            .setView(ll)
            .setPositiveButton(tr("\u0627\u0633\u062a\u062e\u0631\u0627\u062c"), (d, w) -> {
                try {
                    int count = Integer.parseInt(et.getText().toString().trim());
                    if (count < 2 || count > 20) { toast(tr("\u0623\u062f\u062e\u0644 2-20")); return; }
                    extractScreenshots(count);
                } catch (Exception e) { toast(tr("\u0623\u062f\u062e\u0644 \u0631\u0642\u0645\u0627\u064b")); }
            })
            .setNegativeButton(tr("\u0625\u0644\u063a\u0627\u0621"), null)
            .create());
    }



    private void showWatermarkDialog() {
        if (videoPath == null) { toast(tr("\u0627\u062e\u062a\u0631 \u0641\u064a\u062f\u064a\u0648 \u0623\u0648\u0644\u0627\u064b")); return; }
        String[] options = {
            tr("\u0627\u0644\u0634\u0639\u0627\u0631 \u0627\u0644\u062c\u0627\u0647\u0632 \u2b50 (\u0623\u0646\u0648\u0627\u0631 \u0627\u0644\u063a\u0632\u0627\u0644\u064a)"),
            tr("\u0635\u0648\u0631\u0629 \u0645\u0646 \u0627\u0644\u0645\u0639\u0631\u0636")
        };
        showPurpleDialog(tr("العلامة المائية"), options, which -> {
            if (which == 0) showLogosGrid();
            else {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, PICK_IMAGE);
            }
        });
    }

    private void showLogosGrid() {
        final String[] logoFiles = {"logo_white.png", "logo_black.png", "logo_black_gold.png", "logo_gold.png", "logo_red.png", "logo_blue.png"};
        final android.widget.GridView grid = new android.widget.GridView(this);
        grid.setNumColumns(2);
        grid.setBackgroundColor(0xFF1A1A1A);
        grid.setPadding(15, 15, 15, 15);
        grid.setVerticalSpacing(15);
        grid.setHorizontalSpacing(15);
        grid.setAdapter(new android.widget.BaseAdapter() {
            public int getCount() { return logoFiles.length; }
            public Object getItem(int i) { return logoFiles[i]; }
            public long getItemId(int i) { return i; }
            public android.view.View getView(int pos, android.view.View view, android.view.ViewGroup parent) {
                android.widget.ImageView iv;
                if (view == null) {
                    iv = new android.widget.ImageView(MainActivity.this);
                    int sz = (int)(getResources().getDisplayMetrics().density * 140);
                    iv.setLayoutParams(new android.widget.AbsListView.LayoutParams(sz, sz));
                    iv.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
                    iv.setPadding(10, 10, 10, 10);
                    iv.setBackgroundColor(0xFF2A2A2A);
                } else { iv = (android.widget.ImageView) view; }
                try {
                    java.io.InputStream is = getAssets().open("logos/" + logoFiles[pos]);
                    android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeStream(is);
                    iv.setImageBitmap(bmp);
                    is.close();
                } catch (Exception e) {}
                return iv;
            }
        });
        grid.setOnItemClickListener((parent, view, position, id) -> openLogoPreview(logoFiles[position]));
        showWhite(new AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(tr("\u0627\u062e\u062a\u0631 \u0627\u0644\u0634\u0639\u0627\u0631"))
            .setView(grid)
            .setNegativeButton(tr("\u0625\u0644\u063a\u0627\u0621"), null)
            .create());
    }

    private void openLogoPreview(String logoFile) {
        try {
            java.io.File cacheFile = new java.io.File(getCacheDir(), "preview_logo.png");
            java.io.InputStream is = getAssets().open("logos/" + logoFile);
            java.io.FileOutputStream fos = new java.io.FileOutputStream(cacheFile);
            byte[] buf = new byte[8192];
            int len;
            while ((len = is.read(buf)) > 0) fos.write(buf, 0, len);
            is.close(); fos.close();
            stickerPath = cacheFile.getAbsolutePath();
            Intent intent = new Intent(this, WatermarkPreviewActivity.class);
            intent.putExtra(WatermarkPreviewActivity.EXTRA_VIDEO_PATH, videoPath);
            intent.putExtra(WatermarkPreviewActivity.EXTRA_IMAGE_PATH, stickerPath);
            startActivityForResult(intent, PICK_STICKER_PREVIEW);
        } catch (Exception e) { toast(tr("\u062e\u0637\u0623: ") + e.getMessage()); }
    }

    private void showCollageDialog() {
        String[] options = {
            tr("\u0641\u064a\u062f\u064a\u0648\u064a\u0646 (\u0641\u0648\u0642 \u0648\u062a\u062d\u062a)"),
            tr("\u0641\u064a\u062f\u064a\u0648\u064a\u0646 (\u064a\u0645\u064a\u0646 \u0648\u064a\u0633\u0627\u0631)"),
            tr("4 \u0641\u064a\u062f\u064a\u0648\u0647\u0627\u062a (\u0634\u0628\u0643\u0629 2x2)")
        };
        showPurpleDialog(tr("\u0627\u062e\u062a\u0631 \u0646\u0648\u0639 \u0627\u0644\u0643\u0648\u0644\u0627\u062c"), options, which -> {
                if (which == 0) {
                    Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("video/*");
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(i, PICK_COLLAGE_VIDEOS);
                    collageMode = "vertical";
                } else if (which == 1) {
                    Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("video/*");
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(i, PICK_COLLAGE_VIDEOS);
                    collageMode = "horizontal";
                } else {
                    Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("video/*");
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(i, PICK_COLLAGE_VIDEOS);
                    collageMode = "grid";
                }
            });
    }




    private void applyCollage() {
        if (multiVideoPaths.isEmpty()) { return; }
        if (videoPath == null) { videoPath = multiVideoPaths.get(0); multiVideoPaths.remove(0); }
        // نخزن عدد الفيديوهات المتاحة
        int totalVideos = 1 + multiVideoPaths.size();
        String[] audioOpts;
        if (totalVideos == 2) {
            audioOpts = new String[]{
                tr("\u0635\u0648\u062a \u0627\u0644\u0641\u064a\u062f\u064a\u0648 1"),
                tr("\u0635\u0648\u062a \u0627\u0644\u0641\u064a\u062f\u064a\u0648 2"),
                tr("\u062f\u0645\u062c \u0627\u0644\u0635\u0648\u062a\u064a\u0646"),
                tr("\u0628\u062f\u0648\u0646 \u0635\u0648\u062a")
            };
        } else {
            audioOpts = new String[]{
                tr("\u0635\u0648\u062a \u0627\u0644\u0641\u064a\u062f\u064a\u0648 1"),
                tr("\u0635\u0648\u062a \u0627\u0644\u0641\u064a\u062f\u064a\u0648 2"),
                tr("\u0635\u0648\u062a \u0627\u0644\u0641\u064a\u062f\u064a\u0648 3"),
                tr("\u0635\u0648\u062a \u0627\u0644\u0641\u064a\u062f\u064a\u0648 4"),
                tr("\u062f\u0645\u062c \u0643\u0644 \u0627\u0644\u0623\u0635\u0648\u0627\u062a"),
                tr("\u0628\u062f\u0648\u0646 \u0635\u0648\u062a")
            };
        }
        showPurpleDialog(tr("\u0627\u062e\u062a\u0631 \u0627\u0644\u0635\u0648\u062a"), audioOpts, which -> {
                buildCollageCmd(which, totalVideos);
            });
    }

    private void buildCollageCmd(int audioChoice, int totalVideos) {
        ProjectState.isCollage = true;
        ProjectState.collageMode = collageMode;
        ProjectState.collagePaths = new ArrayList<>(multiVideoPaths);
        ProjectState.collageAudioChoice = audioChoice;
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تم إضافة الكولاج (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم تطبيق الكولاج عند التصدير");
    }


















    private void showColorTintDialog() {
        if (videoPath == null) { toast(tr("\u0627\u062e\u062a\u0627\u0631 \u0641\u064a\u062f\u064a\u0648 \u0623\u0648\u0644\u0627\u064b")); return; }
        String[] names = {
            tr("\u0623\u062d\u0645\u0631 (Red Tint)"),
            tr("\u0623\u062e\u0636\u0631 (Green Tint)"),
            tr("\u0623\u0632\u0631\u0642 (Blue Tint)"),
            tr("\u0628\u0646\u0641\u0633\u062c\u064a (Purple Tint)"),
            tr("\u0628\u0631\u062a\u0642\u0627\u0644\u064a (Orange Tint)"),
            tr("\u0648\u0631\u062f\u064a (Pink Tint)"),
            tr("\u0630\u0647\u0628\u064a (Gold Tint)"),
            tr("\u0633\u064a\u0628\u064a\u0627 (Sepia)"),
            tr("\u0623\u0628\u064a\u0636 \u0648\u0623\u0633\u0648\u062f (B&W)")
        };
        final String[] filters = {
            "colorchannelmixer=1.5:0.2:0.1:0.2:0.7:0.1:0.1:0.2:0.7",
            "colorchannelmixer=0.7:0.2:0.1:0.2:1.5:0.1:0.1:0.2:0.7",
            "colorchannelmixer=0.7:0.2:0.1:0.2:0.7:0.1:0.2:0.3:1.5",
            "colorchannelmixer=1.2:0.2:0.3:0.2:0.6:0.2:0.4:0.3:1.3",
            "colorchannelmixer=1.6:0.4:0.1:0.3:0.9:0.2:0.1:0.2:0.5",
            "colorchannelmixer=1.4:0.3:0.4:0.3:0.7:0.3:0.4:0.3:0.9",
            "colorchannelmixer=1.5:0.3:0.1:0.3:1.2:0.1:0.1:0.3:0.7",
            "colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131",
            "hue=s=0"
        };
        showPurpleDialog(tr("\u0627\u062e\u062a\u0631 \u0627\u0644\u0644\u0648\u0646"), names, which -> applyColorTint(filters[which], names[which]));
    }

    private void applyColorTint(String filter, String label) {
        ProjectState.filters.add(filter);
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تم إضافة " + label + " (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم تطبيق التلوين عند التصدير");
    }


    private void compressVideo() {
        if (videoPath == null) { toast(tr("اختر فيديو أولاً")); return; }
        ProjectState.compressVideo = true;
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تم إضافة الضغط (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم ضغط الفيديو عند التصدير");
    }

    private void showAudioFadeDialog() {
        if (videoPath == null) { toast(tr("\u0627\u062e\u062a\u0627\u0631 \u0641\u064a\u062f\u064a\u0648 \u0623\u0648\u0644\u0627\u064b")); return; }
        String[] names = {
            tr("\u062a\u0644\u0627\u0634\u064a \u062e\u0641\u064a\u0641 (1 \u062b\u0627\u0646\u064a\u0629)"),
            tr("\u062a\u0644\u0627\u0634\u064a \u0645\u062a\u0648\u0633\u0637 (2 \u062b\u0627\u0646\u064a\u062a\u064a\u0646)"),
            tr("\u062a\u0644\u0627\u0634\u064a \u0642\u0648\u064a (3 \u062b\u0648\u0627\u0646\u064a)"),
            tr("\u062a\u0644\u0627\u0634\u064a \u062f\u0627\u062e\u0644 \u0641\u0642\u0637 (\u0627\u0644\u0628\u062f\u0627\u064a\u0629)"),
            tr("\u062a\u0644\u0627\u0634\u064a \u062e\u0627\u0631\u062c \u0641\u0642\u0637 (\u0627\u0644\u0646\u0647\u0627\u064a\u0629)")
        };
        final float[] durations = {1.0f, 2.0f, 3.0f, 2.0f, 2.0f};
        final String[] types = {"both", "both", "both", "in", "out"};
        showPurpleDialog(tr("\u0627\u062e\u062a\u0631 \u0645\u062f\u0629 \u0627\u0644\u062a\u0644\u0627\u0634\u064a"), names, which -> applyAudioFade(durations[which], types[which]));
    }

    private void applyAudioFade(float duration, String type) {
        double totalDur = videoDurationMs / 1000.0;
        if (totalDur < duration * 2) {
            duration = (float)(totalDur / 3.0);
            if (duration < 0.5f) duration = 0.5f;
        }
        String durStr = String.format(java.util.Locale.US, "%.2f", duration);
        double fadeOutStart = totalDur - duration;
        if (fadeOutStart < 0) fadeOutStart = 0;
        String startOutStr = String.format(java.util.Locale.US, "%.2f", fadeOutStart);

        String af;
        if (type.equals("in")) {
            af = "afade=t=in:st=0:d=" + durStr;
        } else if (type.equals("out")) {
            af = "afade=t=out:st=" + startOutStr + ":d=" + durStr;
        } else {
            af = "afade=t=in:st=0:d=" + durStr + ",afade=t=out:st=" + startOutStr + ":d=" + durStr;
        }

        ProjectState.audioFilter = af;
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تم إضافة تلاشي الصوت (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم تطبيق تلاشي الصوت عند التصدير");
    }

    private void showAudioDuckDialog() {
        if (videoPath == null) { toast(tr("\u0627\u062e\u062a\u0627\u0631 \u0641\u064a\u062f\u064a\u0648 \u0623\u0648\u0644\u0627\u064b")); return; }
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(tr("\u062e\u0641\u0636 \u0627\u0644\u0645\u0648\u0633\u064a\u0642\u0649 \u062a\u0644\u0642\u0627\u0626\u064a\u0627\u064b"))
            .setMessage(tr("\u0633\u064a\u062a\u0645 \u062e\u0641\u0636 \u0627\u0644\u0645\u0648\u0633\u064a\u0642\u0649 \u062a\u0644\u0642\u0627\u0626\u064a\u0627\u064b \u0639\u0646\u062f\u0645\u0627 \u064a\u062a\u062d\u062f\u062b \u0627\u0644\u0634\u062e\u0635 \u0641\u064a \u0627\u0644\u0641\u064a\u062f\u064a\u0648.\n\n\u0627\u062e\u062a\u0631 \u0645\u0644\u0641 \u0645\u0648\u0633\u064a\u0642\u0649 \u0627\u0644\u062e\u0644\u0641\u064a\u0629:"))
            .setPositiveButton(tr("\u0627\u062e\u062a\u064a\u0627\u0631 \u0645\u0648\u0633\u064a\u0642\u0649"), (d, w) -> {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("audio/*");
                startActivityForResult(i, PICK_DUCK_MUSIC);
            })
            .setNegativeButton(tr("\u0625\u0644\u063a\u0627\u0621"), null)
            .create();
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFFFFFFFF);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFFFFFFFF);
            int titleId = getResources().getIdentifier("alertTitle", "id", "android");
            android.widget.TextView titleTv = dialog.findViewById(titleId);
            if (titleTv != null) titleTv.setTextColor(0xFFFFFFFF);
            android.widget.TextView msgTv = dialog.findViewById(android.R.id.message);
            if (msgTv != null) msgTv.setTextColor(0xFFFFFFFF);
        });
        dialog.show();
    }

    private void applyAudioDuck(String musicFile) {
        ProjectState.duckMusicPath = musicFile;
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تم إضافة خفض الموسيقى (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم خفض الموسيقى عند التصدير");
    }

    private void showSpeedRampDialog() {
        if (videoPath == null) { toast(tr("\u0627\u062e\u062a\u0627\u0631 \u0641\u064a\u062f\u064a\u0648 \u0623\u0648\u0644\u0627\u064b")); return; }
        String[] names = {
            tr("\u0628\u0637\u064a\u0621 \u2192 \u0633\u0631\u064a\u0639 (Slow \u2192 Fast)"),
            tr("\u0633\u0631\u064a\u0639 \u2192 \u0628\u0637\u064a\u0621 (Fast \u2192 Slow)"),
            tr("\u0628\u0637\u064a\u0621 \u2192 \u0633\u0631\u064a\u0639 \u2192 \u0628\u0637\u064a\u0621 (\u0627\u0644\u0623\u0634\u0647\u0631)"),
            tr("\u0633\u0631\u064a\u0639 \u2192 \u0628\u0637\u064a\u0621 \u2192 \u0633\u0631\u064a\u0639 (\u0646\u0628\u0636)"),
            tr("\u0628\u0637\u064a\u0621 \u062c\u062f\u0627\u064b (0.25x \u0637\u0648\u0627\u0644 \u0627\u0644\u0641\u064a\u062f\u064a\u0648)"),
            tr("\u0633\u0631\u064a\u0639 \u062c\u062f\u0627\u064b (4x \u0637\u0648\u0627\u0644 \u0627\u0644\u0641\u064a\u062f\u064a\u0648)")
        };
        final String[] types = {"slow_fast", "fast_slow", "slow_fast_slow", "fast_slow_fast", "all_slow", "all_fast"};
        showPurpleDialog(tr("\u0627\u062e\u062a\u0631 \u0646\u0648\u0639 \u0627\u0644\u0633\u0631\u0639\u0629"), names, which -> applySpeedRamp(types[which]));
    }




    private void applySpeedRamp(String type) {
        double durSec = videoDurationMs / 1000.0;
        if (durSec < 2) { toast("الفيديو قصير جداً"); return; }
        String durStr = String.format(java.util.Locale.US, "%.3f", durSec);

        if (type.equals("all_slow")) {
            ProjectState.videoSpeed = 0.5f;
        } else if (type.equals("all_fast")) {
            ProjectState.videoSpeed = 2.0f;
        } else {
            String vf;
            if (type.equals("slow_fast")) {
                vf = "setpts='(2-1.5*(T/" + durStr + "))*PTS'";
            } else if (type.equals("fast_slow")) {
                vf = "setpts='(0.5+1.5*(T/" + durStr + "))*PTS'";
            } else if (type.equals("slow_fast_slow")) {
                vf = "setpts='(2-1.5*sin(PI*T/" + durStr + "))*PTS'";
            } else {
                vf = "setpts='(0.5+1.5*sin(PI*T/" + durStr + "))*PTS'";
            }
            ProjectState.filters.add(vf);
        }
        
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تم إضافة سرعة متدرجة (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم تطبيق السرعة المتدرجة عند التصدير");
    }

    private void showAudioBoostDialog() {
        if (videoPath == null) { toast(tr("\u0627\u062e\u062a\u0627\u0631 \u0641\u064a\u062f\u064a\u0648 \u0623\u0648\u0644\u0627\u064b")); return; }
        String[] names = {
            tr("\u062a\u0646\u0642\u064a\u0629 \u062e\u0641\u064a\u0641\u0629 (\u0625\u0632\u0627\u0644\u0629 \u0627\u0644\u0647\u0645\u0647\u0645\u0629)"),
            tr("\u062a\u0646\u0642\u064a\u0629 \u0645\u062a\u0648\u0633\u0637\u0629"),
            tr("\u062a\u0646\u0642\u064a\u0629 \u0642\u0648\u064a\u0629 (\u0636\u062c\u064a\u062c \u0639\u0627\u0644\u064a)"),
            tr("\u062a\u0639\u0632\u064a\u0632 \u0627\u0644\u062c\u0647\u064a\u0631 (Bass Boost)"),
            tr("\u0648\u0636\u0639 \u0627\u0644\u0631\u0627\u062f\u064a\u0648 (\u062a\u0631\u062f\u062f\u0627\u062a \u0645\u062a\u0648\u0633\u0637\u0629)"),
            tr("\u0648\u0636\u0639 \u0627\u0644\u062d\u0645\u0627\u0645 (\u0635\u062f\u0649 \u0646\u0627\u0639\u0645)"),
            tr("\u062a\u0639\u0632\u064a\u0632 \u0627\u0644\u0648\u0636\u0648\u062d (\u0627\u0644\u0623\u0635\u0648\u0627\u062a \u0627\u0644\u0628\u0634\u0631\u064a\u0629)")
        };
        final String[] filters = {
            "afftdn=nf=-25",
            "afftdn=nf=-30",
            "afftdn=nf=-40",
            "bass=g=8:f=100:w=0.5",
            "equalizer=f=1000:t=q:w=1:g=4,equalizer=f=3000:t=q:w=1:g=3",
            "aecho=0.8:0.9:40:0.3",
            "highpass=f=80,lowpass=f=8000,acompressor=threshold=0.1:ratio=3:attack=5:release=50"
        };
        showPurpleDialog(tr("\u0627\u062e\u062a\u0631 \u062a\u0639\u0632\u064a\u0632 \u0627\u0644\u0635\u0648\u062a"), names, which -> {
                ProjectState.audioFilter = filters[which];
                ProjectState.pendingCount = ProjectState.getPendingCount();
                tvStatus.setText("تم إضافة تعزيز الصوت (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                tvStatus.setTextColor(0xFFFFC107);
                toast("سيتم تطبيق تعزيز الصوت عند التصدير");
            });
    }


    private void showRemoveSilenceDialog() {
        if (videoPath == null) { toast(tr("\u0627\u062e\u062a\u0627\u0631 \u0641\u064a\u062f\u064a\u0648 \u0623\u0648\u0644\u0627\u064b")); return; }
        String[] names = {
            tr("\u062d\u0630\u0641 \u0635\u0645\u062a 0.3 \u062b\u0627\u0646\u064a\u0629 (\u062d\u0633\u0627\u0633)"),
            tr("\u062d\u0630\u0641 \u0635\u0645\u062a 0.5 \u062b\u0627\u0646\u064a\u0629"),
            tr("\u062d\u0630\u0641 \u0635\u0645\u062a 1 \u062b\u0627\u0646\u064a\u0629"),
            tr("\u062d\u0630\u0641 \u0635\u0645\u062a 2 \u062b\u0627\u0646\u064a\u0629 (\u0627\u0644\u0637\u0648\u064a\u0644 \u0641\u0642\u0637)")
        };
        final float[] durations = {0.3f, 0.5f, 1.0f, 2.0f};
        showPurpleDialog(tr("\u062d\u0630\u0641 \u0627\u0644\u0635\u0645\u062a - \u0627\u062e\u062a\u0631 \u0627\u0644\u0645\u062f\u0629"), names, which -> detectSilence(durations[which]));
    }



    private void detectSilence(final float minDuration) {
        ProjectState.removeSilence = true;
        ProjectState.silenceDuration = minDuration;
        ProjectState.pendingCount = ProjectState.getPendingCount();
        tvStatus.setText("تم إضافة حذف الصمت (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
        tvStatus.setTextColor(0xFFFFC107);
        toast("سيتم حذف الصمت عند التصدير");
    }

    private void pickCutoutImage() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, PICK_CUTOUT_IMAGE);
    }

    private void applyCutoutImage(String cutoutPath) {
        try {
            java.io.File cacheFile = new java.io.File(getCacheDir(), "cutout_image.png");
            java.io.File src = new java.io.File(cutoutPath);
            byte[] buf = new byte[8192];
            java.io.InputStream is = new java.io.FileInputStream(src);
            java.io.FileOutputStream fos = new java.io.FileOutputStream(cacheFile);
            int len;
            while ((len = is.read(buf)) > 0) fos.write(buf, 0, len);
            is.close(); fos.close();

            stickerPath = cacheFile.getAbsolutePath();
            Intent intent = new Intent(this, WatermarkPreviewActivity.class);
            intent.putExtra(WatermarkPreviewActivity.EXTRA_VIDEO_PATH, videoPath);
            intent.putExtra(WatermarkPreviewActivity.EXTRA_IMAGE_PATH, stickerPath);
            startActivityForResult(intent, PICK_STICKER_PREVIEW);
        } catch (Exception e) {
            toast("Error: " + e.getMessage());
        }
    }

    private void handleEditorTool(String tool) {
        switch (tool) {
            case "cut": showCutDialog(); break;
            case "precise_cut": showPreciseCutDialog(); break;
            case "trim_secs": showTrimBySecondsDialog(); break;
                    case "text": showTextDialog(); break;
            case "shadow_text": showShadowTextDialog(); break;
            case "music": pickMusic(); break;
            case "speed": showSpeedDialog(); break;
            case "speed_ramp": showSpeedRampDialog(); break;
            case "rotate": showRotateDialog(); break;
            case "compress": compressVideo(); break;
            case "audio": showAudioDialog(); break;
            case "audio_boost": showAudioBoostDialog(); break;
            case "audio_fade": showAudioFadeDialog(); break;
            case "audio_duck": showAudioDuckDialog(); break;
            case "video_tools": showVideoToolsDialog(); break;
            case "watermark": showWatermarkDialog(); break;
            case "effects": showVisualEffectsDialog(); break;
            case "slideshow": pickImages(); break;
            case "quality": showQualityDialog(); break;
            case "repeat": showRepeatDialog(); break;
            case "replace_music": pickReplaceMusic(); break;
            case "denoise": denoiseAudio(); break;
            case "screenshot": captureScreenshot(); break;
            case "theme": toggleTheme(); break;
            case "stickers": showStickersDialog(); break;
            case "color_adjust": showColorAdjustDialog(); break;
            case "frame": showFrameDialog(); break;
            case "social_size": showSocialSizeDialog(); break;
            case "pip": pickPipVideo(); break;
            case "swap_audio": pickSwapAudioVideo(); break;
            case "split": showSplitDialog(); break;
            case "color_tint": showColorTintDialog(); break;
            case "cutout_image": pickCutoutImage(); break;
            case "gif": showGifDialog(); break;
            case "collage": showCollageDialog(); break;
            case "manual_resize": showManualResizeDialog(); break;
            case "remove_silence": showRemoveSilenceDialog(); break;
            default: toast(tr("أداة غير معروفة: ") + tool);
        }
    }


    // ============ دالة مساعدة: نافذة بنفسجية بأزرار ============
    private androidx.appcompat.app.AlertDialog purpleDialog;
    private void showPurpleDialog(String title, String[] items, java.util.function.IntConsumer onItem) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(30, 30, 30, 30);
        container.setBackgroundColor(0xFF6A1B9A);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextColor(0xFFFFFFFF);
        titleView.setTextSize(20);
        titleView.setGravity(Gravity.CENTER);
        titleView.setPadding(0, 0, 0, 30);
        container.addView(titleView);

        for (int i = 0; i < items.length; i++) {
            final int idx = i;
            Button btn = new Button(this);
            btn.setText(items[i]);
            btn.setTextColor(0xFFFFFFFF);
            btn.setTextSize(16);
            btn.setBackgroundResource(R.drawable.button_click_effect);
            btn.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.setMargins(0, 8, 0, 8);
            btn.setLayoutParams(lp);
            btn.setOnClickListener(v -> {
                if (purpleDialog != null) purpleDialog.dismiss();
                onItem.accept(idx);
            });
            container.addView(btn);
        }

        Button cancelBtn = new Button(this);
        cancelBtn.setText(tr("إلغاء"));
        cancelBtn.setTextColor(0xFFFFFFFF);
        cancelBtn.setTextSize(14);
        cancelBtn.setBackgroundResource(R.drawable.button_click_effect);
        cancelBtn.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(-1, -2);
        clp.setMargins(0, 25, 0, 0);
        cancelBtn.setLayoutParams(clp);
        cancelBtn.setOnClickListener(v -> { if (purpleDialog != null) purpleDialog.dismiss(); });
        container.addView(cancelBtn);

        purpleDialog = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.PurpleDialog)
                .setView(container).create();
        purpleDialog.show();
    }


    // ============ دالة: نافذة بنفسجية مع إدخال ============
    private void showPurpleInputDialog(String title, String hint, String actionText, java.util.function.Consumer<String> onAction) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(30, 30, 30, 30);
        container.setBackgroundColor(0xFF6A1B9A);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextColor(0xFFFFFFFF);
        titleView.setTextSize(20);
        titleView.setGravity(Gravity.CENTER);
        titleView.setPadding(0, 0, 0, 30);
        container.addView(titleView);

        final EditText et = new EditText(this);
        et.setHint(hint);
        et.setHintTextColor(0xFFE1BEE7);
        et.setTextColor(0xFFFFFFFF);
        et.setBackgroundColor(0x33FFFFFF);
        et.setPadding(20, 20, 20, 20);
        LinearLayout.LayoutParams elp = new LinearLayout.LayoutParams(-1, -2);
        elp.setMargins(0, 0, 0, 20);
        et.setLayoutParams(elp);
        container.addView(et);

        Button actionBtn = new Button(this);
        actionBtn.setText(actionText);
        actionBtn.setTextColor(0xFFFFFFFF);
        actionBtn.setTextSize(16);
        actionBtn.setBackgroundResource(R.drawable.button_click_effect);
        actionBtn.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
        LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(-1, -2);
        alp.setMargins(0, 8, 0, 8);
        actionBtn.setLayoutParams(alp);
        actionBtn.setOnClickListener(v -> {
            String text = et.getText().toString().trim();
            if (purpleDialog != null) purpleDialog.dismiss();
            onAction.accept(text);
        });
        container.addView(actionBtn);

        Button cancelBtn = new Button(this);
        cancelBtn.setText(tr("إلغاء"));
        cancelBtn.setTextColor(0xFFFFFFFF);
        cancelBtn.setTextSize(14);
        cancelBtn.setBackgroundResource(R.drawable.button_click_effect);
        cancelBtn.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(-1, -2);
        clp.setMargins(0, 8, 0, 0);
        cancelBtn.setLayoutParams(clp);
        cancelBtn.setOnClickListener(v -> { if (purpleDialog != null) purpleDialog.dismiss(); });
        container.addView(cancelBtn);

        purpleDialog = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.PurpleDialog)
                .setView(container).create();
        purpleDialog.show();
    }

    // ============ دالة: نافذة بنفسجية مع رسالة ============
    private void showPurpleMessageDialog(String title, String message, String actionText, Runnable onAction) {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(30, 30, 30, 30);
        container.setBackgroundColor(0xFF6A1B9A);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextColor(0xFFFFFFFF);
        titleView.setTextSize(20);
        titleView.setGravity(Gravity.CENTER);
        titleView.setPadding(0, 0, 0, 20);
        container.addView(titleView);

        TextView msgView = new TextView(this);
        msgView.setText(message);
        msgView.setTextColor(0xFFE1BEE7);
        msgView.setTextSize(16);
        msgView.setGravity(Gravity.CENTER);
        msgView.setPadding(0, 0, 0, 30);
        container.addView(msgView);

        Button actionBtn = new Button(this);
        actionBtn.setText(actionText);
        actionBtn.setTextColor(0xFFFFFFFF);
        actionBtn.setTextSize(16);
        actionBtn.setBackgroundResource(R.drawable.button_click_effect);
        actionBtn.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
        LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(-1, -2);
        alp.setMargins(0, 8, 0, 8);
        actionBtn.setLayoutParams(alp);
        actionBtn.setOnClickListener(v -> {
            if (purpleDialog != null) purpleDialog.dismiss();
            onAction.run();
        });
        container.addView(actionBtn);

        Button cancelBtn = new Button(this);
        cancelBtn.setText(tr("إلغاء"));
        cancelBtn.setTextColor(0xFFFFFFFF);
        cancelBtn.setTextSize(14);
        cancelBtn.setBackgroundResource(R.drawable.button_click_effect);
        cancelBtn.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(-1, -2);
        clp.setMargins(0, 8, 0, 0);
        cancelBtn.setLayoutParams(clp);
        cancelBtn.setOnClickListener(v -> { if (purpleDialog != null) purpleDialog.dismiss(); });
        container.addView(cancelBtn);

        purpleDialog = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.PurpleDialog)
                .setView(container).create();
        purpleDialog.show();
    }

    private void notifyGallery(String filePath) {
        // Show interstitial ad (but not too often)
        try {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(this);
                android.util.Log.d("AdMob", "Interstitial shown after save");
            } else {
                loadInterstitialAd();
            }
        } catch (Exception e) {
            android.util.Log.e("AdMob", "Interstitial error: " + e.getMessage());
        }
        
        try {
            android.media.MediaScannerConnection.scanFile(this, new String[]{filePath}, null, null);
        } catch (Exception e) { e.printStackTrace(); }
    }
    private void notifyGallery_OLD(String filePath) {
        try {
            // الطريقة 1: MediaScannerConnection
            android.media.MediaScannerConnection.scanFile(this,
                    new String[]{filePath}, null, null);

            // الطريقة 2: MediaStore API (أقوى)
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) return;

            android.content.ContentValues values = new android.content.ContentValues();
            values.put(android.provider.MediaStore.Video.Media.DISPLAY_NAME, file.getName());
            values.put(android.provider.MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            values.put(android.provider.MediaStore.Video.Media.RELATIVE_PATH, android.os.Environment.DIRECTORY_MOVIES + "/VideoEditorPro");
            values.put(android.provider.MediaStore.Video.Media.IS_PENDING, 0);
            values.put(android.provider.MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(android.provider.MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());

            getContentResolver().insert(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            // تجاهل الأخطاء
        }
    }


    // ===== AdMob Helper Methods =====
        private void setupBannerAd() {
        try {
            mAdView = findViewById(R.id.adView);
            if (mAdView != null) {
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
                android.util.Log.d("AdMob", "Banner loaded from XML");
            }
        } catch (Exception e) {
            android.util.Log.e("AdMob", "Banner error: " + e.getMessage());
        }
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, INTERSTITIAL_AD_UNIT_ID, adRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            mInterstitialAd = null;
                            loadInterstitialAd();
                        }
                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            mInterstitialAd = null;
                        }
                    });
                }
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    mInterstitialAd = null;
                }
            });
    }

    private void showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        }
    }

    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, REWARDED_AD_UNIT_ID, adRequest,
            new RewardedAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    mRewardedAd = rewardedAd;
                    mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            mRewardedAd = null;
                            loadRewardedAd();
                        }
                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            mRewardedAd = null;
                        }
                    });
                }
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    mRewardedAd = null;
                }
            });
    }

    private void showRewardedAd(Runnable onReward) {
        if (mRewardedAd != null) {
            mRewardedAd.show(this, rewardItem -> {
                onReward.run();
                loadRewardedAd();
            });
        } else {
            // No ad available, give reward anyway
            onReward.run();
        }
    }

        
    

    
    
    
    private void doSlowMerge(final java.util.List<String> allPaths) {
        StringBuilder inputsB = new StringBuilder("-y -nostdin ");
        StringBuilder filterB = new StringBuilder();
        long totalMs = 0;
        
        for (int i = 0; i < allPaths.size(); i++) {
            inputsB.append("-i \"").append(allPaths.get(i)).append("\" ");
            filterB.append("[").append(i).append(":v]scale=1280:720:force_original_aspect_ratio=decrease,pad=1280:720:(ow-iw)/2:(oh-ih)/2,setsar=1,fps=30,format=yuv420p[v").append(i).append("];");
            filterB.append("[").append(i).append(":a]aresample=48000,asetpts=PTS-STARTPTS[a").append(i).append("];");
            try {
                android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
                mmr.setDataSource(allPaths.get(i));
                String dur = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
                if (dur != null) totalMs += Long.parseLong(dur);
                mmr.release();
            } catch (Exception e) {}
        }
        for (int i = 0; i < allPaths.size(); i++) {
            filterB.append("[v").append(i).append("][a").append(i).append("]");
        }
        filterB.append("concat=n=").append(allPaths.size()).append(":v=1:a=1[outv][outa]");
        
        final long finalTotalMs = totalMs;
        final String tempMerged = new java.io.File(getCacheDir(), "merged_slow_" + System.currentTimeMillis() + ".mp4").getAbsolutePath();
        String cmd = inputsB.toString() + "-filter_complex \"" + filterB.toString() + "\" -map \"[outv]\" -map \"[outa]\" -c:v libx264 -preset ultrafast -crf 23 -c:a aac -b:a 128k -threads 4 -metadata:s:v:0 rotate=0 -movflags +faststart \"" + tempMerged + "\"";
        
        tvStatus.setText("جاري دمج الفيديوهات...");
        progressBar.setVisibility(android.view.View.VISIBLE);
        progressBar.setIndeterminate(false);
        progressBar.setProgress(0);
        btnCancelOp.setVisibility(android.view.View.VISIBLE);
        
        FFmpegKit.executeAsync(cmd, session -> runOnUiThread(() -> {
            progressBar.setVisibility(android.view.View.INVISIBLE);
            btnCancelOp.setVisibility(android.view.View.GONE);
            if (ReturnCode.isSuccess(session.getReturnCode())) {
                ProjectState.isMerging = false;
                ProjectState.mergePaths.clear();
                processQueueWithVideo(tempMerged);
            } else {
                String log = session.getAllLogsAsString();
                if (log != null && log.length() > 500) log = log.substring(log.length() - 500);
                new android.app.AlertDialog.Builder(this, R.style.PurpleDialog)
                    .setTitle("خطأ في دمج الفيديوهات")
                    .setMessage(log != null ? log : "unknown")
                    .setPositiveButton("حسناً", null)
                    .show();
                tvStatus.setText("فشل دمج الفيديوهات");
                tvStatus.setTextColor(0xFFFF0000);
            }
        }), null, statistics -> {
            if (statistics != null && finalTotalMs > 0) {
                int progress = (int) ((statistics.getTime() / (float) finalTotalMs) * 100);
                if (progress >= 0 && progress <= 100) {
                    runOnUiThread(() -> {
                        progressBar.setProgress(progress);
                        tvStatus.setText("جاري دمج الفيديوهات... " + progress + "%");
                    });
                }
            }
        });
    }
    
    
    private void doPreMerge(final String firstVideo, final java.util.List<String> others) {
        final java.util.List<String> allPaths = new java.util.ArrayList<>();
        allPaths.add(firstVideo);
        allPaths.addAll(others);
        
        // 1. محاولة الدمج السريع (بدون إعادة ترميز) باستخدام -c copy
        try {
            java.io.File listFile = new java.io.File(getCacheDir(), "merge_list_fast_" + System.currentTimeMillis() + ".txt");
            java.io.FileWriter w = new java.io.FileWriter(listFile);
            w.write("file '" + firstVideo + "'\n");
            for (String p : others) w.write("file '" + p + "'\n");
            w.close();
            
            final String tempMergedFast = new java.io.File(getCacheDir(), "merged_fast_" + System.currentTimeMillis() + ".mp4").getAbsolutePath();
            String fastCmd = "-y -nostdin -fflags +genpts -f concat -safe 0 -i \"" + listFile.getAbsolutePath() + "\" -c copy -bsf:a aac_adtstoasc \"" + tempMergedFast + "\"";
            
            tvStatus.setText("جاري دمج الفيديوهات (سريع)...");
            tvStatus.setTextColor(0xFFFFC107);
            progressBar.setVisibility(android.view.View.VISIBLE);
            progressBar.setIndeterminate(true);
            btnCancelOp.setVisibility(android.view.View.VISIBLE);
            
            // دائماً نستخدم الدمج البطيء (إعادة ترميز) لضمان صحة الدوران
            doSlowMerge(allPaths);
        } catch (Exception e) {
            doSlowMerge(allPaths);
        }
    }
    
    
        private void extractAudioToMp3(String mp4Path) {
        final String tempMp3 = new java.io.File(getCacheDir(), "audio_" + System.currentTimeMillis() + ".mp3").getAbsolutePath();
        String cmd = "-y -i \"" + mp4Path + "\" -vn -ar 44100 -ac 2 -b:a 192k \"" + tempMp3 + "\"";
        
        tvStatus.setText("جاري استخراج الصوت...");
        progressBar.setVisibility(android.view.View.VISIBLE);
        progressBar.setIndeterminate(true);
        
        FFmpegKit.executeAsync(cmd, session -> runOnUiThread(() -> {
            progressBar.setVisibility(android.view.View.INVISIBLE);
            progressBar.setIndeterminate(false);
            if (ReturnCode.isSuccess(session.getReturnCode())) {
                saveMp3ToGallery(tempMp3);
                try { new java.io.File(tempMp3).delete(); } catch(Exception e) {}
                try { new java.io.File(mp4Path).delete(); } catch(Exception e) {}
            } else {
                tvStatus.setText("فشل استخراج الصوت");
                tvStatus.setTextColor(0xFFFF0000);
                toast("حدث خطأ أثناء استخراج الصوت");
            }
        }));
    }

    private void processQueueWithVideo(String sourceVideo) {
        // حماية من مسارات VOI الفارغة
        if (ProjectState.isVOI) {
            if (ProjectState.voiImagePath == null || ProjectState.voiVideoPath == null) {
                toast("خطأ: لم يتم العثور على ملفات VOI، يرجى إعادة الاختيار");
                ProjectState.isVOI = false;
                ProjectState.reset();
                return;
            }
            if (!new java.io.File(ProjectState.voiImagePath).exists() || !new java.io.File(ProjectState.voiVideoPath).exists()) {
                toast("خطأ: ملفات VOI مفقودة، يرجى إعادة الاختيار");
                ProjectState.isVOI = false;
                ProjectState.reset();
                return;
            }
        }
        if (sourceVideo == null) {
            toast("خطأ: مسار الفيديو الرئيسي فارغ");
            return;
        }
        tvStatus.setText("جاري التصدير النهائي...");
        tvStatus.setTextColor(0xFFFFC107);
        progressBar.setVisibility(android.view.View.VISIBLE);
        progressBar.setIndeterminate(true);
        btnCancelOp.setVisibility(android.view.View.VISIBLE);

        String out = (ProjectState.splitCount > 1) ? new java.io.File(getCacheDir(), "temp_split_full_" + System.currentTimeMillis() + ".mp4").getAbsolutePath() : newOut("final_export");
        StringBuilder inputs = new StringBuilder("-y -nostdin ");

        long totalMs = 0;
        try {
            android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
            if (ProjectState.isVOI && ProjectState.voiVideoPath != null) {
                mmr.setDataSource(ProjectState.voiVideoPath);
            } else {
                if (ProjectState.isVOI && ProjectState.voiVideoPath != null) {
                mmr.setDataSource(ProjectState.voiVideoPath);
            } else {
                if (ProjectState.isVOI && ProjectState.voiVideoPath != null) {
                mmr.setDataSource(ProjectState.voiVideoPath);
            } else {
                mmr.setDataSource(sourceVideo);
            }
            }
            }
            String dur = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (dur != null) totalMs = Long.parseLong(dur);
            mmr.release();
            
            // حساب مدة الفيديوهات المدمجة إذا كان الدمج مفعلاً
            if (ProjectState.isMerging && !ProjectState.mergePaths.isEmpty()) {
                for (String p : ProjectState.mergePaths) {
                    try {
                        android.media.MediaMetadataRetriever mmr2 = new android.media.MediaMetadataRetriever();
                        mmr2.setDataSource(p);
                        String dur2 = mmr2.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
                        if (dur2 != null) totalMs += Long.parseLong(dur2);
                        mmr2.release();
                    } catch (Exception e) {}
                }
            }
        } catch (Exception e) { }
        float totalSec = totalMs / 1000.0f;

        if (ProjectState.trimStart >= 0 && ProjectState.trimEnd >= 0 && totalSec > 0) {
            float cutStart = ProjectState.trimStart;
            float cutEnd = ProjectState.trimEnd;
            float keepDuration = totalSec - cutStart - cutEnd;
            if (keepDuration > 0.1f) {
                inputs.append("-ss ").append(cutStart).append(" -t ").append(keepDuration).append(" ");
            }
        }

        if (ProjectState.repeatCount > 1) {
            inputs.append("-stream_loop ").append(ProjectState.repeatCount - 1).append(" ");
        }

        if (ProjectState.isMerging && !ProjectState.mergePaths.isEmpty()) {
            try {
                java.io.File listFile = new java.io.File(getCacheDir(), "merge_list_" + System.currentTimeMillis() + ".txt");
                java.io.FileWriter w = new java.io.FileWriter(listFile);
                w.write("file '" + sourceVideo + "'\n");
                for (String p : ProjectState.mergePaths) {
                    w.write("file '" + p + "'\n");
                }
                w.close();
                inputs.append("-fflags +genpts -f concat -safe 0 -i ").append(listFile.getAbsolutePath());
            } catch (Exception e) {
                inputs.append("-i ").append(sourceVideo);
            }
        } else {
            inputs.append("-i ").append(sourceVideo);
        }
        if (ProjectState.isCollage || !ProjectState.collagePaths.isEmpty()) {
            for (String p : ProjectState.collagePaths) {
                inputs.append(" -i ").append(p);
            }
        }
        int inputIndex = 1;
        int pipInputIndex = -1;
        int voiInputIndex = -1;

        int musicInputIndex = -1;
        int duckInputIndex = -1;
        if (ProjectState.duckMusicPath != null) {
            duckInputIndex = inputIndex;
            inputs.append(" -i ").append(ProjectState.duckMusicPath);
            inputIndex++;
        }

        if (ProjectState.musicPath != null) {
            musicInputIndex = inputIndex;
            inputs.append(" -i ").append(ProjectState.musicPath);
            inputIndex++;
        }

        int swapAudioInputIndex = -1;
        if (ProjectState.swapAudioPath != null) {
            swapAudioInputIndex = inputIndex;
            inputs.append(" -i ").append(ProjectState.swapAudioPath);
            inputIndex++;
        }

        StringBuilder filterGraph = new StringBuilder();
        String currentMap = "0:v";
        
        
        
        if (ProjectState.isCollage || !ProjectState.collagePaths.isEmpty()) {
            String vf = "";
            if ("vertical".equals(ProjectState.collageMode)) {
                vf = "[0:v]scale=1080:960:force_original_aspect_ratio=increase,crop=1080:960,fps=30,format=yuv420p[top];"
                   + "[1:v]scale=1080:960:force_original_aspect_ratio=increase,crop=1080:960,fps=30,format=yuv420p[bottom];"
                   + "[top][bottom]vstack=inputs=2:shortest=1[v]";
            } else if ("horizontal".equals(ProjectState.collageMode)) {
                vf = "[0:v]scale=960:1920:force_original_aspect_ratio=increase,crop=960:1920,fps=30,format=yuv420p[left];"
                   + "[1:v]scale=960:1920:force_original_aspect_ratio=increase,crop=960:1920,fps=30,format=yuv420p[right];"
                   + "[left][right]hstack=inputs=2:shortest=1[v]";
            } else {
                vf = "[0:v]scale=960:960:force_original_aspect_ratio=increase,crop=960:960,fps=30,format=yuv420p[a];"
                   + "[1:v]scale=960:960:force_original_aspect_ratio=increase,crop=960:960,fps=30,format=yuv420p[b];"
                   + "[2:v]scale=960:960:force_original_aspect_ratio=increase,crop=960:960,fps=30,format=yuv420p[c];"
                   + "[3:v]scale=960:960:force_original_aspect_ratio=increase,crop=960:960,fps=30,format=yuv420p[d];"
                   + "[a][b]hstack[top];[c][d]hstack[bottom];[top][bottom]vstack=inputs=2:shortest=1[v]";
            }
            filterGraph.append(vf).append(";");
            currentMap = "v";
        }

        if (ProjectState.cropFilter != null) {
            filterGraph.append("[").append(currentMap).append("]").append(ProjectState.cropFilter).append("[vCrop];");
            currentMap = "vCrop";
        }

        int filterCounter = 0;
        for (String f : ProjectState.filters) {
            String newMap = "vFilter" + filterCounter;
            filterGraph.append("[").append(currentMap).append("]").append(f).append("[").append(newMap).append("];");
            currentMap = newMap;
            filterCounter++;
        }

        if (ProjectState.videoSpeed != 1.0f) {
            String newMap = "vSpeed";
            filterGraph.append("[").append(currentMap).append("]setpts=1/").append(ProjectState.videoSpeed).append("*PTS[").append(newMap).append("];");
            currentMap = newMap;
        }

        if (ProjectState.qualityChanged && ProjectState.targetResolution != null) {
            String newMap = "vScale";
            filterGraph.append("[").append(currentMap).append("]scale=-2:").append(ProjectState.targetResolution).append("[").append(newMap).append("];");
            currentMap = newMap;
        }

        for (ProjectState.Overlay ov : ProjectState.overlays) {
            inputs.append(" -i ").append(ov.path);
            String wmMap = "wm" + inputIndex;
            filterGraph.append("[").append(inputIndex).append(":v]scale=").append(ov.w).append(":").append(ov.h).append("[").append(wmMap).append("];");
            String newMap = "vOverlay" + inputIndex;
            filterGraph.append("[").append(currentMap).append("][").append(wmMap).append("]overlay=").append(ov.x).append(":").append(ov.y).append("[").append(newMap).append("];");
            currentMap = newMap;
            inputIndex++;
        }

        if (ProjectState.pipVideoPath != null) {
            inputs.append(" -i ").append(ProjectState.pipVideoPath);
            String pipMap = "pipScale" + inputIndex;
            filterGraph.append("[").append(inputIndex).append(":v]scale=").append(ProjectState.pipW).append(":").append(ProjectState.pipH).append("[").append(pipMap).append("];");
            String newPipMap = "vPip" + inputIndex;
            filterGraph.append("[").append(currentMap).append("][").append(pipMap).append("]overlay=").append(ProjectState.pipX).append(":").append(ProjectState.pipY).append(":eof_action=pass[").append(newPipMap).append("];");
            currentMap = newPipMap;
            pipInputIndex = inputIndex;
            inputIndex++;
        }

        if (ProjectState.isVOI && ProjectState.voiVideoPath != null && new java.io.File(ProjectState.voiVideoPath).exists()) {
            inputs.append(" -i ").append(ProjectState.voiVideoPath);
            String voiMap = "voiScale" + inputIndex;
            filterGraph.append("[").append(inputIndex).append(":v]scale=").append(ProjectState.voiW).append(":").append(ProjectState.voiH).append("[").append(voiMap).append("];");
            String newVoiMap = "vVOI" + inputIndex;
            filterGraph.append("[").append(currentMap).append("][").append(voiMap).append("]overlay=").append(ProjectState.voiX).append(":").append(ProjectState.voiY).append(":eof_action=pass[").append(newVoiMap).append("];");
            currentMap = newVoiMap;
            voiInputIndex = inputIndex;
            inputIndex++;
        }

        if (ProjectState.hasSocialSize) {
            String newMap = "vSocial";
            filterGraph.append("[").append(currentMap).append("]scale=").append(ProjectState.socialWidth).append(":").append(ProjectState.socialHeight).append(":force_original_aspect_ratio=decrease,pad=").append(ProjectState.socialWidth).append(":").append(ProjectState.socialHeight).append(":(ow-iw)/2:(oh-ih)/2:color=black,setsar=1[").append(newMap).append("];");
            currentMap = newMap;
        }

        String finalAudioMap = ProjectState.isVOI ? "-map 1:a?" : "-map 0:a?";
        String mainAudio = ProjectState.isVOI ? "1:a" : "0:a";
        String finalAudioCodec = "-c:a aac -b:a 96k";
        String audioComplex = "";

        // === منطق صوت الفيديو داخل الفيديو (PiP) ===
        if (ProjectState.pipVideoPath != null && pipInputIndex != -1 && !ProjectState.isCollage && ProjectState.collagePaths.isEmpty()) {
            if (ProjectState.pipAudioType == 1) {
                // صوت الفيديو الصغير فقط
                finalAudioMap = "-map " + pipInputIndex + ":a?";
            } else if (ProjectState.pipAudioType == 2) {
                // دمج الصوتين
                audioComplex = ";[" + mainAudio + ":a?][" + pipInputIndex + ":a?]amix=inputs=2:duration=first[aout]";
                finalAudioMap = "-map \"[aout]\"";
            } else if (ProjectState.pipAudioType == 3) {
                // بدون صوت
                finalAudioMap = "";
                finalAudioCodec = "-an";
            }
        }

        if (ProjectState.isCollage || !ProjectState.collagePaths.isEmpty()) {
            int total = 1 + ProjectState.collagePaths.size();
            int choice = ProjectState.collageAudioChoice;
            boolean noAudio = false;
            int audioMapIndex = 0;

            if (total == 2) {
                if (choice == 0) audioMapIndex = 0;
                else if (choice == 1) audioMapIndex = 1;
                else if (choice == 2) { audioComplex = ";[" + mainAudio + ":a][1:a]amix=inputs=2:duration=first[aout]"; finalAudioMap = "-map \"[aout]\""; audioMapIndex = -1; }
                else { noAudio = true; }
            } else if (total == 3) {
                if (choice == 0) audioMapIndex = 0;
                else if (choice == 1) audioMapIndex = 1;
                else if (choice == 2) audioMapIndex = 2;
                else if (choice == 3) { audioComplex = ";[" + mainAudio + ":a][1:a][2:a]amix=inputs=3:duration=first[aout]"; finalAudioMap = "-map \"[aout]\""; audioMapIndex = -1; }
                else { noAudio = true; }
            } else {
                if (choice == 0) audioMapIndex = 0;
                else if (choice == 1) audioMapIndex = 1;
                else if (choice == 2) audioMapIndex = 2;
                else if (choice == 3) audioMapIndex = 3;
                else if (choice == 4) { audioComplex = ";[" + mainAudio + ":a][1:a][2:a][3:a]amix=inputs=4:duration=first[aout]"; finalAudioMap = "-map \"[aout]\""; audioMapIndex = -1; }
                else { noAudio = true; }
            }
            if (noAudio) {
                finalAudioMap = "";
                finalAudioCodec = "-an";
            } else if (audioMapIndex != -1) {
                finalAudioMap = "-map " + audioMapIndex + ":a?";
            }
        } else if (ProjectState.muteAudio) {
            finalAudioMap = "";
            finalAudioCodec = "-an";
        } else if (ProjectState.swapAudioPath != null && swapAudioInputIndex != -1) {
            if (ProjectState.swapAudioType == 0) { // Replace
                finalAudioMap = "-map " + swapAudioInputIndex + ":a";
            } else { // Mix
                audioComplex = ";[" + mainAudio + ":a][" + swapAudioInputIndex + ":a]amix=inputs=2:duration=first[aout]";
                finalAudioMap = "-map \"[aout]\"";
            }
        } else if (ProjectState.musicPath != null) {
            if (ProjectState.musicMergeType == 0 || ProjectState.musicMergeType == 1 || ProjectState.musicMergeType == 2) {
                finalAudioMap = "-map " + musicInputIndex + ":a";
            } else {
                audioComplex = ";[" + mainAudio + ":a][" + musicInputIndex + ":a]amix=inputs=2:duration=first[aout]";
                finalAudioMap = "-map \"[aout]\"";
            }
        } else if (ProjectState.audioFilter != null) {
            audioComplex = ";[" + mainAudio + ":a]" + ProjectState.audioFilter + "[aout]";
            finalAudioMap = "-map \"[aout]\"";
        }

        if (ProjectState.videoSpeed != 1.0f) {
            if (audioComplex.isEmpty()) {
                audioComplex = ";[" + mainAudio + ":a]atempo=" + ProjectState.videoSpeed + "[aspeed]";
                finalAudioMap = "-map \"[aspeed]\"";
            } else {
                audioComplex += ";[aout]atempo=" + ProjectState.videoSpeed + "[aspeed2]";
                finalAudioMap = "-map \"[aspeed2]\"";
            }
        }

        // === الإعداد النهائي لصوت الفيديو داخل الفيديو (لا يمكن تجاوزه) ===
        if (ProjectState.pipVideoPath != null && pipInputIndex != -1 && !ProjectState.isCollage && ProjectState.collagePaths.isEmpty()) {
            if (ProjectState.pipAudioType == 1) {
                // صوت الفيديو الصغير فقط
                finalAudioMap = "-map " + pipInputIndex + ":a?";
                audioComplex = "";
            } else if (ProjectState.pipAudioType == 2) {
                // دمج الصوتين
                audioComplex = ";[" + mainAudio + ":a][" + pipInputIndex + ":a]amix=inputs=2:duration=first[aout]";
                finalAudioMap = "-map \"[aout]\"";
            } else if (ProjectState.pipAudioType == 3) {
                // بدون صوت
                finalAudioMap = "";
                finalAudioCodec = "-an";
                audioComplex = "";
            }
        }


        String finalFilter = filterGraph.toString();
        if (finalFilter.endsWith(";")) finalFilter = finalFilter.substring(0, finalFilter.length() - 1);

        String fullFilter = finalFilter;
        if (!audioComplex.isEmpty()) {
            if (!fullFilter.isEmpty()) fullFilter += ";";
            fullFilter += audioComplex.substring(1);
        }
        if (fullFilter.endsWith(";")) fullFilter = fullFilter.substring(0, fullFilter.length() - 1);

        String videoCodec = ProjectState.compressVideo ? "-c:v libx264 -preset fast -crf 28 -pix_fmt yuv420p " : "-c:v libx264 -preset ultrafast -crf 32 -pix_fmt yuv420p ";

        String videoMapParam;
        if (currentMap.equals("0:v")) {
            videoMapParam = "0:v"; // فيديو بدون تعديلات بصرية
        } else {
            videoMapParam = "\"[" + currentMap + "]\""; // فيديو تم تعديله بالفلاتر
        }

        String cmd;
        // Add safety duration limit
        String safetyTime = "";
        if (ProjectState.isCollage || !ProjectState.collagePaths.isEmpty()) {
            safetyTime = " -t " + (totalMs / 1000f) + " ";
        }
        if (fullFilter.isEmpty()) {
            // لا توجد أي فلاتر (لا فيديو ولا صوت)
            cmd = inputs.toString() + " -map 0:v " + finalAudioMap + " " + videoCodec + finalAudioCodec + " -vsync vfr " + out;
        } else {
            // توجد فلاتر (قد تكون صوتية فقط أو بصرية أو كلاهما)
            cmd = inputs.toString() + " -filter_complex \"" + fullFilter + "\" -map " + videoMapParam + " " + finalAudioMap + " " + videoCodec + finalAudioCodec + " -vsync vfr " + out;
        }

        android.util.Log.d("VOI_EXPORT", "CMD: " + cmd);
        final long finalTotalMs = totalMs;

        currentSession = FFmpegKit.executeAsync(cmd, session -> runOnUiThread(() -> {
            progressBar.setVisibility(android.view.View.INVISIBLE);
            progressBar.setIndeterminate(false);
            btnCancelOp.setVisibility(android.view.View.GONE);

            if (ReturnCode.isSuccess(session.getReturnCode())) {
                if (ProjectState.toMp3) {
                    ProjectState.reset();
                    extractAudioToMp3(out);
                } else if (ProjectState.toGif) {
                    int preset = ProjectState.gifPreset;
                    ProjectState.reset();
                    convertVideoToGif(out, preset);
                } else if (ProjectState.splitCount > 1) {
                    int parts = ProjectState.splitCount;
                    ProjectState.reset();
                    splitExportedVideo(out, parts);
                } else {
                    tvStatus.setText("تم التصدير بنجاح!");
                    tvStatus.setTextColor(0xFF4CAF50);
                    notifyGallery(out);
                    ProjectState.reset();
                    toast("تم حفظ الفيديو في المعرض بنجاح!");
                }
            } else if (ReturnCode.isCancel(session.getReturnCode())) {
                // تم الإلغاء بواسطة المستخدم - لا نعرض نافذة خطأ
                tvStatus.setText("تم إلغاء التصدير");
                tvStatus.setTextColor(0xFFFF9800);
                toast("تم إلغاء العملية");
                try { new java.io.File(out).delete(); } catch(Exception e) {}
                ProjectState.reset();
            } else {
                tvStatus.setText("فشل التصدير");
                tvStatus.setTextColor(0xFFFF0000);
                String log = session.getAllLogsAsString();
                if (log != null && log.length() > 500) log = log.substring(log.length() - 500);
                new AlertDialog.Builder(this, R.style.PurpleDialog).setTitle("خطأ في FFmpeg").setMessage(log != null ? log : "unknown").setPositiveButton("حسناً", null).show();
            }
        }), null, statistics -> {
            if (finalTotalMs > 0 && statistics != null) {
                int progress = (int) ((statistics.getTime() / (float) finalTotalMs) * 100);
                if (progress >= 0 && progress <= 100) {
                    runOnUiThread(() -> {
                        progressBar.setIndeterminate(false);
                        progressBar.setVisibility(android.view.View.VISIBLE);
                        progressBar.setProgress(progress);
                        tvStatus.setText("جاري التصدير النهائي... " + progress + "%");
                    });
                } else if (progress > 100) {
                    runOnUiThread(() -> {
                        progressBar.setProgress(100);
                        tvStatus.setText("جاري إنهاء التصدير...");
                    });
                }
            }
        });
    }

private void exportFinalVideo() {
        if (ProjectState.slideshowImages.isEmpty() && ProjectState.originalVideoPath == null && videoPath == null && !(ProjectState.isVOI && ProjectState.voiVideoPath != null)) { toast("اختر فيديو أو صور أولاً"); return; }
        if (ProjectState.slideshowImages.isEmpty() && ProjectState.pendingCount == 0) { toast("لم تقم بأي تعديلات بعد."); return; }

        if (!ProjectState.slideshowImages.isEmpty()) {
            try {
                File listFile = new File(getCacheDir(), "slides.txt");
                java.io.FileWriter w = new java.io.FileWriter(listFile);
                String nl = System.lineSeparator();
                for (int i = 0; i < ProjectState.slideshowImages.size(); i++) {
                    w.write("file '" + ProjectState.slideshowImages.get(i) + "'" + nl);
                    w.write("duration 3" + nl);
                }
                w.write("file '" + ProjectState.slideshowImages.get(ProjectState.slideshowImages.size() - 1) + "'" + nl);
                w.close();
                
                String tempOut = new File(getCacheDir(), "temp_slideshow.mp4").getAbsolutePath();
                String baseVf = "scale=1080:1920:force_original_aspect_ratio=decrease,pad=1080:1920:(ow-iw)/2:(oh-ih)/2:color=black,setsar=1";
                String[] cmd;
                if (ProjectState.slideshowMusic != null && !ProjectState.slideshowMusic.isEmpty()) {
                    cmd = new String[]{"-y", "-f", "concat", "-safe", "0", "-i", listFile.getAbsolutePath(),
                            "-i", ProjectState.slideshowMusic, "-vf", baseVf,
                            "-map", "0:v", "-map", "1:a", "-c:v", "libx264", "-preset", "ultrafast", "-crf", "32",
                            "-pix_fmt", "yuv420p", "-r", "30", "-c:a", "aac", "-b:a", "96k", "-shortest", tempOut};
                } else {
                    cmd = new String[]{"-y", "-f", "concat", "-safe", "0", "-i", listFile.getAbsolutePath(),
                            "-vf", baseVf, "-c:v", "libx264", "-preset", "ultrafast", "-crf", "32",
                            "-pix_fmt", "yuv420p", "-r", "30", "-an", tempOut};
                }
                
                tvStatus.setText("جاري إنشاء عرض الشرائح...");
                tvStatus.setTextColor(0xFFFFC107);
                progressBar.setVisibility(android.view.View.VISIBLE);
                progressBar.setIndeterminate(true);
                
                FFmpegKit.executeAsync(String.join(" ", cmd), session -> runOnUiThread(() -> {
                    if (ReturnCode.isSuccess(session.getReturnCode())) {
                        processQueueWithVideo(tempOut);
                    } else {
                        progressBar.setVisibility(android.view.View.INVISIBLE);
                        progressBar.setIndeterminate(false);
                        toast("فشل إنشاء عرض الشرائح");
                    }
                }));
                
            } catch (Exception e) {
                toast("خطأ في عرض الشرائح: " + e.getMessage());
            }
            return;
        }
        
        String sourceVideo;
        if (ProjectState.isVOI && ProjectState.voiImagePath != null) {
            sourceVideo = ProjectState.voiImagePath;
        } else {
            sourceVideo = (ProjectState.originalVideoPath != null) ? ProjectState.originalVideoPath : videoPath;
        }
        
        if (ProjectState.isMerging && !ProjectState.mergePaths.isEmpty()) {
            doPreMerge(sourceVideo, ProjectState.mergePaths);
        } else {
            processQueueWithVideo(sourceVideo);
        }
    }

    private void showTrimBySecondsDialog() {
        if (videoPath == null) { toast("اختر فيديو أولاً"); return; }

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(40, 40, 40, 40);

        final EditText etStart = new EditText(this);
        etStart.setHint("ثانية البداية (مثال: 0)");
        etStart.setTextColor(0xFFFFFFFF);
        etStart.setHintTextColor(0xFFAAAAAA);
        ll.addView(etStart);

        final EditText etEnd = new EditText(this);
        etEnd.setHint("ثانية النهاية (مثال: 10)");
        etEnd.setTextColor(0xFFFFFFFF);
        etEnd.setHintTextColor(0xFFAAAAAA);
        ll.addView(etEnd);

        AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(tr("قص بالثواني"))
            .setView(ll)
            .setPositiveButton("تطبيق", (d, w) -> {
                try {
                    String sStr = etStart.getText().toString().trim();
                    String eStr = etEnd.getText().toString().trim();
                    if (sStr.isEmpty() || eStr.isEmpty()) { toast("أدخل الوقتين"); return; }
                    
                    float start = Float.parseFloat(sStr);
                    float end = Float.parseFloat(eStr);
                    
                    if (start < 0 || end < 0) {
                        toast("لا يمكن إدخال أرقام سالبة");
                        return;
                    }

                    ProjectState.trimStart = start;
                    ProjectState.trimEnd = end;
                    ProjectState.pendingCount++;
                    
                    tvStatus.setText("تم تحديد القص بالثواني (" + ProjectState.pendingCount + " تعديلات في الانتظار)");
                    tvStatus.setTextColor(0xFFFFC107);
                    toast("سيتم التطبيق عند التصدير");
                    
                } catch (Exception e) {
                    toast("أدخل أرقاماً صحيحة (ثواني)");
                }
            })
            .setNegativeButton("إلغاء", null)
            .create();
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFFFFFFFF);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFFFFFFFF);
        });
        dialog.show();
    }

}
