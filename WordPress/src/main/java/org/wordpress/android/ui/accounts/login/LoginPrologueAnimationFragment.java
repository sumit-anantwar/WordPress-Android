package org.wordpress.android.ui.accounts.login;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import org.wordpress.android.R;

public class LoginPrologueAnimationFragment extends Fragment {
    private final static String KEY_ANIMATION_FILENAME = "KEY_ANIMATION_FILENAME";
    private final static String KEY_PROMO_TEXT = "KEY_PROMO_TEXT";

    private LottieAnimationView mLottieAnimationView;

    private String mAnimationFilename;
    private @StringRes int mPromoText;

    private LoginListener mLoginListener;

    static LoginPrologueAnimationFragment newInstance(String animationFilename, @StringRes int promoText) {
        LoginPrologueAnimationFragment fragment = new LoginPrologueAnimationFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ANIMATION_FILENAME, animationFilename);
        bundle.putInt(KEY_PROMO_TEXT, promoText);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAnimationFilename = getArguments().getString(KEY_ANIMATION_FILENAME);
        mPromoText = getArguments().getInt(KEY_PROMO_TEXT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.login_intro_template_view, container, false);

        TextView promoText = (TextView) rootView.findViewById(R.id.promo_text);
        promoText.setText(mPromoText);

        mLottieAnimationView = (LottieAnimationView) rootView.findViewById(R.id.animation_view);
        mLottieAnimationView.setAnimation(mAnimationFilename, LottieAnimationView.CacheStrategy.Weak);
//        mLottieAnimationView.setImageAssetsFolder("login_anims");
        mLottieAnimationView.loop(true);

        mLottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            private int mCount;

            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {
                mCount++;

                if (mCount > 2) {
                    mCount = 0;

                    if (mLoginListener != null) {
                        mLoginListener.nextPromo();
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginListener) {
            mLoginListener = (LoginListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement LoginListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mLoginListener = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // toggle the animation but only if already resumed.
        //  Needed because setUserVisibleHint is called before onCreateView
        if (isResumed()) {
            toggleAnimation(isVisibleToUser);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // need to toggle the animation so the first time the fragment is resumed it starts animating (if visible).
        toggleAnimation(getUserVisibleHint());
    }

    private void toggleAnimation(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            mLottieAnimationView.playAnimation();
        } else {
            mLottieAnimationView.cancelAnimation();
        }
    }
}
