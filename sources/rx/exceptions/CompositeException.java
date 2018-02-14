package rx.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class CompositeException extends RuntimeException {
    private Throwable cause;
    private final List<Throwable> exceptions;
    private final String message;

    static final class CompositeExceptionCausalChain extends RuntimeException {
        static String MESSAGE = "Chain of Causes for CompositeException In Order Received =>";

        CompositeExceptionCausalChain() {
        }

        public final String getMessage() {
            return MESSAGE;
        }
    }

    private static abstract class PrintStreamOrWriter {
        abstract Object lock();

        abstract void println(Object obj);

        private PrintStreamOrWriter() {
        }
    }

    private static class WrappedPrintStream extends PrintStreamOrWriter {
        private final PrintStream printStream;

        WrappedPrintStream(PrintStream printStream) {
            super();
            this.printStream = printStream;
        }

        final Object lock() {
            return this.printStream;
        }

        final void println(Object o) {
            this.printStream.println(o);
        }
    }

    private static class WrappedPrintWriter extends PrintStreamOrWriter {
        private final PrintWriter printWriter;

        WrappedPrintWriter(PrintWriter printWriter) {
            super();
            this.printWriter = printWriter;
        }

        final Object lock() {
            return this.printWriter;
        }

        final void println(Object o) {
            this.printWriter.println(o);
        }
    }

    public CompositeException(Collection<? extends Throwable> errors) {
        this.cause = null;
        Set<Throwable> deDupedExceptions = new LinkedHashSet();
        List<Throwable> _exceptions = new ArrayList();
        if (errors != null) {
            for (Throwable ex : errors) {
                if (ex instanceof CompositeException) {
                    deDupedExceptions.addAll(((CompositeException) ex).exceptions);
                } else if (ex != null) {
                    deDupedExceptions.add(ex);
                } else {
                    deDupedExceptions.add(new NullPointerException());
                }
            }
        } else {
            deDupedExceptions.add(new NullPointerException());
        }
        _exceptions.addAll(deDupedExceptions);
        this.exceptions = Collections.unmodifiableList(_exceptions);
        this.message = this.exceptions.size() + " exceptions occurred. ";
    }

    public CompositeException(Collection<? extends Throwable> errors, byte b) {
        this(errors);
    }

    public final String getMessage() {
        return this.message;
    }

    public final synchronized Throwable getCause() {
        if (this.cause == null) {
            Throwable _cause = new CompositeExceptionCausalChain();
            Set<Throwable> seenCauses = new HashSet();
            Throwable chain = _cause;
            for (Throwable e : this.exceptions) {
                Throwable e2;
                if (!seenCauses.contains(e2)) {
                    seenCauses.add(e2);
                    for (Throwable child : getListOfCauses(e2)) {
                        if (seenCauses.contains(child)) {
                            e2 = new RuntimeException("Duplicate found in causal chain so cropping to prevent loop ...");
                        } else {
                            seenCauses.add(child);
                        }
                    }
                    try {
                        chain.initCause(e2);
                    } catch (Throwable th) {
                    }
                    chain = chain.getCause();
                }
            }
            this.cause = _cause;
        }
        return this.cause;
    }

    public final void printStackTrace() {
        printStackTrace(System.err);
    }

    public final void printStackTrace(PrintStream s) {
        printStackTrace(new WrappedPrintStream(s));
    }

    public final void printStackTrace(PrintWriter s) {
        printStackTrace(new WrappedPrintWriter(s));
    }

    private void printStackTrace(PrintStreamOrWriter s) {
        StringBuilder bldr = new StringBuilder();
        bldr.append(this).append("\n");
        for (StackTraceElement myStackElement : getStackTrace()) {
            bldr.append("\tat ").append(myStackElement).append("\n");
        }
        int i = 1;
        for (Throwable ex : this.exceptions) {
            bldr.append("  ComposedException ").append(i).append(" :\n");
            appendStackTrace(bldr, ex, "\t");
            i++;
        }
        synchronized (s.lock()) {
            s.println(bldr.toString());
        }
    }

    private void appendStackTrace(StringBuilder bldr, Throwable ex, String prefix) {
        while (true) {
            bldr.append(prefix).append(ex).append("\n");
            for (StackTraceElement stackElement : ex.getStackTrace()) {
                bldr.append("\t\tat ").append(stackElement).append("\n");
            }
            if (ex.getCause() != null) {
                bldr.append("\tCaused by: ");
                ex = ex.getCause();
                prefix = "";
            } else {
                return;
            }
        }
    }

    private static List<Throwable> getListOfCauses(Throwable ex) {
        List<Throwable> list = new ArrayList();
        Throwable root = ex.getCause();
        if (root != null) {
            while (true) {
                list.add(root);
                if (root.getCause() == null) {
                    break;
                }
                root = root.getCause();
            }
        }
        return list;
    }
}
