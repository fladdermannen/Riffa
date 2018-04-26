package com.example.absol.riffa;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class FragmentMain extends Fragment {
    View view;
    SwipeRefreshLayout swipeLayout;
    CardView card1, card2, card3, card4, card5, card6, card7, card8, card9;
    TextView title1, title2, title3, title4, title5, title6, title7, title8, title9;
    TextView author1, author2, author3, author4, author5, author6, author7, author8, author9;

    private ArrayList<Recording> trendingRecordingsList = new ArrayList<>();


    public FragmentMain() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fragment,container,false);


        swipeLayout = view.findViewById(R.id.main_swipelayout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("Patrik", "onRefresh: swipe TRENDING refresh called");

                updateCards();
                swipeLayout.setRefreshing(false);
            }
        });

        card1 = view.findViewById(R.id.trending_card_first);
        card2 = view.findViewById(R.id.trending_card_second);
        card3 = view.findViewById(R.id.trending_card_third);
        card4 = view.findViewById(R.id.trending_card_fourth);
        card5 = view.findViewById(R.id.trending_card_fifth);
        card6 = view.findViewById(R.id.trending_card_sixth);
        card7 = view.findViewById(R.id.trending_card_seventh);
        card8 = view.findViewById(R.id.trending_card_eighth);
        card9 = view.findViewById(R.id.trending_card_ninth);
        title1 = view.findViewById(R.id.trending_title);
        title2 = view.findViewById(R.id.trending_title_second);
        title3 = view.findViewById(R.id.trending_title_third);
        title4 = view.findViewById(R.id.trending_title_fourth);
        title5 = view.findViewById(R.id.trending_title_fifth);
        title6 = view.findViewById(R.id.trending_title_sixth);
        title7 = view.findViewById(R.id.trending_title_seventh);
        title8 = view.findViewById(R.id.trending_title_eighth);
        title9 = view.findViewById(R.id.trending_title_ninth);
        author1 = view.findViewById(R.id.trending_author);
        author2 = view.findViewById(R.id.trending_author_second);
        author3 = view.findViewById(R.id.trending_author_third);
        author4 = view.findViewById(R.id.trending_author_fourth);
        author5 = view.findViewById(R.id.trending_author_fifth);
        author6 = view.findViewById(R.id.trending_author_sixth);
        author7 = view.findViewById(R.id.trending_author_seventh);
        author8 = view.findViewById(R.id.trending_author_eighth);
        author9 = view.findViewById(R.id.trending_author_ninth);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return view;
    }

    private void updateCards() {

    }

    private void loadData() {

    }
}
