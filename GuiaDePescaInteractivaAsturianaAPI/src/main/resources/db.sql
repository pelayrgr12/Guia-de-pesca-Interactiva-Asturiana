DROP DATABASE IF EXISTS GuiaPesca;
CREATE DATABASE GuiaPesca;
USE GuiaPesca;

create table roles
(
    id_rol int auto_increment
        primary key,
    rol    varchar(255) null,
    nombre varchar(255) null
);

create table usuarios
(
    id_usuario       int auto_increment
        primary key,
    nombre           varchar(255)         null,
    correo           varchar(255)         null,
    contrasena       varchar(255)         not null,
    fecha_nacimiento datetime(6)          null,
    habilitado       tinyint(1) default 0 null,
    id_rol           int                  null,
    constraint correo
        unique (correo),
    constraint usuarios_ibfk_1
        foreign key (id_rol) references roles (id_rol)
            on delete set null
);

create table medidas
(
    id_medida  int auto_increment
        primary key,
    id_usuario int          null,
    nombre     varchar(255) null,
    tamano     double       not null,
    peso       double       not null,
    constraint medidas_ibfk_1
        foreign key (id_usuario) references usuarios (id_usuario)
            on delete cascade
);

create index id_usuario
    on medidas (id_usuario);

create table puntos
(
    id_punto   int auto_increment
        primary key,
    id_usuario int          not null,
    latitud    double       not null,
    longitud   double       not null,
    nombre     varchar(255) not null,
    constraint puntos_ibfk_1
        foreign key (id_usuario) references usuarios (id_usuario)
            on delete cascade
);

create table historial_puntos
(
    id_historial int auto_increment
        primary key,
    id_punto     int          not null,
    fecha        datetime(6)  null,
    descripcion  varchar(255) null,
    imagenes     varchar(255) null,
    constraint historial_puntos_ibfk_1
        foreign key (id_punto) references puntos (id_punto)
            on delete cascade
);

create index id_punto
    on historial_puntos (id_punto);

create index id_usuario
    on puntos (id_usuario);

create index id_rol
    on usuarios (id_rol);

