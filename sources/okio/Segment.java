package okio;

final class Segment {
    final byte[] data;
    int limit;
    Segment next;
    boolean owner;
    int pos;
    Segment prev;
    boolean shared;

    Segment() {
        this.data = new byte[8192];
        this.owner = true;
        this.shared = false;
    }

    Segment(Segment shareFrom) {
        this(shareFrom.data, shareFrom.pos, shareFrom.limit);
        shareFrom.shared = true;
    }

    Segment(byte[] data, int pos, int limit) {
        this.data = data;
        this.pos = pos;
        this.limit = limit;
        this.owner = false;
        this.shared = true;
    }

    public final Segment pop() {
        Segment result;
        if (this.next != this) {
            result = this.next;
        } else {
            result = null;
        }
        this.prev.next = this.next;
        this.next.prev = this.prev;
        this.next = null;
        this.prev = null;
        return result;
    }

    public final Segment push(Segment segment) {
        segment.prev = this;
        segment.next = this.next;
        this.next.prev = segment;
        this.next = segment;
        return segment;
    }

    public final void writeTo(Segment sink, int byteCount) {
        if (sink.owner) {
            if (sink.limit + byteCount > 8192) {
                if (sink.shared) {
                    throw new IllegalArgumentException();
                } else if ((sink.limit + byteCount) - sink.pos > 8192) {
                    throw new IllegalArgumentException();
                } else {
                    System.arraycopy(sink.data, sink.pos, sink.data, 0, sink.limit - sink.pos);
                    sink.limit -= sink.pos;
                    sink.pos = 0;
                }
            }
            System.arraycopy(this.data, this.pos, sink.data, sink.limit, byteCount);
            sink.limit += byteCount;
            this.pos += byteCount;
            return;
        }
        throw new IllegalArgumentException();
    }
}
