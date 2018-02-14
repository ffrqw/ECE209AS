package com.instabug.library.d;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.instabug.library.IBGCustomTextPlaceHolder.Key;
import com.instabug.library.InstabugMessageUploaderService;
import com.instabug.library.h;
import com.instabug.library.internal.d.a.d;
import com.instabug.library.internal.d.a.e;
import com.instabug.library.internal.d.a.f;
import com.instabug.library.internal.d.a.j;
import com.instabug.library.model.g;
import com.instabug.library.model.i;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.l;
import com.instabug.library.view.CircularImageView;
import com.rachio.iro.R;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

public final class b extends h implements OnClickListener, e, d<com.instabug.library.model.c> {
    private ListView a;
    private EditText b;
    private String c;
    private a d;
    private MediaPlayer e;
    private b f;
    private com.instabug.library.model.c g;
    private boolean h = true;
    private PublishSubject<Integer> i;
    private Subscription j;

    public interface b {
        void c(String str);
    }

    public class a extends BaseAdapter {
        final /* synthetic */ b a;
        private List<Object> b;
        private String c = "";

        public a(b bVar, List<Object> list) {
            this.a = bVar;
            this.b = list;
        }

        public final int getViewTypeCount() {
            return 6;
        }

        public final int getItemViewType(int i) {
            Object item = getItem(i);
            if (item instanceof g) {
                return ((g) item).k() ? 0 : 1;
            } else {
                if (((com.instabug.library.model.b) item).a().contains("image")) {
                    return ((com.instabug.library.model.b) item).d() ? 2 : 3;
                } else {
                    if (((com.instabug.library.model.b) item).a().contains("video") || ((com.instabug.library.model.b) item).a().equalsIgnoreCase("application/octet-stream")) {
                        return ((com.instabug.library.model.b) item).d() ? 4 : 5;
                    } else {
                        return -1;
                    }
                }
            }
        }

        public final int getCount() {
            return this.b.size();
        }

        public final Object getItem(int i) {
            return this.b.get(i);
        }

        public final long getItemId(int i) {
            return (long) i;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final android.view.View getView(int r6, android.view.View r7, android.view.ViewGroup r8) {
            /*
            r5 = this;
            r3 = 0;
            r2 = r5.getItemViewType(r6);
            if (r7 != 0) goto L_0x007d;
        L_0x0007:
            switch(r2) {
                case 0: goto L_0x003d;
                case 1: goto L_0x006d;
                case 2: goto L_0x001d;
                case 3: goto L_0x004d;
                case 4: goto L_0x002d;
                case 5: goto L_0x005d;
                default: goto L_0x000a;
            };
        L_0x000a:
            r0 = new com.instabug.library.d.b$c;
            r1 = r5.a;
            r0.<init>(r1, r7);
            r7.setTag(r0);
            r1 = r0;
        L_0x0015:
            r0 = r5.getItem(r6);	 Catch:{ ParseException -> 0x00bc }
            switch(r2) {
                case 0: goto L_0x0085;
                case 1: goto L_0x0094;
                case 2: goto L_0x00c2;
                case 3: goto L_0x00d1;
                case 4: goto L_0x010a;
                case 5: goto L_0x0119;
                default: goto L_0x001c;
            };
        L_0x001c:
            return r7;
        L_0x001d:
            r0 = r8.getContext();
            r0 = android.view.LayoutInflater.from(r0);
            r1 = 2130903222; // 0x7f0300b6 float:1.7413256E38 double:1.0528060766E-314;
            r7 = r0.inflate(r1, r8, r3);
            goto L_0x000a;
        L_0x002d:
            r0 = r8.getContext();
            r0 = android.view.LayoutInflater.from(r0);
            r1 = 2130903225; // 0x7f0300b9 float:1.7413262E38 double:1.052806078E-314;
            r7 = r0.inflate(r1, r8, r3);
            goto L_0x000a;
        L_0x003d:
            r0 = r8.getContext();
            r0 = android.view.LayoutInflater.from(r0);
            r1 = 2130903223; // 0x7f0300b7 float:1.7413258E38 double:1.052806077E-314;
            r7 = r0.inflate(r1, r8, r3);
            goto L_0x000a;
        L_0x004d:
            r0 = r8.getContext();
            r0 = android.view.LayoutInflater.from(r0);
            r1 = 2130903221; // 0x7f0300b5 float:1.7413254E38 double:1.052806076E-314;
            r7 = r0.inflate(r1, r8, r3);
            goto L_0x000a;
        L_0x005d:
            r0 = r8.getContext();
            r0 = android.view.LayoutInflater.from(r0);
            r1 = 2130903224; // 0x7f0300b8 float:1.741326E38 double:1.0528060776E-314;
            r7 = r0.inflate(r1, r8, r3);
            goto L_0x000a;
        L_0x006d:
            r0 = r8.getContext();
            r0 = android.view.LayoutInflater.from(r0);
            r1 = 2130903220; // 0x7f0300b4 float:1.7413252E38 double:1.0528060756E-314;
            r7 = r0.inflate(r1, r8, r3);
            goto L_0x000a;
        L_0x007d:
            r0 = r7.getTag();
            r0 = (com.instabug.library.d.b.c) r0;
            r1 = r0;
            goto L_0x0015;
        L_0x0085:
            r2 = r1.e;	 Catch:{ ParseException -> 0x00bc }
            r2 = r2.getBackground();	 Catch:{ ParseException -> 0x00bc }
            r2 = com.instabug.library.util.c.a(r2);	 Catch:{ ParseException -> 0x00bc }
            r3 = r1.e;	 Catch:{ ParseException -> 0x00bc }
            r3.setBackgroundDrawable(r2);	 Catch:{ ParseException -> 0x00bc }
        L_0x0094:
            r0 = (com.instabug.library.model.g) r0;	 Catch:{ ParseException -> 0x00bc }
            r2 = r1.b;	 Catch:{ ParseException -> 0x00bc }
            r3 = r0.e();	 Catch:{ ParseException -> 0x00bc }
            r3 = com.instabug.library.util.g.a(r3);	 Catch:{ ParseException -> 0x00bc }
            r2.setText(r3);	 Catch:{ ParseException -> 0x00bc }
            r2 = r1.e;	 Catch:{ ParseException -> 0x00bc }
            r3 = r0.c();	 Catch:{ ParseException -> 0x00bc }
            r2.setText(r3);	 Catch:{ ParseException -> 0x00bc }
            r2 = r1.a;	 Catch:{ ParseException -> 0x00bc }
            if (r2 == 0) goto L_0x001c;
        L_0x00b0:
            r0 = r0.h();	 Catch:{ ParseException -> 0x00bc }
            r1 = r1.a;	 Catch:{ ParseException -> 0x00bc }
            r2 = 0;
            r5.a(r0, r1, r2);	 Catch:{ ParseException -> 0x00bc }
            goto L_0x001c;
        L_0x00bc:
            r0 = move-exception;
            r0.printStackTrace();
            goto L_0x001c;
        L_0x00c2:
            r2 = r1.c;	 Catch:{ ParseException -> 0x00bc }
            r2 = r2.getBackground();	 Catch:{ ParseException -> 0x00bc }
            r2 = com.instabug.library.util.c.a(r2);	 Catch:{ ParseException -> 0x00bc }
            r3 = r1.c;	 Catch:{ ParseException -> 0x00bc }
            r3.setBackgroundDrawable(r2);	 Catch:{ ParseException -> 0x00bc }
        L_0x00d1:
            r0 = (com.instabug.library.model.b) r0;	 Catch:{ ParseException -> 0x00bc }
            r2 = r1.b;	 Catch:{ ParseException -> 0x00bc }
            r3 = r0.f();	 Catch:{ ParseException -> 0x00bc }
            r3 = com.instabug.library.util.g.a(r3);	 Catch:{ ParseException -> 0x00bc }
            r2.setText(r3);	 Catch:{ ParseException -> 0x00bc }
            r2 = r0.b();	 Catch:{ ParseException -> 0x00bc }
            r3 = r1.c;	 Catch:{ ParseException -> 0x00bc }
            r4 = 1;
            r5.a(r2, r3, r4);	 Catch:{ ParseException -> 0x00bc }
            r2 = r1.c;	 Catch:{ ParseException -> 0x00bc }
            r3 = new com.instabug.library.d.b$a$1;	 Catch:{ ParseException -> 0x00bc }
            r3.<init>(r5, r0);	 Catch:{ ParseException -> 0x00bc }
            r2.setOnClickListener(r3);	 Catch:{ ParseException -> 0x00bc }
            r2 = r1.a;	 Catch:{ ParseException -> 0x00bc }
            if (r2 == 0) goto L_0x001c;
        L_0x00f8:
            r2 = r0.e();	 Catch:{ ParseException -> 0x00bc }
            if (r2 == 0) goto L_0x001c;
        L_0x00fe:
            r0 = r0.e();	 Catch:{ ParseException -> 0x00bc }
            r1 = r1.a;	 Catch:{ ParseException -> 0x00bc }
            r2 = 0;
            r5.a(r0, r1, r2);	 Catch:{ ParseException -> 0x00bc }
            goto L_0x001c;
        L_0x010a:
            r2 = r1.f;	 Catch:{ ParseException -> 0x00bc }
            r2 = r2.getBackground();	 Catch:{ ParseException -> 0x00bc }
            r2 = com.instabug.library.util.c.a(r2);	 Catch:{ ParseException -> 0x00bc }
            r3 = r1.f;	 Catch:{ ParseException -> 0x00bc }
            r3.setBackgroundDrawable(r2);	 Catch:{ ParseException -> 0x00bc }
        L_0x0119:
            r0 = (com.instabug.library.model.b) r0;	 Catch:{ ParseException -> 0x00bc }
            r2 = r1.b;	 Catch:{ ParseException -> 0x00bc }
            r3 = r0.f();	 Catch:{ ParseException -> 0x00bc }
            r3 = com.instabug.library.util.g.a(r3);	 Catch:{ ParseException -> 0x00bc }
            r2.setText(r3);	 Catch:{ ParseException -> 0x00bc }
            r2 = r5.a;	 Catch:{ ParseException -> 0x00bc }
            r3 = r5.a;	 Catch:{ ParseException -> 0x00bc }
            r3 = com.instabug.library.d.b.d(r3);	 Catch:{ ParseException -> 0x00bc }
            r2.e = r3;	 Catch:{ ParseException -> 0x00bc }
            r2 = r5.a;	 Catch:{ ParseException -> 0x00bc }
            r2 = r2.e;	 Catch:{ ParseException -> 0x00bc }
            r3 = new com.instabug.library.d.b$a$2;	 Catch:{ ParseException -> 0x00bc }
            r3.<init>(r5, r1, r0);	 Catch:{ ParseException -> 0x00bc }
            r2.setOnPreparedListener(r3);	 Catch:{ ParseException -> 0x00bc }
            r2 = r5.a;	 Catch:{ ParseException -> 0x00bc }
            r2 = r2.e;	 Catch:{ ParseException -> 0x00bc }
            r3 = new com.instabug.library.d.b$a$3;	 Catch:{ ParseException -> 0x00bc }
            r3.<init>(r5, r1, r0);	 Catch:{ ParseException -> 0x00bc }
            r2.setOnCompletionListener(r3);	 Catch:{ ParseException -> 0x00bc }
            r2 = r1.d;	 Catch:{ ParseException -> 0x00bc }
            r3 = new com.instabug.library.d.b$a$4;	 Catch:{ ParseException -> 0x00bc }
            r3.<init>(r5, r0, r1);	 Catch:{ ParseException -> 0x00bc }
            r2.setOnClickListener(r3);	 Catch:{ ParseException -> 0x00bc }
            r2 = r0.c();	 Catch:{ ParseException -> 0x00bc }
            if (r2 == 0) goto L_0x019b;
        L_0x015f:
            r2 = r1.d;	 Catch:{ ParseException -> 0x00bc }
            r3 = 2130837796; // 0x7f020124 float:1.7280556E38 double:1.052773752E-314;
            r2.setImageResource(r3);	 Catch:{ ParseException -> 0x00bc }
        L_0x0167:
            r2 = r5.a;	 Catch:{ ParseException -> 0x00bc }
            r2 = r2.getActivity();	 Catch:{ ParseException -> 0x00bc }
            r3 = r0.b();	 Catch:{ ParseException -> 0x00bc }
            r4 = com.instabug.library.model.a.a.b$6ed2d6d3;	 Catch:{ ParseException -> 0x00bc }
            r2 = com.instabug.library.internal.d.a.b.a$5bd48f4a(r2, r3, r4);	 Catch:{ ParseException -> 0x00bc }
            r3 = r5.a;	 Catch:{ ParseException -> 0x00bc }
            r3 = r3.getActivity();	 Catch:{ ParseException -> 0x00bc }
            r4 = new com.instabug.library.d.b$a$5;	 Catch:{ ParseException -> 0x00bc }
            r4.<init>(r5, r1);	 Catch:{ ParseException -> 0x00bc }
            com.instabug.library.internal.d.a.b.a(r3, r2, r4);	 Catch:{ ParseException -> 0x00bc }
            r2 = r1.a;	 Catch:{ ParseException -> 0x00bc }
            if (r2 == 0) goto L_0x001c;
        L_0x0189:
            r2 = r0.e();	 Catch:{ ParseException -> 0x00bc }
            if (r2 == 0) goto L_0x001c;
        L_0x018f:
            r0 = r0.e();	 Catch:{ ParseException -> 0x00bc }
            r1 = r1.a;	 Catch:{ ParseException -> 0x00bc }
            r2 = 0;
            r5.a(r0, r1, r2);	 Catch:{ ParseException -> 0x00bc }
            goto L_0x001c;
        L_0x019b:
            r2 = r1.d;	 Catch:{ ParseException -> 0x00bc }
            r3 = 2130837797; // 0x7f020125 float:1.7280558E38 double:1.0527737524E-314;
            r2.setImageResource(r3);	 Catch:{ ParseException -> 0x00bc }
            goto L_0x0167;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.instabug.library.d.b.a.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
        }

        private void a(String str, final ImageView imageView, final boolean z) {
            com.instabug.library.internal.d.a.b.a(this.a.getActivity(), com.instabug.library.internal.d.a.b.a$5bd48f4a(this.a.getActivity(), str, com.instabug.library.model.a.a.a$6ed2d6d3), new com.instabug.library.internal.d.a.b.b(this) {
                final /* synthetic */ a c;

                public final void a(com.instabug.library.model.a aVar) {
                    InstabugSDKLogger.d(this, "Asset Entity downloaded: " + aVar.c().getPath());
                    try {
                        imageView.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(aVar.c())));
                        if (z && this.c.a.h) {
                            this.c.a.a.setSelection(this.c.getCount() - 1);
                            this.c.a.h = false;
                        }
                    } catch (Throwable e) {
                        InstabugSDKLogger.e(this, "Asset Entity downloading got FileNotFoundException error", e);
                    }
                }

                public final void a(Throwable th) {
                    InstabugSDKLogger.e(this, "Asset Entity downloading got error", th);
                }
            });
        }

        public final List<Object> a() {
            return this.b;
        }

        static /* synthetic */ void a(a aVar, ImageView imageView, com.instabug.library.model.b bVar) {
            if (aVar.a.e.isPlaying()) {
                aVar.a.e.pause();
                imageView.setImageResource(R.drawable.instabug_ic_play);
                bVar.a(false);
                return;
            }
            aVar.a.e.start();
            imageView.setImageResource(R.drawable.instabug_ic_pause);
            bVar.a(true);
        }

        static /* synthetic */ void a(a aVar, String str) {
            if (aVar.a.e.isPlaying()) {
                aVar.a.e.stop();
                aVar.a.e.reset();
            }
            com.instabug.library.internal.d.a.b.a(aVar.a.getActivity(), com.instabug.library.internal.d.a.b.a$5bd48f4a(aVar.a.getActivity(), str, com.instabug.library.model.a.a.b$6ed2d6d3), new com.instabug.library.internal.d.a.b.b(aVar) {
                final /* synthetic */ a a;

                {
                    this.a = r1;
                }

                public final void a(com.instabug.library.model.a aVar) {
                    InstabugSDKLogger.d(this, "Asset Entity downloaded: " + aVar.c().getPath());
                    try {
                        this.a.a.e.reset();
                        this.a.a.e.setDataSource(aVar.c().getPath());
                        this.a.a.e.prepareAsync();
                    } catch (Throwable e) {
                        InstabugSDKLogger.e(this, "Asset Entity downloading got FileNotFoundException error", e);
                    }
                }

                public final void a(Throwable th) {
                    InstabugSDKLogger.e(this, "Asset Entity downloading got error", th);
                }
            });
        }
    }

    public class c {
        public CircularImageView a;
        public TextView b;
        public ImageView c;
        public ImageView d;
        public TextView e;
        public FrameLayout f;
        public ProgressBar g;
        final /* synthetic */ b h;

        public c(b bVar, View view) {
            this.h = bVar;
            this.a = (CircularImageView) view.findViewById(R.id.instabug_img_message_sender);
            this.b = (TextView) view.findViewById(R.id.instabug_txt_message_time);
            this.e = (TextView) view.findViewById(R.id.instabug_txt_message_body);
            this.c = (ImageView) view.findViewById(R.id.instabug_img_attachment);
            this.d = (ImageView) view.findViewById(R.id.instabug_btn_play_audio);
            this.f = (FrameLayout) view.findViewById(R.id.instabug_audio_attachment);
            this.g = (ProgressBar) view.findViewById(R.id.instabug_audio_attachment_progress_bar);
        }
    }

    public static b b(String str) {
        b bVar = new b();
        Bundle bundle = new Bundle();
        bundle.putString("issue.number", str);
        bVar.setArguments(bundle);
        return bVar;
    }

    protected final int b() {
        return R.layout.instabug_lyt_conversation;
    }

    protected final String c() {
        return getString(R.string.instabug_str_empty);
    }

    protected final void a() {
        this.c = getArguments().getString("issue.number");
    }

    protected final void b(Bundle bundle) {
        this.c = bundle.getString("issue.number");
        this.g = f.a(this.c);
    }

    public final void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.a = (ListView) view.findViewById(R.id.instabug_lst_messages);
        this.b = (EditText) view.findViewById(R.id.edtxt_new_message);
        ImageView imageView = (ImageView) view.findViewById(R.id.instabug_btn_send);
        imageView.setOnClickListener(this);
        com.instabug.library.util.c.a(imageView);
        this.g = f.a(this.c);
        this.g.d();
        f.a().a(this.g.a(), this.g);
        String g = this.g.g();
        if (g != null) {
            a(g.substring(0, g.indexOf(60)));
        } else {
            a(new com.instabug.library.util.f(e()).a() + getActivity().getApplicationContext().getString(R.string.instabug_str_notification_title));
        }
        Collections.sort(this.g.b(), new com.instabug.library.model.g.a());
        b(this.g.b());
        this.d = new a(this, c(this.g.b()));
        this.a.setAdapter(this.d);
        InstabugSDKLogger.d(this, "Conversation +" + this.c + " loaded from cache where number of messages = " + this.g.b().size());
        this.b.setHint(l.a(Key.CONVERSATION_TEXT_FIELD_HINT, getString(R.string.instabug_str_sending_message_hint)));
        this.i = PublishSubject.create();
        this.j = this.i.debounce(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>(this) {
            final /* synthetic */ b a;

            {
                this.a = r1;
            }

            public final void onCompleted() {
            }

            public final void onError(Throwable th) {
            }

            public final /* synthetic */ void onNext(Object obj) {
                this.a.d.a().clear();
                this.a.g = f.a(this.a.c);
                if (this.a.g != null) {
                    Collections.sort(this.a.g.b(), new com.instabug.library.model.g.a());
                    this.a.b(this.a.g.b());
                    this.a.d.a().addAll(b.c(this.a.g.b()));
                    this.a.d.notifyDataSetChanged();
                    return;
                }
                this.a.getActivity().onBackPressed();
            }
        });
    }

    private void b(List<g> list) {
        int size = list.size() - 1;
        while (size > 0) {
            if (((g) list.get(size)).k()) {
                size--;
            } else {
                InstabugSDKLogger.v(this, "Adding message " + list.get(size) + " to read queue");
                i iVar = new i();
                iVar.a(Integer.parseInt(this.c));
                iVar.b(Integer.parseInt(((g) list.get(size)).a()));
                iVar.a(Long.toString(System.currentTimeMillis() / 1000));
                j a = j.a();
                InstabugSDKLogger.v(a, "Adding message " + iVar + " to read queue in-memory cache");
                e.a().a("read_queue_memory_cache_key").a(Integer.valueOf(iVar.a()), iVar);
                InstabugSDKLogger.v(a, "Added message " + iVar + " to read queue in-memory cache " + e.a().a("read_queue_memory_cache_key").c());
                return;
            }
        }
    }

    protected final void a(Bundle bundle) {
        bundle.putString("issue.number", this.c);
    }

    public final void onPause() {
        super.onPause();
        e.a().b("CONVERSATIONS_MEMORY_CACHE", this);
        d.a().b(this);
    }

    public final void onResume() {
        super.onResume();
        e.a().a("CONVERSATIONS_MEMORY_CACHE", this);
        d.a().a((e) this);
    }

    public final void a_() {
        InstabugSDKLogger.d(this, "Thread with issues id " + this.c + " was invalidated");
    }

    public final void onClick(View view) {
        if (view.getId() == R.id.instabug_btn_send) {
            Object obj = this.b.getText().toString();
            if (!TextUtils.isEmpty(obj)) {
                String str = this.c;
                Calendar instance = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                String format = simpleDateFormat.format(instance.getTime());
                g gVar = new g("offline_message_id", str, this.g.e(), obj, format, format, null, null, com.instabug.library.model.g.b.inbound);
                gVar.a(com.instabug.library.model.g.c.NOT_SENT);
                InstabugSDKLogger.d(this, "Adding not sent message with body \"" + gVar.c() + "\" to conversation " + this.g + " cache " + f.a());
                this.g.b().add(gVar);
                f.a().a(this.g.a(), this.g);
                InstabugSDKLogger.d(this, "Sent message with body \"" + gVar.c() + "\" added to Conversations last message cache");
                getActivity().getApplicationContext().startService(new Intent(getActivity(), InstabugMessageUploaderService.class));
                this.b.setText("");
            }
        }
    }

    public final List<g> a(List<g> list) {
        InstabugSDKLogger.d(this, list.size() + " messages received while in thread number " + this.c);
        if (e() != null) {
            for (g gVar : list) {
                if (gVar.g().equals(this.c)) {
                    InstabugSDKLogger.d(this, "Found message that belongs to this thread " + this.c + " deleting it");
                    list.remove(gVar);
                    InstabugSDKLogger.d(this, "Matching message removed from list, remaining messages count is " + list.size());
                    com.instabug.library.b.a().b(e());
                    this.g.d();
                }
            }
            InstabugSDKLogger.d(this, "Matching messages done, remaining messages count is " + list.size());
        } else {
            InstabugSDKLogger.d(this, "Couldn't find activity returning list as-is with size " + list.size());
        }
        return list;
    }

    public final void onDestroyView() {
        super.onDestroyView();
        if (this.e != null) {
            if (this.e.isPlaying()) {
                this.e.stop();
            }
            this.e.release();
            this.e = null;
        }
        if (this.j != null && !this.j.isUnsubscribed()) {
            this.j.unsubscribe();
        }
    }

    public final void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.f = (b) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnAttachmentClickListener");
        }
    }

    public final void onDetach() {
        super.onDetach();
        this.f = null;
    }

    private static List<Object> c(List<g> list) {
        List<Object> arrayList = new ArrayList();
        for (g gVar : list) {
            if (gVar.j() != null && gVar.j().size() > 0) {
                Iterator it = gVar.j().iterator();
                while (it.hasNext()) {
                    com.instabug.library.model.b bVar = (com.instabug.library.model.b) it.next();
                    bVar.b(gVar.k());
                    bVar.a(gVar.h());
                    bVar.b(gVar.e());
                    arrayList.add(bVar);
                }
            }
            if (!TextUtils.isEmpty(gVar.c())) {
                arrayList.add(gVar);
            }
        }
        return arrayList;
    }

    static /* synthetic */ MediaPlayer d(b bVar) {
        if (bVar.e == null) {
            bVar.e = new MediaPlayer();
            bVar.e.setAudioStreamType(3);
        }
        return bVar.e;
    }
}
