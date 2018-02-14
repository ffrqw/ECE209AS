package com.instabug.library.d;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.instabug.library.IBGCustomTextPlaceHolder.Key;
import com.instabug.library.h;
import com.instabug.library.internal.d.a.d;
import com.instabug.library.internal.d.a.e;
import com.instabug.library.model.IssueType;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.f;
import com.instabug.library.util.g;
import com.instabug.library.util.l;
import com.instabug.library.view.CircularImageView;
import com.rachio.iro.R;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

public final class c extends h implements OnItemClickListener, com.instabug.library.a, e, d<com.instabug.library.model.c> {
    private b a;
    private ListView b;
    private ArrayList<com.instabug.library.model.c> c = new ArrayList();
    private f d;
    private a e;
    private PublishSubject<Integer> f;
    private Subscription g;

    public interface a {
        void d(String str);
    }

    public class b extends BaseAdapter {
        final /* synthetic */ c a;

        public b(c cVar) {
            this.a = cVar;
        }

        public final /* synthetic */ Object getItem(int i) {
            return a(i);
        }

        public final int getCount() {
            return this.a.c.size();
        }

        public final com.instabug.library.model.c a(int i) {
            return (com.instabug.library.model.c) this.a.c.get(i);
        }

        public final long getItemId(int i) {
            return (long) a(i).a().hashCode();
        }

        public final View getView(int i, View view, ViewGroup viewGroup) {
            c cVar;
            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.instabug_conversation_list_item, viewGroup, false);
                cVar = new c(this.a, view);
                view.setTag(cVar);
            } else {
                cVar = (c) view.getTag();
            }
            com.instabug.library.model.c a = a(i);
            InstabugSDKLogger.v(this, "Binding conversation " + a + " to view");
            Collections.sort(a.b(), new com.instabug.library.model.g.a());
            cVar.e.setText(a.i().c());
            if (a.e() == IssueType.FEEDBACK) {
                cVar.f.setImageResource(R.drawable.instabug_ic_feedback_dark);
            } else {
                cVar.f.setImageResource(R.drawable.instabug_ic_bug_dark);
            }
            String g = a.g();
            if (g != null) {
                cVar.b.setText(g.substring(0, g.indexOf(60)));
            } else {
                cVar.b.setText(this.a.d.a() + this.a.getActivity().getApplicationContext().getString(R.string.instabug_str_notification_title));
            }
            cVar.d.setText(g.a(a.h()));
            if (a.c() != 0) {
                TypedValue typedValue = new TypedValue();
                this.a.getActivity().getTheme().resolveAttribute(R.attr.instabug_color_unread_background, typedValue, true);
                cVar.g.setBackgroundColor(typedValue.data);
            } else {
                cVar.g.setBackgroundDrawable(null);
            }
            if (a.f() != null) {
                com.instabug.library.internal.d.a.b.a(this.a.getActivity(), com.instabug.library.internal.d.a.b.a$5bd48f4a(this.a.getActivity(), a.f(), com.instabug.library.model.a.a.a$6ed2d6d3), new com.instabug.library.internal.d.a.b.b(this) {
                    final /* synthetic */ b b;

                    public final void a(com.instabug.library.model.a aVar) {
                        InstabugSDKLogger.d(this, "Asset Entity downloaded: " + aVar.c().getPath());
                        try {
                            cVar.c.setImageDrawable(null);
                            cVar.c.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(aVar.c())));
                        } catch (Throwable e) {
                            InstabugSDKLogger.e(this, "Asset Entity downloading got FileNotFoundException error", e);
                        }
                    }

                    public final void a(Throwable th) {
                        InstabugSDKLogger.e(this, "Asset Entity downloading got error", th);
                    }
                });
            }
            return view;
        }
    }

    public class c {
        final /* synthetic */ c a;
        private final TextView b;
        private final CircularImageView c;
        private final TextView d;
        private final TextView e;
        private final ImageView f;
        private final LinearLayout g;

        public c(c cVar, View view) {
            this.a = cVar;
            this.g = (LinearLayout) view.findViewById(R.id.conversation_container);
            this.b = (TextView) view.findViewById(R.id.instabug_txt_message_sender);
            this.c = (CircularImageView) view.findViewById(R.id.instabug_img_message_sender);
            this.f = (ImageView) view.findViewById(R.id.instabug_img_bug_type);
            this.d = (TextView) view.findViewById(R.id.instabug_txt_message_time);
            this.e = (TextView) view.findViewById(R.id.instabug_txt_message_snippet);
        }
    }

    public final /* bridge */ /* synthetic */ void a(Object obj, Object obj2) {
        final com.instabug.library.model.c cVar = (com.instabug.library.model.c) obj2;
        InstabugSDKLogger.d(this, "Conversation updated in cache");
        e().runOnUiThread(new Runnable(this) {
            final /* synthetic */ c b;

            public final void run() {
                if (cVar.b().size() > 0) {
                    this.b.j();
                }
            }
        });
    }

    public static c f() {
        return new c();
    }

    protected final int b() {
        return R.layout.instabug_lyt_conversations;
    }

    protected final String c() {
        return l.a(Key.CONVERSATIONS_LIST_TITLE, getString(R.string.instabug_str_conversations));
    }

    protected final void a() {
    }

    public final void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.b = (ListView) view.findViewById(R.id.instabug_lst_conversations);
        this.b.setOnItemClickListener(this);
        this.a = new b(this);
        this.d = new f(e());
        this.b.setAdapter(this.a);
        this.f = PublishSubject.create();
        this.g = this.f.debounce(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>(this) {
            final /* synthetic */ c a;

            {
                this.a = r1;
            }

            public final void onCompleted() {
            }

            public final void onError(Throwable th) {
            }

            public final /* synthetic */ void onNext(Object obj) {
                this.a.i();
            }
        });
        i();
        this.b.setVisibility(0);
        getView().findViewById(R.id.instabug_disconnected).setVisibility(8);
        getView().findViewById(R.id.instabug_blank).setVisibility(8);
    }

    public final void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.e = (a) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement InstabugConversationListFragment.Callbacks");
        }
    }

    public final void onDetach() {
        super.onDetach();
        this.e = null;
    }

    protected final void a(Bundle bundle) {
        bundle.putSerializable("CONVERSATIONS", this.c);
    }

    protected final void b(Bundle bundle) {
        this.c = (ArrayList) bundle.getSerializable("CONVERSATIONS");
    }

    public final void onResume() {
        super.onResume();
        e.a().a("CONVERSATIONS_MEMORY_CACHE", this);
        d.a().a((e) this);
    }

    public final void onPause() {
        InstabugSDKLogger.d(this, "onPause called, un-subscribing from all listeners");
        super.onPause();
        e.a().b("CONVERSATIONS_MEMORY_CACHE", this);
        d.a().b(this);
    }

    public final void a_() {
        e().runOnUiThread(new Runnable(this) {
            final /* synthetic */ c a;

            {
                this.a = r1;
            }

            public final void run() {
                InstabugSDKLogger.d(this.a, "Conversations cache was invalidated");
                this.a.j();
            }
        });
    }

    public final void a(boolean z) {
        InstabugSDKLogger.d(this, "InstabugConversationListFragment isVisible " + z);
        if (z) {
            d.a().a((e) this);
            e.a().a("CONVERSATIONS_MEMORY_CACHE", this);
            j();
            return;
        }
        d.a().b(this);
        e.a().b("CONVERSATIONS_MEMORY_CACHE", this);
    }

    public final void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        com.instabug.library.model.c a = this.a.a(i);
        com.instabug.library.internal.d.a.f.a().a(a.a(), a);
        if (this.e != null) {
            this.e.d(((com.instabug.library.model.c) adapterView.getItemAtPosition(i)).a());
        }
    }

    public final List<com.instabug.library.model.g> a(List<com.instabug.library.model.g> list) {
        if (e() == null) {
            return list;
        }
        com.instabug.library.b.a().b(e());
        return null;
    }

    private void i() {
        Collection b = com.instabug.library.internal.d.a.f.a().b();
        InstabugSDKLogger.d(this, "Conversations loaded from cache " + b.size());
        this.c.clear();
        this.c.addAll(b);
        Collections.sort(this.c, Collections.reverseOrder(new com.instabug.library.model.c.a()));
        this.a.notifyDataSetChanged();
    }

    private void j() {
        this.f.onNext(Integer.valueOf(0));
    }

    public final void onDestroyView() {
        if (!(this.g == null || this.g.isUnsubscribed())) {
            this.g.unsubscribe();
        }
        super.onDestroyView();
    }
}
