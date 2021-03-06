package com.google.android.gms.internal;

import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

class zzani<T extends zzanh> extends zzamg {
    private zzanj<T> zzagT;

    public zzani(zzamj zzamj, zzanj<T> zzanj) {
        super(zzamj);
        this.zzagT = zzanj;
    }

    private final T zza(XmlResourceParser xmlResourceParser) {
        try {
            xmlResourceParser.next();
            int eventType = xmlResourceParser.getEventType();
            while (eventType != 1) {
                if (xmlResourceParser.getEventType() == 2) {
                    String toLowerCase = xmlResourceParser.getName().toLowerCase();
                    if (toLowerCase.equals("screenname")) {
                        CharSequence attributeValue = xmlResourceParser.getAttributeValue(null, "name");
                        CharSequence trim = xmlResourceParser.nextText().trim();
                        if (!TextUtils.isEmpty(attributeValue)) {
                            TextUtils.isEmpty(trim);
                        }
                    } else if (toLowerCase.equals("string")) {
                        r0 = xmlResourceParser.getAttributeValue(null, "name");
                        String trim2 = xmlResourceParser.nextText().trim();
                        if (!(TextUtils.isEmpty(r0) || trim2 == null)) {
                            this.zzagT.zzm(r0, trim2);
                        }
                    } else if (toLowerCase.equals("bool")) {
                        r0 = xmlResourceParser.getAttributeValue(null, "name");
                        r1 = xmlResourceParser.nextText().trim();
                        if (!(TextUtils.isEmpty(r0) || TextUtils.isEmpty(r1))) {
                            try {
                                this.zzagT.zze(r0, Boolean.parseBoolean(r1));
                            } catch (NumberFormatException e) {
                                zzc("Error parsing bool configuration value", r1, e);
                            }
                        }
                    } else if (toLowerCase.equals("integer")) {
                        r0 = xmlResourceParser.getAttributeValue(null, "name");
                        r1 = xmlResourceParser.nextText().trim();
                        if (!(TextUtils.isEmpty(r0) || TextUtils.isEmpty(r1))) {
                            try {
                                this.zzagT.zzd(r0, Integer.parseInt(r1));
                            } catch (NumberFormatException e2) {
                                zzc("Error parsing int configuration value", r1, e2);
                            }
                        }
                    } else {
                        continue;
                    }
                }
                eventType = xmlResourceParser.next();
            }
        } catch (XmlPullParserException e3) {
            zze("Error parsing tracker configuration file", e3);
        } catch (IOException e4) {
            zze("Error parsing tracker configuration file", e4);
        }
        return this.zzagT.zzlm();
    }

    public final T zzP(int i) {
        try {
            return zza(zzkp().zzkE().getResources().getXml(i));
        } catch (NotFoundException e) {
            zzd("inflate() called with unknown resourceId", e);
            return null;
        }
    }
}
