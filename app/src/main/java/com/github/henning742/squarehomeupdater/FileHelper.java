package com.github.henning742.squarehomeupdater;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FileHelper {
    public static void CopyFolder(String source, String extra) throws IOException {
        File s = new File(source);
        File d = new File(s.getParent(), s.getName() + extra);
        FileUtils.copyDirectory(s, d);
    }
}
