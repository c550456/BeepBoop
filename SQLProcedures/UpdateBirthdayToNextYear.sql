CREATE OR ALTER PROCEDURE [dbo].[UpdateBirthdayToNextYear]
	@UserId varchar(100)
as
BEGIN

	UPDATE	B
	SET		B.Birthday = DATEADD(year,1,B.Birthday)
	FROM	Birthdays B
	WHERE	B.UserId = @UserId

END
GO


