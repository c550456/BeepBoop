CREATE OR ALTER FUNCTION [dbo].[GetBirthdaySettings]()

RETURNS @BirthdaySettings TABLE
		(
			BirthdayChannelId varchar(100),
			BirthdayRoleId varchar(100)
		)
AS
BEGIN

INSERT	@BirthdaySettings(BirthdayChannelId,BirthdayRoleId)
SELECT	(
			SELECT	G.GuildSettingValue
			FROM	GuildSettings G
			WHERE	G.GuildSettingKey = 'BirthdayChannelId'
		),
		(
			SELECT	G.GuildSettingValue
			FROM	GuildSettings G
			WHERE	G.GuildSettingKey = 'BirthdayRoleId'
		)

RETURN	

END
GO


