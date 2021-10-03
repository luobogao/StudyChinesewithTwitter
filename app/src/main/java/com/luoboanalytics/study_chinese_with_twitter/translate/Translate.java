package com.luoboanalytics.study_chinese_with_twitter.translate;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.luoboanalytics.study_chinese_with_twitter.MainActivity;

public class Translate {

    FirebaseTranslator translator;
    translateCallback callback;
    public void registerCallback(translateCallback newCallback){callback = newCallback;};


    public void translate(String text) {
        FirebaseTranslatorOptions options =
                new FirebaseTranslatorOptions.Builder()
                        // below line we are specifying our source language.
                        .setSourceLanguage(FirebaseTranslateLanguage.ZH)
                        // in below line we are displaying our target language.
                        .setTargetLanguage(FirebaseTranslateLanguage.EN)
                        // after that we are building our options.
                        .build();
        // below line is to get instance
        // for firebase natural language.
        translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {

                translator.translate(text).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String translateText) {
                        Log.i("TRANSLATE", "translated: " + translateText);

                        // Send translated text back through callback
                        callback.translated(translateText);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }
}
