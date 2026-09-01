package com.portfolio.writing.content;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ContentPolicy {
    private static final int MAX_TITLE_LENGTH = 150;
    private static final int MAX_ATTACHMENTS = 5;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "docx", "hwp", "png", "jpg");

    public void validate(String title, String body, List<String> attachmentNames) {
        if (title == null || title.isBlank() || title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("title is required and must be 150 characters or fewer");
        }
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("body is required");
        }
        if (attachmentNames.size() > MAX_ATTACHMENTS) {
            throw new IllegalArgumentException("too many attachments");
        }
        for (String name : attachmentNames) {
            String extension = extensionOf(name);
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new IllegalArgumentException("unsupported attachment type");
            }
        }
    }

    private String extensionOf(String fileName) {
        int separator = fileName == null ? -1 : fileName.lastIndexOf('.');
        if (separator < 0 || separator == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(separator + 1).toLowerCase(Locale.ROOT);
    }
}

