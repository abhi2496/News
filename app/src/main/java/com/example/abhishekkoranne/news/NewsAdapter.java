package com.example.abhishekkoranne.news;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, List<News> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent, false);
        }

        News currentNews = getItem(position);

        //Topic of news
        TextView newsTopic = (TextView)listItemView.findViewById(R.id.newsTopic);
        newsTopic.setText(currentNews.getTopic());

        //Title of News
        TextView newsTitle = (TextView)listItemView.findViewById(R.id.newsTitle);
        newsTitle.setText(currentNews.getTitle());

        //Author of News
        TextView newsAuthor = (TextView)listItemView.findViewById(R.id.newsAuthor);
        newsAuthor.setText(currentNews.getAuthor());

        //Date of News
        TextView newsDate = (TextView)listItemView.findViewById(R.id.newsDate);
        String formattedDate = dateFormatter(currentNews.getDate());
        newsDate.setText(formattedDate);

        return listItemView;
    }

    private String dateFormatter(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM h:mm a");
        String formattedDate = null;
        try{
            formattedDate = outputFormat.format(inputFormat.parse(inputDate));
        } catch (ParseException e) {
            Log.e("NewsAdapter", "Problem To Get Date", e);
        }
        return formattedDate;
    }
}
