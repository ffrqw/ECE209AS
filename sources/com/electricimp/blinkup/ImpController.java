package com.electricimp.blinkup;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

final class ImpController {
    String apiKey = null;
    private String baseUrl;
    String planID = null;
    String setupToken = null;
    private Boolean shouldPollTokenStatus = Boolean.valueOf(true);

    private static class PollTokenStatusHandler extends Handler {
        private final ImpController controller;
        private final Handler resultHandler;
        private long startTime;
        private final long timeout;
        private final Handler tokenStatusHandler;

        public PollTokenStatusHandler(ImpController controller, Handler resultHandler) {
            this(controller, resultHandler, 60000);
        }

        private PollTokenStatusHandler(ImpController controller, Handler resultHandler, long timeoutMs) {
            this.controller = controller;
            this.resultHandler = resultHandler;
            this.tokenStatusHandler = new TokenStatusHandler(this);
            this.timeout = 60000;
            this.startTime = System.currentTimeMillis();
        }

        public final void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    synchronized (this.controller.shouldPollTokenStatus) {
                        if (this.controller.shouldPollTokenStatus.booleanValue()) {
                            ImpController.access$2(this.controller, (String) msg.obj, this.tokenStatusHandler);
                            return;
                        }
                        this.controller.shouldPollTokenStatus = Boolean.valueOf(true);
                        return;
                    }
                case 1:
                    if (msg.arg1 == 1) {
                        JSONObject jSONObject = (JSONObject) msg.obj;
                        try {
                            String string = jSONObject.has("agent_url") ? jSONObject.getString("agent_url") : null;
                            if (string != null && string.length() != 0) {
                                this.resultHandler.sendMessage(this.resultHandler.obtainMessage(msg.what, msg.arg1, msg.arg2, msg.obj));
                                return;
                            } else if (System.currentTimeMillis() - this.startTime < this.timeout) {
                                sendMessageDelayed(obtainMessage(0, jSONObject.getString("id")), 1000);
                                return;
                            } else {
                                Message obtainMessage = this.resultHandler.obtainMessage();
                                obtainMessage.arg1 = 2;
                                this.resultHandler.sendMessage(obtainMessage);
                                return;
                            }
                        } catch (JSONException e) {
                            Message obtainMessage2 = this.resultHandler.obtainMessage();
                            obtainMessage2.obj = e.getMessage();
                            obtainMessage2.arg1 = 0;
                            this.resultHandler.sendMessage(obtainMessage2);
                            return;
                        }
                    }
                    this.resultHandler.sendMessage(this.resultHandler.obtainMessage(msg.what, msg.arg1, msg.arg2, msg.obj));
                    return;
                default:
                    return;
            }
        }
    }

    private static class TokenStatusHandler extends Handler {
        private PollTokenStatusHandler poller;

        public TokenStatusHandler(PollTokenStatusHandler poller) {
            this.poller = poller;
        }

        public final void handleMessage(Message msg) {
            Message resultMsg = this.poller.obtainMessage(msg.what, msg.arg1, msg.arg2, msg.obj);
            resultMsg.what = 1;
            this.poller.sendMessage(resultMsg);
        }
    }

    public ImpController(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    final void acquireSetupToken(String apiKey, final Handler resultHandler) {
        this.apiKey = apiKey;
        new ImpAPIClient(this.baseUrl, apiKey).createSetupToken(this.planID, new Handler() {
            public final void handleMessage(Message msg) {
                Object responseObj = msg.obj;
                int status = msg.arg1;
                if (msg.arg1 == 1) {
                    JSONObject json = msg.obj;
                    try {
                        ImpController.this.setupToken = json.getString("id");
                    } catch (JSONException e) {
                        Log.e("BlinkUp", Log.getStackTraceString(e));
                        responseObj = e.getMessage();
                        status = 0;
                    }
                }
                if (resultHandler != null) {
                    resultHandler.sendMessage(resultHandler.obtainMessage(msg.what, status, msg.arg2, responseObj));
                }
            }
        });
    }

    final void getTokenStatus(String token, Handler resultHandler) {
        synchronized (this.shouldPollTokenStatus) {
            this.shouldPollTokenStatus = Boolean.valueOf(true);
        }
        Handler handler = new PollTokenStatusHandler(this, resultHandler);
        handler.sendMessage(handler.obtainMessage(0, token));
    }

    final void cancelTokenStatusPolling() {
        synchronized (this.shouldPollTokenStatus) {
            this.shouldPollTokenStatus = Boolean.valueOf(false);
        }
    }

    static /* synthetic */ void access$2(ImpController impController, String str, final Handler handler) {
        if (str == null || str.length() == 0) {
            Message obtainMessage = handler.obtainMessage();
            obtainMessage.obj = "No token specified";
            obtainMessage.arg1 = 0;
            handler.sendMessage(obtainMessage);
            return;
        }
        new ImpAPIClient(impController.baseUrl, impController.apiKey).readSetupToken(str, new Handler() {
            public final void handleMessage(Message msg) {
                handler.sendMessage(handler.obtainMessage(msg.what, msg.arg1, msg.arg2, msg.obj));
            }
        });
    }
}
