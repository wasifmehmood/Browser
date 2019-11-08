package com.example.privatebrowser.Classes;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.privatebrowser.R;

import java.util.Locale;

public class ChangeLanguage {

    private static final String Locale_Preference = "Locale Preference";
    public static final String Locale_KeyValue = "Saved Locale";
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    private Activity activity;

    public ChangeLanguage(Activity activity) {
        this.activity = activity;

        sharedPreferences = activity.getSharedPreferences(Locale_Preference, Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    private Spinner languageSpinner;

    //DIALOG
    public void customDialog() {

        // custom dialog
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_language_dialog);
        dialog.setTitle("Title...");

        dialog.setCanceledOnTouchOutside(false);


        // set the custom dialog components - text, image and button

//        Button selectLanguage = dialog.findViewById(R.id.btn_select_language);
        Button englishLanguage = dialog.findViewById(R.id.button_english);
        Button frenchLanguage = dialog.findViewById(R.id.button_french);
        Button russianLanguage = dialog.findViewById(R.id.button_russian);
        Button spanishLanguage = dialog.findViewById(R.id.button_spanish);

//        addListenerOnSpinnerItemSelection(dialog);

        englishLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lang = "en";
                setAppLocale(lang);
                loadLocale();
                activity.finish();
                activity.startActivity(activity.getIntent());
                dialog.dismiss();
            }
        });

        frenchLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lang = "fr";
                setAppLocale(lang);
                loadLocale();
                activity.finish();
                activity.startActivity(activity.getIntent());
                dialog.dismiss();
            }
        });
        russianLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lang = "ru";
                setAppLocale(lang);
                loadLocale();
                activity.finish();
                activity.startActivity(activity.getIntent());
                dialog.dismiss();
            }
        });
        spanishLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lang = "es";
                setAppLocale(lang);
                loadLocale();
                activity.finish();
                activity.startActivity(activity.getIntent());
                dialog.dismiss();
            }
        });

//        selectLanguage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                setAppLocale(lang);
//                loadLocale();
//                activity.finish();
//                activity.startActivity(activity.getIntent());
//                dialog.dismiss();
//            }
//        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private String lang;

//    private void addListenerOnSpinnerItemSelection(Dialog dialog) {
//
//        languageSpinner = dialog.findViewById(R.id.language_spinner);
////        Toast.makeText(getActivity(), "null"+emergencyDisasterSpinner, Toast.LENGTH_SHORT).show();
//        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                lang = parent.getItemAtPosition(position).toString();
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//    }

    public void setAppLocale(String localeCode) {
        Resources resources = activity.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }

        saveLocale(localeCode);

        resources.updateConfiguration(config, dm);
    }

    //Save locale method in preferences

    private void saveLocale(String lang) {
        editor.putString(Locale_KeyValue, lang);
        editor.commit();
    }

    //Get locale method in preferences
    public void loadLocale() {
        String language = sharedPreferences.getString(Locale_KeyValue, "");
        setAppLocale(language);
    }

}
