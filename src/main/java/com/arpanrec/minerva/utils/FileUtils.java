package com.arpanrec.minerva.utils;

import com.arpanrec.minerva.exceptions.MinervaException;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileUtils {

    public static String fileOrString(String pathOrString) {
        Path path = Paths.get(pathOrString);
       if (Files.exists(path) && path.toFile().isFile()) {
            log.debug("Loading key from file.");
            try {
                return Files.readString(path);
            } catch (Exception e) {
                throw new MinervaException("Unable to read file: " + pathOrString, e);
            }
        } else {
            log.debug("Loading key from string.");
            return pathOrString;
        }
    }
}
