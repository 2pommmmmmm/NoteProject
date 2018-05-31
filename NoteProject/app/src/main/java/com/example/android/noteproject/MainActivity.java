package com.example.android.noteproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.android.noteproject.db.Note;
import com.example.android.noteproject.db.NoteLab;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private MaterialSearchView mSearchView;
    private Spinner mSpinner;
    private List<Note> mNoteList;
    private NoteReclcyerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void initData(){
        mNoteList = NoteLab.getsNoteUtil(this).getNotes();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        Log.i("test","current size "+ mNoteList.size());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.addItemDecoration(new SpaceItemDecoration(20));
        mAdapter = new NoteReclcyerAdapter(this,mNoteList);

        mRecyclerView.setAdapter(mAdapter);

    }

    private void initView(){
        setSupportActionBar(mToolbar);
        registerForContextMenu(mToolbar);
        initSearchView();

    }

    private void updateUI(){
        List<Note> noteList = NoteLab.getsNoteUtil(this).getNotes();
        mNoteList = noteList;
        if(mAdapter == null){
            mAdapter = new NoteReclcyerAdapter(this,noteList);
            mRecyclerView.setAdapter(mAdapter);
        }else {
            if(mAdapter.getItemCount() != noteList.size()){
                mAdapter = new NoteReclcyerAdapter(this,noteList);
                mRecyclerView.setAdapter(mAdapter);
            }else {
                mAdapter.setNoteList(noteList);
                mAdapter.notifyDataSetChanged();
            }
        }
        mAdapter.notifyDataSetChanged();
    }

//    private void updateUI(String order){
//        List<Note> noteList = NoteLab.getsNoteUtil(this).getNotes();
//        mNoteList = noteList;
//        if(mAdapter == null){
//            mAdapter = new NoteReclcyerAdapter(this,noteList);
//            mRecyclerView.setAdapter(mAdapter);
//        }else {
//            if(mAdapter.getItemCount() != noteList.size()){
//                mAdapter = new NoteReclcyerAdapter(this,noteList);
//                mRecyclerView.setAdapter(mAdapter);
//            }else {
//                mAdapter.setNoteList(noteList);
//                mAdapter.notifyDataSetChanged();
//            }
//        }
//        mAdapter.notifyDataSetChanged();
//    }
    /**
     * 初始化搜索框
     */
    private void initSearchView(){
        mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        mSearchView.setVoiceSearch(false);
        mSearchView.setCursorDrawable(R.drawable.custom_cursor);
        mSearchView.setEllipsize(true);

        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Note> queryList = NoteLab.getsNoteUtil(MainActivity.this).search(newText);
                mAdapter.setNoteList(queryList);
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });

        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                updateUI();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent intent = new Intent(MainActivity.this,NoteActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_backup:
                //数据备份
                try {
                    File dbFile = new File("/data/data/com.example.android.noteproject/databases/"+"noteDB.db");
                    File file = new File(Environment.getExternalStorageDirectory(), "noteDB.db");
                    if (!file.exists()) {
                        try {
                            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                                    PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                            }
                            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!=
                                    PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                            }
                            file.createNewFile();

                        } catch (IOException e) {

                            e.printStackTrace();
                        }
                    }else {

                    }
                    FileInputStream is = new FileInputStream(dbFile);
                    FileOutputStream out = new FileOutputStream(file);
                    byte[] buff = new byte[1024];
                    int n = 0;
                    while ((n = is.read(buff)) > 0) {
                        Log.e("tag", "len=" + n);
                        out.write(buff, 0, n);
                    }
                    is.close();
                    out.close();
                }catch (Exception e){
                    Log.i("test","catch " + e.toString());
                }

                return true;
            case R.id.action_backdata:
                //数据恢复
                NoteLab.getsNoteUtil(this).returnData();
                updateUI();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
