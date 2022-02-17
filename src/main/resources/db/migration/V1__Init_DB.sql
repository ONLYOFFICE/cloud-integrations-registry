create table demo_license (
    workspace_id varchar(255) not null,
    workspace_type varchar(255) not null,
    created_at timestamp,
    expires_at timestamp,
    primary key (workspace_id, workspace_type)
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
    workspace_id varchar(255) not null,
    workspace_type varchar(255) not null,
    license_id varchar(255) not null,
    created_at timestamp,
    primary key (workspace_id, workspace_type)
);

create table workspace_user (
    id varchar(255) not null,
    user_id varchar(255) not null,
    workspace_id varchar(255) not null,
    workspace_type varchar(255) not null,
    username varchar(255) not null,
    token text not null,
    created_at timestamp,
    primary key (id)
);

alter table workspace add constraint workspace_license_uk unique (license_id);
alter table workspace_user add constraint workspace_user_id unique (user_id, workspace_id, workspace_type);
alter table workspace add constraint workspace_license_fk foreign key (license_id) references licenses;
alter table workspace_user add constraint workspace_id_fk foreign key (workspace_id, workspace_type) references workspace on delete cascade;