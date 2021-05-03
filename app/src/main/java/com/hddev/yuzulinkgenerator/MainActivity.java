package com.hddev.yuzulinkgenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import static com.hddev.yuzulinkgenerator.constants.Values.APP_PACKAGE_YUZU;
import static com.hddev.yuzulinkgenerator.constants.Values.DOMAIN_URL_PREFIX;

/**
 * wrote all the code in a single activity.
 * I know this is bad practice to write unorganized code
 * I need to provide a quick solution.Time limit made me to write this ugly code.
 */
public class MainActivity extends AppCompatActivity {


    private Intent intent;
    private Button generateButton;
    private TextInputLayout browseLinkInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generateButton = findViewById(R.id.generate_button);
        browseLinkInput = findViewById(R.id.input_webLink);

        intent = getIntent();

        if (intent.getStringExtra(Intent.EXTRA_TEXT) != null) {
            if (!intent.getStringExtra(Intent.EXTRA_TEXT).isEmpty()) {
                setUrlInBrowseLinkInput(intent);
            }
        }


        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!browseLinkInput.getEditText().getText().toString().isEmpty())
                    generateDynamicLink(browseLinkInput.getEditText().getText().toString());
                else
                    makeAlertDialog("Empty Fields");
            }
        });
    }

    private void setUrlInBrowseLinkInput(Intent intent) {
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (text != null) {
            browseLinkInput.getEditText().setText(text);
        }
    }

    public void generateDynamicLink(String url) {
        String deepLink = createDynamicLink(url).getUri().toString();
        Intent shareIntent = createShareWebIntent(deepLink);
        try {
            startActivity(Intent.createChooser(shareIntent, getText(R.string.share)));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }



    public void makeAlertDialog(String message) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setMessage(message)
                .create().show();
    }

    public DynamicLink createDynamicLink(String url) {
        return FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setLink(Uri.parse(url))
                .setDomainUriPrefix(DOMAIN_URL_PREFIX)
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder(APP_PACKAGE_YUZU).build())
                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder()
                        .build()    )
                .buildDynamicLink();
    }

    public static Intent createShareWebIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);

        return intent;
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onNewIntent(Intent newIntent) {
        super.onNewIntent(intent);
        intent = newIntent;
        if (intent.getStringExtra(Intent.EXTRA_TEXT) != null) {
            if (!intent.getStringExtra(Intent.EXTRA_TEXT).isEmpty()) {
                setUrlInBrowseLinkInput(intent);
            }
        }
    }
}