package com.fasterxml.jackson.annotation;

import java.io.Serializable;

public abstract class ObjectIdGenerator<T> implements Serializable {

    public static final class IdKey implements Serializable {
        private final int hashCode;
        public final Object key;
        public final Class<?> scope;
        public final Class<?> type;

        public IdKey(Class<?> type, Class<?> scope, Object key) {
            if (key == null) {
                throw new IllegalArgumentException("Can not construct IdKey for null key");
            }
            this.type = type;
            this.scope = scope;
            this.key = key;
            int h = key.hashCode() + type.getName().hashCode();
            if (scope != null) {
                h ^= scope.getName().hashCode();
            }
            this.hashCode = h;
        }

        public final int hashCode() {
            return this.hashCode;
        }

        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o.getClass() != getClass()) {
                return false;
            }
            IdKey other = (IdKey) o;
            if (other.key.equals(this.key) && other.type == this.type && other.scope == this.scope) {
                return true;
            }
            return false;
        }

        public final String toString() {
            String str = "[ObjectId: key=%s, type=%s, scope=%s]";
            Object[] objArr = new Object[3];
            objArr[0] = this.key;
            objArr[1] = this.type == null ? "NONE" : this.type.getName();
            objArr[2] = this.scope == null ? "NONE" : this.scope.getName();
            return String.format(str, objArr);
        }
    }

    public abstract boolean canUseFor(ObjectIdGenerator<?> objectIdGenerator);

    public abstract ObjectIdGenerator<T> forScope(Class<?> cls);

    public abstract T generateId(Object obj);

    public abstract Class<?> getScope();

    public abstract IdKey key(Object obj);

    public abstract ObjectIdGenerator<T> newForSerialization(Object obj);

    public boolean maySerializeAsObject() {
        return false;
    }

    public boolean isValidReferencePropertyName(String name, Object parser) {
        return false;
    }
}
