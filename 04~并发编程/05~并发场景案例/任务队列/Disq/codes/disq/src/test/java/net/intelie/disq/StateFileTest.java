package net.intelie.disq;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class StateFileTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();
    private File statePath;
    private StateFile state;

    @Before
    public void setUp() throws Exception {
        statePath = temp.newFile();
        state = new StateFile(statePath.toPath(), false);
    }

    @Test
    public void willHaveDefaults() throws Exception {
        assertThreeFirst(state, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    @Test
    public void testNumberOfFilesOtherWayAround() {
        for (int i = 0; i < StateFile.MAX_FILE_ID - 50; i++)
            state.advanceWriteFile();

        for (int i = 0; i < StateFile.MAX_FILE_ID - 70; i++)
            state.advanceReadFile(0);

        assertThat(state.getNumberOfFiles()).isEqualTo(20);

        for (int i = 0; i < 100; i++)
            state.advanceWriteFile();

        assertThat(state.getNumberOfFiles()).isEqualTo(120);
    }

    @Test
    public void willSave() throws Exception {
        exampleData(state);

        assertThat(statePath.length()).isEqualTo(0);
        state.flush();
        assertThat(statePath.length()).isEqualTo(512);

        DataInputStream data = new DataInputStream(new FileInputStream(statePath));
        assertThat(data.readShort()).isEqualTo((short) 1);
        assertThat(data.readShort()).isEqualTo((short) 2);
        assertThat(data.readInt()).isEqualTo(50);
        assertThat(data.readInt()).isEqualTo(82);
        assertThat(data.readLong()).isEqualTo(1);
        assertThat(data.readLong()).isEqualTo(83);

        assertThat(data.readInt()).isEqualTo(0);
        assertThat(data.readInt()).isEqualTo(-1);
        assertThat(data.readInt()).isEqualTo(2);
        for (int i = 3; i < StateFile.MAX_FILES; i++) {
            assertThat(data.readInt()).isEqualTo(0);
        }
        assertThat(data.read()).isEqualTo(-1);
    }

    private void exampleData(StateFile state) {
        state.addReadCount(2);
        state.addWriteCount(5);
        state.advanceWriteFile();
        state.advanceWriteFile();
        state.advanceReadFile(4);
        state.addReadCount(50);
        state.addWriteCount(56);
        state.addWriteCount(26);
    }

    @Test
    public void testSaveAndReopen() throws Exception {
        exampleData(state);

        assertThat(statePath.length()).isEqualTo(0);
        state.flush();
        state.close();

        state = new StateFile(statePath.toPath(), false);

        assertThreeFirst(state, 1, 2, 50, 82, 1, 83, 0, -1, 2);
    }

    private void assertThreeFirst(StateFile state, int readFile, int writeFile, int readPosition, int writePosition, int count, int bytes, int c1, int c2, int c3) {
        assertThat(state.getReadFile()).isEqualTo(readFile);
        assertThat(state.getWriteFile()).isEqualTo(writeFile);
        assertThat(state.getReadPosition()).isEqualTo(readPosition);
        assertThat(state.getWritePosition()).isEqualTo(writePosition);
        assertThat(state.getCount()).isEqualTo(count);
        assertThat(state.getBytes()).isEqualTo(bytes);
        assertThat(state.getNumberOfFiles()).isEqualTo(writeFile - readFile + (writePosition > 0 ? 1 : 0));

        assertThat(state.getFileCount(0)).isEqualTo(c1);
        assertThat(state.getFileCount(1)).isEqualTo(c2);
        assertThat(state.getFileCount(2)).isEqualTo(c3);
        for (int i = 3; i < StateFile.MAX_FILES; i++) {
            assertThat(state.getFileCount(i)).isEqualTo(0);
        }
    }

    @Test
    public void testClear() throws Exception {
        exampleData(state);

        assertThat(statePath.length()).isEqualTo(0);
        state.flush();
        state.clear();
        state.flush();
        state.close();

        state = new StateFile(statePath.toPath(), false);

        assertThreeFirst(state, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    @Test
    public void testUnflushed() throws Exception {
        state.addWriteCount(1);
        state.addWriteCount(2);
        state.addWriteCount(3);

        assertThat(state.getCount()).isEqualTo(3);
        assertThat(state.getUnflushedCount()).isEqualTo(3);
        assertThat(state.getFlushedCount()).isEqualTo(0);

        state.flush();

        assertThat(state.getCount()).isEqualTo(3);
        assertThat(state.getUnflushedCount()).isEqualTo(0);
        assertThat(state.getFlushedCount()).isEqualTo(3);

        state.addReadCount(1);
        state.addReadCount(2);
        state.addWriteCount(4);
        assertThat(state.getUnflushedCount()).isEqualTo(1);
        assertThat(state.getFlushedCount()).isEqualTo(1);
        assertThat(state.getCount()).isEqualTo(2);

    }

    @Test
    public void assertAdvanceWrite() throws Exception {
        state.addWriteCount(42);

        assertThat(state.getWritePosition()).isEqualTo(42);
        assertThat(state.getBytes()).isEqualTo(42);
        assertThat(state.getCount()).isEqualTo(1);
        assertThat(state.getFileCount(0)).isEqualTo(1);
    }

    @Test
    public void assertAdvanceRead() throws Exception {
        state.addReadCount(42);

        assertThat(state.getReadPosition()).isEqualTo(42);
        assertThat(state.getBytes()).isEqualTo(0);
        assertThat(state.getCount()).isEqualTo(-1);
        assertThat(state.getFileCount(0)).isEqualTo(-1);
    }

    @Test
    public void assertAdvanceFileWrite() throws Exception {
        state.addWriteCount(42);
        state.advanceWriteFile();

        assertThat(state.getWritePosition()).isEqualTo(0);
        assertThat(state.getWriteFile()).isEqualTo(1);
        assertThat(state.getBytes()).isEqualTo(42);
        assertThat(state.getCount()).isEqualTo(1);
        assertThat(state.getFileCount(0)).isEqualTo(1);
        assertThat(state.getFileCount(1)).isEqualTo(0);
    }

    @Test
    public void assertAdvanceFileRead() throws Exception {
        state.addWriteCount(42);
        state.addWriteCount(42);
        state.addReadCount(42);

        assertThat(state.readFileEof()).isFalse();
        state.advanceReadFile(84);

        assertThat(state.readFileEof()).isTrue();

        assertThat(state.getReadPosition()).isEqualTo(0);
        assertThat(state.getBytes()).isEqualTo(0);
        assertThat(state.getCount()).isEqualTo(0);
        assertThat(state.getFileCount(0)).isEqualTo(0);
        assertThat(state.getFileCount(1)).isEqualTo(0);
    }

    @Test
    public void testIsInUse() throws Exception {
        for (int i = 0; i < 10; i++)
            state.advanceWriteFile();

        for (int i = 0; i < 5; i++)
            state.advanceReadFile(0);

        for (int i = 0; i < 5; i++)
            assertThat(state.isInUse(i)).isFalse();
        for (int i = 5; i <= 10; i++)
            assertThat(state.isInUse(i)).isTrue();
        for (int i = 11; i < 20; i++)
            assertThat(state.isInUse(i)).isFalse();
    }

    @Test
    public void testIsInUseInverse() throws Exception {
        for (int i = 0; i < 10; i++)
            state.advanceWriteFile();

        for (int i = 0; i < 10; i++)
            state.advanceReadFile(0);

        for (int i = 0; i < StateFile.MAX_FILES - 5; i++)
            state.advanceWriteFile();

        for (int i = 0; i <= 5; i++)
            assertThat(state.isInUse(i)).isTrue();
        for (int i = 6; i < 10; i++)
            assertThat(state.isInUse(i)).isFalse();
        for (int i = 10; i < 20; i++)
            assertThat(state.isInUse(i)).isTrue();

    }
}