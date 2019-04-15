USE linksdb;

START TRANSACTION;
SET SQL_SAFE_UPDATES = 0;

DELETE FROM link_tag;
DELETE FROM link;
DELETE FROM tag;

INSERT INTO tag VALUES (1,'search engines',NULL);
INSERT INTO tag VALUES (2,'privacy advocates',NULL);
INSERT INTO tag VALUES (3,'ad companies',NULL);

INSERT INTO link VALUES (1,'https://duckduckgo.com/');
INSERT INTO link_tag VALUES (1,1);
INSERT INTO link_tag VALUES (1,2);
INSERT INTO link VALUES (2,'https://www.google.com/');
INSERT INTO link_tag VALUES (2,1);
INSERT INTO link_tag VALUES (2,3);

SET SQL_SAFE_UPDATES = 1;
COMMIT;
