package com.eysa.shapp;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreateGroupAcivity extends Activity {
    ImageView back_image, group_image;
    TextView create;
    EditText group_name;
    String groupImageInBase64;
    static final int REQUEST_CAMERA = 1;
    static final int SELECT_FILE = 2;
    DataAccess dataAccess;
    Button select_group_pic,group_update_btn;
    LinearLayout open_group_select,closed_group_select;
    ImageView open_group_tick,closed_group_tick;
    int groupFlag=0;
    String group_type=null,groupId;
    int updateFlag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group_view);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            updateFlag = bundle.getInt("updateFlag");
            groupId = bundle.getString("groupId");
        }

        back_image = (ImageView) this.findViewById(R.id.close_image);
        group_image = (ImageView) this.findViewById(R.id.group_image);
        select_group_pic=(Button) this.findViewById(R.id.select_group_pic);
        create = (TextView) this.findViewById(R.id.create);
        group_name = (EditText) this.findViewById(R.id.group_name);
        group_update_btn=(Button) this.findViewById(R.id.group_update_btn);


        open_group_select=(LinearLayout) this.findViewById(R.id.open_group_select);
        closed_group_select=(LinearLayout) this.findViewById(R.id.closed_group_select);
        open_group_tick=(ImageView) this.findViewById(R.id.open_group_tick);
        open_group_tick.setColorFilter(getResources().getColor(R.color.WHITE_COLOR));
        closed_group_tick=(ImageView) this.findViewById(R.id.closed_group_tick);
        closed_group_tick.setColorFilter(getResources().getColor(R.color.WHITE_COLOR));

        dataAccess = DataAccess.getInstance(getApplicationContext());
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        select_group_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               selectImage();
            }
        });

        open_group_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupFlag=1;
                open_group_tick.setColorFilter(getResources().getColor(R.color.BID_NOW_COLOR));
                closed_group_tick.setColorFilter(getResources().getColor(R.color.WHITE_COLOR));

            }
        });
        closed_group_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupFlag=2;
                open_group_tick.setColorFilter(getResources().getColor(R.color.WHITE_COLOR));
                closed_group_tick.setColorFilter(getResources().getColor(R.color.BID_NOW_COLOR));
            }
        });
        if(updateFlag==1){
            create.setVisibility(View.GONE);
            group_update_btn.setVisibility(View.VISIBLE);
        }


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupFlag==0){
                    new AlertDialog.Builder(CreateGroupAcivity.this)
                            .setTitle("Shapp")
                            .setMessage("Please select if group is open or closed")
                            .setIcon(R.drawable.app_icon)
                            .setNegativeButton("Ok", null).show();
                }else if(groupFlag==1){
                    group_type="open";
                    if (group_name.getText().toString().equals("")){
                        new AlertDialog.Builder(CreateGroupAcivity.this)
                                .setTitle("Shapp")
                                .setMessage("Group Name is required")
                                .setIcon(R.drawable.app_icon)
                                .setNegativeButton("Ok", null).show();
                    } else {
                        dataAccess.create_group(group_name.getText().toString(), groupImageInBase64, group_type);
                        new AlertDialog.Builder(CreateGroupAcivity.this)
                                .setTitle("Shapp")
                                .setMessage("Group created successfully")
                                .setIcon(R.drawable.app_icon)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //((MainActivity) getActivity()).onCreateFragBackPressed();
                                        finish();
                                    }
                                }).show();
                    }
                }else if(groupFlag==2){
                    group_type="closed";
                    if (group_name.getText().toString().equals("")){
                        new AlertDialog.Builder(CreateGroupAcivity.this)
                                .setTitle("Shapp")
                                .setMessage("Group Name is required")
                                .setIcon(R.drawable.app_icon)
                                .setNegativeButton("Ok", null).show();
                    } else {
                        dataAccess.create_group(group_name.getText().toString(), groupImageInBase64, group_type);
                        new AlertDialog.Builder(CreateGroupAcivity.this)
                                .setTitle("Shapp")
                                .setMessage("Group created successfully")
                                .setIcon(R.drawable.app_icon)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //((MainActivity) getActivity()).onCreateFragBackPressed();
                                        finish();
                                    }
                                }).show();
                    }
                }
            }
        });

        group_update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupFlag==0){
                    new AlertDialog.Builder(CreateGroupAcivity.this)
                            .setTitle("Shapp")
                            .setMessage("Please select if group is open or closed")
                            .setIcon(R.drawable.app_icon)
                            .setNegativeButton("Ok", null).show();
                }else if(groupFlag==1){
                    group_type="open";
                    if (group_name.getText().toString().equals("")){
                        new AlertDialog.Builder(CreateGroupAcivity.this)
                                .setTitle("Shapp")
                                .setMessage("Group Name is required")
                                .setIcon(R.drawable.app_icon)
                                .setNegativeButton("Ok", null).show();
                    } else {
                        dataAccess.update_group(group_name.getText().toString(), groupImageInBase64, group_type, groupId);
                        new AlertDialog.Builder(CreateGroupAcivity.this)
                                .setTitle("Shapp")
                                .setMessage("Group updated successfully")
                                .setIcon(R.drawable.app_icon)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //((MainActivity) getActivity()).onCreateFragBackPressed();
                                        finish();
                                    }
                                }).show();
                    }
                }else if(groupFlag==2){
                    group_type="closed";
                    if (group_name.getText().toString().equals("")){
                        new AlertDialog.Builder(CreateGroupAcivity.this)
                                .setTitle("Shapp")
                                .setMessage("Group Name is required")
                                .setIcon(R.drawable.app_icon)
                                .setNegativeButton("Ok", null).show();
                    } else {
                        dataAccess.update_group(group_name.getText().toString(), groupImageInBase64, group_type, groupId);
                        new AlertDialog.Builder(CreateGroupAcivity.this)
                                .setTitle("Shapp")
                                .setMessage("Group updated successfully")
                                .setIcon(R.drawable.app_icon)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //((MainActivity) getActivity()).onCreateFragBackPressed();
                                        finish();
                                    }
                                }).show();
                    }
                }
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("resultCode:", "" + resultCode + " - " + requestCode);
        if (resultCode == Activity.RESULT_OK) {
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
                group_image.setImageBitmap(thumbnail);
                groupImageInBase64 = getImageInBase64(group_image);

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this.getApplicationContext(), selectedImageUri, projection, null, null, null);
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
                group_image.setImageBitmap(bm);
                groupImageInBase64 = getImageInBase64(group_image);
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
