CREATE TABLE test82_test_depends_ns_nokg (
  id int(11) NOT NULL,
  master_id int(11) NOT NULL default '0',
  descrip varchar(50) NOT NULL default '',
  constraint prim_id primary key (id)
);

CREATE TABLE test82_test_master_ns_nokg (
  id int(11) NOT NULL,
  descrip varchar(50) NOT NULL default '',
  constraint prim_id primary key (id)
);