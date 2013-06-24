#@(#) script.ddl

DROP TABLE IF EXISTS MiembroTendencia;
DROP TABLE IF EXISTS Plebiscito;
DROP TABLE IF EXISTS Enlazes;
DROP TABLE IF EXISTS Post;
DROP TABLE IF EXISTS Foro;
DROP TABLE IF EXISTS Padron;
DROP TABLE IF EXISTS Usuario;
DROP TABLE IF EXISTS Tendencia;
DROP TABLE IF EXISTS Votacion;
CREATE TABLE Votacion
(
	NombrePlebiscito varchar (32),
	NombreTendencia varchar (32),
	Cedula int,
	PRIMARY KEY(NombrePlebiscito, NombreTendencia, Cedula),
	FOREIGN KEY(NombrePlebiscito, NombreTendencia) REFERENCES Tendencia (NombrePlebiscito, NombreTendencia),
	FOREIGN KEY(Cedula) REFERENCES Usuario (Cedula)
);

CREATE TABLE Tendencia
(
	NombreTendencia varchar (32),
	NombrePlebiscito varchar (32),
	Representante int,
	Descripcion varchar (256) NOT NULL,
	Pagina varchar (64),
	Imagen image,
	Contacto varchar (32),
	InformacionAdicional varchar (256),
	PRIMARY KEY(NombreTendencia, NombrePlebiscito),
	FOREIGN KEY(NombrePlebiscito) REFERENCES Plebiscito (NombrePlebiscito),
	FOREIGN KEY(Representante) REFERENCES Usuario (Cedula)
);

CREATE TABLE Usuario
(
	Cedula int PRIMARY KEY,
	NombreUsuario NOT NULL UNIQUE,
	Password varchar (24) NOT NULL,
	Nombre varchar (32) NOT NULL,
	Apellido1 varchar (32) NOT NULL,
	Apellido2 varchar (32) NOT NULL,
	Distrito varchar (32) NOT NULL,
	Canton varchar (32) NOT NULL,
	Provincia varchar (32) NOT NULL,
	Fecha Nacimiento datetime NOT NULL,
	Sexo char NOT NULL
);

CREATE TABLE Padron
(
	Cedula int,
	NombrePlebiscito varchar (32),
	PRIMARY KEY(Cedula, NombrePlebiscito),
	FOREIGN KEY(Cedula) REFERENCES Usuario (Cedula),
	FOREIGN KEY(NombrePlebiscito) REFERENCES Plebiscito (NombrePlebiscito)
);

CREATE TABLE Foro
(
	NombreTema varchar (32),
	NombrePlebiscito varchar (32),
	FechaCreacion date NOT NULL,
	PRIMARY KEY(NombreTema, NombrePlebiscito),
	FOREIGN KEY(NombrePlebiscito) REFERENCES Plebiscito (NombrePlebiscito)
);

CREATE TABLE Post
(
	NombreTema varchar (32),
	NombrePlebiscito varchar (32),
	Usuario,
	NumeroPost int,
	Texto varchar (256) NOT NULL,
	FechaUltimaEdicion date NOT NULL,
	PRIMARY KEY(NombreTema, NombrePlebiscito, Usuario, NumeroPost),
	FOREIGN KEY(NombreTema, NombrePlebiscito) REFERENCES Foro (NombreTema, NombrePlebiscito),
	FOREIGN KEY(Usuario) REFERENCES Usuario (Cedula)
);

CREATE TABLE Enlazes
(
	NombreTendencia varchar (32),
	NombrePlebiscito varchar (32),
	Enlaze varchar (32),
	PRIMARY KEY(NombreTendencia, NombrePlebiscito, Enlaze),
	CONSTRAINT FCK FOREIGN KEY(NombreTendencia, NombrePlebiscito) REFERENCES ,
	FOREIGN KEY(NombreTendencia, NombrePlebiscito) REFERENCES Tendencia (NombreTendencia, NombrePlebiscito)
);

CREATE TABLE Plebiscito
(
	NombrePlebiscito varchar (32) PRIMARY KEY,
	Organizador int,
	Descripcion varchar (256) NOT NULL,
	Comunidad varchar (32) NOT NULL,
	Tipo char NOT NULL,
	Estilo char NOT NULL,
	InicioInscripcionTendencias date,
	InicioPeriodoDiscucion date,
	InicioPeriodoVotacion date,
	FinInscripcionTendencias date,
	FinPeriodoDiscusion date,
	FinPeriodoVotacion date,
	Imagen image,
	FOREIGN KEY(Organizador) REFERENCES Usuario (Cedula)
);

CREATE TABLE MiembroTendencia
(
	NombreTendencia varchar (32),
	NombrePlebiscito varchar (32),
	Cedula int,
	PRIMARY KEY(Cedula, NombreTendencia, NombrePlebiscito),
	FOREIGN KEY(NombreTendencia, NombrePlebiscito) REFERENCES Tendencia (NombreTendencia, NombrePlebiscito),
	FOREIGN KEY(Cedula) REFERENCES Usuario (Cedula)
);
