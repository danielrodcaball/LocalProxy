package uci.localproxy.data.applicationPackage;

import android.support.annotation.NonNull;

/**
 * Created by daniel on 3/11/17.
 */

public class ApplicationPackage {

    private String name;

    @NonNull
    private String packageName;

    public ApplicationPackage(String name, @NonNull String packageName) {
        this.packageName = packageName;
        this.name = name;
    }

    @NonNull
    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }
}
