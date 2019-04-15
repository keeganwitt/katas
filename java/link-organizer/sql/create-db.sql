CREATE DATABASE IF NOT EXISTS linksdb;

USE linksdb;

DROP TABLE IF EXISTS link;
CREATE TABLE link (
    id INT NOT NULL AUTO_INCREMENT,
    url VARCHAR(300) NOT NULL,
    CONSTRAINT link_pk PRIMARY KEY (id)
);

DROP TABLE IF EXISTS tag;
CREATE TABLE tag (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    description VARCHAR(100),
    CONSTRAINT tag_pk PRIMARY KEY (id)
);

DROP TABLE IF EXISTS link_tag;
CREATE TABLE link_tag (
    link_id INT NOT NULL,
    tag_id INT NOT NULL,
    PRIMARY KEY (link_id,tag_id),
    FOREIGN KEY (link_id) REFERENCES link (id),
    FOREIGN KEY (tag_id) REFERENCES tag (id)
);
