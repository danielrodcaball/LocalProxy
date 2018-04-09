package uci.wifiproxy.aboutscreen;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;

import uci.wifiproxy.R;
import uci.wifiproxy.util.fontAwesome.DrawableAwesome;

/**
 * Created by daniel on 6/04/18.
 */

public class AboutActivity extends MaterialAboutActivity {

    @NonNull
    @Override
    protected MaterialAboutList getMaterialAboutList(@NonNull Context context) {
        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();

        // Add items to card

        appCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text("WifiProxy")
                .desc("© 2018 Daniel A. Rodríguez")
                .icon(R.mipmap.ic_launcher)
                .build());

        appCardBuilder.addItem(ConvenienceBuilder.createVersionActionItem(getApplicationContext(),
                getResources().getDrawable(R.drawable.ic_drawer_about),
                getString(R.string.version_text),
                false));

        appCardBuilder.addItem(ConvenienceBuilder.createWebsiteActionItem(
                getApplicationContext(),
                new DrawableAwesome(R.string.fa_github, 40,Color.GRAY, false, false, 0, 0, 0, 0, this),
                getString(R.string.sourcecode_text),
                true,
                Uri.parse("https://github.com/darodriguez1994/WifiProxy")
        ));


        MaterialAboutCard.Builder authorCardBuilder = new MaterialAboutCard.Builder();
        authorCardBuilder.title(getString(R.string.author_text));
//        authorCardBuilder.titleColor(ContextCompat.getColor(c, R.color.colorAccent));

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Daniel Alejandro Rodríguez Caballero")
                .subText("Cuba")
                .icon(new DrawableAwesome(
                        R.string.fa_user,
                        40,Color.GRAY,
                        false, false,
                        0, 0, 0, 0, this))
                .build());

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Fork on GitHub")
                .icon(new DrawableAwesome(
                        R.string.fa_github,
                        40,Color.GRAY,
                        false, false,
                        0, 0, 0, 0, this))
                .setOnClickAction(ConvenienceBuilder.createWebsiteOnClickAction(getApplicationContext(), Uri.parse("https://github.com/darodriguez1994")))
                .subText("https://github.com/darodriguez1994")
                .build());
//
//        MaterialAboutCard.Builder convenienceCardBuilder = new MaterialAboutCard.Builder();
//
//        convenienceCardBuilder.title("Convenience Builder");
//
//        convenienceCardBuilder.addItem(ConvenienceBuilder.createVersionActionItem(c,
//                new IconicsDrawable(c)
//                        .icon(CommunityMaterial.Icon.cmd_information_outline)
//                        .color(ContextCompat.getColor(c, colorIcon))
//                        .sizeDp(18),
//                "Version",
//                false));
//
//        convenienceCardBuilder.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
//                new IconicsDrawable(c)
//                        .icon(CommunityMaterial.Icon.cmd_earth)
//                        .color(ContextCompat.getColor(c, colorIcon))
//                        .sizeDp(18),
//                "Visit Website",
//                true,
//                Uri.parse("http://daniel-stone.uk")));
//
//        convenienceCardBuilder.addItem(ConvenienceBuilder.createRateActionItem(c,
//                new IconicsDrawable(c)
//                        .icon(CommunityMaterial.Icon.cmd_star)
//                        .color(ContextCompat.getColor(c, colorIcon))
//                        .sizeDp(18),
//                "Rate this app",
//                null
//        ));
//
//        convenienceCardBuilder.addItem(ConvenienceBuilder.createEmailItem(c,
//                new IconicsDrawable(c)
//                        .icon(CommunityMaterial.Icon.cmd_email)
//                        .color(ContextCompat.getColor(c, colorIcon))
//                        .sizeDp(18),
//                "Send an email",
//                true,
//                "apps@daniel-stone.uk",
//                "Question concerning MaterialAboutLibrary"));
//
//        convenienceCardBuilder.addItem(ConvenienceBuilder.createPhoneItem(c,
//                new IconicsDrawable(c)
//                        .icon(CommunityMaterial.Icon.cmd_phone)
//                        .color(ContextCompat.getColor(c, colorIcon))
//                        .sizeDp(18),
//                "Call me",
//                true,
//                "+44 12 3456 7890"));
//
//        convenienceCardBuilder.addItem(ConvenienceBuilder.createMapItem(c,
//                new IconicsDrawable(c)
//                        .icon(CommunityMaterial.Icon.cmd_map)
//                        .color(ContextCompat.getColor(c, colorIcon))
//                        .sizeDp(18),
//                "Visit London",
//                null,
//                "London Eye"));
//
//        MaterialAboutCard.Builder otherCardBuilder = new MaterialAboutCard.Builder();
//        otherCardBuilder.title("Other");
//
//        otherCardBuilder.cardColor(Color.parseColor("#7986CB"));
//
//        otherCardBuilder.addItem(new MaterialAboutActionItem.Builder()
//                .icon(new IconicsDrawable(c)
//                        .icon(CommunityMaterial.Icon.cmd_language_html5)
//                        .color(ContextCompat.getColor(c, colorIcon))
//                        .sizeDp(18))
//                .text("HTML Formatted Sub Text")
//                .subTextHtml("This is <b>HTML</b> formatted <i>text</i> <br /> This is very cool because it allows lines to get very long which can lead to all kinds of possibilities. <br /> And line breaks. <br /> Oh and by the way, this card has a custom defined background.")
//                .setIconGravity(MaterialAboutActionItem.GRAVITY_TOP)
//                .build()
//        );

        return new MaterialAboutList(appCardBuilder.build(), authorCardBuilder.build());
    }

    @Nullable
    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.about_title);
    }
}
