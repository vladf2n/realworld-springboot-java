CREATE TABLE IF NOT EXISTS users
(
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    bio      VARCHAR(1024),
    image    VARCHAR(1024),
    email    VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_followings
(
    follower_id BIGINT NOT NULL,
    followee_id BIGINT NOT NULL,
    PRIMARY KEY (follower_id, followee_id),
    CONSTRAINT fk_follower FOREIGN KEY (follower_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_followee FOREIGN KEY (followee_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS articles
(
    id          BIGSERIAL PRIMARY KEY,
    author_id   BIGINT       NOT NULL,
    author_name VARCHAR(255),
    title       VARCHAR(255) NOT NULL,
    slug        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    body        VARCHAR      NOT NULL,
    import_id   BIGINT,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES users (id),
    CONSTRAINT unique_author_slug UNIQUE (author_id, slug),
    CONSTRAINT fk_import FOREIGN KEY (import_id) REFERENCES import(id)
);

CREATE TABLE IF NOT EXISTS tags
(
    id    BIGSERIAL PRIMARY KEY,
    value VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS articles_tags
(
    article_id BIGINT NOT NULL,
    tag_id BIGINT     NOT NULL,
    PRIMARY KEY (article_id, tag_id),
    CONSTRAINT fk_article FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE,
    CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS article_favorites
(
    article_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (article_id, user_id),
    CONSTRAINT fk_article_favorited FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    id          BIGSERIAL PRIMARY KEY,
    author_id   BIGINT       NOT NULL,
    article_id  BIGINT       NOT NULL,
    body        VARCHAR      NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_article FOREIGN KEY (article_id) REFERENCES articles (id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS import
(
  id          BIGSERIAL PRIMARY KEY,
  user_id     BIGINT       NOT NULL,
  created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  imported    BOOL  NOT NULL,
  content     TEXT  NOT NULL,
  CONSTRAINT fk_user_import FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  );
