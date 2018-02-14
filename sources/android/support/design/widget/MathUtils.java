package android.support.design.widget;

final class MathUtils {
    static int constrain(int amount, int low, int high) {
        if (amount < low) {
            return low;
        }
        return amount > high ? high : amount;
    }
}
