package com.videocraft.pro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private int currentLang = Translations.LANG_AR;
    private void loadLang() {
        currentLang = getSharedPreferences("app_prefs", MODE_PRIVATE).getInt("lang", Translations.LANG_AR);
    }
    public String tr(String ar) {
        return Translations.tr(currentLang, ar);
    }

    private List<File> videos = new ArrayList<File>();
    private GridView grid;
    private VideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLang();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF0F0F0F);

        TextView title = new TextView(this);
        title.setText(tr("المعرض"));
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(22);
        title.setPadding(20, 40, 20, 20);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView hint = new TextView(this);
        hint.setText(tr("اضغط طويلا للمشاركة او الحذف"));
        hint.setTextColor(0xFFB0B0B0);
        hint.setTextSize(12);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(20, 0, 20, 20);
        root.addView(hint);

        Button btnDeleteAll = new Button(this);
        btnDeleteAll.setText("\uD83D\uDDD1\uFE0F \u062d\u0630\u0641 \u0627\u0644\u0643\u0644");
        btnDeleteAll.setBackgroundResource(R.drawable.button_click_effect);
        btnDeleteAll.setStateListAnimator(android.animation.AnimatorInflater.loadStateListAnimator(this, R.animator.button_bounce));
        btnDeleteAll.setTextColor(0xFFFFFFFF);
        LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(-1, -2);
        dlp.setMargins(30, 5, 30, 15);
        btnDeleteAll.setLayoutParams(dlp);
        btnDeleteAll.setOnClickListener(v -> confirmDeleteAll());
        root.addView(btnDeleteAll);

        grid = new GridView(this);
        grid.setNumColumns(2);
        grid.setPadding(10, 10, 10, 10);
        grid.setHorizontalSpacing(10);
        grid.setVerticalSpacing(10);
        root.addView(grid, new LinearLayout.LayoutParams(-1, 0, 1f));

        setContentView(root);

        adapter = new VideoAdapter();
        grid.setAdapter(adapter);

        grid.setOnItemClickListener((parent, view, position, id) -> {
            File f = videos.get(position);
            try {
                Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", f);
                Intent i = new Intent(Intent.ACTION_VIEW);
                String ln = f.getName().toLowerCase();
                String mime;
                if (ln.endsWith(".mp3")) mime = "audio/*";
                else if (ln.endsWith(".png") || ln.endsWith(".jpg") || ln.endsWith(".jpeg")) mime = "image/*";
                else mime = "video/*";
                i.setDataAndType(uri, mime);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(i, tr("تشغيل")));
            } catch (Exception e) {
                Toast.makeText(this, "خطأ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        grid.setOnItemLongClickListener((parent, view, position, id) -> {
            File f = videos.get(position);
            String[] opts = {tr("مشاركة"), tr("حذف")};
            AlertDialog dlg1 = new AlertDialog.Builder(this, R.style.PurpleDialog)
                .setTitle(f.getName())
                .setItems(opts, (d, w) -> {
                    if (w == 0) shareFile(f);
                    else deleteFile(f);
                }).create();
            dlg1.setOnShowListener(d -> {
                int titleId = getResources().getIdentifier("alertTitle", "id", "android");
                android.widget.TextView titleTv = dlg1.findViewById(titleId);
                if (titleTv != null) titleTv.setTextColor(0xFFFFFFFF);
                android.widget.ListView lv = dlg1.getListView();
                if (lv != null) {
                    for (int i = 0; i < lv.getChildCount(); i++) {
                        android.view.View child = lv.getChildAt(i);
                        if (child instanceof android.widget.TextView) {
                            ((android.widget.TextView) child).setTextColor(0xFFFFFFFF);
                        }
                    }
                    lv.setOnHierarchyChangeListener(new android.view.ViewGroup.OnHierarchyChangeListener() {
                        public void onChildViewAdded(android.view.View parent, android.view.View child) {
                            if (child instanceof android.widget.TextView) {
                                ((android.widget.TextView) child).setTextColor(0xFFFFFFFF);
                            }
                        }
                        public void onChildViewRemoved(android.view.View parent, android.view.View child) {}
                    });
                }
            });
            dlg1.show();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadVideos();
    }

    private void loadVideos() {
        videos.clear();
        File dir1 = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "VideoEditorPro");
        File dir2 = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "VideoEditorPro");
        File dir3 = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "VideoEditorPro");
        if (!dir3.exists()) dir3.mkdirs();
        if (!dir1.exists()) dir1.mkdirs();
        if (!dir2.exists()) dir2.mkdirs();
        java.util.List<File> allDirs = new java.util.ArrayList<File>();
        allDirs.add(dir1);
        allDirs.add(dir2);
        allDirs.add(dir3);
        for (File currentDir : allDirs) {
            if (currentDir.exists()) {
                File[] files = currentDir.listFiles((folder, name) -> {
                    String n = name.toLowerCase();
                    return n.endsWith(".mp4") || n.endsWith(".mp3") || n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".gif");
                });
                if (files != null) {
                    Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
                    videos.addAll(Arrays.asList(files));
                }
            }
        }
        adapter.notifyDataSetChanged();
        if (videos.isEmpty()) {
            Toast.makeText(this, tr("لا توجد فيديوهات محفوظة بعد"), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareFile(File f) {
        try {
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", f);
            Intent i = new Intent(Intent.ACTION_SEND);
            String fn = f.getName().toLowerCase();
            String mime2;
            if (fn.endsWith(".mp3")) mime2 = "audio/*";
            else if (fn.endsWith(".png") || fn.endsWith(".jpg") || fn.endsWith(".jpeg")) mime2 = "image/*";
            else mime2 = "video/*";
            i.setType(mime2);
            i.putExtra(Intent.EXTRA_STREAM, uri);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(i, tr("مشاركة")));
        } catch (Exception e) {
            Toast.makeText(this, "خطأ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteFile(final File f) {
        AlertDialog dlg2 = new AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(tr("حذف"))
            .setMessage(tr("هل تريد حذف") + " " + f.getName() + "؟")
            .setPositiveButton(tr("حذف"), (d, w) -> {
                if (f.delete()) {
                    Toast.makeText(this, tr("تم الحذف"), Toast.LENGTH_SHORT).show();
                    loadVideos();
                } else {
                    Toast.makeText(this, tr("فشل الحذف"), Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton(tr("إلغاء"), null)
            .create();
        dlg2.setOnShowListener(d -> {
            dlg2.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFFFFFFFF);
            dlg2.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFFFFFFFF);
            int titleId = getResources().getIdentifier("alertTitle", "id", "android");
            android.widget.TextView titleTv = dlg2.findViewById(titleId);
            if (titleTv != null) titleTv.setTextColor(0xFFFFFFFF);
            android.widget.TextView msgTv = dlg2.findViewById(android.R.id.message);
            if (msgTv != null) msgTv.setTextColor(0xFFFFFFFF);
        });
        dlg2.show();
    }

    private void confirmDeleteAll() {
        if (videos.isEmpty()) {
            Toast.makeText(this, tr("لا توجد ملفات لحذفها"), Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(tr("حذف الكل"))
            .setMessage("هل تريد حذف " + videos.size() + " ملف؟ لا يمكن التراجع!")
            .setPositiveButton(tr("حذف الكل"), (d, w) -> {
                int count = 0;
                for (File f : videos) {
                    if (f.delete()) count++;
                }
                Toast.makeText(this, "تم حذف " + count + " ملف", Toast.LENGTH_SHORT).show();
                loadVideos();
            })
            .setNegativeButton(tr("إلغاء"), null)
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

    class VideoAdapter extends BaseAdapter {
        @Override public int getCount() { return videos.size(); }
        @Override public Object getItem(int i) { return videos.get(i); }
        @Override public long getItemId(int i) { return i; }

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            File f = videos.get(pos);
            String lname = f.getName().toLowerCase();
            boolean isImage = lname.endsWith(".png") || lname.endsWith(".jpg") || lname.endsWith(".jpeg") || lname.endsWith(".gif");

            if (view == null) {
                LinearLayout ll = new LinearLayout(GalleryActivity.this);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setBackgroundColor(isImage ? 0xFF2E7D32 : 0xFF1E1E1E);
                ll.setPadding(8, 8, 8, 8);

                ImageView iv = new ImageView(GalleryActivity.this);
                iv.setId(1001);
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LinearLayout.LayoutParams ivp = new LinearLayout.LayoutParams(-1, isImage ? 220 : 400);
                iv.setLayoutParams(ivp);
                ll.addView(iv);

                TextView tv = new TextView(GalleryActivity.this);
                tv.setId(1002);
                tv.setTextColor(0xFFFFFFFF);
                tv.setTextSize(isImage ? 9 : 11);
                tv.setPadding(5, 8, 5, 5);
                tv.setMaxLines(2);
                ll.addView(tv);

                view = ll;
            } else {
                LinearLayout ll = (LinearLayout) view;
                ll.setBackgroundColor(isImage ? 0xFF2E7D32 : 0xFF1E1E1E);
                ImageView iv = view.findViewById(1001);
                LinearLayout.LayoutParams ivp = (LinearLayout.LayoutParams) iv.getLayoutParams();
                ivp.height = isImage ? 220 : 400;
                iv.setLayoutParams(ivp);
                TextView tv = view.findViewById(1002);
                tv.setTextSize(isImage ? 9 : 11);
            }

            ImageView iv = view.findViewById(1001);
            TextView tv = view.findViewById(1002);
            tv.setText(f.getName());

            if (isImage) {
                try {
                    android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeFile(f.getAbsolutePath());
                    if (bmp != null) iv.setImageBitmap(bmp);
                    else iv.setBackgroundColor(0xFF6D4C41);
                } catch (Exception e) { iv.setBackgroundColor(0xFF6D4C41); }
            } else if (lname.endsWith(".gif")) {
                try {
                    java.io.InputStream is = new java.io.FileInputStream(f);
                    android.graphics.drawable.Drawable d = android.graphics.drawable.Drawable.createFromStream(is, "gif");
                    iv.setImageDrawable(d);
                    is.close();
                } catch (Exception e) { iv.setBackgroundColor(0xFFD84315); }
            } else if (lname.endsWith(".mp3")) {
                iv.setImageBitmap(null);
                iv.setBackgroundColor(0xFF9C27B0);
            } else {
                iv.setImageBitmap(null);
                iv.setBackgroundColor(0xFF000000);
                new LoadThumb(iv).execute(f);
            }
            return view;
        }
    }

    class LoadThumb extends AsyncTask<File, Void, Bitmap> {
        ImageView iv;
        LoadThumb(ImageView iv) { this.iv = iv; }
        @Override
        protected Bitmap doInBackground(File... files) {
            try {
                MediaMetadataRetriever r = new MediaMetadataRetriever();
                r.setDataSource(files[0].getAbsolutePath());
                Bitmap b = r.getFrameAtTime(1000000);
                r.release();
                return b;
            } catch (Exception e) { return null; }
        }
        @Override
        protected void onPostExecute(Bitmap b) {
            if (b != null && iv != null) iv.setImageBitmap(b);
        }
    }
}
