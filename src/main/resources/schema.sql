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
                                    FOREIGN KEY (owner_id) REFERENCES users (ID) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS booking(
                                    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                    start_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                    end_date     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                    item_id      BIGINT NOT NULL,
                                    booker_id    BIGINT NOT NULL,
                                    status       VARCHAR(16) NOT NULL,
                                    CONSTRAINT pk_booking PRIMARY KEY (id),
                                    FOREIGN KEY (item_id) REFERENCES items (ID) ON UPDATE CASCADE,
                                    FOREIGN KEY (booker_id) REFERENCES users (ID) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS comments(
                                    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                    text         VARCHAR(512) NOT NULL,
                                    item_id      BIGINT NOT NULL,
                                    author_id    BIGINT NOT NULL,
                                    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                    CONSTRAINT pk_comment PRIMARY KEY (id),
                                    FOREIGN KEY (item_id) REFERENCES items (ID) ON UPDATE CASCADE,
                                    FOREIGN KEY (author_id) REFERENCES users (ID) ON UPDATE CASCADE
);


