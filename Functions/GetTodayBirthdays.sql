CREATE OR ALTER	FUNCTION [dbo].[GetTodayBirthdays]()
RETURNS varchar(max)
AS
BEGIN

DECLARE	@Users varchar(max)

SELECT	@Users = STRING_AGG(B.UserId,',')
FROM	Birthdays B
WHERE	B.Birthday = CAST(GETUTCDATE() as date)

RETURN	@Users

END
GO


