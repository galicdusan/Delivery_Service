CREATE TRIGGER TR_TransportOffer
   ON  Paket
   AFTER INSERT,UPDATE
AS 
BEGIN
	SET NOCOUNT ON;
	declare @MyCursor Cursor
	declare @tipPaketa int, @tezina decimal(10,3), @idAdrOd int, @idAdrDo int
	declare @x1 int, @x2 int, @y1 int, @y2 int
	declare @cena decimal(10,3), @osnovnaCena decimal(10,3), @cenaPoKg decimal(10,3)
	declare @distance decimal(10,3), @idP int

    set @MyCursor = cursor for
	select TipPaketa, Tezina, IDAdresaOd, IDAdresaDo, IDPaket
	from inserted

	open @MyCursor

	fetch next from @MyCursor
	into @tipPaketa, @tezina, @idAdrOd, @idAdrDo, @idP

	while @@FETCH_STATUS = 0
	begin
		select @x1 = Xkoord, @y1 = Ykoord
		from Adresa
		where IDAdresa = @idAdrOd

		select @x2 = Xkoord, @y2 = Ykoord
		from Adresa
		where IDAdresa = @idAdrDo

		set @osnovnaCena = case 
		when @tipPaketa = 0 then 115.0
		when @tipPaketa = 1 then 175.0
		when @tipPaketa = 2 then 250.0
		when @tipPaketa = 3 then 350.0
		end
		
		set @cenaPoKg = case 
		when @tipPaketa = 0 then 0.0
		when @tipPaketa = 1 then 100.0
		when @tipPaketa = 2 then 100.0
		when @tipPaketa = 3 then 500.0
		end

		set @distance = sqrt( square( @x2 - @x1 ) + square( @y2 - @y1 ) )

		set @cena = (@osnovnaCena + @tezina * @cenaPoKg) * @distance
			
		update Paket
		set Cena = @cena
		where IDPaket = @idP

		fetch next from @MyCursor
		into @tipPaketa, @tezina, @idAdrOd, @idAdrDo, @idP
	end

	close @MyCursor
	deallocate @MyCursor

END
GO





CREATE PROCEDURE spDeleteAllData
AS
BEGIN
	SET NOCOUNT ON;

    EXEC sp_MSForEachTable 'DISABLE TRIGGER ALL ON ?'

	EXEC sp_MSForEachTable 'ALTER TABLE ? NOCHECK CONSTRAINT ALL'
	
	EXEC sp_MSForEachTable 'DELETE FROM ?'
	
	EXEC sp_MSForEachTable 'ALTER TABLE ? CHECK CONSTRAINT ALL'
	
	EXEC sp_MSForEachTable 'ENABLE TRIGGER ALL ON ?'
	
END
GO





CREATE PROCEDURE spGetAvailableCar
	@idK int,
	@idV int output,
	@idM int output
AS
BEGIN
	SET NOCOUNT ON;
	declare @num int, @idG int
	
	select @num = count(*) from Voznja where IDKorisnik = @idK and StatusVoznje = 1
	if(@num <> 0)
	begin
		set @idV = -1;
	end
	else
	begin
		select @idG = A.IDGrad from Adresa A, Korisnik K where K.IDAdresa = A.IDAdresa and K.IDKorisnik = @idK
		select @idM = M.IDMagacin from Magacin M, Adresa A where M.IDAdresa = A.IDAdresa and A.IDGrad = @idG
		select top 1 @idV = IDVozilo from Parkiran where IDMagacin = @idM
	end

END
GO