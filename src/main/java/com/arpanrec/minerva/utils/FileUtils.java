package com.arpanrec.minerva.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileUtils {

    public static String fileOrString(String pathOrString) throws IOException {
        Path path = Paths.get(pathOrString);
       if (Files.exists(path) && path.toFile().isFile()) {
            log.debug("Loading key from file.");
            return Files.readString(path);
        } else {
            log.debug("Loading key from string.");
            return pathOrString;
        }
    }
}
