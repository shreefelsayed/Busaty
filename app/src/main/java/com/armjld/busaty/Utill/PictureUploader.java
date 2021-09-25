package com.armjld.busaty.Utill;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import Models.User;
import es.dmoral.toasty.Toasty;

public class PictureUploader {

    Context mContext;
    ProgressDialog mDialog;
    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("users");

    public PictureUploader(Context mContext) {
        this.mContext = mContext;
    }


        public Bitmap uploadPhoto(Intent data, User user) {
        Uri photoUri = data.getData();
        Bitmap bitmap = null;

        mDialog = new ProgressDialog(mContext);

        try {
            Bitmap source = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), photoUri);
            bitmap = resizeBitmap(source, 500);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri uri = null;

        try {
            uri = Uri.parse(getFilePath(mContext, photoUri));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if(uri != null) {
            bitmap = rotateImage(bitmap , uri);
        }

        mDialog.setMessage("تحديث الصورة الشخصية ..");
        mDialog.show();

        assert bitmap != null;
        handleUpload(bitmap, user);

        return bitmap;
    }

    @SuppressLint({"NewApi", "Recycle"})
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception ignored) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private Bitmap rotateImage(Bitmap bitmap, Uri uri){
        ExifInterface exifInterface =null;
        try {
            if(uri==null){
                return bitmap;
            }
            exifInterface = new ExifInterface(String.valueOf(uri));
        }
        catch (IOException e){
            e.printStackTrace();
        }

        if(exifInterface != null) {
            int orintation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION ,ExifInterface.ORIENTATION_UNDEFINED);
            if(orintation == 6 || orintation == 3 || orintation == 8) {
                Matrix matrix = new Matrix();
                if (orintation == 6) {
                    matrix.postRotate(90);
                }
                else if (orintation == 3) {
                    matrix.postRotate(180);
                } else {
                    matrix.postRotate(270);
                }
                return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            } else {
                return bitmap;
            }
        } else {
            return bitmap;
        }

    }

    private void handleUpload (Bitmap bitmap, User user) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("ppUsers").child(user.getId() + ".jpeg");
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(taskSnapshot -> getDownUrl(user, reference));
    }

    private void getDownUrl(User user, StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(uri -> {
            uDatabase.child(user.getId()).child("ppURL").setValue(uri.toString());
            user.setpPic(uri.toString());
            UserInFormation.getUser().setpPic(uri.toString());

            Toasty.success(mContext, "تم تحديث الصوره بنجاح", Toast.LENGTH_SHORT, true).show();
            mDialog.dismiss();
        });
    }

    public static Bitmap resizeBitmap(Bitmap source, int maxLength) {
        try {
            if (source.getHeight() >= source.getWidth()) {
                if (source.getHeight() <= maxLength) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                int targetWidth = (int) (maxLength * aspectRatio);

                return Bitmap.createScaledBitmap(source, targetWidth, maxLength, false);
            } else {
                if (source.getWidth() <= maxLength) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = ((double) source.getHeight()) / ((double) source.getWidth());
                int targetHeight = (int) (maxLength * aspectRatio);

                return Bitmap.createScaledBitmap(source, maxLength, targetHeight, false);
            }
        }
        catch (Exception e) {
            return source;
        }
    }
}
