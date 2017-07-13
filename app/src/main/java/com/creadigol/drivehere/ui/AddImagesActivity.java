package com.creadigol.drivehere.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.creadigol.drivehere.MyApplication;
import com.creadigol.drivehere.R;
import com.creadigol.drivehere.dialog.ListDialogListener;
import com.creadigol.drivehere.dialog.SingleChoiceDialogFragment;
import com.creadigol.drivehere.ui.photo.AlbumStorageDirFactory;
import com.creadigol.drivehere.ui.photo.BaseAlbumDirFactory;
import com.creadigol.drivehere.ui.photo.FroyoAlbumDirFactory;
import com.creadigol.drivehere.util.CommonFunctions;
import com.creadigol.drivehere.util.Constant;
import com.creadigol.drivehere.util.DividerItemDecoration;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddImagesActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_KEY_IMAGES = "images_value";
    public static final String EXTRA_KEY_IS_SINGLE = "is_for_single";
    private static final int ACTION_TAKE_PHOTO = 1;
    private static final int ACTION_SELECT_PHOTO = 2;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private final String TAG_DIALOG_IMAGE_OPTION = "Option";
    View.OnClickListener onImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO onImageClick
        }
    };
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String mCurrentPhotoPath;
    private ArrayList<String> mImages;
    private RecyclerView rvImages;
    private ImageAdapter imageAdapter;
    private boolean isForSingle;

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
     *
     * @param context The application's environment.
     * @param action  The Intent action to check for availability.
     * @return True if an Intent with the specified action can be sent and
     * responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_images);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mImages = bundle.getStringArrayList(EXTRA_KEY_IMAGES);
            isForSingle = bundle.getBoolean(EXTRA_KEY_IS_SINGLE, false);
        }

        if (mImages == null) {
            mImages = new ArrayList<>();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        if (isForSingle) {
            showImageOptionDialog(TAG_DIALOG_IMAGE_OPTION);
            return;
        }

        findViewById(R.id.cl_btn_add_new_image).setOnClickListener(this);
        findViewById(R.id.cl_btn_done).setOnClickListener(this);


        rvImages = (RecyclerView) findViewById(R.id.rv_images);
        rvImages.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider), false, false));
        rvImages.setHasFixedSize(true);
        rvImages.setItemAnimator(new DefaultItemAnimator());
        rvImages.setLayoutManager(new LinearLayoutManager(this));

        setImages();

    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO
       /* outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );*/
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // TODO
        /*mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mImageView.setImageBitmap(mImageBitmap);
        mImageView.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            //intent.putStringArrayListExtra(EXTRA_KEY_IMAGES, mImages);
            setResult(RESULT_CANCELED, intent);
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void setImages() {
        if (mImages != null && mImages.size() > 0) {
            if (imageAdapter == null) {
                imageAdapter = new ImageAdapter();
                rvImages.setAdapter(imageAdapter);
            } else {
                imageAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_btn_add_new_image:
                showImageOptionDialog(TAG_DIALOG_IMAGE_OPTION);
                break;

            case R.id.cl_btn_done:
                done();
                break;
        }
    }

    private void done() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(EXTRA_KEY_IMAGES, mImages);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void showImageOptionDialog(String tag) {

        final String[] options = new String[]{"Take Photo", "Choose from Library"};

        final ListDialogListener listener = new ListDialogListener() {

            @Override
            public void onItemClick(int position, String tag) {
                if (tag.equals(TAG_DIALOG_IMAGE_OPTION)) {
                    if (position == 0) {
                        // start take photo
                        if (isIntentAvailable(AddImagesActivity.this, MediaStore.ACTION_IMAGE_CAPTURE)) {
                            dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
                        } else {
                            CommonFunctions.showToast(AddImagesActivity.this, "Device cannot take picture");
                        }
                    } else if (position == 1) {
                        // choose photo from library
                        dispatchGalleryIntent(ACTION_SELECT_PHOTO);
                    }
                }
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {
                if(isForSingle){
                    finish();
                }
            }

        };

        SingleChoiceDialogFragment dialog = new SingleChoiceDialogFragment(listener, options, tag, "Select " + tag, null);
        dialog.show(getSupportFragmentManager(), tag);
    }

    private void dispatchGalleryIntent(int actionCode) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select image"),
                ACTION_SELECT_PHOTO);

       /* Intent intent = new Intent();
        intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), ACTION_SELECT_PHOTO);*/
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch (actionCode) {
            case ACTION_TAKE_PHOTO:
                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        startActivityForResult(takePictureIntent, actionCode);
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                } else if (isForSingle) {
                    finish();
                }
                break;
            } // ACTION_TAKE_PHOTO

            case ACTION_SELECT_PHOTO: {
                if (resultCode == RESULT_OK) {
                    Uri selectedImageURI = data.getData();
                    mCurrentPhotoPath = getRealPathFromURI(selectedImageURI);
                    handleGalleryPhoto();
                } else if (isForSingle) {
                    finish();
                }
                break;
            } // ACTION_SELECT_PHOTO
        } // switch
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            /*setPic();*/
            galleryAddPic();
            if (!mImages.contains(mCurrentPhotoPath))
                mImages.add(mCurrentPhotoPath);
            else
                CommonFunctions.showToast(this, "Already selected");
            mCurrentPhotoPath = null;
            // refresh list of images
            if (isForSingle) {
                done();
            } else
                setImages();

        }

    }

    private void handleGalleryPhoto() {

        if (mCurrentPhotoPath != null) {
            if (!mImages.contains(mCurrentPhotoPath))
                mImages.add(mCurrentPhotoPath);
            else
                CommonFunctions.showToast(this, "Already selected");
            mCurrentPhotoPath = null;
            // refresh list of images
            if (isForSingle)
                done();
            else
                setImages();
        }

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getRealPathFromURI(Uri contentURI) {
        String path = null;

        try {
           // Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(contentURI,
                    filePath, null, null, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor
                    .getColumnIndex(filePath[0]));
            // At the end remember to close the cursor or you will end with
            // the RuntimeException!
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //path = contentURI.getPath();

        /*if(path != null && path.trim().length() > 0){
            return path;
        }*/

       /* if (Build.VERSION.SDK_INT < 11)
            path = CommonFunctions.getRealPathFromURI_BelowAPI11(AddImagesActivity.this, contentURI);

            // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19)
            path = CommonFunctions.getRealPathFromURI_API11to18(AddImagesActivity.this, contentURI);

            // SDK > 19 (Android 4.4)
        else
            path = CommonFunctions.getRealPathFromURI_API19(AddImagesActivity.this, contentURI);*/
        Log.d("AddImagesActivity", "File Path: " + path);
       /* path = CommonFunctions.getPath(AddImagesActivity.this, contentURI);*/
        return path;
    }

    public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;

        public ImageAdapter() {
            this.context = AddImagesActivity.this;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_image, viewGroup, false);
            return new ImageViewHolder(view);

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ImageViewHolder imageViewHolder = (ImageViewHolder) viewHolder;
            // set default_car data
            String imagePath = mImages.get(i);

            if (imagePath != null && imagePath.trim().length() > 0) {
                if (imagePath.startsWith(Constant.PREFIX_HTTPS))
                    MyApplication.getInstance().getImageLoader().displayImage(imagePath, imageViewHolder.ivImage, getDisplayImageOptions());
                else
                    MyApplication.getInstance().getImageLoader().displayImage("file://" + imagePath, imageViewHolder.ivImage, getDisplayImageOptions());
            } else
                MyApplication.getInstance().getImageLoader().displayImage("", imageViewHolder.ivImage, getDisplayImageOptions());

            imageViewHolder.clImage.setTag(i);
            imageViewHolder.clImage.setOnClickListener(onImageClick);

        }

        @Override
        public int getItemCount() {
            return mImages.size();
        }

        public DisplayImageOptions getDisplayImageOptions() {
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.default_car)
                    .showImageOnFail(R.drawable.default_car)
                    .showImageOnLoading(R.drawable.default_car).build();
            return options;
        }

        class ImageViewHolder extends RecyclerView.ViewHolder {

            public ImageView ivImage;
            public ConstraintLayout clImage;

            public ImageViewHolder(View itemView) {
                super(itemView);
                ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
                clImage = (ConstraintLayout) itemView.findViewById(R.id.cl_image);
            }
        }

    }

    /*private void setPic() {

		*//* There isn't enough memory to open up more than a couple camera photos *//*
        *//* So pre-scale the target bitmap into which the file is decoded *//*

		*//* Get the size of the ImageView *//*
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		*//* Get the size of the image *//*
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		*//* Figure out which way needs to be reduced less *//*
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		*//* Set bitmap options to scale the image decode target *//*
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		*//* Decode the JPEG file into a Bitmap *//*
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		*//* Associate the Bitmap to the ImageView *//*
        mImageView.setImageBitmap(bitmap);

        mImageView.setVisibility(View.VISIBLE);

    }*/

}
