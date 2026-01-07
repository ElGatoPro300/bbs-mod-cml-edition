# BBS Cml: IK & FK System Implementation Plan

This document tracks the progress of adding Inverse Kinematics (IK) and Forward Kinematics (FK) systems to the BBS Cml fork.

## Overview
The goal is to enable professional animation capabilities within BBS by leveraging Blockbench's existing features and extending the BBS runtime.
**Key Requirement**: Anchor Forms must work as controllers via the **Body Part** system, supporting Replays and Entity/Model actors.

---

## Phase 1: IK System (Blockbench -> BBS)
**Objective**: Export Blockbench IK chains and implement a runtime solver in BBS that uses Anchor Forms as targets.

### 1.1 Blockbench Plugin Update (`bbs_exporter.js`)
- [x] **Analyze Null Objects**: Modify exporter to traverse `NullObject` elements in the Outliner.
- [x] **Extract Chains**: Identify Null Objects that have `ik_target` (effector) and `ik_source` (root) properties set.
- [x] **Resolve UUIDs**: Convert Blockbench internal UUIDs for target/source bones into BBS-compatible Group Names.
- [x] **Export JSON**: Inject a `kinematics` object into the `.bbs.json` output:
    ```json
    "kinematics": {
      "ik_chains": [
        {
          "name": "leg_left_ctrl",
          "root": "thigh_left",
          "effector": "foot_left",
          "solver": "2_bone" 
        }
      ]
    }
    ```

### 1.2 BBS Runtime Implementation (Java)
- [ ] **Data Loading**: Update BBS Model Loader to parse the new `kinematics` section from `.bbs.json`.
- [ ] **IK Solver**: Implement a lightweight IK solver (CCD or FABRIK) in the mod's animation loop.
- [ ] **Main feature**: Null object will appear in model's bone list, and have a visual circle that could turn on/off. The null object in bone list should work with transform like any other bone. It's work like in blockbench
- [ ] **Anchor Form Update (custom for the user)**:
    - Add `IK Mode` to Anchor Forms.
    - Allow selecting a target **IK Chain** (e.g., "leg_left_ctrl") defined in the parent model.
- [ ] **Body Part Integration for anchor form**:
    - Ensure Anchor Forms attached as **Body Parts** are recognized as valid IK Targets by the parent Replay/Entity.
    - **Logic**: `Parent Model -> checks Body Parts -> if Body Part is Anchor Form & has IK Chain ID -> Apply Solver`.

---

## Phase 2: FK System (Blockbench Export & Animation)
**Objective**: Implement an FK (Forward Kinematics) Control System in Blockbench and ensure all Kinematic animations (IK & FK) are exported to BBS.

### 2.1 FK Control System in Blockbench
*Since Blockbench lacks a dedicated "FK Controller" concept (distinct from geometry), we will add it via the plugin.*
- [ ] **"Create FK Control" Tool**: Add a button in the plugin to generate an "FK Handle" (Null Object) for a selected bone.
    - *Logic*: Creates a Null Object aligned with the bone, potentially parenting it or setting up a logical link.
- [ ] **FK Data Structure**: Define the link between the **FK Handle** (Null Object) and the **Controlled Bone**.
    - Store this in the Null Object's properties or a global registry in the model.

### 2.2 Animation Export (IK & FK)
*The current exporter only supports Groups/Cubes. We must enable Null Object animation export.*
- [ ] **Export Null Objects**: Modify `bbs_exporter.js` to include Null Objects (that act as IK/FK controls) in the exported hierarchy or as a separate `controllers` list.
- [ ] **Export Controller Animations**: Ensure animations applied to these Null Objects (Position/Rotation) are written to the `.bbs.json` animation tracks.
    - *Goal*: When an animation plays in BBS, the "virtual" IK/FK targets move, and the runtime solver forces the mesh to follow them.

---

## Phase 3: FK Support (BBS Runtime)
**Objective**: Allow Anchor Forms to override specific bone rotations in World Space or Local Space.

### 3.1 Runtime Logic
- [ ] **FK Mode**: Update Anchor Form to have an `FK Mode`.
- [ ] **Bone Mapping**: Allow user to select which bone this Anchor Form controls (filtered by the `fk_controllers` list from the model).
- [ ] **Override System**:
    - In the animation pipeline (after animations are applied), check for active FK Anchor Forms.
    - Overwrite the target bone's rotation/position with the Anchor Form's transform.
    - **Space Conversion**: Handle the math to convert the Anchor Form's "World/BodyPart Space" rotation into the "Bone Local Space" required by the model.

---

## Future / Polish
- [ ] **Visual Debugging**: Draw lines in-game between IK Roots, Effectors, and Targets.
- [ ] **Pole Vectors**: Add support for a secondary Anchor Form to control knee/elbow direction.
