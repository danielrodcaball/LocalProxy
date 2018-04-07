package uci.wifiproxy.aboutscreen;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;

/**
 * Created by daniel on 6/04/18.
 */

public class AboutActivity extends MaterialAboutActivity {

    @NonNull
    @Override
    protected MaterialAboutList getMaterialAboutList(@NonNull Context context) {
       return new MaterialAboutList.Builder()
                .build(); // This creates an empty screen, add cards with .addCard()
    }

    @Nullable
    @Override
    protected CharSequence getActivityTitle() {
        return "title";
    }
}
