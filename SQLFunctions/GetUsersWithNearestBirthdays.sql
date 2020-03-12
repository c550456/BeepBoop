CREATE OR ALTER	FUNCTION [dbo].[GetUsersWithNearestBirthdays]()
RETURNS varchar(max)
as
begin

DECLARE	@NearestDay date = 
(
	SELECT	TOP 1
			B.Birthday
	FROM	Birthdays B
	GROUP 
	BY		B.Birthday
	ORDER
	BY		B.Birthday ASC
);

DECLARE	@UserIdList varchar(max);

SET	@UserIdList =
(
	SELECT	STRING_AGG(B.UserId,',')
	FROM	Birthdays B
	WHERE	B.Birthday = @NearestDay
)
RETURN	@UserIdList;

end
GO


