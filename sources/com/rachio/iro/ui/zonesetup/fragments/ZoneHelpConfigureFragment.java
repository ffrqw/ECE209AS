package com.rachio.iro.ui.zonesetup.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.rachio.iro.R;
import com.rachio.iro.model.device.Zone;
import com.rachio.iro.model.user.User;
import com.rachio.iro.model.zoneproperties.Crop;
import com.rachio.iro.model.zoneproperties.Nozzle;
import com.rachio.iro.model.zoneproperties.Shade;
import com.rachio.iro.model.zoneproperties.Slope;
import com.rachio.iro.model.zoneproperties.Soil;
import com.rachio.iro.ui.fragment.zoneproperties.CropListFragment;
import com.rachio.iro.ui.fragment.zoneproperties.NozzleListFragment;
import com.rachio.iro.ui.fragment.zoneproperties.ShadeListFragment;
import com.rachio.iro.ui.fragment.zoneproperties.SlopeListFragment;
import com.rachio.iro.ui.fragment.zoneproperties.SoilListFragment;
import com.rachio.iro.ui.fragment.zoneproperties.ZoneNameFragment;
import com.rachio.iro.ui.fragment.zoneproperties.ZonePropertyFragment;
import com.rachio.iro.ui.zonesetup.ZoneHelpActivity;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.PageIndicator;

public class ZoneHelpConfigureFragment extends BaseZoneHelpFragment {
    private final Fragment[] frags = new Fragment[]{new ZoneNameFragment(), new CropListFragment(), new SoilListFragment(), new ShadeListFragment(), new NozzleListFragment(), new SlopeListFragment()};
    private Button nextButton;
    private ViewPager pager;
    private TextView titleTextView;

    public static ZoneHelpConfigureFragment newInstance() {
        return new ZoneHelpConfigureFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        parent.removeAllViews();
        View v = inflater.inflate(R.layout.fragment_zone_help_configure, parent, false);
        wireUpHelpExit(v);
        this.titleTextView = (TextView) v.findViewById(R.id.configure_title);
        this.pager = (ViewPager) v.findViewById(R.id.zoneconfigure_pager);
        PageIndicator indicator = (PageIndicator) v.findViewById(R.id.zoneconfigure_indicator);
        this.nextButton = (Button) v.findViewById(R.id.next_button);
        this.pager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            public final int getCount() {
                return ZoneHelpConfigureFragment.this.frags.length;
            }

            public final Fragment getItem(int arg0) {
                return ZoneHelpConfigureFragment.this.frags[arg0];
            }
        });
        indicator.setViewPager(this.pager);
        indicator.setOnPageChangeListener(new OnPageChangeListener() {
            public final void onPageSelected(int pos) {
                if (pos != ZoneHelpConfigureFragment.this.frags.length - 1) {
                    ZoneHelpConfigureFragment.this.nextButton.setText("NEXT");
                } else if (((ZoneHelpActivity) ZoneHelpConfigureFragment.this.getActivity()).isLastZone()) {
                    ZoneHelpConfigureFragment.this.nextButton.setText("FINISH");
                } else {
                    ZoneHelpConfigureFragment.this.nextButton.setText("NEXT ZONE");
                }
            }

            public final void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public final void onPageScrollStateChanged(int arg0) {
            }
        });
        this.nextButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ZoneHelpConfigureFragment.this.pager.getCurrentItem() == ZoneHelpConfigureFragment.this.frags.length - 1) {
                    ((ZoneHelpActivity) ZoneHelpConfigureFragment.this.getActivity()).saveThisZone();
                } else {
                    ZoneHelpConfigureFragment.this.pager.setCurrentItem(ZoneHelpConfigureFragment.this.pager.getCurrentItem() + 1, true);
                }
            }
        });
        return v;
    }

    public final void updateState(int number, Zone zone) {
        super.updateState(number, zone);
        this.titleTextView.setText("Configure Zone " + Integer.toString(number));
        ((ZoneNameFragment) ((FragmentPagerAdapter) this.pager.getAdapter()).getItem(0)).setName(zone.name);
        ((ZonePropertyFragment) ((FragmentPagerAdapter) this.pager.getAdapter()).getItem(1)).setSelectedId(zone.customCrop.id);
        ((ZonePropertyFragment) ((FragmentPagerAdapter) this.pager.getAdapter()).getItem(2)).setSelectedId(zone.customSoil.id);
        ((ZonePropertyFragment) ((FragmentPagerAdapter) this.pager.getAdapter()).getItem(3)).setSelectedId(zone.customShade.id);
        ((ZonePropertyFragment) ((FragmentPagerAdapter) this.pager.getAdapter()).getItem(4)).setSelectedId(zone.customNozzle.id);
        ((ZonePropertyFragment) ((FragmentPagerAdapter) this.pager.getAdapter()).getItem(5)).setSelectedId(zone.customSlope.id);
    }

    public final boolean validate() {
        super.validate();
        if (!TextUtils.isEmpty(((ZoneNameFragment) this.frags[0]).getName())) {
            return true;
        }
        Toast.makeText(getActivity(), "You must enter a description for this zone", 0).show();
        return false;
    }

    public final void commitState(Zone zone) {
        super.commitState(zone);
        zone.name = ((ZoneNameFragment) this.frags[0]).getName();
        zone.customCrop.id = ((ZonePropertyFragment) this.frags[1]).getSelectedId();
        zone.customSoil.id = ((ZonePropertyFragment) this.frags[2]).getSelectedId();
        zone.customShade.id = ((ZonePropertyFragment) this.frags[3]).getSelectedId();
        zone.customNozzle.id = ((ZonePropertyFragment) this.frags[4]).getSelectedId();
        zone.customSlope.id = ((ZonePropertyFragment) this.frags[5]).getSelectedId();
        zone.enabled = true;
    }

    public static void preload$48c7a957(User user) {
        int i = 0;
        if (user != null) {
            if (user.nozzles != null) {
                for (Nozzle n : user.nozzles) {
                    Picasso.with().load(n.imageUrl).fetch();
                }
            }
            if (user.soils != null) {
                for (Soil s : user.soils) {
                    Picasso.with().load(s.imageUrl).fetch();
                }
            }
            if (user.crops != null) {
                for (Crop c : user.crops) {
                    Picasso.with().load(c.imageUrl).fetch();
                }
            }
            if (user.slopes != null) {
                for (Slope s2 : user.slopes) {
                    Picasso.with().load(s2.imageUrl).fetch();
                }
            }
            if (user.shades != null) {
                Shade[] shadeArr = user.shades;
                int length = shadeArr.length;
                while (i < length) {
                    Picasso.with().load(shadeArr[i].imageUrl).fetch();
                    i++;
                }
            }
        }
    }
}
