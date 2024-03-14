--liquibase formatted sql

--changeset Daniel-Rue:01-create-link
CREATE TABLE IF NOT EXISTS link
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY,
    url             TEXT NOT NULL,
    last_check_time TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by      TEXT NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (url)
);

--changeset Daniel-Rue:02-create-chat.sql
CREATE TABLE IF NOT EXISTS chat
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,

    PRIMARY KEY (id)
);

--changeset Daniel-Rue:03-create-chat_link.sql
CREATE TABLE IF NOT EXISTS chat_link
(
    chat_id        BIGINT NOT NULL,
    link_id        BIGINT NOT NULL,
    subscribed_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    PRIMARY KEY (chat_id, link_id),
    FOREIGN KEY (chat_id) REFERENCES chat (id) ON DELETE CASCADE,
    FOREIGN KEY (link_id) REFERENCES link (id) ON DELETE CASCADE
);
