package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Iterator;
import java.util.Map.Entry;

abstract class NodeCursor extends JsonStreamContext {
    protected String _currentName;
    protected Object _currentValue;
    protected final NodeCursor _parent;

    protected static final class ArrayCursor extends NodeCursor {
        protected Iterator<JsonNode> _contents;
        protected JsonNode _currentNode;

        public final /* bridge */ /* synthetic */ JsonStreamContext getParent() {
            return super.getParent();
        }

        public ArrayCursor(JsonNode n, NodeCursor p) {
            super(1, p);
            this._contents = n.elements();
        }

        public final JsonToken nextToken() {
            if (this._contents.hasNext()) {
                this._currentNode = (JsonNode) this._contents.next();
                return this._currentNode.asToken();
            }
            this._currentNode = null;
            return null;
        }

        public final JsonToken nextValue() {
            return nextToken();
        }

        public final JsonToken endToken() {
            return JsonToken.END_ARRAY;
        }

        public final JsonNode currentNode() {
            return this._currentNode;
        }

        public final boolean currentHasChildren() {
            return ((ContainerNode) currentNode()).size() > 0;
        }
    }

    protected static final class ObjectCursor extends NodeCursor {
        protected Iterator<Entry<String, JsonNode>> _contents;
        protected Entry<String, JsonNode> _current;
        protected boolean _needEntry = true;

        public final /* bridge */ /* synthetic */ JsonStreamContext getParent() {
            return super.getParent();
        }

        public ObjectCursor(JsonNode n, NodeCursor p) {
            super(2, p);
            this._contents = ((ObjectNode) n).fields();
        }

        public final JsonToken nextToken() {
            if (!this._needEntry) {
                this._needEntry = true;
                return ((JsonNode) this._current.getValue()).asToken();
            } else if (this._contents.hasNext()) {
                this._needEntry = false;
                this._current = (Entry) this._contents.next();
                this._currentName = this._current == null ? null : (String) this._current.getKey();
                return JsonToken.FIELD_NAME;
            } else {
                this._currentName = null;
                this._current = null;
                return null;
            }
        }

        public final JsonToken nextValue() {
            JsonToken t = nextToken();
            if (t == JsonToken.FIELD_NAME) {
                return nextToken();
            }
            return t;
        }

        public final JsonToken endToken() {
            return JsonToken.END_OBJECT;
        }

        public final JsonNode currentNode() {
            return this._current == null ? null : (JsonNode) this._current.getValue();
        }

        public final boolean currentHasChildren() {
            return ((ContainerNode) currentNode()).size() > 0;
        }
    }

    protected static final class RootCursor extends NodeCursor {
        protected boolean _done = false;
        protected JsonNode _node;

        public final /* bridge */ /* synthetic */ JsonStreamContext getParent() {
            return super.getParent();
        }

        public RootCursor(JsonNode n, NodeCursor p) {
            super(0, p);
            this._node = n;
        }

        public final void overrideCurrentName(String name) {
        }

        public final JsonToken nextToken() {
            if (this._done) {
                this._node = null;
                return null;
            }
            this._done = true;
            return this._node.asToken();
        }

        public final JsonToken nextValue() {
            return nextToken();
        }

        public final JsonToken endToken() {
            return null;
        }

        public final JsonNode currentNode() {
            return this._node;
        }

        public final boolean currentHasChildren() {
            return false;
        }
    }

    public abstract boolean currentHasChildren();

    public abstract JsonNode currentNode();

    public abstract JsonToken endToken();

    public abstract JsonToken nextToken();

    public abstract JsonToken nextValue();

    public NodeCursor(int contextType, NodeCursor p) {
        this._type = contextType;
        this._index = -1;
        this._parent = p;
    }

    public final NodeCursor getParent() {
        return this._parent;
    }

    public final String getCurrentName() {
        return this._currentName;
    }

    public void overrideCurrentName(String name) {
        this._currentName = name;
    }

    public Object getCurrentValue() {
        return this._currentValue;
    }

    public void setCurrentValue(Object v) {
        this._currentValue = v;
    }

    public final NodeCursor iterateChildren() {
        JsonNode n = currentNode();
        if (n == null) {
            throw new IllegalStateException("No current node");
        } else if (n.isArray()) {
            return new ArrayCursor(n, this);
        } else {
            if (n.isObject()) {
                return new ObjectCursor(n, this);
            }
            throw new IllegalStateException("Current node of type " + n.getClass().getName());
        }
    }
}
