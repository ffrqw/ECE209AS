package io.fabric.sdk.android.services.concurrency;

public final class Priority {
    private static final /* synthetic */ int[] $VALUES$7238288f = new int[]{1, 2, 3, 4};
    public static final int HIGH$4601d4ec = 3;
    public static final int IMMEDIATE$4601d4ec = 4;
    public static final int LOW$4601d4ec = 1;
    public static final int NORMAL$4601d4ec = 2;

    static <Y> int compareTo(PriorityProvider self, Y other) {
        int otherPriority;
        if (other instanceof PriorityProvider) {
            otherPriority = ((PriorityProvider) other).getPriority$16699175();
        } else {
            otherPriority = 2;
        }
        return (otherPriority - 1) - (self.getPriority$16699175() - 1);
    }
}
