package com.Rseo.Ads;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.Rseo.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.Date;
import java.util.Locale;


public class AdmobAds {

    private static final String TAG = "AdsAdmob";
    private static final String AD_UNIT_NATIVE_EXIT = "ca-app-pub-3940256099942544/2247696110";
    private static final String AD_UNIT_NATIVE_NORMAL = "ca-app-pub-3940256099942544/2247696110";
    private static final String AD_UNIT_INTER = "ca-app-pub-3940256099942544/1033173712";
    private InterstitialAd mInterstitialAd;
    private  AppCompatActivity mAtcitivity;
    private NativeAd nativeAds ;

    public AdmobAds(AppCompatActivity activity,NativeAd nativeAds) {
        this.mAtcitivity = activity ;
        this.nativeAds = nativeAds ;

        MobileAds.initialize(activity, initializationStatus -> {});

        loadInterstitiel();

    }


    private void loadInterstitiel(){
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(mAtcitivity,AD_UNIT_INTER, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.d(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });

    }

    public void interShow(final AdFinish adFinish){
        if (mInterstitialAd != null ) {
            mInterstitialAd.show(mAtcitivity);

            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    adFinish.adFinished();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    adFinish.adFinished();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    mInterstitialAd = null;
                    loadInterstitiel();
                }

                @Override
                public void onAdClicked() {
                    Log.d(TAG, "Ad was clicked.");
                }
                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.");
                }

            });
        } else {
            adFinish.adFinished();
        }


    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getMediaContent().getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (nativeAd.getMediaContent() != null && nativeAd.getMediaContent().hasVideoContent()) {

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(
                    new VideoController.VideoLifecycleCallbacks() {
                        @Override
                        public void onVideoEnd() {
                            // Publishers should allow native ads to complete video playback before
                            // refreshing or replacing them with another ad in the same UI location.
                            super.onVideoEnd();
                        }
                    });
        }
    }

    public void refreshAd(FrameLayout frameLayout) {
        AdLoader.Builder builder = new AdLoader.Builder(mAtcitivity, AD_UNIT_NATIVE_NORMAL);

        builder.forNativeAd(
                new NativeAd.OnNativeAdLoadedListener() {
                    // OnLoadedListener implementation.
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        // If this callback occurs after the activity is destroyed, you must call
                        // destroy and return or you may get a memory leak.
                        boolean isDestroyed = false;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            isDestroyed = mAtcitivity.isDestroyed();
                        }
                        if (isDestroyed || mAtcitivity.isFinishing() || mAtcitivity.isChangingConfigurations()) {
                            nativeAd.destroy();
                            return;
                        }
                        // You must call destroy on old ads when you are done with them,
                        // otherwise you will have a memory leak.
                        if (nativeAds != null) {
                            nativeAds.destroy();
                        }
                        nativeAds = nativeAd;
                        NativeAdView adView =
                                (NativeAdView) mAtcitivity.getLayoutInflater().inflate(R.layout.ad_unified, frameLayout, false);
                        populateNativeAdView(nativeAd, adView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);
                    }
                });

        VideoOptions videoOptions =
                new VideoOptions.Builder().setStartMuted(false).build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader =
                builder
                        .withAdListener(
                                new AdListener() {
                                    @Override
                                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                                        String error =
                                                String.format(
                                                        Locale.getDefault(),
                                                        "domain: %s, code: %d, message: %s",
                                                        loadAdError.getDomain(),
                                                        loadAdError.getCode(),
                                                        loadAdError.getMessage());

                                    }
                                })
                        .build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }

    public void initializeNativeExit(){
        AdLoader.Builder builder = new AdLoader.Builder(mAtcitivity,AD_UNIT_NATIVE_EXIT);
        builder.withAdListener(new AdListener(){
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }
        });

        builder.forNativeAd(NativeAd -> {
            this.nativeAds = NativeAd;
        });
        AdLoader adLoader =builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    public NativeAd getNativeAds(){
        return this.nativeAds;
    }




    public interface AdFinish {
        void adFinished();
    }

}
