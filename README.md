# Map Zones
A Zone item is required to view zones

Able to specify zone entities:


Zone corner: (Done)
- left/right click for corner 1/2
- Settings:
    - edit y/n
- if edit:
  - right click zone to specify targets if unspecified
  - shift right click to clear

Zone corner (Attach to entities): (postponed)
- see zone corner

Zone anchor: (postponed)
position changes reflect onto zone (and connected zone anchors):
- right click zone to specify target if unspecified
- shift right click into air to clear
- works for exclusion zones, too

Zone exclusion corner: (scrapped)

same behavior as zone, but excludes behavior from target zone
- right click zone to specify targets if unspecified
- shift right click to clear

Zone exclusion corner (attach to entities): (scrapped)

see Zone exclusion corner

Zone Wrench: (done)
edit zone properties:
- enter command (execute as entity entering)
- exit command (execute as entity leaving)
- Game mode? -> Very risky

TODO before release:
- [X] Investigate permission APIs => use lucko's fabric-permissions-api-v0
- [X] Implement permission checks for
  - [X] moving corners
  - [X] creating zones
  - [X] adding/deleting/editing commands (depending on commands)
