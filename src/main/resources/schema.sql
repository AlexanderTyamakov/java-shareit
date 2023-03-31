-- drop TABLE IF EXISTS users ,items ,item_request ,booking ,comments  CASCADE;

CREATE TABLE IF NOT EXISTS users (
                                     id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                     name        VARCHAR(255) NOT NULL,
                                     email       VARCHAR(512) NOT NULL,
                                     CONSTRAINT pk_user PRIMARY KEY (id),
                                     CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items(
                                    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                    name         VARCHAR(255) NOT NULL,
                                    description  VARCHAR(512) NOT NULL,
                                    is_available BOOLEAN NOT NULL,
                                    owner_id     BIGINT NOT NULL,
                                    request_id   BIGINT,
                                    CONSTRAINT pk_item PRIMARY KEY (id),
                                    CONSTRAINT fk_item_user FOREIGN KEY (owner_id) REFERENCES users (ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS booking(
                                    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                    start_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                    end_date     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                    item_id      BIGINT NOT NULL,
                                    booker_id    BIGINT NOT NULL,
                                    status       VARCHAR(16) NOT NULL,
                                    CONSTRAINT pk_booking PRIMARY KEY (id),
                                    CONSTRAINT fk_booking_item FOREIGN KEY (item_id) REFERENCES items (ID) ON DELETE CASCADE ON UPDATE CASCADE,
                                    CONSTRAINT fk_booking_user FOREIGN KEY (booker_id) REFERENCES users (ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS comments(
                                    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                    text         VARCHAR(512) NOT NULL,
                                    item_id      BIGINT NOT NULL,
                                    author_id    BIGINT NOT NULL,
                                    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                    CONSTRAINT pk_comment PRIMARY KEY (id),
                                    CONSTRAINT fk_comment_item FOREIGN KEY (item_id) REFERENCES items (ID) ON DELETE CASCADE ON UPDATE CASCADE,
                                    CONSTRAINT fk_comment_user FOREIGN KEY (author_id) REFERENCES users (ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS item_request(
                                    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                    description  VARCHAR(512) NOT NULL,
                                    requestor_id BIGINT NOT NULL,
                                    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                    CONSTRAINT pk_request PRIMARY KEY (id),
                                    CONSTRAINT fk_request_user FOREIGN KEY (requestor_id) REFERENCES users (ID) ON DELETE CASCADE ON UPDATE CASCADE
);

