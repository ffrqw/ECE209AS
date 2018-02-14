package com.instabug.library;

public enum Feature {
    INSTABUG,
    CRASH_REPORTING,
    PUSH_NOTIFICATION,
    WHITE_LABELING,
    IN_APP_MESSAGING,
    TRACK_USER_STEPS,
    MULTIPLE_ATTACHMENTS,
    INSTABUG_LOGS,
    USER_DATA,
    CONSOLE_LOGS;

    public enum State {
        private static final /* synthetic */ State[] $VALUES = null;
        public static final State DISABLED = null;
        public static final State ENABLED = null;

        private State(String str, int i) {
        }

        public static State valueOf(String str) {
            return (State) Enum.valueOf(State.class, str);
        }

        public static State[] values() {
            return (State[]) $VALUES.clone();
        }

        static {
            ENABLED = new State("ENABLED", 0);
            DISABLED = new State("DISABLED", 1);
            $VALUES = new State[]{ENABLED, DISABLED};
        }
    }
}
