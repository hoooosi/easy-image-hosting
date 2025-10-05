package io.github.hoooosi.imagehosting.constant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Permission {
    // SPACE
    // ------------------------------------------------------------------------
    public static final long SPACE_VIEW = 1L;
    public static final long SPACE_SEARCH = 1L << 1;
    public static final long SPACE_JOIN = 1L << 2;
    public static final long SPACE_AUTO_APPROVE = 1L << 3;
    public static final long SPACE_MANGE = 1L << 4;

    // IMAGE
    // ------------------------------------------------------------------------
    public static final long IMAGE_VIEW = 1L << 21;
    public static final long IMAGE_UPLOAD = 1L << 22;
    public static final long IMAGE_EDIT = 1L << 23;
    public static final long IMAGE_DELETE = 1L << 24;

    // BASE MASK CONFIG
    // ------------------------------------------------------------------------

    public static final long MEMBER_BASE_MASK = IMAGE_VIEW | SPACE_VIEW;

    // PERMISSION MASK SCOPE CONFIG
    // ------------------------------------------------------------------------
    public static final long PUBLIC_MASK_SCOPE = SPACE_VIEW | SPACE_AUTO_APPROVE | SPACE_SEARCH | SPACE_JOIN | IMAGE_VIEW;
    public static final long MEMBER_MASK_SCOPE = IMAGE_EDIT | IMAGE_UPLOAD | IMAGE_DELETE;

    // FUNC
    // ------------------------------------------------------------------------
    public static boolean has(Long hasMask, Long targetMask) {
        log.info("{} : {} : {}", hasMask, targetMask, (hasMask & targetMask) != targetMask);
        return (hasMask & targetMask) == targetMask;
    }
}