package com.instabug.library.d;

import com.instabug.library.internal.d.a.f;
import com.instabug.library.model.IssueType;
import com.instabug.library.model.c;
import com.instabug.library.model.g;
import com.instabug.library.model.g.b;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public final class d {
    private static d a;
    private List<e> b = new ArrayList();

    public static d a() {
        if (a == null) {
            a = new d();
        }
        return a;
    }

    public final void a(boolean z, JSONObject... jSONObjectArr) {
        List<g> a = a(jSONObjectArr);
        List arrayList = new ArrayList(a);
        for (g gVar : a) {
            if (d(gVar) || e(gVar) || gVar.l() || gVar.d()) {
                InstabugSDKLogger.d(this, "Message " + gVar.toString() + " removed from list to be notified");
                arrayList.remove(gVar);
            }
        }
        if (z) {
            a(a((List) a, f.g()));
        } else {
            a((List) a);
        }
        if (this.b.size() > 0) {
            d(arrayList);
            return;
        }
        throw new IllegalStateException("No one is listening for unread messages");
    }

    private void a(List<g> list) {
        InstabugSDKLogger.v(this, "Chats cache updating starting with " + list + " new messages");
        for (g gVar : list) {
            InstabugSDKLogger.v(this, "new message to updating: " + gVar.toString());
            c a = a(gVar);
            if (a == null) {
                InstabugSDKLogger.v(this, "Chat with id " + gVar.g() + " doesn't exist, creating new one");
                a = new c(gVar.g());
            }
            if (!d(gVar)) {
                if (e(gVar)) {
                    try {
                        f.a(gVar);
                        return;
                    } catch (Throwable e) {
                        InstabugSDKLogger.e(this, "Failed to update local message: " + c(gVar) + " with synced message: " + gVar, e);
                        return;
                    }
                }
                a.b().add(gVar);
                InstabugSDKLogger.d(this, "Message " + gVar + " added to cached thread " + a);
                f.a().a(a.a(), a);
            } else {
                return;
            }
        }
    }

    private static List<g> a(List<g> list, List<g> list2) {
        List<g> arrayList = new ArrayList(list);
        for (g gVar : list2) {
            for (g g : list) {
                if (gVar.g().equals(g.g())) {
                    arrayList.add(gVar);
                    break;
                }
            }
        }
        return arrayList;
    }

    private c a(g gVar) {
        c cVar = (c) f.a().d(gVar.g());
        if (cVar != null) {
            return cVar;
        }
        InstabugSDKLogger.v(this, "No local chats match messages's chat");
        return null;
    }

    private g c(g gVar) {
        c a = a(gVar);
        List list;
        if (a == null) {
            list = null;
        } else {
            list = a.b();
        }
        if (r0 != null) {
            for (g gVar2 : r0) {
                if (gVar2.e().equalsIgnoreCase(gVar.e()) && gVar2.a().equalsIgnoreCase(gVar.a())) {
                    return gVar2;
                }
            }
        }
        return null;
    }

    private boolean d(g gVar) {
        g c = c(gVar);
        return c != null && c.e().equalsIgnoreCase(gVar.e()) && c.a().equalsIgnoreCase(gVar.a()) && c.i().equals(g.c.SYNCED) && c.j().size() == gVar.j().size();
    }

    private boolean e(g gVar) {
        g c = c(gVar);
        return c != null && c.e().equalsIgnoreCase(gVar.e()) && c.a().equalsIgnoreCase(gVar.a()) && c.i().equals(g.c.SENT) && c.j().size() == gVar.j().size();
    }

    private void d(List<g> list) {
        InstabugSDKLogger.d(this, "Number of listeners to notify " + this.b.size());
        int size = this.b.size() - 1;
        while (size >= 0) {
            e eVar = (e) this.b.get(size);
            InstabugSDKLogger.d(this, "Notifying listener " + eVar);
            if (list != null && list.size() > 0) {
                Object valueOf;
                InstabugSDKLogger.d(this, "Notifying listener with " + list.size() + " message(s)");
                list = eVar.a(list);
                StringBuilder stringBuilder = new StringBuilder("Notified listener remained ");
                if (list != null) {
                    valueOf = Integer.valueOf(list.size());
                } else {
                    valueOf = null;
                }
                InstabugSDKLogger.d(this, stringBuilder.append(valueOf).append(" message(s) to be sent to next listener").toString());
                size--;
            } else {
                return;
            }
        }
    }

    public final void a(e eVar) {
        this.b.add(eVar);
    }

    public final void b(e eVar) {
        this.b.remove(eVar);
    }

    private List<g> a(JSONObject[] jSONObjectArr) {
        List<g> arrayList = new ArrayList();
        for (int i = 0; i < jSONObjectArr.length; i++) {
            try {
                IssueType issueType;
                JSONObject jSONObject = jSONObjectArr[i];
                JSONArray jSONArray = jSONObject.getJSONArray("attachments");
                if (jSONObject.getString("issue_type").equals(IssueType.BUG.toString())) {
                    issueType = IssueType.BUG;
                } else {
                    issueType = IssueType.FEEDBACK;
                }
                g gVar = new g(jSONObject.getString("id"), jSONObject.getString("issue_id"), issueType, jSONObject.getString("body"), jSONObject.getString("created_at"), jSONObject.getString("read_at"), jSONObject.getString("from"), jSONObject.getString("avatar"), b.valueOf(jSONObject.getString("direction")));
                gVar.a(g.c.SYNCED);
                for (int length = jSONArray.length() - 1; length >= 0; length--) {
                    JSONObject jSONObject2 = jSONArray.getJSONObject(length);
                    gVar.j().add(new com.instabug.library.model.b(jSONObject2.getString("type"), jSONObject2.getString("url")));
                }
                arrayList.add(gVar);
            } catch (Throwable e) {
                InstabugSDKLogger.e(this, "Failed to parse message number " + i, e);
            }
        }
        return arrayList;
    }
}
