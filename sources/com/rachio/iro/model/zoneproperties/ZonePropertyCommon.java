package com.rachio.iro.model.zoneproperties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.PostId;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.user.User;
import java.util.Date;

public abstract class ZonePropertyCommon extends ModelObject implements Comparable<ZonePropertyCommon> {
    private static final long serialVersionUID = 1;
    public Date createDate;
    public String description;
    public boolean editable;
    @JsonView({TransmittableView.class})
    public String id;
    @JsonView({TransmittableView.class})
    public String imageUrl;
    public Date lastUpdateDate;
    @JsonView({TransmittableView.class})
    public String name;
    @JsonView({TransmittableView.class})
    public PostId person;
    public Integer sortOrder = Integer.valueOf(0);

    @JsonIgnore
    public abstract String getDescriptiveString(User user);

    public ZonePropertyCommon(String id) {
        this.id = id;
    }

    public int compareTo(ZonePropertyCommon another) {
        return this.sortOrder.compareTo(another.sortOrder);
    }

    public boolean equals(Object o) {
        if (o instanceof ZonePropertyCommon) {
            return ((ZonePropertyCommon) o).id.equals(this.id);
        }
        return super.equals(o);
    }
}
