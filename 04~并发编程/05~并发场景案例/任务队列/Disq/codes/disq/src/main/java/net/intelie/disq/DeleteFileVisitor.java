package net.intelie.disq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class DeleteFileVisitor extends SimpleFileVisitor<Path> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteFileVisitor.class);

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        tryDelete(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        tryDelete(dir);
        return FileVisitResult.CONTINUE;
    }

    private void tryDelete(Path dir) {
        try {
            Files.delete(dir);
        } catch (Exception e) {
            LOGGER.info("Could not delete {}: {}", dir, e.getMessage());
            LOGGER.debug("Stacktrace", e);
        }
    }
}
