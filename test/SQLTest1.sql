#PushSelectionDown
Select TableA.ID
From TableA, TableB
Where TableA.ID = TableB.ID
And TableA.ID = "100"
And TableA.Name = "Thang"