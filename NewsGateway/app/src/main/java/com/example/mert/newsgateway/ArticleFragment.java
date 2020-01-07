package com.example.mert.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;



public class ArticleFragment extends Fragment implements Serializable {
    public static final ArticleFragment newInstance(Article article, int size) {
        ArticleFragment articleFragment = new ArticleFragment();
        Bundle bdl = new Bundle(1);
        bdl.putSerializable("article", article);
        bdl.putInt("size", size);
        articleFragment.setArguments(bdl);
        return articleFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragmentarticle, container, false);

        final Article article = (Article) getArguments().getSerializable("article");

        TextView titleView = rootView.findViewById(R.id.titleView);
        titleView.setText(article.getTitle());
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(article.getUrl()));
                startActivity(i);
            }
        });
        TextView dateView = rootView.findViewById(R.id.dateView);
        if (!article.getDate().isEmpty()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy'T'HH:mm:ss");
            try {
                dateView.setText(simpleDateFormat.parse(article.getDate()).toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            dateView.setText(article.getDate());
        }

        TextView authorView = rootView.findViewById(R.id.authorView);
        authorView.setText(article.getAuthor());

        ImageView imageView = rootView.findViewById(R.id.imageView);
        Picasso picasso = new Picasso.Builder(getActivity()).build();
        picasso.load(article.getImageUrl())
                .error(R.drawable.image_not_found)
                .placeholder(R.drawable.placeholder)
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(article.getUrl()));
                startActivity(i);
            }
        });

        TextView descriptionView = rootView.findViewById(R.id.descView);
        descriptionView.setText(article.getDescription());

        descriptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(article.getUrl()));
                startActivity(i);
            }
        });

        descriptionView.setMovementMethod(new ScrollingMovementMethod());

        TextView countView = rootView.findViewById(R.id.indexView);
        countView.setText(Integer.toString((article.getIndex())+1) + " of " + Integer.toString(getArguments().getInt("size")));

        return rootView;
    }
}