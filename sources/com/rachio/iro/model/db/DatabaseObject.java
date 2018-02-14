package com.rachio.iro.model.db;

import android.text.TextUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.db.Database.DatabasePreSaveCheckException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;

public abstract class DatabaseObject<T extends DatabaseObject> extends ModelObject<T> implements Serializable {
    private static final long serialVersionUID = 1;
    @DatabaseField(dataType = DataType.DATE_LONG)
    public Date createDate;
    @JsonView({TransmittableView.class})
    @DatabaseField(canBeNull = false, id = true)
    public String id;
    @DatabaseField(dataType = DataType.DATE_LONG)
    public Date lastUpdateDate;

    public void setId(String id) {
        if (TextUtils.isEmpty(id)) {
            throw new IllegalArgumentException("id cannot be null or empty");
        }
        this.id = id;
    }

    public void doPreSaveSanityCheck() throws DatabasePreSaveCheckException {
        if (TextUtils.isEmpty(this.id)) {
            throw new DatabasePreSaveCheckException("id must be a valid uuid");
        }
    }

    public void preSave() {
    }

    public void pruneCollections(Database database) throws SQLException {
    }

    public synchronized void saveTransients(Database database) throws SQLException {
    }

    @JsonIgnore
    public void setParent(DatabaseObject parent) {
    }

    @JsonIgnore
    public DatabaseObject getParent() {
        return null;
    }

    @JsonIgnore
    public void setAltParent(DatabaseObject parent) {
    }

    @JsonIgnore
    public DatabaseObject getAltParent() {
        return null;
    }
}
