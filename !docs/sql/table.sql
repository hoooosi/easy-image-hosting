CREATE DATABASE simple_picture;

DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
CREATE TABLE "tb_user"
(
    id          BIGINT PRIMARY KEY,
    account     VARCHAR(256) UNIQUE NOT NULL,
    password    VARCHAR(512)        NOT NULL,
    name        VARCHAR(256)        NULL,
    avatar      VARCHAR(1024)       NULL,
    profile     VARCHAR(512)        NULL,
    role        VARCHAR(20) DEFAULT 'USER' CHECK (role IN ('ADMIN', 'USER')),
    create_time BIGINT,
    update_time BIGINT
);

CREATE TABLE "tb_space"
(
    id                     BIGINT PRIMARY KEY,
    name                   VARCHAR(128),
    max_size               BIGINT DEFAULT 0,
    total_size             BIGINT DEFAULT 0,
    public_permission_mask BIGINT DEFAULT 0,
    member_permission_mask BIGINT DEFAULT 0,
    create_time            BIGINT,
    update_time            BIGINT,
    CONSTRAINT check_total_size_lt_max_size CHECK (total_size <= max_size)
);

CREATE TABLE "tb_member"
(
    id              BIGINT PRIMARY KEY,
    space_id        BIGINT NOT NULL,
    user_id         BIGINT NOT NULL,
    permission_mask BIGINT DEFAULT 0,
    create_time     BIGINT,
    update_time     BIGINT,
    FOREIGN KEY (space_id) REFERENCES tb_space (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES tb_user (id) ON DELETE CASCADE,
    UNIQUE (space_id, user_id)
);

CREATE TABLE "tb_image_index"
(
    id            BIGINT PRIMARY KEY,
    first_item_id BIGINT       NOT NULL,
    space_id      BIGINT       NOT NULL,
    user_id       BIGINT       NOT NULL,
    name          VARCHAR(128) NOT NULL,
    introduction  VARCHAR(512) DEFAULT '',
    tags          JSONB        DEFAULT '[]',
    create_time   BIGINT,
    update_time   BIGINT,
    FOREIGN KEY (space_id) REFERENCES tb_space (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES tb_user (id) ON DELETE CASCADE
);

CREATE TABLE "tb_image_file"
(
    id           BIGINT PRIMARY KEY,
    md5          VARCHAR(32) UNIQUE NOT NULL,
    content_type VARCHAR(32)        NOT NULL,
    size         BIGINT             NOT NULL,
    width        INT                NOT NULL,
    height       INT                NOT NULL,
    create_time  BIGINT,
    update_time  BIGINT
);

CREATE TABLE "tb_image_item"
(
    id           BIGINT PRIMARY KEY,
    space_id     BIGINT      NOT NULL,
    idx_id       BIGINT      NOT NULL,
    file_id      BIGINT,
    status       VARCHAR(20) CHECK (status IN ('PROCESSING', 'SUCCESS', 'FAILED')),
    md5          VARCHAR(32),
    content_type VARCHAR(32),
    size         BIGINT,
    width        INT,
    height       INT,
    create_time  BIGINT,
    update_time  BIGINT,
    FOREIGN KEY (space_id) REFERENCES tb_space (id) ON DELETE CASCADE,
    FOREIGN KEY (idx_id) REFERENCES tb_image_index (id) ON DELETE CASCADE,
    FOREIGN KEY (file_id) REFERENCES tb_image_file (id) ON DELETE CASCADE
);


CREATE TABLE "tb_application"
(
    id          BIGINT PRIMARY KEY,
    type        VARCHAR(20) NOT NULL CHECK (type IN ('INVITE', 'APPLY')),
    user_id     BIGINT      NOT NULL,
    space_id    BIGINT      NOT NULL,
    status      VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    handle_type VARCHAR(20) CHECK (handle_type IN ('AUTO', 'MANUAL')),
    create_time BIGINT,
    update_time BIGINT,
    handle_time BIGINT,
    FOREIGN KEY (user_id) REFERENCES tb_user (id) ON DELETE CASCADE,
    FOREIGN KEY (space_id) REFERENCES tb_space (id) ON DELETE CASCADE
);
