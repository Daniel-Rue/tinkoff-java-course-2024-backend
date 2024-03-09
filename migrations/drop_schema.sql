--liquibase formatted sql

--changeset Daniel-Rue:01-drop-link
DROP TABLE IF EXISTS link CASCADE;

--changeset Daniel-Rue:02-drop-chat.sql
DROP TABLE IF EXISTS chat CASCADE;

--changeset Daniel-Rue:03-create-chat_link.sql
DROP TABLE IF EXISTS chat_link CASCADE;
