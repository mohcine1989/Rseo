package com.Rseo.Ads;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback;
import java.util.Date;

public class MyApplication extends Application implements ActivityLifecycleCallbacks, DefaultLifecycleObserver {


    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;
    private static final String TAG = "MyApplication";
    private boolean isReturningFromBackground = false;
    private int activityReferences = 0;
    private boolean isChangingConfigurations = false;

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        MobileAds.initialize(this, initializationStatus -> {});
        appOpenAdManager = new AppOpenAdManager();
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);

        // Afficher l'annonce uniquement si l'application revient de l'arrière-plan
        if (isReturningFromBackground && currentActivity != null && !appOpenAdManager.isShowingAd) {
            isReturningFromBackground = false; // Réinitialiser après affichage
            appOpenAdManager.showAdIfAvailable(currentActivity);
        }

    }




    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;

        // Si aucune activité n'était active, l'application revient de l'arrière-plan
        if (++activityReferences == 1 && !isChangingConfigurations) {
            isReturningFromBackground = true; // Application revient de l'arrière-plan
        } else {
            isReturningFromBackground = false; // Transition entre activités
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {}

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        isChangingConfigurations = activity.isChangingConfigurations();

        // Réduire le compteur des activités actives
        if (--activityReferences == 0) {
            isReturningFromBackground = false; // L'application passe en arrière-plan
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {}


    public void loadAd(@NonNull Activity activity) {
        appOpenAdManager.loadAd(activity);
    }


    public void showAdIfAvailable(@NonNull Activity activity, @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener);
    }

    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }


    private class AppOpenAdManager {

        private static final String LOG_TAG = "AppOpenAdManager";
        private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921";

        private AppOpenAd appOpenAd = null;
        private boolean isLoadingAd = false;
        private boolean isShowingAd = false;

        private long loadTime = 0;

        public AppOpenAdManager() {}

        private void loadAd(Context context) {
            // Do not load ad if there is an unused ad or one is already loading.
            if (isLoadingAd || isAdAvailable()) {
                return;
            }

            isLoadingAd = true;
            AdRequest request = new AdRequest.Builder().build();
            AppOpenAd.load(
                    context,
                    AD_UNIT_ID,
                    request,
                    new AppOpenAdLoadCallback() {
                        @Override
                        public void onAdLoaded(AppOpenAd ad) {
                            appOpenAd = ad;
                            isLoadingAd = false;
                            loadTime = (new Date()).getTime();

                            Log.d(LOG_TAG, "onAdLoaded.");
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            isLoadingAd = false;
                            Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
                        }
                    });
        }

        private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
            long dateDifference = (new Date()).getTime() - loadTime;
            long numMilliSecondsPerHour = 3600000;
            return (dateDifference < (numMilliSecondsPerHour * numHours));
        }

        private boolean isAdAvailable() {
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
        }

        private void showAdIfAvailable(@NonNull final Activity activity) {
            showAdIfAvailable(
                    activity,
                    new OnShowAdCompleteListener() {
                        @Override
                        public void onShowAdComplete() {
                            // Empty because the user will go back to the activity that shows the ad.
                        }
                    });
        }

        private void showAdIfAvailable(
                @NonNull final Activity activity,
                @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
            // If the app open ad is already showing, do not show the ad again.
            if (isShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.");
                return;
            }

            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable()) {
                Log.d(LOG_TAG, "The app open ad is not ready yet.");
                onShowAdCompleteListener.onShowAdComplete();
                loadAd(currentActivity);
                return;
            }

            Log.d(LOG_TAG, "Will show ad.");

            appOpenAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        /** Called when full screen content is dismissed. */
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            appOpenAd = null;
                            isShowingAd = false;

                            Log.d(LOG_TAG, "onAdDismissedFullScreenContent.");

                            onShowAdCompleteListener.onShowAdComplete();
                            loadAd(currentActivity);
                        }
 
                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            appOpenAd = null;
                            isShowingAd = false;

                            Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());

                            onShowAdCompleteListener.onShowAdComplete();
                            loadAd(currentActivity);
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            Log.d(LOG_TAG, "onAdShowedFullScreenContent.");
                        }
                    });

            isShowingAd = true;
            appOpenAd.show(activity);
        }
    }
}