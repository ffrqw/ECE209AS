package com.rachio.iro.ui.fragment.blinkup;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.rachio.iro.R;
import com.rachio.iro.ui.activity.HelpActivity;
import com.rachio.iro.ui.fragment.BaseFragment;

public abstract class BaseBlinkupFragment extends BaseFragment {
    protected final void wireUpHelp(View contentView) {
        ((ImageView) contentView.findViewById(R.id.blinkup_help)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(BaseBlinkupFragment.this.getActivity(), HelpActivity.class);
                i.putExtra("article", "33-connecting-iro-to-your-wifi-network");
                BaseBlinkupFragment.this.startActivity(i);
            }
        });
    }
}
