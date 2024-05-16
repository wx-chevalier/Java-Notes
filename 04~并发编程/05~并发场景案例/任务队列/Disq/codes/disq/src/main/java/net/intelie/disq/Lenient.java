package net.intelie.disq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class Lenient {
    private static final Logger LOGGER = LoggerFactory.getLogger(Lenient.class);

    public static long perform(RawQueue queue, Buffer buffer, Op supplier) throws IOException {
        try {
            return supplier.call(buffer);
        } catch (Throwable e) {
            LOGGER.info("First try queue operation error", e);
            queue.reopen();
            try {
                return supplier.call(buffer);
            } catch (Throwable e2) {
                LOGGER.info("Second try queue operation error", e2);
                queue.reopen();
                throw e2;
            }
        }
    }

    public static long performSafe(RawQueue queue, Buffer buffer, Op supplier, long defaultValue) {
        try {
            return perform(queue, buffer, supplier);
        } catch (Throwable e) {
            return defaultValue;
        }
    }


    public static void safeClose(AutoCloseable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (Throwable e) {
            LOGGER.info("Error closing closeable", e);
        }
    }

    public static void safeDelete(Path directory) {
        try {
            Files.walkFileTree(directory, new DeleteFileVisitor());
        } catch (Throwable e) {
            LOGGER.info("Error deleting directory", e);
        }
    }

    public interface Op {
        long call(Buffer buffer) throws IOException;
    }
}
