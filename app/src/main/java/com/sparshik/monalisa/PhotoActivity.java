package com.sparshik.monalisa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.firebase.crash.FirebaseCrash;
import com.sparshik.monalisa.base.BaseActivity;
import com.sparshik.monalisa.emojis.EmojiDialogFragment;
import com.sparshik.monalisa.patch.SafeFaceDetector;
import com.sparshik.monalisa.utils.Constants;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PhotoActivity extends BaseActivity implements EmojiDialogFragment.Callback {

    private static final String TAG = "PhotoActivity";
    private static final int REQUEST_NEW_IMAGE_FILE = 1;
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
    private Uri file, mCropImageUri;
    private Bitmap bitmap;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (bitmap == null) {
            InputStream stream = getResources().openRawResource(R.raw.friends_2);
            file = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getBaseContext().getPackageName() + "/raw/friends_2.png");
            bitmap = BitmapFactory.decodeStream(stream);

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    FirebaseCrash.report(e);
                }
            }

            int current_emoji = preferences.getInt(Constants.KEY_CURRENT_EMOJI, Constants.DEFAULT_EMOJI);

            Drawable d = getResources().getDrawable(current_emoji);
            Bitmap secondBitmap = ((BitmapDrawable) (d)).getBitmap();

            overlayFace(bitmap, secondBitmap);

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    FirebaseCrash.report(e);
                }
            }
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intentImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intentImage, REQUEST_NEW_IMAGE_FILE);
                onSelectImageClick(view);
            }
        });

        FloatingActionButton fab_emoji = (FloatingActionButton) findViewById(R.id.fab_emoji);
        fab_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEmojiDialog(view);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void overlayFace(Bitmap bitmap, Bitmap secondBitmap) {

        // A new face detector is created for detecting the face and its landmarks.
        //
        // Setting "tracking enabled" to false is recommended for detection with unrelated
        // individual images (as opposed to video or a series of consecutively captured still
        // images).  For detection on unrelated individual images, this will give a more accurate
        // result.  For detection on consecutive images (e.g., live video), tracking gives a more
        // accurate (and faster) result.
        //
        // By default, landmark detection is not enabled since it increases detection time.  We
        // enable it here in order to visualize detected landmarks.
        FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        // This is a temporary workaround for a bug in the face detector with respect to operating
        // on very small images.  This will be fixed in a future release.  But in the near term, use
        // of the SafeFaceDetector class will patch the issue.
        Detector<Face> safeDetector = new SafeFaceDetector(detector);

        // Create a frame from the bitmap and run face detection on the frame.
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = safeDetector.detect(frame);

        if (!safeDetector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
//            Log.w(TAG, "Face detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }
        if (faces.size() > 0) {
            FaceView overlay = (FaceView) findViewById(R.id.faceView);
            if (secondBitmap == null) {
                overlay.setContent(bitmap, null, faces);
            } else {
                overlay.setContent(bitmap, secondBitmap, faces);
            }
        } else {
            Toast.makeText(this, R.string.no_faces_error, Toast.LENGTH_LONG).show();
        }

        // Although detector may be used multiple times for different images, it should be released
        // when it is no longer needed in order to free native resources.
        safeDetector.release();
    }


    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NEW_IMAGE_FILE && resultCode == Activity.RESULT_OK && data != null) {

            Uri selectedImageFile = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImageFile,
                    filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            } else {
                return;
            }
            //GET PATHS
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
//            Log.d(getClass().getSimpleName(), " Image File PATH IN PHONE: " + imagePath);
            cursor.close();
            file = Uri.fromFile(new File(imagePath));
            bitmap = BitmapFactory.decodeFile(imagePath);

            int current_emoji = preferences.getInt(Constants.KEY_CURRENT_EMOJI, Constants.DEFAULT_EMOJI);
            Drawable d = getResources().getDrawable(current_emoji);
            Bitmap secondBitmap = ((BitmapDrawable) (d)).getBitmap();

            overlayFace(bitmap, secondBitmap);

        }

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(PhotoActivity.this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    FirebaseCrash.report(e);
                }
                if (bitmap != null) {
                    int current_emoji = preferences.getInt(Constants.KEY_CURRENT_EMOJI, Constants.DEFAULT_EMOJI);
                    Drawable d = getResources().getDrawable(current_emoji);
                    Bitmap secondBitmap = ((BitmapDrawable) (d)).getBitmap();
                    overlayFace(bitmap, secondBitmap);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                FirebaseCrash.report(result.getError());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_live) {
            startActivity(new Intent(this, FaceTrackerActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void showEmojiDialog(View view) {
        DialogFragment dialogFragment = new EmojiDialogFragment();
        dialogFragment.show(PhotoActivity.this.getFragmentManager(), "EmojiDialogFragment");
    }

    @Override
    public void onClose() {

        int current_emoji = preferences.getInt(Constants.KEY_CURRENT_EMOJI, Constants.DEFAULT_EMOJI);
        Drawable d = getResources().getDrawable(current_emoji);
        Bitmap secondBitmap = ((BitmapDrawable) (d)).getBitmap();
        overlayFace(bitmap, secondBitmap);

    }

    public void onSelectImageClick(View view) {
        if (Build.VERSION.SDK_INT > 23) {
            if (CropImage.isExplicitCameraPermissionRequired(this)) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
            } else {
                CropImage.startPickImageActivity(this);
            }
        } else {
            CropImage.startPickImageActivity(this);
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .start(this);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(this);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

}
