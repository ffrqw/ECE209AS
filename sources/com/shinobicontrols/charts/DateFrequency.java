package com.shinobicontrols.charts;

public class DateFrequency {
    int gu = 1;
    Denomination gv = Denomination.MINUTES;

    public enum Denomination {
        SECONDS(13, 1000),
        MINUTES(12, 60000),
        HOURS(10, 3600000),
        DAYS(7, 86400000),
        WEEKS(3, 604800000),
        MONTHS(2, 0),
        YEARS(1, 0);
        
        final long gx;
        final int value;

        private Denomination(int value, long milliSeconds) {
            this.value = value;
            this.gx = milliSeconds;
        }
    }

    public DateFrequency(int quantity, Denomination denomination) {
        this.gu = quantity;
        this.gv = denomination;
    }

    public final int getQuantity() {
        return this.gu;
    }

    public final Denomination getDenomination() {
        return this.gv;
    }

    public void setQuantity(int quantity) {
        this.gu = quantity;
    }

    public void setDenomination(Denomination denomination) {
        this.gv = denomination;
    }

    void a(int i, Denomination denomination) {
        this.gu = i;
        this.gv = denomination;
    }

    boolean b(int i, Denomination denomination) {
        return this.gu == i && this.gv == denomination;
    }

    long bY() {
        switch (this.gv) {
            case SECONDS:
                return ((long) this.gu) * 1000;
            case MINUTES:
                return ((long) this.gu) * 60000;
            case HOURS:
                return ((long) this.gu) * 3600000;
            case DAYS:
                return ((long) this.gu) * 86400000;
            case WEEKS:
                return ((long) (this.gu * 7)) * 86400000;
            case MONTHS:
                return (((long) this.gu) * 86400000) * 28;
            case YEARS:
                return (((long) this.gu) * 86400000) * 365;
            default:
                return 1;
        }
    }

    public DateFrequency clone() {
        return new DateFrequency(this.gu, this.gv);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DateFrequency)) {
            return false;
        }
        DateFrequency dateFrequency = (DateFrequency) o;
        if (this.gu == dateFrequency.gu) {
            if (this.gv == null) {
                if (dateFrequency.gv == null) {
                    return true;
                }
            } else if (this.gv == dateFrequency.gv) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return (this.gv == null ? 0 : this.gv.hashCode()) + ((this.gu + 527) * 31);
    }
}
