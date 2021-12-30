package com.zlpls.plantinsta.visualselection;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.zlpls.plantinsta.AddNewPlant;
import com.zlpls.plantinsta.R;
import com.zlpls.plantinsta.UploadPlantFollow;
import com.zlpls.plantinsta.UserActions;
import com.squareup.picasso.Picasso;
import com.zlpls.plantinsta.menulist.PlantInstaWeb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
// https://www.youtube.com/watch?v=CLXXXtslPik&list=PLgCYzUzKIBE9XqkckEJJA0I1wVKbUAOdv&index=46

/**
 * part 44 3:45'de hangi fragmenteten geliyorsa ona göre davranma var.
 */
public class TakePhotoFragment extends Fragment {
   UserActions userActions;
    private static final String TAG = "AndroidCameraApi";
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    Rect zoom;
    int selectedCameraId= 0;
    File photoFile; //çekilen resimin adı
    String currentPhotoPath; //yolu
   FileOperations  fileOperations;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    //****Zoom için ***//
    public float finger_spacing = 0;
    public int zoom_level = 1;
    TextView zoomvalue;
    //******************///
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Button takePictureButton;
    private TextureView textureView;
    private String cameraId;
    private Size imageDimension;
    TextureView.SurfaceTextureListener textureListener;
    private ImageView imageView;
    public TakePhotoFragment() {

    }

    public static TakePhotoFragment newInstance() {
        return new TakePhotoFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userActions = new UserActions();


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_take_photo, container, false);

        Button takePhotoButton = v.findViewById(R.id.btn_takepicture);
         textureListener = new TextureView.SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                //open your camera here
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                // Transform you image captured size according to the surface width and height
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        };
        imageView = (v.findViewById(R.id.phototaken));

        fileOperations = new FileOperations();

        textureView = (TextureView) v.findViewById(R.id.texture);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);
        takePictureButton = (Button) v.findViewById(R.id.btn_takepicture);
        zoomvalue= v.findViewById(R.id.zoom);
        assert takePictureButton != null;
        ;
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        //ZOOM ayarlama
        v.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    System.out.println("ekrana dokunuldu");
                    //ekrana zoom seviyesini yazdırma
                    zoomvalue.setText("Zoom %"+String.valueOf(zoom_level*100/80));
                    CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                    float maxzoom = (characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)) * 10;
                    System.out.println("max zoom : "+ maxzoom);
                    Rect m = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
                    int action = event.getAction();
                    float current_finger_spacing;

                    if (event.getPointerCount() > 1) {
                        // Multi touch logic
                        current_finger_spacing = getFingerSpacing(event);
                        if (finger_spacing != 0) {
                            if (current_finger_spacing > finger_spacing && maxzoom > zoom_level) {
                                zoom_level++;
                            } else if (current_finger_spacing < finger_spacing && zoom_level > 1) {
                                zoom_level--;
                            }
                            int minW = (int) (m.width() / maxzoom);
                            int minH = (int) (m.height() / maxzoom);
                            int difW = m.width() - minW;
                            int difH = m.height() - minH;
                            int cropW = difW / 100 * (int) zoom_level;
                            int cropH = difH / 100 * (int) zoom_level;
                            cropW -= cropW & 3;
                            cropH -= cropH & 3;
                            zoom = new Rect(cropW, cropH, m.width() - cropW, m.height() - cropH);


                            captureRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoom);
                        }
                        finger_spacing = current_finger_spacing;
                    } else {
                        if (action == MotionEvent.ACTION_UP) {
                            //single touch logic
                        }
                    }

                    try {
                        cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                } catch (CameraAccessException e) {
                    throw new RuntimeException("can not access camera.", e);
                }
                return true;
            }
        });
        return v;

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
            ;
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    private ImageReader imageReader;
    private File file;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;

            createCameraPreview();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };
    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
           // Toast.makeText(getContext().getApplicationContext(), "Saved:" + file, Toast.LENGTH_SHORT).show();
            createCameraPreview();
        }
    };
    private HandlerThread mBackgroundThread;



    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void stopBackgroundThread() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBackgroundThread.quitSafely();
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBackgroundThread.join();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBackgroundThread = null;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBackgroundHandler = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


  /*******************FOTO çekme ????????????????*************/




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void takePicture() {
        if (null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);

        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            //zoom yapmak için bunu ekliyorsun
            captureBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoom);

            // Orientation
            int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            /* önce sil*/
           // fileOperations.deleteImageFileInStorage(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/PlantInsta.jpg"));

            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/PlantInsta.jpg");

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Image image = null;
                            image = reader.acquireLatestImage();
                            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                            buffer.clear();
                            byte[] bytes = new byte[buffer.capacity()];

                            buffer.get(bytes);
                           // Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            //int degree = fileOperations.getImageRotation(photoFile);
                           // bitmap = fileOperations.getBitmapRotatedByDegree(bitmap, 90);
                            /**
                             * eğer resimi imageview e koymak istersen
                             *  imageView.setImageBitmap(bitmap); ekle
                             */
                       try {

                           Save(bytes);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                }

                private void Save(byte[] bytes) throws IOException {
                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);

            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback()
            {
                @Override
                public void onCaptureCompleted (CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result)

                {
                    super.onCaptureCompleted(session, request, result);

                   //Toast.makeText(getActivity(), "Saved:" + file, Toast.LENGTH_SHORT).show();

                    switch (userActions.getFromList()) {
                        case 0:
                            userActions.setImageFromGallery(false); //resim galeriden gidiyor mu?
                            Intent intent = new Intent(getContext(), AddNewPlant.class);
                            intent.putExtra("selectedimagefromuser", file.getAbsolutePath());
                            getActivity().finish();                    ; // bu da kapatmak içi
                            startActivity(intent);
                            break;
                        case 1:
                            userActions.setImageFromGallery(false); //resim galeriden gidiyor mu?
                            Intent intentf = new Intent(getContext(), UploadPlantFollow.class);
                            intentf.putExtra("selectedimagefromuser", file.getAbsolutePath());
                            getActivity().finish();
                            startActivity(intentf);

                    }

                   // createCameraPreview(); // bu da devam etmek için
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                  //  Toast.makeText(getActivity(), "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");

        try {
            cameraId = manager.getCameraIdList()[selectedCameraId]; // arka veya ön kamera
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                // todo

            }
        }
    }
    protected void updatePreview() {
        if (null == cameraDevice) {

            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(getContext().getApplicationContext(), "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                //finish();
            }
        }
    }


    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        //closeCamera();
        stopBackgroundThread();
        super.onPause();
    }
    public void switchCamera( View view){

        switch (selectedCameraId) {
            case (0):
                selectedCameraId = 1;
                closeCamera();
                openCamera();
                break;
            case(1):
                selectedCameraId= 0;
                closeCamera();
                openCamera();
                break;
        }

        System.out.println("Seçilen kamera "+selectedCameraId);

    }
    //zoom *****



    //Determine the space between the first two fingers
    @SuppressWarnings("deprecation")
    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

}
/*
  Bitmap  photoFile2Bitmap = fileOperations.getStreamByteFromImage(photoFile);
                photoFile2Bitmap = fileOperations.getBitmapRotatedByDegree(photoFile2Bitmap,270);

                FileOutputStream outStream = null;
                try {
                    outStream = new FileOutputStream(photoFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

              photoFile2Bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
 */