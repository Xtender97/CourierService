
CREATE TRIGGER TR_TransportOfferPrice
   ON Paketi
   AFTER INSERT,UPDATE
AS 
BEGIN
	
	declare @cursor cursor
	declare @idPreuz int, @idDost int, @tip int, @idP int, @x1 int, @x2 int, @y1 int, @y2 int
	declare @tezina decimal(10,3)

	declare @distance decimal(10,3), @price decimal(10,3)
	declare @startPrice int, @priceByKg int

	set @cursor = cursor for 
	select idP, idAPreuzimanja, idADostavljanja, TipPaketa, tezina
	from inserted

	open @cursor

	fetch next from @cursor
	into @idP, @idPreuz, @idDost, @tip, @tezina

	while @@FETCH_STATUS = 0
	begin

		select @x1 = xcord, @y1 = ycord
		from adrese
		where idA = @idPreuz

		select @x2 = xcord, @y2 = ycord
		from adrese
		where idA = @idDost

		set @distance = SQRT((@x1-@x2)*(@x1-@x2) + (@y1-@y2)*(@y1-@y2))

		print(@distance)

		set @startPrice = CASE @tip  
         WHEN 0 THEN 115 
         WHEN 1 THEN 175  
         WHEN 2 THEN 250  
         WHEN 3 THEN 350
		 END

		 set @priceByKg = CASE @tip  
         WHEN 0 THEN 0 
         WHEN 1 THEN 100  
         WHEN 2 THEN 100  
         WHEN 3 THEN 500
		 END

		 set @price = (@startPrice + @tezina * @priceByKg) * @distance;

		 print(@price)

		 update Paketi set cena = @price where idP = @idP


		fetch next from @cursor
		into @idP, @idPreuz, @idDost, @tip, @tezina
	end


	close @cursor
	deallocate @cursor

END
GO
