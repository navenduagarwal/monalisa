package com.sparshik.monalisa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

/**
 * View which displays a bitmap containing a face along with overlay graphics that identify the
 * locations of detected facial landmarks.
 */

public class FaceView extends View {
    private Bitmap mBitmap;
    private Bitmap mSecondBitmap;
    private SparseArray<Face> mFaces;

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Sets the bitmap background and the associated face detections.
     */
    void setContent(Bitmap bitmap, Bitmap secondBitmap, SparseArray<Face> faces) {
        mBitmap = bitmap;
        mSecondBitmap = secondBitmap;
        mFaces = faces;
        invalidate();
    }

    /**
     * Draws the bitmap background and the associated face landmarks.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ((mBitmap != null) && (mFaces != null)) {
            double scale = drawBitmap(canvas);
            if (mSecondBitmap == null) {
            drawFaceAnnotations(canvas, scale);
            } else {
            drawSecondBitmap(canvas, scale);
            }
        }
    }

    /**
     * Draws the bitmap background, scaled to the device size.  Returns the scale for future use in
     * positioning the facial landmark graphics.
     */
    private double drawBitmap(Canvas canvas) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = mBitmap.getWidth();
        double imageHeight = mBitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);

        Rect destBounds = new Rect(0, 0, (int) (imageWidth * scale), (int) (imageHeight * scale));
        canvas.drawBitmap(mBitmap, null, destBounds, null);
        return scale;
    }

    /**
     * Draws a small circle for each detected landmark, centered at the detected landmark position.
     * <p>
     * <p>
     * Note that eye landmarks are defined to be the midpoint between the detected eye corner
     * positions, which tends to place the eye landmarks at the lower eyelid rather than at the
     * pupil position.
     */
    private void drawFaceAnnotations(Canvas canvas, double scale) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        for (int i = 0; i < mFaces.size(); ++i) {
            Face face = mFaces.valueAt(i);
//            Log.d("Height", face.getHeight() + "");

            for (Landmark landmark : face.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x * scale);
                int cy = (int) (landmark.getPosition().y * scale);
                canvas.drawCircle(cx, cy, 10, paint);
//                Log.d("Points", cx + "," + cy);
            }
        }
    }

    private void drawSecondBitmap(Canvas canvas, double scale) {
        if (mFaces.size() > 0 && mSecondBitmap != null) {
            for (int i = 0; i < mFaces.size(); ++i) {
                Face face = mFaces.valueAt(i);

                int height = (int) (face.getHeight() * scale * .75);  //added factor of .75 to adjust image size based on hit and trial
                int width = (int) (face.getWidth() * scale * .75);

//                Log.d("Height", height + "");
//                Log.d("Width", width + "");

                // computing height and width manually
                int minX = (int) (face.getLandmarks().get(0).getPosition().x * scale);
                int minY = (int) (face.getLandmarks().get(0).getPosition().y * scale);
                ;
                int maxX = (int) (face.getLandmarks().get(0).getPosition().x * scale);
                ;
                int maxY = (int) (face.getLandmarks().get(0).getPosition().y * scale);
                ;

                for (Landmark landmark : face.getLandmarks()) {
                    int cx = (int) (landmark.getPosition().x * scale);
                    int cy = (int) (landmark.getPosition().y * scale);
                    if (cx < minX) {
                        minX = cx;
                    }
                    if (cy < minY) {
                        minY = cy;
                    }
                    if (cy > maxX) {
                        maxX = cx;
                    }
                    if (cy > maxY) {
                        maxY = cy;
                    }
                }

                int cmHeight = maxY - minY;
                int cmWidth = maxX - minX;

//                Log.d("Computed Height", cmHeight + "");
//                Log.d("Computed Width", cmWidth + "");

                float eulerY = face.getEulerY(); //Returns the rotation of the face about the vertical axis of the image.
//                Log.d("eulerY", face.getEulerY() + "");
                float eulerZ = face.getEulerZ(); // Returns the rotation of the face about the axis pointing out of the image.
                Landmark left_eye = face.getLandmarks().get(0);
                Matrix matrix = new Matrix();
                matrix.postRotate(eulerY);

                Bitmap rotatedBitmap = Bitmap.createBitmap(mSecondBitmap, 0, 0, mSecondBitmap.getWidth(), mSecondBitmap.getHeight(), matrix, true);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, width, height, true);
                int cx = (int) (left_eye.getPosition().x * scale - scaledBitmap.getWidth() / 4);
                int cy = (int) (left_eye.getPosition().y * scale - scaledBitmap.getHeight() / 4);
                canvas.drawBitmap(scaledBitmap, cx, cy, null);
//                Log.d("Points", cx + "," + cy);

                /**
                 * List of landmark points
                 * RIGHT_EYE of subject picture - 0
                 * LEFT_EYE of subject picture - 1
                 * NOSE_BASE - 2
                 * LEFT_CHEEK of subject picture - 3
                 * RIGHT_CHEEK of subject picture - 4
                 * LEFT_MOUTH of subject picture- 5
                 * RIGHT_MOUTH of subject picture - 6
                 * BOTTOM_MOUTH - 7
                 */
            }
        }
    }
}