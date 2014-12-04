#PushProjectionDown
Select TableA.ID, TableB.Name,TableC.Grade,TableB.Grade, TableB.ID, TableC.ID, TableA.Name
From TableA, TableB, TableC
Where TableA.ID = TableB.ID
And TableA.Name = TableC.Name