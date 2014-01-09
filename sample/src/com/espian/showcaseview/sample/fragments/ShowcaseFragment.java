package com.espian.showcaseview.sample.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.sample.R;
import com.espian.showcaseview.targets.ViewTarget;

public class ShowcaseFragment extends Fragment {

    ShowcaseView sv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_layout, container);

        Button button = (Button) layout.findViewById(R.id.buttonFragments);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), R.string.it_does_work, Toast.LENGTH_LONG).show();
            }
        });

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //setContentView() needs to be called in the Activity first.
        //That's why it has to be in onActivityCreated().
        ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
        co.hideOnClickOutside = true;
        sv = ShowcaseView.insertShowcaseView(new ViewTarget(R.id.buttonFragments, getActivity()), getActivity(), R.string.showcase_fragment_title, R.string.showcase_fragment_message, co);
    }
}
