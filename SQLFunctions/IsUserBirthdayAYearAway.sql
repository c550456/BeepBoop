CREATE OR ALTER FUNCTION [dbo].[IsUserBirthdayAYearAway]
(
	@UserId varchar(100)
) returns bit
as
BEGIN
	/*---------------------------------------------
	--DEBUG
	DECLARE	@UserId varchar(100) = '165534549135196160'
	---------------------------------------------*/
	
	DECLARE	@IsAYearAway bit = 0;
	DECLARE	@UserBirthday date = 
			(
				SELECT	B.Birthday
				FROM	Birthdays B
				WHERE	B.UserId = @UserId
			)
	
	SET	@IsAYearAway = 
		CASE
			WHEN @UserBirthday = CAST(DATEADD(year,1,GETUTCDATE()) as date)
				THEN 1
			ELSE 0
		END

	--SELECT	@IsAYearAway
	RETURN	@IsAYearAway;
END
GO


