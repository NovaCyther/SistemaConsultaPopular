
CREATE TABLE firmadigital
(
  persona integer NOT NULL,
  llavepublica bytea,
  CONSTRAINT firmadigital_pkey PRIMARY KEY (persona)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE firmadigital
  OWNER TO postgres;
GRANT ALL ON TABLE firmadigital TO public;
GRANT ALL ON TABLE firmadigital TO postgres;

CREATE TABLE miembrostendencia
(
  nombretendencia character varying(32) NOT NULL,
  nombreplebiscito character varying(32) NOT NULL,
  nombremiembro character varying(32) NOT NULL,
  CONSTRAINT miembrostendencia_pkey PRIMARY KEY (nombretendencia, nombreplebiscito, nombremiembro),
  CONSTRAINT miembrostendencia_nombretendencia_fkey FOREIGN KEY (nombretendencia, nombreplebiscito)
      REFERENCES tendencia (nombretendencia, nombreplebiscito) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE miembrostendencia
  OWNER TO postgres;
GRANT ALL ON TABLE miembrostendencia TO public;
GRANT ALL ON TABLE miembrostendencia TO postgres;

CREATE TABLE padron
(
  cedula integer NOT NULL,
  nombreplebiscito character varying(32) NOT NULL,
  nombre character varying(32) NOT NULL,
  apellido1 character varying(32) NOT NULL,
  apellido2 character varying(32) NOT NULL,
  CONSTRAINT padron_pkey PRIMARY KEY (cedula, nombreplebiscito),
  CONSTRAINT padron_nombreplebiscito_fkey FOREIGN KEY (nombreplebiscito)
      REFERENCES plebiscito (nombreplebiscito) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE padron
  OWNER TO postgres;
GRANT ALL ON TABLE padron TO public;
GRANT ALL ON TABLE padron TO postgres;

CREATE TABLE plebiscito
(
  nombreplebiscito character varying(32) NOT NULL,
  organizador integer,
  descripcion character varying(256) NOT NULL,
  comunidad character varying(32) NOT NULL,
  tipo character(1) NOT NULL,
  estilo character(1) NOT NULL,
  inicioinscripciontendencias date,
  inicioperiododiscucion date,
  inicioperiodovotacion date,
  fininscripciontendencias date,
  finperiododiscusion date,
  finperiodovotacion date,
  imagen character varying(256),
  CONSTRAINT plebiscito_pkey PRIMARY KEY (nombreplebiscito),
  CONSTRAINT plebiscito_organizador_fkey FOREIGN KEY (organizador)
      REFERENCES usuario (cedula) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE plebiscito
  OWNER TO postgres;
GRANT ALL ON TABLE plebiscito TO public;
GRANT ALL ON TABLE plebiscito TO postgres;


CREATE TABLE post
(
  nombretema character varying(32) NOT NULL,
  nombreplebiscito character varying(32) NOT NULL,
  usuario character varying(32) NOT NULL,
  texto character varying(256) NOT NULL,
  id integer NOT NULL,
  CONSTRAINT post_pkey PRIMARY KEY (id, nombreplebiscito, nombretema),
  CONSTRAINT post_usuario_fkey FOREIGN KEY (usuario)
      REFERENCES usuario (nombreusuario) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE post
  OWNER TO postgres;
GRANT ALL ON TABLE post TO public;
GRANT ALL ON TABLE post TO postgres;

CREATE TABLE resultados
(
  nombreplebiscito character varying(32) NOT NULL,
  nombretendencia character varying(32) NOT NULL,
  votos integer,
  CONSTRAINT resultados_pkey PRIMARY KEY (nombreplebiscito, nombretendencia),
  CONSTRAINT resultados_nombreplebiscito_fkey FOREIGN KEY (nombreplebiscito)
      REFERENCES plebiscito (nombreplebiscito) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE resultados
  OWNER TO postgres;

CREATE TABLE tendencia
(
  nombretendencia character varying(32) NOT NULL,
  nombreplebiscito character varying(32) NOT NULL,
  representante integer,
  descripcion character varying(256) NOT NULL,
  pagina character varying(64),
  contacto character varying(32),
  informacionadicional character varying(256),
  CONSTRAINT tendencia_pkey PRIMARY KEY (nombretendencia, nombreplebiscito),
  CONSTRAINT tendencia_nombreplebiscito_fkey FOREIGN KEY (nombreplebiscito)
      REFERENCES plebiscito (nombreplebiscito) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT tendencia_representante_fkey FOREIGN KEY (representante)
      REFERENCES usuario (cedula) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tendencia
  OWNER TO postgres;
GRANT ALL ON TABLE tendencia TO public;
GRANT ALL ON TABLE tendencia TO postgres;

CREATE TABLE usuario
(
  cedula integer NOT NULL,
  nombreusuario character varying(32) NOT NULL,
  password character varying(24) NOT NULL,
  nombre character varying(32) NOT NULL,
  apellido1 character varying(32) NOT NULL,
  apellido2 character varying(32) NOT NULL,
  distrito character varying(32) NOT NULL,
  canton character varying(32) NOT NULL,
  provincia character varying(32) NOT NULL,
  fechanacimiento date NOT NULL,
  sexo character(1) NOT NULL,
  CONSTRAINT usuario_pkey PRIMARY KEY (cedula),
  CONSTRAINT usuario_nombreusuario_key UNIQUE (nombreusuario)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE usuario
  OWNER TO postgres;
GRANT ALL ON TABLE usuario TO public;
GRANT ALL ON TABLE usuario TO postgres;

CREATE TABLE votacion
(
  nombreplebiscito character varying(32) NOT NULL,
  nombretendencia character varying(32) NOT NULL,
  cedula integer NOT NULL,
  CONSTRAINT votacion_pkey PRIMARY KEY (nombreplebiscito, nombretendencia, cedula),
  CONSTRAINT votacion_cedula_fkey FOREIGN KEY (cedula)
      REFERENCES usuario (cedula) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT votacion_nombreplebiscito_fkey FOREIGN KEY (nombreplebiscito, nombretendencia)
      REFERENCES tendencia (nombreplebiscito, nombretendencia) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE votacion
  OWNER TO postgres;
GRANT ALL ON TABLE votacion TO public;
GRANT ALL ON TABLE votacion TO postgres;





CREATE OR REPLACE FUNCTION addResult() RETURNS TRIGGER AS $addResult$
	DECLARE
	nombretendencia varchar :=NEW.nombretendencia;
	nombreplebiscito varchar :=NEW.nombreplebiscito;
	BEGIN
	INSERT INTO resultados VALUES(nombreplebiscito,nombretendencia,0);
	RETURN NEW;
	END;
$addResult$ LANGUAGE plpgsql;

CREATE TRIGGER addResult BEFORE INSERT
	ON tendencia FOR EACH ROW
	EXECUTE PROCEDURE addResult();
	
	
	
CREATE OR REPLACE FUNCTION sumResult() RETURNS TRIGGER AS $sumResult$
	DECLARE
	nt varchar :=NEW.nombretendencia;
	np varchar :=NEW.nombreplebiscito;
	BEGIN
	UPDATE resultados
	SET votos=votos+1
	WHERE nombretendencia=nt
	AND nombreplebiscito=np;
	RETURN NEW;
	END;
$sumResult$ LANGUAGE plpgsql;

CREATE TRIGGER sumResult BEFORE INSERT
	ON votacion FOR EACH ROW
	EXECUTE PROCEDURE sumResult();	
