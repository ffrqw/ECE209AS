package com.google.android.gms.analytics;

import android.text.TextUtils;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.Promotion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class HitBuilders {

    public static class HitBuilder<T extends HitBuilder> {
        private Map<String, String> zzadJ = new HashMap();
        private Map<String, List<Product>> zzadL = new HashMap();
        private List<Promotion> zzadM = new ArrayList();
        private List<Product> zzadN = new ArrayList();

        protected HitBuilder() {
        }

        public final Map<String, String> build() {
            Map<String, String> hashMap = new HashMap(this.zzadJ);
            int i = 1;
            for (Promotion zzbl : this.zzadM) {
                hashMap.putAll(zzbl.zzbl(zzf.zzI(i)));
                i++;
            }
            i = 1;
            for (Product zzbl2 : this.zzadN) {
                hashMap.putAll(zzbl2.zzbl(zzf.zzG(i)));
                i++;
            }
            int i2 = 1;
            for (Entry entry : this.zzadL.entrySet()) {
                List<Product> list = (List) entry.getValue();
                String zzL = zzf.zzL(i2);
                int i3 = 1;
                for (Product product : list) {
                    String valueOf = String.valueOf(zzL);
                    String valueOf2 = String.valueOf(zzf.zzK(i3));
                    hashMap.putAll(product.zzbl(valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf)));
                    i3++;
                }
                if (!TextUtils.isEmpty((CharSequence) entry.getKey())) {
                    String valueOf3 = String.valueOf(zzL);
                    String valueOf4 = String.valueOf("nm");
                    hashMap.put(valueOf4.length() != 0 ? valueOf3.concat(valueOf4) : new String(valueOf3), (String) entry.getKey());
                }
                i2++;
            }
            return hashMap;
        }

        public final T set(String str, String str2) {
            this.zzadJ.put(str, str2);
            return this;
        }
    }

    public static class EventBuilder extends HitBuilder<EventBuilder> {
        public EventBuilder() {
            set("&t", "event");
        }

        public EventBuilder(String str, String str2) {
            this();
            set("&ec", str);
            set("&ea", str2);
        }
    }

    public static class ScreenViewBuilder extends HitBuilder<ScreenViewBuilder> {
        public ScreenViewBuilder() {
            set("&t", "screenview");
        }
    }
}
