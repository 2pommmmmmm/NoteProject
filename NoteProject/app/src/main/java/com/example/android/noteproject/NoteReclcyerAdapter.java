package com.example.android.noteproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.noteproject.db.Note;
import com.example.android.noteproject.db.NoteLab;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Derrick on 2018/5/30.
 */

public class NoteReclcyerAdapter  extends RecyclerView.Adapter<NoteReclcyerAdapter.NoteViewHolder>{

    private List<Note> mNoteList;
    private static Context mContext;


    public void setNoteList(List<Note> noteList) {
            mNoteList = noteList;
            }

    public NoteReclcyerAdapter(Context context,List<Note> noteList){
            this.mContext = context.getApplicationContext();
            this.mNoteList = noteList;
            }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_item, parent, false);
            return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
            holder.bind(mNoteList.get(position));
            }

    @Override
    public int getItemCount() {
            return mNoteList.size();
            }

    public static class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTitleTextView;
        private TextView mTimeTextView;
        private ImageView mImageView;
        private View mView;
        private Note mNote;

        public NoteViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.title_text_view);
            mTimeTextView = itemView.findViewById(R.id.time_text_view);
            mImageView = itemView.findViewById(R.id.image_view);
            mView = itemView;
            itemView.setOnClickListener(this);
        }

        public void bind(Note note){
            mNote = note;

            if(mTitleTextView != null && mTimeTextView != null ){
                mTitleTextView.setText(note.getTitle());
                Date date = new Date(note.getModifyTime());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = formatter.format(date);
                mTimeTextView.setText(dateString);
                Log.i("test"," note " + mNote.getUrl());
                File file = NoteLab.getsNoteUtil(mContext).getPhotoFile(mNote);

                if(file != null)
                {
                    mImageView.setImageBitmap(PictureUtils.getScaledBitmap(file.getPath(),mImageView.getWidth(),mImageView.getHeight()));
                }


            }
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(mContext,NoteActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("id",mNote.getId());
            bundle.putString("url",mNote.getUrl());
            bundle.putString("title",mNote.getTitle());
            bundle.putString("content",mNote.getContent());
            bundle.putLong("time",mNote.getModifyTime());
            intent.putExtra("bundle",bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);

        }
    }
}
