package com.videocraft.pro;
import java.util.ArrayList;
import java.util.List;

public class ProjectState {
    public static String originalVideoPath = null;
    
    public static class Overlay {
        public String path; public int x, y, w, h;
        public Overlay(String path, int x, int y, int w, int h) {
            this.path = path; this.x = x; this.y = y; this.w = w; this.h = h;
        }
    }
    
    public static List<Overlay> overlays = new ArrayList<>();
    public static List<String> filters = new ArrayList<>();
    public static String cropFilter = null;
    public static float trimStart = -1;
    public static float trimEnd = -1;
    public static int pendingCount = 0;
    
    public static boolean muteAudio = false;
    public static String musicPath = null;
    public static int musicMergeType = 3;
    public static String audioFilter = null;
    public static float videoSpeed = 1.0f;
    public static boolean compressVideo = false;
    public static String targetResolution = "1080";
    public static boolean qualityChanged = false;
    
    public static List<String> slideshowImages = new ArrayList<>();
    public static String slideshowMusic = null;
    public static int repeatCount = 1;
    
    public static String swapAudioPath = null;
    public static int swapAudioType = 0;
    
    public static String pipVideoPath = null;
    public static int pipX = 0, pipY = 0, pipW = 200, pipH = 200;
    public static int pipAudioType = 0;
    public static boolean toMp3 = false;
    
    public static boolean hasSocialSize = false;
    public static int socialWidth = 0;
    public static int socialHeight = 0;
    
    public static int splitCount = 0;
    public static boolean toGif = false;
    public static int gifPreset = 0;
    
    public static boolean hasManualResize = false;
    public static int manualW = 0;
    public static int manualH = 0;
    public static String manualMode = "pad";
    
    public static boolean removeSilence = false;
    public static float silenceDuration = 0.5f;
    public static String duckMusicPath = null;
    
    public static boolean isCollage = false;
    public static String collageMode = "";
    public static List<String> collagePaths = new ArrayList<>();
    public static int collageAudioChoice = 0;
    
    public static boolean isMerging = false;
    public static List<String> mergePaths = new ArrayList<>();
    
    public static int getPendingCount() {
        int count = overlays.size() + filters.size();
        if (cropFilter != null) count++;
        if (trimStart >= 0) count++;
        if (musicPath != null) count++;
        if (audioFilter != null) count++;
        if (videoSpeed != 1.0f) count++;
        if (compressVideo) count++;
        if (muteAudio) count++;
        if (qualityChanged) count++;
        if (!slideshowImages.isEmpty()) count++;
        if (repeatCount > 1) count++;
        if (swapAudioPath != null) count++;
        if (pipVideoPath != null) count++;
        if (toMp3) count++;
        if (hasSocialSize) count++;
        if (splitCount > 1) count++;
        if (toGif) count++;
        if (hasManualResize) count++;
        if (removeSilence) count++;
        if (duckMusicPath != null) count++;
        if (isCollage) count++;
        if (isMerging) count++;
        return count;
    }
    
    public static void reset() {
        originalVideoPath = null;
        overlays.clear();
        filters.clear();
        cropFilter = null;
        pendingCount = 0;
        trimStart = -1;
        trimEnd = -1;
        muteAudio = false;
        musicPath = null;
        musicMergeType = 3;
        audioFilter = null;
        videoSpeed = 1.0f;
        compressVideo = false;
        targetResolution = "1080";
        qualityChanged = false;
        slideshowImages.clear();
        slideshowMusic = null;
        repeatCount = 1;
        swapAudioPath = null;
        swapAudioType = 0;
        pipVideoPath = null;
        pipX = 0; pipY = 0; pipW = 200; pipH = 200;
        pipAudioType = 0; toMp3 = false;
        hasSocialSize = false;
        socialWidth = 0;
        socialHeight = 0;
        splitCount = 0;
        toGif = false;
        gifPreset = 0;
        hasManualResize = false;
        manualW = 0;
        manualH = 0;
        manualMode = "pad";
        removeSilence = false;
        silenceDuration = 0.5f;
        duckMusicPath = null;
        isCollage = false;
        collageMode = "";
        collagePaths.clear();
        collageAudioChoice = 0;
        isMerging = false;
        mergePaths.clear();
    }
}
