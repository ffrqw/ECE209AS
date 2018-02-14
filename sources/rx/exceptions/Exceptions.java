package rx.exceptions;

import java.util.List;

public final class Exceptions {
    public static void throwIfFatal(Throwable t) {
        if (t instanceof OnErrorNotImplementedException) {
            throw ((OnErrorNotImplementedException) t);
        } else if (t instanceof OnErrorFailedException) {
            Throwable cause = ((OnErrorFailedException) t).getCause();
            if (cause instanceof RuntimeException) {
                throw ((RuntimeException) cause);
            }
            throw ((OnErrorFailedException) t);
        } else if (t instanceof StackOverflowError) {
            throw ((StackOverflowError) t);
        } else if (t instanceof VirtualMachineError) {
            throw ((VirtualMachineError) t);
        } else if (t instanceof ThreadDeath) {
            throw ((ThreadDeath) t);
        } else if (t instanceof LinkageError) {
            throw ((LinkageError) t);
        }
    }

    public static void throwIfAny(List<? extends Throwable> exceptions) {
        if (exceptions != null && !exceptions.isEmpty()) {
            if (exceptions.size() == 1) {
                Throwable t = (Throwable) exceptions.get(0);
                if (t instanceof RuntimeException) {
                    throw ((RuntimeException) t);
                } else if (t instanceof Error) {
                    throw ((Error) t);
                } else {
                    throw new RuntimeException(t);
                }
            }
            throw new CompositeException(exceptions);
        }
    }
}
