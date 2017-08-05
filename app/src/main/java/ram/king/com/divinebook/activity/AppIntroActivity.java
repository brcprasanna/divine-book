package ram.king.com.divinebook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;

import ram.king.com.divinebook.R;
import ram.king.com.divinebook.util.AppConstants;
import ram.king.com.divinebook.util.AppUtil;
import ram.king.com.divinebook.util.SampleSlide;

public class AppIntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBackButtonVisibilityWithDone(true);

        addSlide(SampleSlide.newInstance(R.layout.intro_custom_layout1));
        addSlide(SampleSlide.newInstance(R.layout.intro_custom_layout2));
        addSlide(SampleSlide.newInstance(R.layout.intro_custom_layout3));

        setSlideOverAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        goToHome();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        goToHome();
    }

    public void goToHome() {
        AppUtil.putBoolean(this, AppConstants.FIRST_TIME_LOGIN, false);
        startActivity(new Intent(AppIntroActivity.this, MainActivity.class));
        finish();
    }
}
