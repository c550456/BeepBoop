CREATE OR ALTER PROCEDURE [dbo].[AddBirthday]
	@Birthday date,
	@UserId varchar(50)
as
BEGIN
	SET NOCOUNT ON

	BEGIN TRY
		INSERT	Birthdays(UserId,Birthday)
		VALUES	(@UserId,@Birthday)
	END TRY
	BEGIN CATCH
		DECLARE	@message varchar(300) = ERROR_MESSAGE()
		RAISERROR(@message,6,1)
		--SELECT	ERROR_NUMBER() as ErrorNumber,
		--		ERROR_MESSAGE() as ErrorMessage
	END CATCH
END
GO


