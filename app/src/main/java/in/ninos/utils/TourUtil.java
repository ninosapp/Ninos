package in.ninos.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;

import in.ninos.R;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

/**
 * Created by FAMILY on 20-02-2018.
 */

public class TourUtil {
    private Activity activity;

    public TourUtil(Activity activity) {
        this.activity = activity;
    }

    public void showHomePrompt() {

        if (activity != null && !activity.isFinishing()) {
            boolean isTourShown = PreferenceUtil.isTourShown(activity);

            if (!isTourShown) {
                new MaterialTapTargetPrompt.Builder(activity)
                        .setPrimaryText(R.string.welcome)
                        .setSecondaryText(R.string.add_welcome_prompt_description)
                        .setAnimationInterpolator(new FastOutSlowInInterpolator())
                        .setTarget(R.id.iv_home)
                        .setCaptureTouchEventOnFocal(true)
                        .setAutoDismiss(false)
                        .setCaptureTouchEventOutsidePrompt(true)
                        .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                            @Override
                            public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state) {
                                if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                                    showAddPostPrompt();
                                }
                            }
                        })
                        .setBackgroundColour(ContextCompat.getColor(activity, R.color.accent_transparent))
                        .show();
            }
        }
    }

    private void showAddPostPrompt() {
        if (activity != null && !activity.isFinishing()) {
            new MaterialTapTargetPrompt.Builder(activity)
                    .setPrimaryText(R.string.add_post)
                    .setSecondaryText(R.string.add_post_prompt_description)
                    .setAnimationInterpolator(new FastOutSlowInInterpolator())
                    .setTarget(R.id.iv_add)
                    .setCaptureTouchEventOnFocal(true)
                    .setAutoDismiss(false)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                        @Override
                        public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state) {
                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                                showChallengePrompt();
                            }
                        }
                    })
                    .setBackgroundColour(ContextCompat.getColor(activity, R.color.accent_transparent))
                    .show();
        }
    }

    private void showChallengePrompt() {
        if (activity != null && !activity.isFinishing()) {
            new MaterialTapTargetPrompt.Builder(activity)
                    .setPrimaryText(R.string.challenges)
                    .setSecondaryText(R.string.challenges_prompt_description)
                    .setAnimationInterpolator(new FastOutSlowInInterpolator())
                    .setTarget(R.id.iv_challenges)
                    .setCaptureTouchEventOnFocal(true)
                    .setAutoDismiss(false)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                        @Override
                        public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state) {
                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                                showSearchPrompt();
                            }
                        }
                    })
                    .setBackgroundColour(ContextCompat.getColor(activity, R.color.accent_transparent))
                    .show();
        }
    }

    private void showSearchPrompt() {
        if (activity != null && !activity.isFinishing()) {
            new MaterialTapTargetPrompt.Builder(activity)
                    .setPrimaryText(R.string.search)
                    .setSecondaryText(R.string.search_prompt_description)
                    .setAnimationInterpolator(new FastOutSlowInInterpolator())
                    .setIcon(R.drawable.ic_search)
                    .setTarget(R.id.iv_search)
                    .setCaptureTouchEventOnFocal(true)
                    .setAutoDismiss(false)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                        @Override
                        public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state) {
                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                                showNotificationsPrompt();
                            }
                        }
                    })
                    .setBackgroundColour(ContextCompat.getColor(activity, R.color.accent_transparent))
                    .show();
        }
    }

    private void showNotificationsPrompt() {
        if (activity != null && !activity.isFinishing()) {
            new MaterialTapTargetPrompt.Builder(activity)
                    .setPrimaryText(R.string.notifications)
                    .setSecondaryText(R.string.notifications_prompt_description)
                    .setAnimationInterpolator(new FastOutSlowInInterpolator())
                    .setIcon(R.drawable.ic_notification)
                    .setTarget(R.id.iv_notification)
                    .setCaptureTouchEventOnFocal(true)
                    .setAutoDismiss(false)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                        @Override
                        public void onPromptStateChanged(@NonNull MaterialTapTargetPrompt prompt, int state) {
                            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                                PreferenceUtil.setTourShown(activity);
                            }
                        }
                    })
                    .setBackgroundColour(ContextCompat.getColor(activity, R.color.accent_transparent))
                    .show();
        }
    }

}
