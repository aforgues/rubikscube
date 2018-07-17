@author: Arnaud Forgues

This is a simple java application for Rubik's Cube.

Including
-
- 2x2, 3x3, 4x4... NxN manipulation with ascii, 2D display and beta 3D display (powered by JMonkeyEngine)
- keyboard and mouse moves
- AI implementation for 3x3 configuration, inspired by http://www.chessandpoker.com/rubiks-cube-solution.html

Roadmap / backlog (with no priority)
-
- On going => add 3D web display 
- add more Unit / integration tests
- optimize AI with more OO code
- add rest API (play framework or spring boot ?)
- add Android version ? 
- add iOS version with spriteKit (2D) or sceneKit (3D)
- display AI moves ? 

3D display keyboard commands
-
Global keys
- ESC : quit the game
- P key input : rotate all the rubiksCube around X axis (Pitch)
- Y key input : rotate all the rubiksCube around Y axis (Yaw)
- R key input : rotate all the rubiksCube around Z axis (Roll)

Face rotations keys

- E key input : rotate the first face (at the left) of the rubiksCube around X axis (Pitch)
- D key input : rotate the second face of the rubiksCube around X axis (Pitch)
- C key input : rotate the third face (at the right) of the rubiksCube around X axis (Pitch) 
- A key input : rotate the first face (at the top) of the rubiksCube around Y axis (Yaw)
- Q key input : rotate the second face of the rubiksCube around Y axis (Yaw)
- W key input : rotate the third face (at the bottom) of the rubiksCube around Y axis (Yaw)
- Z key input : rotate the first face (at the front  - the nearest from you) of the rubiksCube around Z axis (Roll)
- S key input : rotate the second face of the rubiksCube around Z axis (Roll)
- X key input : rotate the third face (at the back) of the rubiksCube around Z axis (Roll)