package uci.wifiproxy.data.applicationPackage;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 3/11/17.
 */

public class ApplicationPackageLocalDataSource {

    private static ApplicationPackageLocalDataSource INSTANCE;

    public static final String ALL_APPLICATION_PACKAGES_STRING = "AllApplicationsPackages";

    private PackageManager packageManager;

    private ApplicationPackage allApplicationsPackages =
            new ApplicationPackage(ALL_APPLICATION_PACKAGES_STRING ,ALL_APPLICATION_PACKAGES_STRING, null);

    private ApplicationPackageLocalDataSource(@NonNull Context context) {
        packageManager = context.getPackageManager();
    }

    public static ApplicationPackageLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ApplicationPackageLocalDataSource(context);
        }
        return INSTANCE;
    }

    public List<ApplicationPackage> getApplicationPackages() {
        List<ApplicationPackage> applicationPackages = new ArrayList<>();
        applicationPackages.add(allApplicationsPackages);

        List<ApplicationInfo> packageInfos = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo ai : packageInfos) {
            String name = ai.loadLabel(packageManager).toString();
            String packageName = ai.packageName;
            Drawable packageLogo = null;
            packageLogo = packageManager.getApplicationIcon(ai);
            applicationPackages.add(new ApplicationPackage(name, packageName, packageLogo));
        }

        return applicationPackages;
    }

}
