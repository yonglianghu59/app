package com.example.project_new_take_out.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project_new_take_out.R;

/**
 * 引导页单项
 */
public class OnboardingPageFragment extends Fragment {

    private static final String ARG_ICON = "icon_res";
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESC = "desc";

    static OnboardingPageFragment newInstance(int iconRes, String title, String desc) {
        OnboardingPageFragment f = new OnboardingPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ICON, iconRes);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESC, desc);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_onboarding_page, container, false);

        Bundle args = getArguments();
        if (args != null) {
            ImageView iv = view.findViewById(R.id.iv_onboarding);
            TextView tvTitle = view.findViewById(R.id.tv_onboarding_title);
            TextView tvDesc = view.findViewById(R.id.tv_onboarding_desc);

            iv.setImageResource(args.getInt(ARG_ICON));
            tvTitle.setText(args.getString(ARG_TITLE));
            tvDesc.setText(args.getString(ARG_DESC));
        }
        return view;
    }
}
