package com.rssj.basecore.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;

/**
 * Description: 今日头条适配方式
 * Data：2019/1/16-10:49
 * Author: cwh
 */
public final class ScreenAdpater {
    /**
     * 宽度适配模式
     */
    public final static int WIDTH_MATCH = 1;

    /**
     * 高度适配模式
     */
    public final static int HIGHT_MATCH = 2;

    /**
     * 适配DP单位
     */
    public final static int MACTH_UNIT_DP = 0;

    /**
     * 适配PT单位(没用过)
     */
    public final static int MATCH_UNIT_PT = 1;

    //原始MatchInfo
    private static MatchInfo mMatchInfo;

    //适配MiUI
    private static MatchInfo mMiUIMatchInfo;

    private static Application.ActivityLifecycleCallbacks lifecycleCallbacks;

    /**
     * 初始化获取原来MatchInfo 在Application中调用
     *
     * @param context
     */
    public static void init(Context context) {
        final DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        if (displayMetrics != null && mMatchInfo == null) {
            mMatchInfo = new MatchInfo();
            mMatchInfo.screenWidth = displayMetrics.widthPixels;
            mMatchInfo.screenHeight = displayMetrics.heightPixels;
            mMatchInfo.appDensity = displayMetrics.density;
            mMatchInfo.appDensityDpi = displayMetrics.densityDpi;
            //用于适配字体缩放
            mMatchInfo.appScaledDensity = displayMetrics.scaledDensity;
            mMatchInfo.appXdpi = displayMetrics.xdpi;
        }

        final DisplayMetrics displayMetricsOnMiUI = getMetricsOnMiui(context.getApplicationContext().getResources());
        if (displayMetricsOnMiUI != null && mMiUIMatchInfo == null) {
            mMiUIMatchInfo = new MatchInfo();
            mMiUIMatchInfo.screenWidth = displayMetricsOnMiUI.widthPixels;
            mMiUIMatchInfo.screenHeight = displayMetricsOnMiUI.heightPixels;
            mMiUIMatchInfo.appDensity = displayMetricsOnMiUI.density;
            mMiUIMatchInfo.appDensityDpi = displayMetricsOnMiUI.densityDpi;
            //用于适配字体缩放
            mMiUIMatchInfo.appScaledDensity = displayMetricsOnMiUI.scaledDensity;
            mMiUIMatchInfo.appXdpi = displayMetricsOnMiUI.xdpi;
        }
        //用具监听字体改变后
        context.registerComponentCallbacks(new ComponentCallbacks() {
            @Override
            public void onConfigurationChanged(Configuration newConfig) {
                if (newConfig != null && mMatchInfo != null) {
                    mMatchInfo.appScaledDensity = displayMetrics.scaledDensity;
                    if (displayMetricsOnMiUI != null && mMiUIMatchInfo != null) {
                        mMiUIMatchInfo.appScaledDensity = displayMetricsOnMiUI.scaledDensity;
                    }
                }
            }

            @Override
            public void onLowMemory() {
            }
        });
        Log.d("Screen", "match showErrorView is :" + mMatchInfo.toString());
        if (mMiUIMatchInfo != null) {
            Log.d("Screen", "MUIMatch showErrorView is :" + mMiUIMatchInfo.toString());
        }
    }

    /**
     * 注册lifecyle监听，在activity create时，调用适配方法
     * <p>
     * 在Application 中调用
     *
     * @param application
     * @param designBase  设计基于的基础 尺寸
     * @param matchBase   基于 宽 或 高 适配
     * @param matchUnit   单位
     */
    public static void register(Application application, final int designBase, final int matchBase, final int matchUnit) {
        if (lifecycleCallbacks == null) {
            lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    //activity 适配时，执行适配方法
                    if (activity != null) {
                        match(activity, designBase, matchBase, matchUnit);
                        Log.d("Screen", "Activity name is:" + activity.getClass().getSimpleName());
                    }
                }

                @Override
                public void onActivityStarted(Activity activity) {
                }

                @Override
                public void onActivityResumed(Activity activity) {
                }

                @Override
                public void onActivityPaused(Activity activity) {
                }

                @Override
                public void onActivityStopped(Activity activity) {
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                }
            };
        }
        application.registerActivityLifecycleCallbacks(lifecycleCallbacks);
    }

    /**
     * 在Applictaion 中调用
     *
     * @param application
     */
    public static void unregister(Application application, int... matchUnits) {
        if (lifecycleCallbacks != null) {
            application.unregisterActivityLifecycleCallbacks(lifecycleCallbacks);
            lifecycleCallbacks = null;
        }
        for (int unit : matchUnits) {
            cancelMach(application, unit);
        }
    }


    private static void matchDP(MatchInfo matchInfo, DisplayMetrics displayMetrics, int matchBase, int designBase) {
        float targetDensity;
        if (matchBase == WIDTH_MATCH) {
            targetDensity = matchInfo.screenWidth * 1.0f / designBase;
        } else if (matchBase == HIGHT_MATCH) {
            targetDensity = matchInfo.screenHeight * 1.0f / designBase;
        } else {
            targetDensity = matchInfo.screenWidth * 1.0f / designBase;
        }
        displayMetrics.density = targetDensity;
        displayMetrics.densityDpi = (int) (targetDensity * 160);
        displayMetrics.scaledDensity = targetDensity * (matchInfo.getAppScaledDensity() / matchInfo.getAppDensity());
        Log.d("Screen", "displayMetrics :" + displayMetrics.toString());
    }

    //pt * metrics.xdpi * (1.0f/72)
    private static void matchPt(MatchInfo matchInfo, DisplayMetrics displayMetrics, int matchBase, int designBase) {
        float targetXdpi;
        if (matchBase == WIDTH_MATCH) {
            targetXdpi = matchInfo.screenWidth * 72.0f / designBase;
        } else if (matchBase == HIGHT_MATCH) {
            targetXdpi = matchInfo.screenHeight * 72.0f / designBase;
        } else {
            targetXdpi = matchInfo.screenWidth * 72.0f / designBase;
        }
        displayMetrics.xdpi = targetXdpi;
    }

    private static void matchDP(Context context, int matchBase, int designBase) {
        //这里要根据对activity的resource和application的resource 都做适配，否者会不是全部生效
        // https://github.com/JessYanCoding/AndroidAutoSize   me.jessyan.autosize.utils.AutoSize
        if (mMatchInfo != null) {
            DisplayMetrics displayMetrics = null;
            if (context instanceof Activity) {
                displayMetrics = ((Activity) context).getResources().getDisplayMetrics();
            } else {
                displayMetrics = context.getResources().getDisplayMetrics();
            }
            if (displayMetrics != null) {
                matchDP(mMatchInfo, displayMetrics, matchBase, designBase);
            }
        }
        if (mMiUIMatchInfo != null) {
            DisplayMetrics displayMetricsOnMiUI = null;
            if (context instanceof Activity) {
                displayMetricsOnMiUI = getMetricsOnMiui(((Activity) context).getResources());
            } else {
                displayMetricsOnMiUI = getMetricsOnMiui(context.getResources());
            }
            if (displayMetricsOnMiUI != null) {
                matchDP(mMiUIMatchInfo, displayMetricsOnMiUI, matchBase, designBase);
            }
        }
    }

    private static void matchPT(Context context, int matchBase, int designBase) {
        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        if (displayMetrics != null && mMatchInfo != null) {
            matchPt(mMatchInfo, displayMetrics, matchBase, designBase);
        }
        if (mMiUIMatchInfo != null) {
            DisplayMetrics displayMetricsOnMiUI = getMetricsOnMiui(context.getApplicationContext().getResources());
            if (displayMetricsOnMiUI != null) {
                matchPt(mMiUIMatchInfo, displayMetricsOnMiUI, matchBase, designBase);
            }
        }
    }

    /**
     * 调用该方法，适配指定单位和以宽 或 高为基准适配
     */
    public static void match(@NonNull Context context, int designBase, int matchBase, int matchUnit) {
        if (designBase == 0) {
            throw new UnsupportedOperationException("The designSize cannot be equal to 0");
        }
        if (matchUnit == MACTH_UNIT_DP) {
            matchDP(context, matchBase, designBase);
        } else if (matchUnit == MATCH_UNIT_PT) {
            matchPT(context, matchBase, designBase);
        } else {
            matchDP(context, matchBase, designBase);
        }
    }

    /**
     * 以DP为单位，适配
     *
     * @param context
     * @param designBase
     * @param matchBase
     */
    public static void match(@NonNull Context context, int designBase, int matchBase) {
        match(context, designBase, matchBase, MACTH_UNIT_DP);
    }

    /**
     * 以DP为单位，宽为基准
     *
     * @param context
     * @param designBase
     */
    public static void match(@NonNull Context context, int designBase) {
        match(context, designBase, WIDTH_MATCH, MACTH_UNIT_DP);
    }


    public static void cancelMatch(@NonNull Context context) {
        cancelMach(context, MACTH_UNIT_DP);
        cancelMach(context, MATCH_UNIT_PT);
    }

    public static void cancelMach(@NonNull Context context, int matchUnit) {
        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        if (displayMetrics != null && mMatchInfo != null) {
            cancelMatch(mMatchInfo, displayMetrics, matchUnit);
        }

        if (mMiUIMatchInfo != null) {
            DisplayMetrics displayMetricsOmMiUI = getMetricsOnMiui(context.getApplicationContext().getResources());
            if (displayMetricsOmMiUI != null) {
                cancelMatch(mMiUIMatchInfo, displayMetricsOmMiUI, matchUnit);
            }
        }

    }

    private static void cancelMatch(MatchInfo matchInfo, @NonNull DisplayMetrics displayMetrics, int matchUnit) {
        if (matchUnit == MACTH_UNIT_DP) {
            if (matchInfo.appDensity != 0 && displayMetrics.density != matchInfo.appDensity) {
                displayMetrics.density = matchInfo.appDensity;
            }

            if (matchInfo.appDensityDpi != 0 && displayMetrics.densityDpi != matchInfo.appDensityDpi) {
                displayMetrics.densityDpi = (int) matchInfo.appDensityDpi;
            }

            if (matchInfo.appScaledDensity != 0 && displayMetrics.scaledDensity != matchInfo.appScaledDensity) {
                displayMetrics.scaledDensity = (int) matchInfo.appScaledDensity;
            }
        } else if (matchUnit == MATCH_UNIT_PT) {
            if (matchInfo.appXdpi != 0 && displayMetrics.xdpi != matchInfo.appXdpi) {
                displayMetrics.xdpi = matchInfo.appXdpi;
            }
        }
    }

    /**
     * 解决 MIUI 更改框架导致的 MIUI7 + Android5.1.1 上出现的失效问题 (以及极少数基于这部分 MIUI 去掉 ART 然后置入 XPosed 的手机)
     * 来源于: https://github.com/Firedamp/Rudeness/blob/master/rudeness-sdk/src/main/java/com/bulong/rudeness/RudenessScreenHelper.java#L61:5
     *
     * @param resources {@link Resources}
     * @return {@link DisplayMetrics}, 可能为 {@code null}
     */
    private static DisplayMetrics getMetricsOnMiui(Resources resources) {
        if ("MiuiResources".equals(resources.getClass().getSimpleName()) || "XResources".equals(resources.getClass().getSimpleName())) {
            try {
                Field field = Resources.class.getDeclaredField("mTmpMetrics");
                field.setAccessible(true);
                return (DisplayMetrics) field.get(resources);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }


    /**
     * Adaptation information
     */
    public static class MatchInfo {
        private int screenWidth;
        private int screenHeight;
        private float appDensity;
        private float appDensityDpi;
        private float appScaledDensity;
        private float appXdpi;

        public int getScreenWidth() {
            return screenWidth;
        }

        public void setScreenWidth(int screenWidth) {
            this.screenWidth = screenWidth;
        }

        public int getScreenHeight() {
            return screenHeight;
        }

        public void setScreenHeight(int screenHeight) {
            this.screenHeight = screenHeight;
        }

        public float getAppDensity() {
            return appDensity;
        }

        public void setAppDensity(float appDensity) {
            this.appDensity = appDensity;
        }

        public float getAppDensityDpi() {
            return appDensityDpi;
        }

        public void setAppDensityDpi(float appDensityDpi) {
            this.appDensityDpi = appDensityDpi;
        }

        public float getAppScaledDensity() {
            return appScaledDensity;
        }

        public void setAppScaledDensity(float appScaledDensity) {
            this.appScaledDensity = appScaledDensity;
        }

        public float getAppXdpi() {
            return appXdpi;
        }

        public void setAppXdpi(float appXdpi) {
            this.appXdpi = appXdpi;
        }

        @Override
        public String toString() {
            return "MatchInfo{" +
                    "screenWidth=" + screenWidth +
                    ", screenHeight=" + screenHeight +
                    ", appDensity=" + appDensity +
                    ", appDensityDpi=" + appDensityDpi +
                    ", appScaledDensity=" + appScaledDensity +
                    ", appXdpi=" + appXdpi +
                    '}';
        }
    }

}
