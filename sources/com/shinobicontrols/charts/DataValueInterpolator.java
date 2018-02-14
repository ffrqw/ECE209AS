package com.shinobicontrols.charts;

import java.util.List;

public interface DataValueInterpolator<Tx, Ty> {
    List<Data<Tx, Ty>> getDataValuesForDisplay(List<Data<Tx, Ty>> list);
}
