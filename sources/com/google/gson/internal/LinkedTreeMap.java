package com.google.gson.internal;

import com.j256.ormlite.stmt.query.SimpleComparison;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public final class LinkedTreeMap<K, V> extends AbstractMap<K, V> implements Serializable {
    static final /* synthetic */ boolean $assertionsDisabled = (!LinkedTreeMap.class.desiredAssertionStatus());
    private static final Comparator<Comparable> NATURAL_ORDER = new Comparator<Comparable>() {
        public final /* bridge */ /* synthetic */ int compare(Object obj, Object obj2) {
            return ((Comparable) obj).compareTo((Comparable) obj2);
        }
    };
    Comparator<? super K> comparator;
    private EntrySet entrySet;
    final Node<K, V> header;
    private KeySet keySet;
    int modCount;
    Node<K, V> root;
    int size;

    private abstract class LinkedTreeMapIterator<T> implements Iterator<T> {
        int expectedModCount = LinkedTreeMap.this.modCount;
        Node<K, V> lastReturned = null;
        Node<K, V> next = LinkedTreeMap.this.header.next;

        LinkedTreeMapIterator() {
        }

        public final boolean hasNext() {
            return this.next != LinkedTreeMap.this.header;
        }

        final Node<K, V> nextNode() {
            Node<K, V> e = this.next;
            if (e == LinkedTreeMap.this.header) {
                throw new NoSuchElementException();
            } else if (LinkedTreeMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            } else {
                this.next = e.next;
                this.lastReturned = e;
                return e;
            }
        }

        public final void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            LinkedTreeMap.this.removeInternal(this.lastReturned, true);
            this.lastReturned = null;
            this.expectedModCount = LinkedTreeMap.this.modCount;
        }
    }

    class EntrySet extends AbstractSet<Entry<K, V>> {
        EntrySet() {
        }

        public final int size() {
            return LinkedTreeMap.this.size;
        }

        public final Iterator<Entry<K, V>> iterator() {
            return new LinkedTreeMapIterator<Entry<K, V>>() {
                {
                    LinkedTreeMap linkedTreeMap = LinkedTreeMap.this;
                }

                public final /* bridge */ /* synthetic */ Object next() {
                    return nextNode();
                }
            };
        }

        public final boolean contains(Object o) {
            return (o instanceof Entry) && LinkedTreeMap.this.findByEntry((Entry) o) != null;
        }

        public final boolean remove(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Node<K, V> node = LinkedTreeMap.this.findByEntry((Entry) o);
            if (node == null) {
                return false;
            }
            LinkedTreeMap.this.removeInternal(node, true);
            return true;
        }

        public final void clear() {
            LinkedTreeMap.this.clear();
        }
    }

    final class KeySet extends AbstractSet<K> {
        KeySet() {
        }

        public final int size() {
            return LinkedTreeMap.this.size;
        }

        public final Iterator<K> iterator() {
            return new LinkedTreeMapIterator<K>() {
                {
                    LinkedTreeMap linkedTreeMap = LinkedTreeMap.this;
                }

                public final K next() {
                    return nextNode().key;
                }
            };
        }

        public final boolean contains(Object o) {
            return LinkedTreeMap.this.containsKey(o);
        }

        public final boolean remove(Object key) {
            return LinkedTreeMap.this.removeInternalByKey(key) != null;
        }

        public final void clear() {
            LinkedTreeMap.this.clear();
        }
    }

    static final class Node<K, V> implements Entry<K, V> {
        int height;
        final K key;
        Node<K, V> left;
        Node<K, V> next;
        Node<K, V> parent;
        Node<K, V> prev;
        Node<K, V> right;
        V value;

        Node() {
            this.key = null;
            this.prev = this;
            this.next = this;
        }

        Node(Node<K, V> parent, K key, Node<K, V> next, Node<K, V> prev) {
            this.parent = parent;
            this.key = key;
            this.height = 1;
            this.next = next;
            this.prev = prev;
            prev.next = this;
            next.prev = this;
        }

        public final K getKey() {
            return this.key;
        }

        public final V getValue() {
            return this.value;
        }

        public final V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry other = (Entry) o;
            if (this.key == null) {
                if (other.getKey() != null) {
                    return false;
                }
            } else if (!this.key.equals(other.getKey())) {
                return false;
            }
            if (this.value == null) {
                if (other.getValue() != null) {
                    return false;
                }
            } else if (!this.value.equals(other.getValue())) {
                return false;
            }
            return true;
        }

        public final int hashCode() {
            int i = 0;
            int hashCode = this.key == null ? 0 : this.key.hashCode();
            if (this.value != null) {
                i = this.value.hashCode();
            }
            return hashCode ^ i;
        }

        public final String toString() {
            return this.key + SimpleComparison.EQUAL_TO_OPERATION + this.value;
        }
    }

    public LinkedTreeMap() {
        this(NATURAL_ORDER);
    }

    private LinkedTreeMap(Comparator<? super K> comparator) {
        this.size = 0;
        this.modCount = 0;
        this.header = new Node();
        if (comparator == null) {
            comparator = NATURAL_ORDER;
        }
        this.comparator = comparator;
    }

    public final int size() {
        return this.size;
    }

    public final V get(Object key) {
        Node<K, V> node = findByObject(key);
        return node != null ? node.value : null;
    }

    public final boolean containsKey(Object key) {
        return findByObject(key) != null;
    }

    public final V put(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        Node<K, V> created = find(key, true);
        V result = created.value;
        created.value = value;
        return result;
    }

    public final void clear() {
        this.root = null;
        this.size = 0;
        this.modCount++;
        Node<K, V> header = this.header;
        header.prev = header;
        header.next = header;
    }

    public final V remove(Object key) {
        Node<K, V> node = removeInternalByKey(key);
        return node != null ? node.value : null;
    }

    private Node<K, V> find(K key, boolean create) {
        Comparator<? super K> comparator = this.comparator;
        Node<K, V> nearest = this.root;
        int comparison = 0;
        if (nearest != null) {
            Comparable<Object> comparableKey = comparator == NATURAL_ORDER ? (Comparable) key : null;
            while (true) {
                if (comparableKey != null) {
                    comparison = comparableKey.compareTo(nearest.key);
                } else {
                    comparison = comparator.compare(key, nearest.key);
                }
                if (comparison != 0) {
                    Node<K, V> child = comparison < 0 ? nearest.left : nearest.right;
                    if (child == null) {
                        break;
                    }
                    nearest = child;
                } else {
                    return nearest;
                }
            }
        }
        if (!create) {
            return null;
        }
        Node<K, V> created;
        Node<K, V> header = this.header;
        if (nearest != null) {
            created = new Node(nearest, key, header, header.prev);
            if (comparison < 0) {
                nearest.left = created;
            } else {
                nearest.right = created;
            }
            rebalance(nearest, true);
        } else if (comparator != NATURAL_ORDER || (key instanceof Comparable)) {
            created = new Node(nearest, key, header, header.prev);
            this.root = created;
        } else {
            throw new ClassCastException(key.getClass().getName() + " is not Comparable");
        }
        this.size++;
        this.modCount++;
        return created;
    }

    private Node<K, V> findByObject(Object key) {
        Node<K, V> node = null;
        if (key != null) {
            try {
                node = find(key, false);
            } catch (ClassCastException e) {
            }
        }
        return node;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final com.google.gson.internal.LinkedTreeMap.Node<K, V> findByEntry(java.util.Map.Entry<?, ?> r6) {
        /*
        r5 = this;
        r1 = 1;
        r2 = 0;
        r3 = r6.getKey();
        r0 = r5.findByObject(r3);
        if (r0 == 0) goto L_0x0024;
    L_0x000c:
        r3 = r0.value;
        r4 = r6.getValue();
        if (r3 == r4) goto L_0x001c;
    L_0x0014:
        if (r3 == 0) goto L_0x0022;
    L_0x0016:
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x0022;
    L_0x001c:
        r3 = r1;
    L_0x001d:
        if (r3 == 0) goto L_0x0024;
    L_0x001f:
        if (r1 == 0) goto L_0x0026;
    L_0x0021:
        return r0;
    L_0x0022:
        r3 = r2;
        goto L_0x001d;
    L_0x0024:
        r1 = r2;
        goto L_0x001f;
    L_0x0026:
        r0 = 0;
        goto L_0x0021;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.internal.LinkedTreeMap.findByEntry(java.util.Map$Entry):com.google.gson.internal.LinkedTreeMap$Node<K, V>");
    }

    final void removeInternal(Node<K, V> node, boolean unlink) {
        if (unlink) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        Node<K, V> left = node.left;
        Node<K, V> right = node.right;
        Node<K, V> originalParent = node.parent;
        if (left == null || right == null) {
            if (left != null) {
                replaceInParent(node, left);
                node.left = null;
            } else if (right != null) {
                replaceInParent(node, right);
                node.right = null;
            } else {
                replaceInParent(node, null);
            }
            rebalance(originalParent, false);
            this.size--;
            this.modCount++;
            return;
        }
        Node<K, V> adjacent;
        Node<K, V> node2;
        if (left.height > right.height) {
            adjacent = left;
            for (node2 = left.right; node2 != null; node2 = node2.right) {
                adjacent = node2;
            }
        } else {
            adjacent = right;
            for (node2 = right.left; node2 != null; node2 = node2.left) {
                adjacent = node2;
            }
        }
        removeInternal(adjacent, false);
        int leftHeight = 0;
        left = node.left;
        if (left != null) {
            leftHeight = left.height;
            adjacent.left = left;
            left.parent = adjacent;
            node.left = null;
        }
        int rightHeight = 0;
        right = node.right;
        if (right != null) {
            rightHeight = right.height;
            adjacent.right = right;
            right.parent = adjacent;
            node.right = null;
        }
        adjacent.height = Math.max(leftHeight, rightHeight) + 1;
        replaceInParent(node, adjacent);
    }

    final Node<K, V> removeInternalByKey(Object key) {
        Node<K, V> node = findByObject(key);
        if (node != null) {
            removeInternal(node, true);
        }
        return node;
    }

    private void replaceInParent(Node<K, V> node, Node<K, V> replacement) {
        Node<K, V> parent = node.parent;
        node.parent = null;
        if (replacement != null) {
            replacement.parent = parent;
        }
        if (parent == null) {
            this.root = replacement;
        } else if (parent.left == node) {
            parent.left = replacement;
        } else if ($assertionsDisabled || parent.right == node) {
            parent.right = replacement;
        } else {
            throw new AssertionError();
        }
    }

    private void rebalance(Node<K, V> unbalanced, boolean insert) {
        for (Node<K, V> node = unbalanced; node != null; node = node.parent) {
            Node<K, V> left = node.left;
            Node<K, V> right = node.right;
            int leftHeight = left != null ? left.height : 0;
            int rightHeight = right != null ? right.height : 0;
            int delta = leftHeight - rightHeight;
            if (delta == -2) {
                Node<K, V> rightLeft = right.left;
                Node<K, V> rightRight = right.right;
                int rightDelta = (rightLeft != null ? rightLeft.height : 0) - (rightRight != null ? rightRight.height : 0);
                if (rightDelta == -1 || (rightDelta == 0 && !insert)) {
                    rotateLeft(node);
                } else if ($assertionsDisabled || rightDelta == 1) {
                    rotateRight(right);
                    rotateLeft(node);
                } else {
                    throw new AssertionError();
                }
                if (insert) {
                    return;
                }
            } else if (delta == 2) {
                Node<K, V> leftLeft = left.left;
                Node<K, V> leftRight = left.right;
                int leftDelta = (leftLeft != null ? leftLeft.height : 0) - (leftRight != null ? leftRight.height : 0);
                if (leftDelta == 1 || (leftDelta == 0 && !insert)) {
                    rotateRight(node);
                } else if ($assertionsDisabled || leftDelta == -1) {
                    rotateLeft(left);
                    rotateRight(node);
                } else {
                    throw new AssertionError();
                }
                if (insert) {
                    return;
                }
            } else if (delta == 0) {
                node.height = leftHeight + 1;
                if (insert) {
                    return;
                }
            } else if ($assertionsDisabled || delta == -1 || delta == 1) {
                node.height = Math.max(leftHeight, rightHeight) + 1;
                if (!insert) {
                    return;
                }
            } else {
                throw new AssertionError();
            }
        }
    }

    private void rotateLeft(Node<K, V> root) {
        int i;
        int i2 = 0;
        Node<K, V> left = root.left;
        Node<K, V> pivot = root.right;
        Node<K, V> pivotLeft = pivot.left;
        Node<K, V> pivotRight = pivot.right;
        root.right = pivotLeft;
        if (pivotLeft != null) {
            pivotLeft.parent = root;
        }
        replaceInParent(root, pivot);
        pivot.left = root;
        root.parent = pivot;
        if (left != null) {
            i = left.height;
        } else {
            i = 0;
        }
        root.height = Math.max(i, pivotLeft != null ? pivotLeft.height : 0) + 1;
        int i3 = root.height;
        if (pivotRight != null) {
            i2 = pivotRight.height;
        }
        pivot.height = Math.max(i3, i2) + 1;
    }

    private void rotateRight(Node<K, V> root) {
        int i;
        int i2 = 0;
        Node<K, V> pivot = root.left;
        Node<K, V> right = root.right;
        Node<K, V> pivotLeft = pivot.left;
        Node<K, V> pivotRight = pivot.right;
        root.left = pivotRight;
        if (pivotRight != null) {
            pivotRight.parent = root;
        }
        replaceInParent(root, pivot);
        pivot.right = root;
        root.parent = pivot;
        if (right != null) {
            i = right.height;
        } else {
            i = 0;
        }
        root.height = Math.max(i, pivotRight != null ? pivotRight.height : 0) + 1;
        int i3 = root.height;
        if (pivotLeft != null) {
            i2 = pivotLeft.height;
        }
        pivot.height = Math.max(i3, i2) + 1;
    }

    public final Set<Entry<K, V>> entrySet() {
        EntrySet result = this.entrySet;
        if (result != null) {
            return result;
        }
        result = new EntrySet();
        this.entrySet = result;
        return result;
    }

    public final Set<K> keySet() {
        KeySet result = this.keySet;
        if (result != null) {
            return result;
        }
        result = new KeySet();
        this.keySet = result;
        return result;
    }
}
