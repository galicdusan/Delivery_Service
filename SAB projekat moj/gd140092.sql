
CREATE TYPE [DecimalniBroj]
	FROM DECIMAL(10,3) NULL
go

CREATE TYPE [TekstualnoPolje]
	FROM VARCHAR(100) NULL
go

CREATE TABLE [Administrator]
( 
	[IDKorisnik]         integer  NOT NULL 
)
go

ALTER TABLE [Administrator]
	ADD CONSTRAINT [XPKAdministrator] PRIMARY KEY  CLUSTERED ([IDKorisnik] ASC)
go

CREATE TABLE [Adresa]
( 
	[IDAdresa]           integer  IDENTITY  NOT NULL ,
	[Ulica]              [TekstualnoPolje]  NOT NULL ,
	[Broj]               integer  NOT NULL 
	CONSTRAINT [VeceOdNule]
		CHECK  ( Broj >= 1 ),
	[IDGrad]             integer  NOT NULL ,
	[Xkoord]             integer  NOT NULL ,
	[Ykoord]             integer  NOT NULL 
)
go

ALTER TABLE [Adresa]
	ADD CONSTRAINT [XPKAdresa] PRIMARY KEY  CLUSTERED ([IDAdresa] ASC)
go

CREATE TABLE [Grad]
( 
	[IDGrad]             integer  IDENTITY  NOT NULL ,
	[Naziv]              [TekstualnoPolje]  NOT NULL ,
	[PostanskiBroj]      [TekstualnoPolje]  NOT NULL 
)
go

ALTER TABLE [Grad]
	ADD CONSTRAINT [XPKGrad] PRIMARY KEY  CLUSTERED ([IDGrad] ASC)
go

CREATE TABLE [Korisnik]
( 
	[IDKorisnik]         integer  IDENTITY  NOT NULL ,
	[Ime]                [TekstualnoPolje]  NOT NULL ,
	[Prezime]            [TekstualnoPolje]  NOT NULL ,
	[Sifra]              [TekstualnoPolje]  NOT NULL ,
	[IDAdresa]           integer  NOT NULL ,
	[KorisnickoIme]      [TekstualnoPolje]  NOT NULL 
)
go

ALTER TABLE [Korisnik]
	ADD CONSTRAINT [XPKKorisnik] PRIMARY KEY  CLUSTERED ([IDKorisnik] ASC)
go

ALTER TABLE [Korisnik]
	ADD CONSTRAINT [KorImeUnique] UNIQUE ([KorisnickoIme]  ASC)
go

CREATE TABLE [Kurir]
( 
	[IDKorisnik]         integer  NOT NULL ,
	[BrojVozackeDozvole] [TekstualnoPolje]  NOT NULL ,
	[BrojIsporucenihPaketa] integer  NULL ,
	[OstvarenProfit]     [DecimalniBroj] 
)
go

ALTER TABLE [Kurir]
	ADD CONSTRAINT [XPKKurir] PRIMARY KEY  CLUSTERED ([IDKorisnik] ASC)
go

CREATE TABLE [Magacin]
( 
	[IDMagacin]          integer  IDENTITY  NOT NULL ,
	[IDAdresa]           integer  NOT NULL 
)
go

ALTER TABLE [Magacin]
	ADD CONSTRAINT [XPKMagacin] PRIMARY KEY  CLUSTERED ([IDMagacin] ASC)
go

CREATE TABLE [Paket]
( 
	[IDPaket]            integer  IDENTITY  NOT NULL ,
	[IDKorisnik]         integer  NOT NULL ,
	[IDAdresaDo]         integer  NOT NULL ,
	[IDAdresaOd]         integer  NOT NULL ,
	[TipPaketa]          integer  NOT NULL 
	CONSTRAINT [IzmedjuNuleITri]
		CHECK  ( TipPaketa BETWEEN 0 AND 3 ),
	[Tezina]             [DecimalniBroj]  NOT NULL 
	CONSTRAINT [Validation_Rule_414_1229718275]
		CHECK  ( Tezina >= 1 ),
	[StatusIsporuke]     integer  NULL 
	CONSTRAINT [OdNuleDoCetiri]
		CHECK  ( StatusIsporuke BETWEEN 0 AND 4 ),
	[Cena]               [DecimalniBroj] ,
	[VremeKreiranja]     datetime  NULL ,
	[VremePrihvatanja]   datetime  NULL ,
	[IDLokacija]         integer  NOT NULL 
)
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [XPKPaket] PRIMARY KEY  CLUSTERED ([IDPaket] ASC)
go

CREATE TABLE [Parkiran]
( 
	[IDVozilo]           integer  NOT NULL ,
	[IDMagacin]          integer  NOT NULL 
)
go

ALTER TABLE [Parkiran]
	ADD CONSTRAINT [XPKParkiran] PRIMARY KEY  CLUSTERED ([IDVozilo] ASC,[IDMagacin] ASC)
go

CREATE TABLE [PlanRute]
( 
	[IDVoznja]           integer  NOT NULL ,
	[RedniBroj]          integer  NOT NULL,
	[IDPaket]	integer NOT NULL,
	[Tip]		integer NULL,
	[Xkoord]	integer NULL,
	[Ykoord]	integer NULL 
)
go

ALTER TABLE [PlanRute]
	ADD CONSTRAINT [XPKPlanRute] PRIMARY KEY  CLUSTERED ([IDVoznja] ASC,[RedniBroj] ASC)
go

CREATE TABLE [Prenosi]
( 
	[IDVoznja]           integer  NOT NULL ,
	[IDPaket]            integer  NOT NULL 
)
go

ALTER TABLE [Prenosi]
	ADD CONSTRAINT [XPKPrenosi] PRIMARY KEY  CLUSTERED ([IDVoznja] ASC,[IDPaket] ASC)
go

CREATE TABLE [PrijavaZaKurira]
( 
	[IDPrijavaZK]        integer  IDENTITY  NOT NULL ,
	[BrojVozackeDozvole] [TekstualnoPolje]  NOT NULL ,
	[IDPrijavitelj]      integer  NOT NULL 
)
go

ALTER TABLE [PrijavaZaKurira]
	ADD CONSTRAINT [XPKPrijavaZaKurira] PRIMARY KEY  CLUSTERED ([IDPrijavaZK] ASC)
go

ALTER TABLE [PrijavaZaKurira]
	ADD CONSTRAINT [BrVozDozvUnique] UNIQUE ([BrojVozackeDozvole]  ASC)
go

CREATE TABLE [Vozilo]
( 
	[IDVozilo]           integer  IDENTITY  NOT NULL ,
	[RegBroj]            [TekstualnoPolje]  NOT NULL ,
	[TipGoriva]          integer  NOT NULL 
	CONSTRAINT [IzmedjuNuleIDva]
		CHECK  ( TipGoriva BETWEEN 0 AND 2 ),
	[Potrosnja]          [DecimalniBroj]  NOT NULL 
	CONSTRAINT [Validation_Rule_414_538997909]
		CHECK  ( Potrosnja >= 1 ),
	[Nosivost]           [DecimalniBroj]  NOT NULL 
	CONSTRAINT [Validation_Rule_414_1091770243]
		CHECK  ( Nosivost >= 1 )
)
go

ALTER TABLE [Vozilo]
	ADD CONSTRAINT [XPKVozilo] PRIMARY KEY  CLUSTERED ([IDVozilo] ASC)
go

CREATE TABLE [Voznja]
( 
	[IDVozilo]           integer  NOT NULL ,
	[IDKorisnik]         integer  NOT NULL ,
	[IDVoznja]           integer  IDENTITY  NOT NULL ,
	[StatusVoznje]       integer  NULL 
	CONSTRAINT [Validation_Rule_431_1634626119]
		CHECK  ( StatusVoznje BETWEEN 0 AND 1 ),
	[OstvarenProfit]     [DecimalniBroj],
	[Xkoord]	integer NULL,
	[Ykoord]	integer NULL 
)
go

ALTER TABLE [Voznja]
	ADD CONSTRAINT [XPKVoznja] PRIMARY KEY  CLUSTERED ([IDVoznja] ASC)
go


ALTER TABLE [Administrator]
	ADD CONSTRAINT [R_7] FOREIGN KEY ([IDKorisnik]) REFERENCES [Korisnik]([IDKorisnik])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Adresa]
	ADD CONSTRAINT [R_1] FOREIGN KEY ([IDGrad]) REFERENCES [Grad]([IDGrad])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Korisnik]
	ADD CONSTRAINT [R_3] FOREIGN KEY ([IDAdresa]) REFERENCES [Adresa]([IDAdresa])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Kurir]
	ADD CONSTRAINT [R_6] FOREIGN KEY ([IDKorisnik]) REFERENCES [Korisnik]([IDKorisnik])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Magacin]
	ADD CONSTRAINT [R_2] FOREIGN KEY ([IDAdresa]) REFERENCES [Adresa]([IDAdresa])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Paket]
	ADD CONSTRAINT [R_12] FOREIGN KEY ([IDKorisnik]) REFERENCES [Korisnik]([IDKorisnik])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [R_13] FOREIGN KEY ([IDAdresaDo]) REFERENCES [Adresa]([IDAdresa])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [R_14] FOREIGN KEY ([IDAdresaOd]) REFERENCES [Adresa]([IDAdresa])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [R_25] FOREIGN KEY ([IDLokacija]) REFERENCES [Adresa]([IDAdresa])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Parkiran]
	ADD CONSTRAINT [R_16] FOREIGN KEY ([IDVozilo]) REFERENCES [Vozilo]([IDVozilo])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Parkiran]
	ADD CONSTRAINT [R_20] FOREIGN KEY ([IDMagacin]) REFERENCES [Magacin]([IDMagacin])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [PlanRute]
	ADD CONSTRAINT [R_23] FOREIGN KEY ([IDVoznja]) REFERENCES [Voznja]([IDVoznja])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [PlanRute]
	ADD CONSTRAINT [R_135] FOREIGN KEY ([IDPaket]) REFERENCES [Paket]([IDPaket])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Prenosi]
	ADD CONSTRAINT [R_21] FOREIGN KEY ([IDVoznja]) REFERENCES [Voznja]([IDVoznja])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Prenosi]
	ADD CONSTRAINT [R_22] FOREIGN KEY ([IDPaket]) REFERENCES [Paket]([IDPaket])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [PrijavaZaKurira]
	ADD CONSTRAINT [R_8] FOREIGN KEY ([IDPrijavitelj]) REFERENCES [Korisnik]([IDKorisnik])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Voznja]
	ADD CONSTRAINT [R_10] FOREIGN KEY ([IDVozilo]) REFERENCES [Vozilo]([IDVozilo])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Voznja]
	ADD CONSTRAINT [R_11] FOREIGN KEY ([IDKorisnik]) REFERENCES [Kurir]([IDKorisnik])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go
