package com.eysa.shapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SellFragment extends Fragment {

    View rootView;
    Button continues;
    ImageView image1,image2, image3, image4, image5,video, uploadImage;
    TextView continue_text,sell_page_header_text;
    String imageString1 = "", imageString2  = "", imageString3  = "", imageString4  = "", imageString5  = "";
    static final int REQUEST_CAMERA = 1;
    static final int SELECT_FILE = 2;
    int selectImageView = 0, flag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       /* super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product_view);*/
        rootView = inflater.inflate(R.layout.sell_view, container, false);

        image1 = (ImageView) rootView.findViewById(R.id.upload_image1);
        image2 = (ImageView) rootView.findViewById(R.id.upload_image2);
        image3 = (ImageView) rootView.findViewById(R.id.upload_image3);
        image4 = (ImageView) rootView.findViewById(R.id.upload_image4);
        image5 = (ImageView) rootView.findViewById(R.id.upload_image5);
        video = (ImageView) rootView.findViewById(R.id.upload_video);
        continue_text = (TextView) rootView.findViewById(R.id.continue_text);
        sell_page_header_text = (TextView) rootView.findViewById(R.id.sell_page_header_text);
        sell_page_header_text.setText(R.string.sell);

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageView =1;
                selectImage();
            }
        }

        );

        image2.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          selectImageView =2;
                                          selectImage();
                                      }
                                  }

        );

        image3.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          selectImageView =3;
                                          selectImage();
                                      }
                                  }

        );

        image4.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          selectImageView =4;
                                          selectImage();
                                      }
                                  }

        );

        image5.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          selectImageView =5;
                                          selectImage();
                                      }
                                  }

        );

        video.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          selectVideo();
                                      }
                                  }

        );


        continues = (Button) rootView.findViewById(R.id.continues);
        continues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == 1) {
                    Fragment fragment = new AddProductFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("image1", imageString1);
                    bundle.putString("image2", imageString2);
                    bundle.putString("image3", imageString3);
                    bundle.putString("image4", imageString4);
                    bundle.putString("image5", imageString5);
                    bundle.putString("vidio", imageString5);
                    fragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_place_holder, fragment);
                    fragmentTransaction.commit();//  object of next fragment

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please Select Image",Toast.LENGTH_LONG).show();
                }
            }
        }

        );

        continue_text.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             if(flag == 1) {
                                                 Fragment fragment = new AddProductFragment();
                                                 FragmentManager fragmentManager = getFragmentManager();
                                                 FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                 Bundle bundle = new Bundle();
                                                 bundle.putString("image1", imageString1);
                                                 bundle.putString("image2", imageString2);
                                                 bundle.putString("image3", imageString3);
                                                 bundle.putString("image4", imageString4);
                                                 bundle.putString("image5", imageString5);
                                                 bundle.putString("vidio", imageString5);
                                                 fragment.setArguments(bundle);
                                                 fragmentTransaction.replace(R.id.fragment_place_holder, fragment);
                                                 fragmentTransaction.commit();//  object of next fragment

                                             } else {
                                                 Toast.makeText(getActivity().getApplicationContext(), "Please Select Image",Toast.LENGTH_LONG).show();
                                             }
                                         }
                                     }

        );

        return rootView;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        //AlertDialog dialog = builder.create();
        builder.show();
    }


    private void selectVideo() {
        final CharSequence[] items = {"Take Video", "Choose from Library", "Cancel"};
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Video!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Video")) {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("video/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        //AlertDialog dialog = builder.create();
        builder.show();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        flag = 1;
        Log.d("resultCode:", "" + resultCode + " - " + requestCode);
        if (resultCode == Activity.RESULT_OK) {
            if(selectImageView == 1){
                uploadImage = image1;
            } else if(selectImageView == 2){
                uploadImage = image2;
            } else if(selectImageView == 3){
                uploadImage = image3;
            } else if(selectImageView == 4){
                uploadImage = image4;
            }else{
                uploadImage = image5;
            }
            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                //thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                thumbnail.compress(Bitmap.CompressFormat.PNG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".png");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                uploadImage.setImageBitmap(thumbnail);

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(getActivity().getApplicationContext(), selectedImageUri, projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 500;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                uploadImage.setImageBitmap(bm);
            }
            if (selectImageView == 1){
                imageString1 = getImageInBase64(uploadImage);
            } else if(selectImageView == 2){
                imageString2 = getImageInBase64(uploadImage);
            } else if(selectImageView == 3){
                imageString3 = getImageInBase64(uploadImage);
            } else if(selectImageView == 4){
                imageString4 = getImageInBase64(uploadImage);
            }else{
                imageString5 = getImageInBase64(uploadImage);
            }
        }
    }
    public String getImageInBase64(ImageView iv){
        iv.buildDrawingCache();
        Bitmap bitmap = iv.getDrawingCache();
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        byte[] image=stream.toByteArray();
        String img_str = Base64.encodeToString(image, 0);
        return img_str;
    }
}
