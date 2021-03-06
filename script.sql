USE [KurirskaSluzba]
GO
SET IDENTITY_INSERT [dbo].[Gradovi] ON 

INSERT [dbo].[Gradovi] ([IdG], [Naziv], [PostanskiBroj]) VALUES (1, N'Beograd', N'11000')
INSERT [dbo].[Gradovi] ([IdG], [Naziv], [PostanskiBroj]) VALUES (2, N'Novi Sad', N'22000')
SET IDENTITY_INSERT [dbo].[Gradovi] OFF
GO
SET IDENTITY_INSERT [dbo].[Adrese] ON 

INSERT [dbo].[Adrese] ([IdA], [Ulica], [Broj], [Xcord], [Ycord], [IdG]) VALUES (1, N'Beogradska', N'11', 2, 2, 1)
INSERT [dbo].[Adrese] ([IdA], [Ulica], [Broj], [Xcord], [Ycord], [IdG]) VALUES (2, N'Novosadska', N'22', 4, 4, 2)
INSERT [dbo].[Adrese] ([IdA], [Ulica], [Broj], [Xcord], [Ycord], [IdG]) VALUES (3, N'Boegradska Druga', N'15', 3, 3, 1)
SET IDENTITY_INSERT [dbo].[Adrese] OFF
GO
SET IDENTITY_INSERT [dbo].[Korisnici] ON 

INSERT [dbo].[Korisnici] ([IdK], [Ime], [Prezime], [KorisnickoIme], [Sifra], [IdA]) VALUES (1, N'Milan', N'Boskovic', N'Xtender', N'password', 3)
SET IDENTITY_INSERT [dbo].[Korisnici] OFF
GO
