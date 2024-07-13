package com.antech.starbiologyedu.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.starproduction.starbiology.R;
import com.starproduction.starbiology.Set.Set1;
import com.starproduction.starbiology.Set.Set2;
import com.starproduction.starbiology.Set.Set3;
import com.starproduction.starbiology.Set.Set4;
import com.starproduction.starbiology.Set.Set5;


public class QuizFragment extends Fragment {

    CardView c1, c2, c3, c4, c5;
    public QuizFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        c1 = view.findViewById(R.id.q1);
        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Set1.class);
                startActivity(intent);
            }
        });

        c2 = view.findViewById(R.id.q2);
        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Set2.class);
                startActivity(intent);
            }
        });

        c3 = view.findViewById(R.id.q3);
        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Set3.class);
                startActivity(intent);
            }
        });

        c4 = view.findViewById(R.id.q4);
        c4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Set4.class);
                startActivity(intent);
            }
        });

        c5 = view.findViewById(R.id.q5);
        c5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Set5.class);
                startActivity(intent);
            }
        });


        return view;
    }
}