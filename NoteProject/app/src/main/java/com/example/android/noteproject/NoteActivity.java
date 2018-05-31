package com.example.android.noteproject;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.noteproject.db.Note;
import com.example.android.noteproject.db.NoteLab;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public class NoteActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener{
    private Toolbar mToolbar;
    private EditText mTitleET;
    private EditText mContentET;
    private ImageView mImageView;
    private Note mNote;
    private File mPhotoFile;
    private Intent mIntent;
    private String[] mCustomltems = new String[]{"相机拍照"};

    private static final int RESULT_CAMERA = 200;   //相机返回码
    private static final int RESULT_IMAGE=100;      //本地图库返回码

    private static final int REQUEST_PHOTO = 2;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        initData();
        initView();
    }

    private void initData(){
        mToolbar = (Toolbar) findViewById(R.id.note_toolbar);
        mTitleET = (EditText) findViewById(R.id.title_edit_view);
        mContentET = (EditText) findViewById(R.id.content_edit_view);
        mImageView = (ImageView) findViewById(R.id.image_button);

        Bundle bundle = getIntent().getBundleExtra("bundle");
        if(bundle != null){

            int id = bundle.getInt("id");
            String url = bundle.getString("url");
            String title = bundle.getString("title");
            String content = bundle.getString("content");
            long time = bundle.getLong("time");

            mNote = new Note(id,title,content,time);
            mNote.setUrl(url);


            mTitleET.setText(mNote.getTitle());
            mContentET.setText(mNote.getContent());
            Log.i("test","file path" +NoteLab.getsNoteUtil(this).getPhotoFile(mNote).getPath());

            mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mImageView.setImageBitmap(PictureUtils.getScaledBitmap(NoteLab.getsNoteUtil(NoteActivity.this).getPhotoFile(mNote).getPath(),
                            mImageView.getWidth(),mImageView.getHeight()));
                }
            });
        }else {

            mNote = new Note();
            mTitleET.setText("");
            mContentET.setText("");

        }

        mPhotoFile = NoteLab.getsNoteUtil(this).getPhotoFile(mNote);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCustom();
            }
        });

    }

    private void initView(){
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.back_24);
        mToolbar.setOnMenuItemClickListener(this);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    /**
     * 显示对话框
     */
    private void showDialogCustom(){


        //创建对话框
        AlertDialog.Builder builder=new AlertDialog.Builder(NoteActivity.this);
        builder.setTitle("请选择：");
        builder.setItems(mCustomltems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Log.i("test","which "+ which);
                if(which==0) {
                    try{

                        mNote.setUrl(UUID.randomUUID().toString());
                        Log.i("test","m " + mNote.getUrl());
                        mPhotoFile = NoteLab.getsNoteUtil(NoteActivity.this).getPhotoFile(mNote);

                        //照相机
                        //调用FileProvider.getUriForFile(...)会把本地文件路径转换为相机能看见的Uri形式
                       // Log.i("test","file path " + mPhotoFile.getAbsolutePath());
                        Uri uri = FileProvider.getUriForFile(NoteActivity.this,"com.example.android.noteproject.fileprovider",mPhotoFile);

                        //要想获得全尺寸照片，就要让它使用文件系统存储照片。这可以通过传入保存在
                        //MediaStore.EXTRA_OUTPUT中的指向存储路径的Uri来完成
                        captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);

                        //找到所有可以启动类似于相机的Activity
                        List<ResolveInfo> cameraActivities = NoteActivity.this.getPackageManager().
                                queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                        //给上述找到的activity赋予写入文件的权限
                        for (ResolveInfo activity : cameraActivities){
                            NoteActivity.this.grantUriPermission(activity.activityInfo.packageName,uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        }

                        startActivityForResult(captureImage,REQUEST_PHOTO);

                    }catch (Exception e){
                        Log.i("test",e.toString());
                    }


                }
//                else if(which==0){
//
//
//                }
            }
        });
        builder.create().show();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("test","here");
        if(resultCode == Activity.RESULT_OK){
            Log.i("test","OK");
            mIntent = data;
            //如果当前版本大于等于Android 6.0，且该权限未被授予，则申请授权
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                //申请授权，第一个参数为要申请用户授权的权限；第二个参数为requestCode 必须大于等于0，主要用于回调的时候检测，匹配特定的onRequestPermissionsResult。
                //可以从方法名requestPermissions以及第二个参数看出，是支持一次性申请多个权限的，系统会通过对话框逐一询问用户是否授权。
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);

            }else{
                //如果该版本低于6.0，或者该权限已被授予，它则可以继续读取联系人。
                //mIntent = data;
                //getContacts(data);
                if(requestCode == REQUEST_PHOTO){
                    Log.i("test","REQUEST_PHOTO");
                    Uri uri = FileProvider.getUriForFile(this,"com.example.android.noteproject.fileprovider",mPhotoFile);

                    //既然相机已保存了文件，那就再次调用权限，关闭文件访问。
                    this.revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    updatePhotoView(mImageView.getWidth(),mImageView.getHeight());

                }

            }


        }else {
            return;
        }

    }

    /**
     * 更新图片
     * @param width
     * @param height
     */
    private void updatePhotoView(int width, int height){
        if(mPhotoFile == null || !mPhotoFile.exists()){
            mImageView.setImageDrawable(null);
        }else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),width,height);
            mImageView.setImageBitmap(bitmap);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.makesure:
                if(getIntent().getBundleExtra("bundle") != null && mNote != null){

                    if(mTitleET.getText().length() != 0 && mContentET.getText().length() != 0){
                        mNote.setTitle(mTitleET.getText().toString());
                        mNote.setContent(mContentET.getText().toString());
                        mNote.setModifyTime(System.currentTimeMillis());

                        Log.i("test","url"+mNote.getFileName().toString());
                        //保存到数据库
                        NoteLab.getsNoteUtil(this).updateNote(mNote);
                        finish();
                    }else {
                        Toast.makeText(this,"不能保存空笔记",Toast.LENGTH_SHORT).show();
                    }

                }else {

                    if(mTitleET.getText().length() != 0 && mContentET.getText().length() != 0){

                        mNote.setTitle(mTitleET.getText().toString());
                        mNote.setContent(mContentET.getText().toString());
                        mNote.setModifyTime(System.currentTimeMillis());
                        Log.i("test","url"+mNote.getFileName().toString());
                        //保存到数据库
                        NoteLab.getsNoteUtil(this).addNote(mNote);
                        finish();
                    }else {
                        Toast.makeText(this,"不能保存空笔记",Toast.LENGTH_SHORT).show();
                    }
                }

                return true;
            case R.id.delete:
                if(getIntent().getBundleExtra("bundle") != null && mNote != null){
                    NoteLab.getsNoteUtil(this).deleteNote(mNote.getId());
                    finish();
                }else {
                    mTitleET.setText("");
                    mContentET.setText("");
                }
                return true;


        }


        return true;
    }
}
