package com.github.amlcurran.showcaseview.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.espian.showcaseview.sample.R;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

public class FragmentDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_demo);
    }

    public void onHiddenFirstShowcase() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_host_two, new SecondDemoFragment())
                .commit();
    }

    public static class FirstDemoFragment extends Fragment {

        private Button button;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_layout, container, false);
            button = (Button) view.findViewById(R.id.fragment_demo_button);
            return view;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            new ShowcaseView.Builder(getActivity())
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setTarget(new ViewTarget(button))
                    .hideOnTouchOutside()
                    .setContentTitle(R.string.showcase_fragment_title)
                    .setContentText(R.string.showcase_fragment_message)
                    .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                        @Override
                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                            ((FragmentDemoActivity) getActivity()).onHiddenFirstShowcase();
                        }

                    })
                    .build();
        }

    }

    public static class SecondDemoFragment extends Fragment {

        private Button button;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_layout, container, false);
            button = (Button) view.findViewById(R.id.fragment_demo_button);
            return view;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            new ShowcaseView.Builder(getActivity())
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setTarget(new ViewTarget(button))
                    .hideOnTouchOutside()
                    .setContentTitle(R.string.showcase_fragment_title_2)
                    .setContentText(R.string.showcase_fragment_message_2)
                    .build();
        }

    }
}
