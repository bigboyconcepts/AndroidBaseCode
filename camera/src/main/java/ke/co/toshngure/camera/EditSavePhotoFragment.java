/*
 * Copyright (c) 2018.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.camera;


import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 *
 */
public class EditSavePhotoFragment extends Fragment {

    public static final String BITMAP_KEY = "bitmap_byte_array";
    public static final String ROTATION_KEY = "rotation";
    public static final String IMAGE_INFO = "image_info";
    private static final String TAG = "EditSavePhotoFragment";

    public EditSavePhotoFragment() {
    }

    public static Fragment newInstance(byte[] bitmapByteArray, int rotation,
                                       @NonNull ImageParameters parameters) {
        Fragment fragment = new EditSavePhotoFragment();

        Bundle args = new Bundle();
        args.putByteArray(BITMAP_KEY, bitmapByteArray);
        args.putInt(ROTATION_KEY, rotation);
        args.putParcelable(IMAGE_INFO, parameters);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_save_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int rotation = getArguments().getInt(ROTATION_KEY);
        byte[] data = getArguments().getByteArray(BITMAP_KEY);
        ImageParameters imageParameters = getArguments().getParcelable(IMAGE_INFO);

        if (imageParameters == null) {
            return;
        }

        final ImageView photoImageView = view.findViewById(R.id.photo);

        imageParameters.mIsPortrait =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        final View topView = view.findViewById(R.id.topView);
        if (imageParameters.mIsPortrait) {
            topView.getLayoutParams().height = imageParameters.mCoverHeight;
        } else {
            topView.getLayoutParams().width = imageParameters.mCoverWidth;
        }

        rotatePicture(rotation, data, photoImageView);

        view.findViewById(R.id.save_photo).setOnClickListener(v -> savePicture());
    }

    private void rotatePicture(int rotation, byte[] data, ImageView photoImageView) {
        Bitmap bitmap = ImageUtility.decodeSampledBitmapFromByte(getActivity(), data);
//        Log.d(TAG, "original bitmap width " + bitmap.getWidth() + " height " + bitmap.getHeight());
        if (rotation != 0) {
            Bitmap oldBitmap = bitmap;

            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);

            bitmap = Bitmap.createBitmap(
                    oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, false
            );

            oldBitmap.recycle();
        }

        photoImageView.setImageBitmap(bitmap);
    }

    private void savePicture() {
        final View view = getView();
        if (view != null) {
            ImageView photoImageView = view.findViewById(R.id.photo);

            Bitmap bitmap = ((BitmapDrawable) photoImageView.getDrawable()).getBitmap();
            Uri photoUri = ImageUtility.savePicture(getActivity(), bitmap);

            ((CameraActivity) getActivity()).returnPhotoUri(photoUri);
        }
    }

}
