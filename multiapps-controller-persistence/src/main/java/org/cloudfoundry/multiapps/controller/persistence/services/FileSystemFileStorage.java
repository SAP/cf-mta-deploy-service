package org.cloudfoundry.multiapps.controller.persistence.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.cloudfoundry.multiapps.controller.persistence.Messages;
import org.cloudfoundry.multiapps.controller.persistence.model.FileEntry;
import org.cloudfoundry.multiapps.controller.persistence.model.ImmutableFileEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemFileStorage implements FileStorage {

    private static final String DEFAULT_FILES_STORAGE_PATH = "files";

    private final String storagePath;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public FileSystemFileStorage() {
        this(DEFAULT_FILES_STORAGE_PATH);
    }

    public FileSystemFileStorage(String storagePath) {
        this.storagePath = storagePath;
    }

    @Override
    public void addFile(FileEntry fileEntry, File file) throws FileStorageException {
        try (InputStream fileInputStream = Files.newInputStream(file.toPath())) {
            Path filesDirectory = getFilesDirectory(fileEntry.getSpace());
            Path newFilePath = Paths.get(filesDirectory.toString(), fileEntry.getId());
            logger.trace(MessageFormat.format(Messages.STORING_FILE_TO_PATH_0, newFilePath));
            Files.copy(fileInputStream, newFilePath, StandardCopyOption.REPLACE_EXISTING);
            File newFile = newFilePath.toFile();
            if (!newFile.exists()) {
                throw new FileStorageException(MessageFormat.format(Messages.FILE_UPLOAD_FAILED, fileEntry.getName(),
                                                                    fileEntry.getNamespace()));
            }
            logger.debug(MessageFormat.format(Messages.STORED_FILE_0_WITH_SIZE_1_SUCCESSFULLY_2, newFile, newFile.length()));
        } catch (IOException e) {
            throw new FileStorageException(e.getMessage(), e);
        }
    }

    @Override
    public List<FileEntry> getFileEntriesWithoutContent(List<FileEntry> fileEntries) throws FileStorageException {
        List<FileEntry> entriesWithoutContent = new ArrayList<>();
        for (FileEntry entry : fileEntries) {
            if (!hasContent(entry)) {
                entriesWithoutContent.add(entry);
            }
        }
        return entriesWithoutContent;
    }

    @Override
    public void deleteFile(String id, String space) throws FileStorageException {
        try {
            Path filesDirectory = getFilesDirectory(space);
            Path filePath = Paths.get(filesDirectory.toString(), id);
            logger.debug(MessageFormat.format(Messages.DELETING_FILE_WITH_PATH_0, filePath.toString()));
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new FileStorageException(MessageFormat.format(Messages.ERROR_DELETING_FILE_WITH_ID, id), e);
        }
    }

    @Override
    public void deleteFilesBySpaces(List<String> spaces) throws FileStorageException {
        for (String space : spaces) {
            deleteFilesBySpace(space);
        }
    }

    @Override
    public void deleteFilesBySpaceAndNamespace(String space, String namespace) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int deleteFilesModifiedBefore(Date modificationTime) throws FileStorageException {
        AtomicInteger deletedFiles = new AtomicInteger();
        final FileTime modificationTimeUpperBound = FileTime.fromMillis(modificationTime.getTime());
        try {

            Files.walkFileTree(Paths.get(storagePath), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (attrs.lastModifiedTime()
                             .compareTo(modificationTimeUpperBound) < 0) {
                        logger.trace(MessageFormat.format(Messages.DELETING_FILE_WITH_PATH_0, file.toString()));
                        boolean deleted = Files.deleteIfExists(file);
                        logger.debug(MessageFormat.format(Messages.DELETED_FILE_0_SUCCESSFULLY_1, file.toString(), deleted));
                        if (deleted) {
                            deletedFiles.incrementAndGet();
                        }
                        return FileVisitResult.CONTINUE;
                    }
                    return super.visitFile(file, attrs);
                }
            });
        } catch (IOException e) {
            throw new FileStorageException(e.getMessage(), e);
        }
        return deletedFiles.intValue();
    }

    @Override
    public <T> T processFileContent(String space, String id, FileContentProcessor<T> fileContentProcessor) throws FileStorageException {
        FileEntry fileEntry = createFileEntry(space, id);
        if (!hasContent(fileEntry)) {
            throw new FileStorageException(MessageFormat.format(Messages.FILE_WITH_ID_AND_SPACE_DOES_NOT_EXIST, fileEntry.getId(),
                                                                fileEntry.getSpace()));
        }
        try (InputStream fileContentStream = getFileContentStream(fileEntry)) {
            return fileContentProcessor.process(fileContentStream);
        } catch (Exception e) {
            throw new FileStorageException(e);
        }
    }

    private FileEntry createFileEntry(String space, String id) {
        return ImmutableFileEntry.builder()
                                 .space(space)
                                 .id(id)
                                 .build();
    }

    private InputStream getFileContentStream(FileEntry fileEntry) throws IOException {
        Path fileLocation = getFilePath(fileEntry);
        logger.trace(MessageFormat.format(Messages.PROCESSING_FILE_0, fileLocation));
        return Files.newInputStream(fileLocation);
    }

    public String getStoragePath() {
        return storagePath;
    }

    private Path getFilesDirectory(String space) throws IOException {
        Path filesPerSpaceDirectory = getFilesPerSpaceDirectory(space);
        Files.createDirectories(filesPerSpaceDirectory);
        return filesPerSpaceDirectory;
    }

    private boolean hasContent(FileEntry entry) throws FileStorageException {
        try {
            Path filePath = getFilePath(entry);
            return filePath.toFile()
                           .exists();// squid:S3725 - java 8 Files.exists() has poor performance
        } catch (IOException e) {
            throw new FileStorageException(e.getMessage(), e);
        }
    }

    private Path getFilePath(FileEntry entry) throws IOException {
        Path filesDirectory = getFilesDirectory(entry.getSpace());
        return Paths.get(filesDirectory.toString(), entry.getId());
    }

    private Path getSpaceDirectory(String space) {
        return Paths.get(storagePath, space);
    }

    private Path getFilesPerSpaceDirectory(String space) {
        return Paths.get(storagePath, space, DEFAULT_FILES_STORAGE_PATH);
    }

    private void deleteFilesBySpace(String space) throws FileStorageException {
        File spaceDirectory = getSpaceDirectory(space).toFile();
        try {
            if (spaceDirectory.exists()) {
                FileUtils.deleteDirectory(spaceDirectory);
            }
        } catch (IOException e) {
            throw new FileStorageException(MessageFormat.format(Messages.ERROR_DELETING_DIRECTORY, spaceDirectory), e);
        }
    }

}