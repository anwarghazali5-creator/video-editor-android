package com.videocraft.pro;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

public class CutoutActivity extends AppCompatActivity {

    private int currentLang = Translations.LANG_AR;
    private void loadLang() {
        currentLang = getSharedPreferences("app_prefs", MODE_PRIVATE).getInt("lang", Translations.LANG_AR);
    }
    public String tr(String ar) {
        return Translations.tr(currentLang, ar);
    }

    public static final String EXTRA_IMAGE_PATH = "image_path";
    public static final String EXTRA_RESULT_PATH = "result_path";

    // Tools
    private static final int TOOL_ERASER = 0;
    private static final int TOOL_MAGIC = 1;
    private static final int TOOL_RECT = 2;
    private static final int TOOL_RESTORE = 3;

    private ImageView imgView;
    private Bitmap workingBitmap;    // current image
    private Bitmap originalBitmap;   // original for restore
    private Paint eraserPaint;
    private Paint restorePaint;
    private Paint rectPaint;
    private float brushSize = 150f;
    private int currentTool = TOOL_ERASER;
    
    private static final int MAX_UNDO = 5;
    private float lastBmpX = -1, lastBmpY = -1;
    private boolean isDrawing = false;
    private float rectStartX = -1, rectStartY = -1;
    private float rectEndX = -1, rectEndY = -1;
    private Button[] toolBtns;
    private Bitmap pendingPreview;
    private final Stack<Stroke> strokeStack = new Stack<>();
    private static final int MAX_STROKES = 50;

    private static class Stroke {
        float x1, y1, x2, y2;
        float size;
        boolean isRestore;
        Stroke(float x1, float y1, float x2, float y2, float size, boolean isRestore) {
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
            this.size = size; this.isRestore = isRestore;
        }
    }
    private float fingerOffset = 150f;

    private final int[] sizeValues = {40, 100, 200, 350, 500};
    private final String[] sizeNames = {"S", "M", "L", "XL", "XXL"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLang();
        String sourcePath = getIntent().getStringExtra(EXTRA_IMAGE_PATH);
        if (sourcePath == null) { finish(); return; }

        BitmapFactory.Options srcOpts = new BitmapFactory.Options();
        srcOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap loaded = BitmapFactory.decodeFile(sourcePath, srcOpts);
        if (loaded == null) { Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show(); finish(); return; }

        // تصغير الصورة إلى حد أقصى 1920 بكسل (يوفّر الذاكرة)
        int maxDim = 720;
        int origW = loaded.getWidth();
        int origH = loaded.getHeight();
        float scale = 1f;
        if (origW > maxDim || origH > maxDim) {
            scale = Math.min((float)maxDim / origW, (float)maxDim / origH);
        }
        int newW = Math.max(1, (int)(origW * scale));
        int newH = Math.max(1, (int)(origH * scale));

        Bitmap scaled = Bitmap.createScaledBitmap(loaded, newW, newH, true);
        if (scaled != loaded) loaded.recycle();

        originalBitmap = Bitmap.createBitmap(newW, newH, Bitmap.Config.ARGB_8888);
        Canvas oc = new Canvas(originalBitmap);
        oc.drawBitmap(scaled, 0, 0, null);
        originalBitmap.setHasAlpha(true);
        if (scaled != originalBitmap) scaled.recycle();

        workingBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        workingBitmap.setHasAlpha(true);

        eraserPaint = new Paint();
        eraserPaint.setAntiAlias(true);
        eraserPaint.setColor(Color.TRANSPARENT);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraserPaint.setStyle(Paint.Style.FILL);

        restorePaint = new Paint();
        restorePaint.setAntiAlias(true);
        restorePaint.setStyle(Paint.Style.FILL);

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(6);
        rectPaint.setColor(0xFFFFEB3B);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF1A1A1A);

        TextView header = new TextView(this);
        header.setText(tr("امسح الخلفية بإصبعك"));
        header.setTextColor(0xFFB0B0B0);
        header.setTextSize(11);
        header.setPadding(20, 4, 20, 4);
        header.setGravity(Gravity.CENTER);
        root.addView(header);

        imgView = new ImageView(this);
        imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgView.setImageBitmap(workingBitmap);
        imgView.setBackgroundColor(0xFF888888);
        LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams(-1, 0, 1f);
        imgView.setLayoutParams(imgLp);
        root.addView(imgView);


        // Slider
        LinearLayout sliderRow = new LinearLayout(this);
        sliderRow.setOrientation(LinearLayout.HORIZONTAL);
        sliderRow.setPadding(20, 0, 20, 6);
        sliderRow.setGravity(Gravity.CENTER_VERTICAL);

        final TextView tvSize = new TextView(this);
        tvSize.setText("دقيق: " + (int)brushSize);
        tvSize.setTextColor(0xFFFFFFFF);
        tvSize.setTextSize(10);
        tvSize.setWidth(150);
        sliderRow.addView(tvSize);

        SeekBar sbSize = new SeekBar(this);
        sbSize.setMax(500);
        sbSize.setProgress((int)brushSize);
        LinearLayout.LayoutParams sbLp = new LinearLayout.LayoutParams(0, -2, 1f);
        sbSize.setLayoutParams(sbLp);
        sbSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar sb, int p, boolean u) {
                brushSize = Math.max(20, p);
                tvSize.setText("دقيق: " + (int)brushSize);
                            }
            public void onStartTrackingTouch(SeekBar sb) {}
            public void onStopTrackingTouch(SeekBar sb) {}
        });
        sliderRow.addView(sbSize);
        root.addView(sliderRow);

        // Tool buttons (BOTTOM)
        LinearLayout toolRow = new LinearLayout(this);
        toolRow.setOrientation(LinearLayout.HORIZONTAL);
        toolRow.setPadding(8, 8, 8, 4);
        toolRow.setBackgroundColor(0xFF2A2A2A);

        String[] toolNames = {tr("🖌️ ممحاة"), tr("🪄 سحرية"), tr("⬛ مستطيل"), tr("↩️ استعادة")};
        toolBtns = new Button[toolNames.length];
        for (int i = 0; i < toolNames.length; i++) {
            final int idx = i;
            Button b = new Button(this);
            b.setText(toolNames[i]);
            b.setTextSize(10);
            b.setTextColor(0xFFFFFFFF);
            b.setPadding(4, 4, 4, 4);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1f);
            lp.setMargins(3, 0, 3, 0);
            b.setLayoutParams(lp);
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) { selectTool(idx); }
            });
            toolRow.addView(b);
            toolBtns[i] = b;
        }
        root.addView(toolRow);

        // Action buttons
        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setPadding(12, 6, 12, 20);

        Button btnOffset = new Button(this);
        btnOffset.setText(tr("↕ إزاحة"));
        btnOffset.setTextColor(0xFFFFFFFF);
        btnOffset.setTextSize(12);
        btnOffset.setBackgroundResource(R.drawable.button_click_effect);
        btnOffset.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
        LinearLayout.LayoutParams olp = new LinearLayout.LayoutParams(0, -2, 1f);
        olp.setMargins(5, 0, 5, 0);
        btnOffset.setLayoutParams(olp);
        btnOffset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showOffsetDialog(); }
        });
        btnRow.addView(btnOffset);

        Button btnUndo = new Button(this);
        btnUndo.setText(tr("↶ تراجع"));
        btnUndo.setTextColor(0xFFFFFFFF);
        btnUndo.setTextSize(12);
        btnUndo.setBackgroundResource(R.drawable.button_click_effect);
        btnUndo.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(0, -2, 1f);
        blp.setMargins(5, 0, 5, 0);
        btnUndo.setLayoutParams(blp);
        btnUndo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { undo(); }
        });
        btnRow.addView(btnUndo);

        Button btnSave = new Button(this);
        btnSave.setText(tr("✔ حفظ"));
        btnSave.setTextColor(0xFFFFFFFF);
        btnSave.setTextSize(12);
        btnSave.setBackgroundResource(R.drawable.button_click_effect);
        btnSave.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
        LinearLayout.LayoutParams blp2 = new LinearLayout.LayoutParams(0, -2, 1f);
        blp2.setMargins(5, 0, 5, 0);
        btnSave.setLayoutParams(blp2);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { save(); }
        });
        btnRow.addView(btnSave);

        Button btnCancel = new Button(this);
        btnCancel.setText(tr("✕ إلغاء"));
        btnCancel.setTextColor(0xFFFFFFFF);
        btnCancel.setTextSize(12);
        btnCancel.setBackgroundResource(R.drawable.button_click_effect);
        btnCancel.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
        LinearLayout.LayoutParams blp3 = new LinearLayout.LayoutParams(0, -2, 1f);
        blp3.setMargins(5, 0, 5, 0);
        btnCancel.setLayoutParams(blp3);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        btnRow.addView(btnCancel);

        root.addView(btnRow);
        setContentView(root);

        selectTool(TOOL_ERASER);
                setupTouch();
    }

    private void selectTool(int tool) {
        currentTool = tool;
        for (int i = 0; i < toolBtns.length; i++) {
            if (i == tool) toolBtns[i].setBackgroundColor(0xFF4CAF50);
            else toolBtns[i].setBackgroundColor(0xFF455A64);
        }
        String[] tips = {tr("امسح بإصبعك"), tr("اضغط على اللون لمسحه"), tr("اسحب لتحديد المستطيل"), tr("ارسم بإصبعك لاستعادة ما مسحته")};
        Toast.makeText(this, tips[tool], Toast.LENGTH_SHORT).show();
    }

    private void highlightSizes(Button[] btns) {
        for (int i = 0; i < btns.length; i++) {
            if (Math.abs(sizeValues[i] - brushSize) < 15) btns[i].setBackgroundColor(0xFF4CAF50);
            else btns[i].setBackgroundColor(0xFF455A64);
        }
    }

    private void setupTouch() {
        imgView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {
                if (workingBitmap == null) return false;
                Matrix m = new Matrix();
                imgView.getImageMatrix().invert(m);
                float[] pts = {e.getX(), e.getY()};
                m.mapPoints(pts);
                float bmpX = pts[0];
                float bmpY = pts[1] - fingerOffset;

                // تثبيت الإحداثيات داخل الصورة
                if (bmpX < 0) bmpX = 0;
                if (bmpY < 0) bmpY = 0;
                if (bmpX >= workingBitmap.getWidth()) bmpX = workingBitmap.getWidth() - 1;
                if (bmpY >= workingBitmap.getHeight()) bmpY = workingBitmap.getHeight() - 1;

                int action = e.getActionMasked();

                if (action == MotionEvent.ACTION_DOWN) {
                    saveUndo();
                    if (currentTool == TOOL_MAGIC) {
                        magicWand((int)bmpX, (int)bmpY);
                        return true;
                    } else if (currentTool == TOOL_RECT) {
                        rectStartX = bmpX; rectStartY = bmpY;
                        rectEndX = bmpX; rectEndY = bmpY;
                        return true;
                    } else {
                        isDrawing = true;
                        lastBmpX = bmpX;
                        lastBmpY = bmpY;
                        drawAt(bmpX, bmpY);
                        return true;
                    }
                } else if (action == MotionEvent.ACTION_MOVE) {
                    if (currentTool == TOOL_RECT && rectStartX >= 0) {
                        rectEndX = bmpX; rectEndY = bmpY;
                        showRectPreview();
                        return true;
                    } else if (isDrawing) {
                        drawLine(lastBmpX, lastBmpY, bmpX, bmpY);
                        lastBmpX = bmpX;
                        lastBmpY = bmpY;
                        return true;
                    }
                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    if (currentTool == TOOL_RECT && rectStartX >= 0) {
                        applyRect(rectStartX, rectStartY, rectEndX, rectEndY);
                        rectStartX = -1;
                    }
                    isDrawing = false;
                    return true;
                }
                return false;
            }
        });
    }

    private void showRectPreview() {
        // رسم المستطيل على نسخة صغيرة مؤقتة (سريع جداً)
        if (workingBitmap == null) return;
        Bitmap temp = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        temp.setHasAlpha(true);
        Canvas c = new Canvas(temp);
        float l = Math.min(rectStartX, rectEndX);
        float t = Math.min(rectStartY, rectEndY);
        float r = Math.max(rectStartX, rectEndX);
        float b = Math.max(rectStartY, rectEndY);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(8);
        p.setColor(0xFFFFEB3B);
        c.drawRect(l, t, r, b, p);
        imgView.setImageBitmap(temp);
        // حفظ مرجع مؤقت حتى الاستبدال
        pendingPreview = temp;
    }

    private void applyRect(float x1, float y1, float x2, float y2) {
        Canvas c = new Canvas(workingBitmap);
        float l = Math.min(x1, x2);
        float t = Math.min(y1, y2);
        float r = Math.max(x1, x2);
        float b = Math.max(y1, y2);
        c.drawRect(l, t, r, b, eraserPaint);
        if (pendingPreview != null && !pendingPreview.isRecycled()) {
            pendingPreview.recycle();
            pendingPreview = null;
        }
        imgView.setImageBitmap(workingBitmap);
    }

    private void drawAt(float x, float y) {
        // حفظ الـ stroke
        if (strokeStack.size() >= MAX_STROKES) strokeStack.remove(0);
        strokeStack.push(new Stroke(x, y, x, y, brushSize, currentTool == TOOL_RESTORE));
        // رسم فوري
        Canvas c = new Canvas(workingBitmap);
        if (currentTool == TOOL_RESTORE) drawRestoreCircle(c, x, y, brushSize);
        else c.drawCircle(x, y, brushSize / 2f, eraserPaint);
        imgView.setImageBitmap(workingBitmap);
    }

    private void drawLine(float x1, float y1, float x2, float y2) {
        // حفظ الـ stroke
        if (strokeStack.size() >= MAX_STROKES) strokeStack.remove(0);
        strokeStack.push(new Stroke(x1, y1, x2, y2, brushSize, currentTool == TOOL_RESTORE));
        // رسم فوري
        Canvas c = new Canvas(workingBitmap);
        if (currentTool == TOOL_RESTORE) {
            drawRestoreSegment(c, x1, y1, x2, y2, brushSize);
        } else {
            float dx = x2 - x1, dy = y2 - y1;
            float dist = (float)Math.sqrt(dx*dx + dy*dy);
            float step = Math.max(1f, brushSize / 4f);
            int steps = (int)(dist / step) + 1;
            for (int i = 0; i <= steps; i++) {
                float t = i / (float)steps;
                c.drawCircle(x1 + dx * t, y1 + dy * t, brushSize / 2f, eraserPaint);
            }
        }
        imgView.setImageBitmap(workingBitmap);
    }

    private void drawRestoreCircle(Canvas c, float cx, float cy) {
        int radius = (int)(brushSize / 2);
        int left = Math.max(0, (int)(cx - radius));
        int top = Math.max(0, (int)(cy - radius));
        int right = Math.min(originalBitmap.getWidth(), (int)(cx + radius));
        int bottom = Math.min(originalBitmap.getHeight(), (int)(cy + radius));

        if (right <= left || bottom <= top) return;

        // قص المنطقة من الصورة الأصلية
        Bitmap src = Bitmap.createBitmap(originalBitmap, left, top, right - left, bottom - top);

        // إنشاء قناع دائري
        Bitmap piece = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        piece.setHasAlpha(true);
        Canvas pc = new Canvas(piece);
        pc.drawBitmap(src, 0, 0, null);

        // قص دائري (Mask)
        Paint clipPaint = new Paint();
        clipPaint.setAntiAlias(true);
        clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        pc.drawCircle(src.getWidth() / 2f, src.getHeight() / 2f, Math.min(src.getWidth(), src.getHeight()) / 2f, clipPaint);

        // رسم القطعة على الصورة الحالية
        c.drawBitmap(piece, left, top, null);

        src.recycle();
        piece.recycle();
    }

    private void magicWand(int x, int y) {
        if (x < 0 || y < 0 || x >= workingBitmap.getWidth() || y >= workingBitmap.getHeight()) return;

        final int origW = workingBitmap.getWidth();
        final int origH = workingBitmap.getHeight();

        // تصغير إلى 400 بكسل (أسرع)
        int maxDim = 400;
        float scale = 1f;
        int smallW = origW, smallH = origH;
        if (origW > maxDim || origH > maxDim) {
            scale = Math.min((float)maxDim / origW, (float)maxDim / origH);
            smallW = Math.max(1, (int)(origW * scale));
            smallH = Math.max(1, (int)(origH * scale));
        }

        // Nearest neighbor (أسرع 5x من الفلتر)
        Bitmap small = Bitmap.createScaledBitmap(workingBitmap, smallW, smallH, false);
        int total = smallW * smallH;
        int[] pixels = new int[total];
        small.getPixels(pixels, 0, smallW, 0, 0, smallW, smallH);

        int sx = Math.min(Math.max(0, (int)(x * scale)), smallW - 1);
        int sy = Math.min(Math.max(0, (int)(y * scale)), smallH - 1);

        int target = pixels[sy * smallW + sx];
        int tr = (target >> 16) & 0xFF;
        int tg = (target >> 8) & 0xFF;
        int tb = target & 0xFF;
        int tolerance = 60;
        int tolSq = tolerance * tolerance * 3;

        boolean[] visited = new boolean[total];
        int[] stack = new int[total];
        int top = 0;
        stack[top++] = sy * smallW + sx;

        while (top > 0) {
            int idx = stack[--top];
            if (visited[idx]) continue;
            visited[idx] = true;

            int cc = pixels[idx];
            int cr = (cc >> 16) & 0xFF;
            int cg = (cc >> 8) & 0xFF;
            int cb = cc & 0xFF;
            int dr = cr - tr, dg = cg - tg, db = cb - tb;
            if (dr*dr + dg*dg + db*db > tolSq) continue;

            pixels[idx] = 0x00000000;

            int px = idx % smallW;
            int py = idx / smallW;
            if (px > 0) stack[top++] = idx - 1;
            if (px < smallW - 1) stack[top++] = idx + 1;
            if (py > 0) stack[top++] = idx - smallW;
            if (py < smallH - 1) stack[top++] = idx + smallW;
        }

        small.setPixels(pixels, 0, smallW, 0, 0, smallW, smallH);

        // Nearest neighbor للرجوع (أسرع)
        Bitmap maskBig = Bitmap.createScaledBitmap(small, origW, origH, false);
        int[] maskPixels = new int[origW * origH];
        maskBig.getPixels(maskPixels, 0, origW, 0, 0, origW, origH);

        int[] origPixels = new int[origW * origH];
        workingBitmap.getPixels(origPixels, 0, origW, 0, 0, origW, origH);

        int count = 0;
        for (int i = 0; i < origPixels.length; i++) {
            if ((maskPixels[i] & 0xFF000000) == 0) {
                origPixels[i] = 0x00000000;
                count++;
            }
        }

        workingBitmap.setPixels(origPixels, 0, origW, 0, 0, origW, origH);
        small.recycle();
        maskBig.recycle();
        imgView.setImageBitmap(workingBitmap);
        Toast.makeText(this, "مسح " + count + " بكسل", Toast.LENGTH_SHORT).show();
    }

    private void saveUndo() { /* no-op, we use strokes */ }

    private void undo() {
        if (strokeStack.isEmpty()) {
            Toast.makeText(this, tr("لا يوجد تراجع"), Toast.LENGTH_SHORT).show();
            return;
        }
        strokeStack.pop();
        redrawFromScratch();
        Toast.makeText(this, "تم التراجع (" + strokeStack.size() + " متبقٍ)", Toast.LENGTH_SHORT).show();
    }

    private void redrawFromScratch() {
        // إعادة رسم الصورة من الأصل + كل الخطوات
        workingBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        workingBitmap.setHasAlpha(true);
        Canvas canvas = new Canvas(workingBitmap);

        for (Stroke s : strokeStack) {
            Paint paint;
            if (s.isRestore) {
                // استعادة - نرسم من الأصل
                drawRestoreSegment(canvas, s.x1, s.y1, s.x2, s.y2, s.size);
            } else {
                // مسح - نستخدم eraserPaint
                paint = new Paint();
                paint.setAntiAlias(true);
                paint.setColor(Color.TRANSPARENT);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                float dx = s.x2 - s.x1;
                float dy = s.y2 - s.y1;
                float dist = (float)Math.sqrt(dx*dx + dy*dy);
                float step = Math.max(1f, s.size / 4f);
                int steps = (int)(dist / step) + 1;
                for (int i = 0; i <= steps; i++) {
                    float t = i / (float)steps;
                    canvas.drawCircle(s.x1 + dx * t, s.y1 + dy * t, s.size / 2f, paint);
                }
            }
        }
        imgView.setImageBitmap(workingBitmap);
    }

    private void drawRestoreSegment(Canvas c, float x1, float y1, float x2, float y2, float size) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dist = (float)Math.sqrt(dx*dx + dy*dy);
        float step = Math.max(1f, size / 4f);
        int steps = (int)(dist / step) + 1;
        for (int i = 0; i <= steps; i++) {
            float t = i / (float)steps;
            drawRestoreCircle(c, x1 + dx * t, y1 + dy * t, size);
        }
    }

    private void drawRestoreCircle(Canvas c, float cx, float cy, float size) {
        try {
            int radius = (int)(size / 2);
            int left = Math.max(0, (int)(cx - radius));
            int top = Math.max(0, (int)(cy - radius));
            int right = Math.min(originalBitmap.getWidth(), (int)(cx + radius));
            int bottom = Math.min(originalBitmap.getHeight(), (int)(cy + radius));
            if (right <= left || bottom <= top) return;

            Bitmap src = Bitmap.createBitmap(originalBitmap, left, top, right - left, bottom - top);
            if (src == null) return;
            Bitmap piece = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
            if (piece == null) { src.recycle(); return; }
            piece.setHasAlpha(true);
            Canvas pc = new Canvas(piece);
            pc.drawBitmap(src, 0, 0, null);
            Paint clipPaint = new Paint();
            clipPaint.setAntiAlias(true);
            clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            pc.drawCircle(src.getWidth() / 2f, src.getHeight() / 2f, Math.min(src.getWidth(), src.getHeight()) / 2f, clipPaint);
            c.drawBitmap(piece, left, top, null);
            src.recycle();
            piece.recycle();
        } catch (Exception e) { }
    }

    private void save() {
        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "VideoEditorPro");
            if (!dir.exists()) dir.mkdirs();
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            File out = new File(dir, "cutout_" + ts + ".png");
            FileOutputStream fos = new FileOutputStream(out);
            workingBitmap.setHasAlpha(true);
            workingBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Intent result = new Intent();
            result.putExtra(EXTRA_RESULT_PATH, out.getAbsolutePath());
            setResult(Activity.RESULT_OK, result);
            Toast.makeText(this, "Saved: " + out.getName(), Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showOffsetDialog() {
        String[] options = {
            "بدون إزاحة (0)",
            "صغيرة (80)",
            "متوسطة (150)",
            "كبيرة (250)",
            "كبيرة جداً (400)"
        };
        final float[] values = {0, 80, 150, 250, 400};
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(tr("إزاحة الممحاة من الإصبع"))
            .setItems(options, (d, which) -> {
                fingerOffset = values[which];
                Toast.makeText(this, "الإزاحة: " + (int)fingerOffset + "px", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton(tr("إلغاء"), null)
            .show();
    }

}
