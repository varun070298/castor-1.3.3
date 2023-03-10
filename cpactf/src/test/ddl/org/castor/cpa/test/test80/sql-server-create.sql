create table test80_self_refer_parent (
  id        int not null,
  fid		int,
  name      varchar(200) not null
)
go

insert into test80_self_refer_parent (id, fid, name) values (1, null, 'entity1')
go
insert into test80_self_refer_parent (id, fid, name) values (2, 1, 'entity2')
go
insert into test80_self_refer_parent (id, fid, name) values (3, 1, 'entity3')
go

create table test80_self_refer_child (
  id        int not null,
  descr     varchar(200) not null
)
go

insert into test80_self_refer_child (id, descr) values (1, 'description1')
go
insert into test80_self_refer_child (id, descr) values (2, 'description2')
go
insert into test80_self_refer_child (id, descr) values (3, 'description3')
go
