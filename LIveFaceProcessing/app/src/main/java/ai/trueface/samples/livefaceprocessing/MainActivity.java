package ai.trueface.samples.livefaceprocessing;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.internal.RotationHelper;
import com.otaliastudios.cameraview.size.Size;

import ai.trueface.sdk.core.ColorCode;
import ai.trueface.sdk.core.FaceBoxAndLandmarks;
import ai.trueface.sdk.core.SDK;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CameraView camera = findViewById(R.id.camera);
        camera.setFacing(Facing.FRONT);

        final SDK sdk = App.getSDK();

        camera.addFrameProcessor(new FrameProcessor() {
            @Override
            @WorkerThread
            public void process(@NonNull Frame frame) {
                Size size = frame.getSize();
                int userRotation = frame.getRotationToUser();

                if (frame.getDataClass() == byte[].class) {
                    byte[] data = frame.getData();

                    // make sure to rotate photo correctly
                    if (userRotation == 90) {
                        data = RotationHelper.rotate(data, size, 90);
                    }
                    if (userRotation == 270) {
                        data = RotationHelper.rotate(data, size, 270);
                    }

                    // for performance we are processing YUV frames
                    sdk.setImage(size.getWidth(), size.getHeight(), data, ColorCode.yuv_i420);
                    final FaceBoxAndLandmarks face = sdk.detectLargestFace();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (face != null) {
                                Log.d("Result", "Face detected");
                            } else {
                                Log.d("Result", "Face not detected");
                            }
                        }
                    });

                }
            }
        });

        camera.setLifecycleOwner(this);
    }
}