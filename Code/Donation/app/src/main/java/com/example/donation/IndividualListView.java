package com.example.donation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class IndividualListView extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private String[] numberWord;
    private String[] numberImage;
    private int length;

    public IndividualListView(Context context, String[] numberWord, String[] numberImage){
        this.context = context;
        this.numberImage = numberImage;
        this.numberWord = numberWord;
    }


    @Override
    public int getCount() {
        return numberWord.length-1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (layoutInflater == null){
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.listview, null);
        }
        ImageView imageView = convertView.findViewById(R.id.list_view_image);
        TextView textView = convertView.findViewById(R.id.list_view_description);
        Glide.with(context).load(numberImage[position]).into(imageView);
        textView.setText(numberWord[position]);

        return convertView;
    }

}
