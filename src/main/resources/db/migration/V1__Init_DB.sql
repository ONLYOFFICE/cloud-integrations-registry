create table demo_license (
    id varchar(255) not null,
    created_at timestamp,
    expires_at timestamp,
    primary key (id)
);

create table licenses (
    id varchar(255) not null,
    header varchar(255),
    secret varchar(255),
    url varchar(255),
    created_at timestamp,
    updated_at timestamp,
    primary key (id)
);

create table workspace (
    id varchar(255) not null,
    created_at timestamp,
    license_id varchar(255),
    type_id integer not null,
    primary key (id)
);

create table workspace_types (
    id serial not null,
    created_at timestamp,
    name varchar(255) not null,
    primary key (id)
);

create table workspace_user (
    userid varchar(255) not null,
    workspaceid varchar(255) not null,
    created_at timestamp,
    token text not null,
    username varchar(255) not null,
    workspace_id varchar(255) not null,
    primary key (userid, workspaceid)
);

alter table workspace_types add constraint name_uk unique (name);
alter table workspace add constraint workspace_license_fk foreign key (license_id) references licenses;
alter table workspace add constraint workspace_types_fk foreign key (type_id) references workspace_types;
alter table workspace_user add constraint workspace_id_fk foreign key (workspace_id) references workspace on delete cascade;