package com.rachio.iro.ui.view;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.model.Event;
import com.rachio.iro.model.Event.Action;
import com.rachio.iro.utils.ProgressDialogAsyncTask;
import com.rachio.iro.utils.TimeStringUtil;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request.Builder;
import com.squareup.picasso.Picasso;
import java.io.IOException;

public class EventCardItem extends RelativeLayout {
    private TextView actionableText;
    private TextView descriptionText;
    private View divider;
    private String eventId;
    private ImageView statusIcon;
    private TextView timestampText;

    private class PerformActionableTask extends ProgressDialogAsyncTask<String, Void, String> {
        protected /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
            super.onPostExecute((String) obj);
        }

        public PerformActionableTask(Context context) {
            super(context);
        }

        private static String doInBackground(String... urls) {
            OkHttpClient httpClient = new OkHttpClient();
            Builder builder = new Builder();
            String str = urls[0];
            if (str == null) {
                throw new IllegalArgumentException("url == null");
            }
            if (str.regionMatches(true, 0, "ws:", 0, 3)) {
                str = "http:" + str.substring(3);
            } else if (str.regionMatches(true, 0, "wss:", 0, 4)) {
                str = "https:" + str.substring(4);
            }
            HttpUrl parse = HttpUrl.parse(str);
            if (parse == null) {
                throw new IllegalArgumentException("unexpected url: " + str);
            }
            try {
                return httpClient.newCall(builder.url(parse).build()).execute().body().string();
            } catch (IOException e) {
                return null;
            }
        }
    }

    public EventCardItem(Context context) {
        this(context, null);
    }

    private EventCardItem(Context context, AttributeSet attrs) {
        super(context, null);
        inflate(context, R.layout.view_card_event_item, this);
        this.statusIcon = (ImageView) findViewById(R.id.event_status_icon);
        this.timestampText = (TextView) findViewById(R.id.event_timestamp_text);
        this.descriptionText = (TextView) findViewById(R.id.event_description_text);
        this.actionableText = (TextView) findViewById(R.id.event_actionable);
        this.divider = findViewById(R.id.event_card_divider);
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.card_padding);
        setPadding(0, dimensionPixelSize, 0, 0);
        this.divider.setPadding(0, dimensionPixelSize, 0, 0);
    }

    public final void setEvent(Event event, boolean lastItem) {
        if (event == null) {
            setVisibility(4);
            return;
        }
        this.eventId = event.id;
        Picasso.with().load(event.iconUrl).into(this.statusIcon);
        this.timestampText.setText(TimeStringUtil.getDisplayDateTimeOfEvent(event.eventDate));
        int startIndex = -1;
        int endIndex = 0;
        String name = event.getStringValue("zoneName");
        if (name == null) {
            name = event.getStringValue("scheduleRuleName");
        }
        if (!TextUtils.isEmpty(name)) {
            startIndex = event.summary.indexOf(name);
            endIndex = startIndex + name.length();
        }
        Spannable summarySpan = new SpannableString(event.summary);
        if (startIndex >= 0) {
            summarySpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.rachio_blue)), startIndex, endIndex, 33);
        }
        this.descriptionText.setText(summarySpan);
        final Action action = event.getAction();
        if (action == null || !action.isActive()) {
            this.actionableText.setVisibility(8);
        } else {
            this.actionableText.setText(action.label);
            this.actionableText.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    new PerformActionableTask(v.getContext()).execute(new String[]{action.url});
                }
            });
        }
        if (lastItem) {
            this.divider.setVisibility(8);
        }
    }
}
