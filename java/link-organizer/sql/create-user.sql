USE mysql;
CREATE USER 'linksUser'@'%' IDENTIFIED BY 'linksUser';
GRANT ALL PRIVILEGES ON linksdb.* TO 'linksUser'@'%';
FLUSH PRIVILEGES;
