package com.nsa.labelimages.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nsa.labelimages.R;

import static com.nsa.labelimages.activities.TaskActivity.userModel;

public class ReferActivity extends AppCompatActivity {

    TextView linkView,referView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);
        linkView=findViewById(R.id.appLinkTextView);
        referView=findViewById(R.id.referTV);
        linkView.setText(userModel.getReferral().getLink());
        referView.append(" "+userModel.getReferral().getMyCode());
    }

    public void backToTask(View view) {
        finish();
    }

    public void shareLink(View view) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Invite Freinds");
        String shareMessage= "\nLet me recommend you this application\n\n";
        shareMessage = shareMessage + "App Link :- "+linkView.getText().toString();
        shareMessage+="\n"+referView.getText().toString();
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "choose one"));
    }
    private ClipboardManager myClipboard;
    private ClipData myClip;

    public void copyLink(View view) {
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        String text;
        text = linkView.getText().toString();

        myClip = ClipData.newPlainText("link", text);
        myClipboard.setPrimaryClip(myClip);

        Toast.makeText(getApplicationContext(), "Link Copied", Toast.LENGTH_SHORT).show();
    }

    public void copyRefer(View view) {
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        String text;
        text = userModel.getId();

        myClip = ClipData.newPlainText("refer", text);
        myClipboard.setPrimaryClip(myClip);

        Toast.makeText(getApplicationContext(), "Referral Code Copied", Toast.LENGTH_SHORT).show();
    }
}