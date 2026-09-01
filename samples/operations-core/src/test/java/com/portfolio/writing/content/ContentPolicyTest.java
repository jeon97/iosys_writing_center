package com.portfolio.writing.content;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class ContentPolicyTest {
    private final ContentPolicy policy = new ContentPolicy();

    @Test
    void acceptsSupportedAttachments() {
        assertDoesNotThrow(() -> policy.validate(
                "상담 자료", "본문입니다.", List.of("draft.docx", "diagram.png")
        ));
    }

    @Test
    void rejectsUnsupportedAttachments() {
        assertThrows(IllegalArgumentException.class,
                () -> policy.validate("제목", "본문", List.of("script.exe")));
    }
}
