package uci.wifiproxy.data.applicationPackage;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by daniel on 3/11/17.
 */

public class ApplicationPackage {

    private String name;

    @NonNull
    private String packageName;

    @Nullable
    private Drawable packageLogo;

    public ApplicationPackage(String name, @NonNull String packageName, Drawable packageLogo) {
        this.packageName = packageName;
        this.packageLogo = packageLogo;
        this.name = name;
    }

    @NonNull
    public String getPackageName() {
        return packageName;
    }

    @Nullable
    public Drawable getPackageLogo() {
        return packageLogo;
    }

    public boolean hasPackageLogo(){
        return packageLogo != null;
    }

    public String getName() {
        return name;
    }
}
