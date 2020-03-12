CREATE OR ALTER FUNCTION [dbo].[GetBirthdayByUserId]
(
	@UserId varchar(50)
)
returns varchar(10)
as
begin
	DECLARE	@Birthday date = 
	(
		SELECT	B.Birthday
		FROM	Birthdays B
		WHERE	B.UserId = @UserId
	)
	DECLARE	@Month varchar(10) = DATEPART(month,@Birthday)
	DECLARE	@Day varchar(10) = DATEPART(day,@Birthday)

	RETURN @Month + '/' + @Day
end
GO


