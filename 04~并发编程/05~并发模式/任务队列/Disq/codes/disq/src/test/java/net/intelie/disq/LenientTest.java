package net.intelie.disq;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.internal.stubbing.answers.Returns;
import org.mockito.internal.stubbing.answers.ThrowsException;

import java.io.Closeable;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class LenientTest extends Lenient {
    @Test
    public void onExceptionOnTheFirstTime() throws Exception {
        Buffer buffer = new Buffer();
        DiskRawQueue queue = mock(DiskRawQueue.class);
        Lenient.Op op = mock(Lenient.Op.class);

        InOrder orderly = inOrder(queue, op);

        when(op.call(buffer))
                .thenAnswer(new ThrowsException(new Error("abc")))
                .thenAnswer(new Returns(42L));

        assertThat(Lenient.perform(queue, buffer, op)).isEqualTo(42);

        when(op.call(buffer))
                .thenAnswer(new ThrowsException(new Error("abc")))
                .thenAnswer(new Returns(42L));
        assertThat(Lenient.performSafe(queue, buffer, op, 100)).isEqualTo(42);

        orderly.verify(op).call(buffer);
        orderly.verify(queue).reopen();
    }

    @Test
    public void onExceptionAlways() throws Exception {
        Buffer buffer = new Buffer();

        DiskRawQueue queue = mock(DiskRawQueue.class);
        Lenient.Op op = mock(Lenient.Op.class);

        InOrder orderly = inOrder(queue, op);

        when(op.call(buffer)).thenThrow(new Error("abc"));

        assertThatThrownBy(() -> Lenient.perform(queue, buffer, op))
                .isInstanceOf(Error.class).hasMessage("abc");
        assertThat(Lenient.performSafe(queue, buffer, op, 100)).isEqualTo(100);

        orderly.verify(op).call(buffer);
        orderly.verify(queue).reopen();
        orderly.verify(op).call(buffer);
        orderly.verify(queue).reopen();
    }

    @Test
    public void testExceptionOnClose() throws Exception {
        Closeable closeable = mock(Closeable.class);
        doThrow(new Error("abc")).when(closeable).close();

        Lenient.safeClose(closeable);
        verify(closeable).close();
    }

    @Test
    public void safeDeleteOnNonExistingFile() throws Exception {
        Lenient.safeDelete(Paths.get("/whatever/does/not/exist"));
    }
}