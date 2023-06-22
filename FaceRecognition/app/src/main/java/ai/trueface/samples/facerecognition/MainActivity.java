package ai.trueface.samples.facerecognition;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.InputStream;

import ai.trueface.sdk.core.Candidate;
import ai.trueface.sdk.core.Faceprint;
import ai.trueface.sdk.core.Image;
import ai.trueface.sdk.core.SDK;

public class MainActivity extends AppCompatActivity {

    Button enrollButton;
    Button takePhotoButton;
    ImageView imageView;

    TextView label;

    Faceprint lastFaceprint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePhotoButton = findViewById(R.id.button);
        takePhotoButton.setOnClickListener(v -> pickUpImage());

        enrollButton = findViewById(R.id.button2);
        enrollButton.setVisibility(View.INVISIBLE);
        enrollButton.setOnClickListener(v -> enrollPerson());

        imageView = findViewById(R.id.imageView);

        label = findViewById(R.id.textView4);
    }

    private void enrollPerson() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Person name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        final Context self = this;
        builder.setPositiveButton("OK", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                App.getSDK().enrollFaceprint(lastFaceprint, name);
                Toast.makeText(self, "Enroll Success", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void pickUpImage() {
        ImagePicker.Companion.with(this)
                .compress(2048)
                .maxResultSize(1080, 1080)
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            Uri fileUri = data.getData();
            imageView.setImageURI(fileUri);

            Bitmap bitmap = null;
            try {
                InputStream inputStream = getContentResolver().openInputStream(fileUri);
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            Image image = App.getSDK().preprocessImage(bitmap);
            lastFaceprint = App.getSDK().getLargestFaceFeatureVector(image);
            if (lastFaceprint == null) {
                Toast.makeText(this, "Face not found", Toast.LENGTH_SHORT).show();
                return;
            }

            Candidate[] candidates = App.getSDK().identifyTopCandidates(lastFaceprint, 2);
            if (candidates.length == 0) {
                label.setText("Unknown");
                enrollButton.setVisibility(View.VISIBLE);
            } else {
                label.setText(candidates[0].identity);
                enrollButton.setVisibility(View.INVISIBLE);
            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}
