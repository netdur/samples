package ai.trueface.samples.livefaceprocessing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.size.Size;

import ai.trueface.sdk.core.ColorCode;
import ai.trueface.sdk.core.FaceBoxAndLandmarks;
import ai.trueface.sdk.core.Image;
import ai.trueface.sdk.core.RotateFlags;
import ai.trueface.sdk.core.SDK;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CameraView camera = findViewById(R.id.camera);
        camera.setFacing(Facing.FRONT);

        final SDK sdk = App.getSDK();

        camera.addFrameProcessor(frame -> {
            Size size = frame.getSize();
            int userRotation = frame.getRotationToUser();

            if (frame.getDataClass() == byte[].class) {
                byte[] data = frame.getData();

                // for performance we are processing YUV frames
                Image image = sdk.preprocessImage(size.getWidth(), size.getHeight(), data, ColorCode.yuv_i420);
                if (userRotation == 270) {
                    image.rotate(RotateFlags.ROTATE_90_COUNTERCLOCKWISE);
                }
                if (userRotation == 180) {
                    image.rotate(RotateFlags.ROTATE_180);
                }
                if (userRotation == 90) {
                    image.rotate(RotateFlags.ROTATE_90_CLOCKWISE);
                }

                final FaceBoxAndLandmarks face = sdk.detectLargestFace(image);

                runOnUiThread(() -> {
                    if (face != null) {
                        Log.d("Result", "Face detected");
                    } else {
                        Log.d("Result", "Face not detected");
                    }
                });

            }
        });

        camera.setLifecycleOwner(this);
    }
}