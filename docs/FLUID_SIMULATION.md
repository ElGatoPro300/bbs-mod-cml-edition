# Fluid Simulation Module for BBS Mod

## Overview
This module integrates a real-time fluid simulation system into the BBS Mod, specifically targeting the Billboard Form. It allows for dynamic, interactive fluid effects on billboards, responding to entity movement and configuration settings.

## User Guide

### Enabling Fluid Simulation
1.  Open the **Billboard Form** editor for your model.
2.  Navigate to the new **Fluid** section.
3.  Toggle **Enable Fluid** to activate the simulation.

### Configuration Settings
-   **Density**: Controls the "heaviness" of the fluid.
-   **Viscosity**: Controls how quickly waves die out.
    -   Value close to 1.0 (e.g., 0.99): Waves last a long time.
    -   Value lower (e.g., 0.90): Waves dissipate quickly.
-   **Sensitivity**: Controls how strongly the fluid reacts to interaction (forces).
-   **Color**: Sets the base color and transparency of the fluid overlay.

## Technical Specifications

### Algorithm
The simulation uses a 2D wave equation solved via finite difference method on a grid.
-   **Equation**: `u(x, t+1) = 2u(x, t) - u(x, t-1) + damping * (neighbors_avg - u(x, t))`
-   **Implementation**: Double-buffered state (`currentBuffer`, `previousBuffer`).
-   **Damping**: Applied via `viscosity` factor.

### Performance
-   **Grid Size**: Fixed at 32x32 for performance balance.
-   **Complexity**: O(W*H) per tick. With 32x32, this is negligible (~1000 operations).
-   **Rendering**: Uses bilinear interpolation to map the 32x32 grid to the billboard quad, rendered as a triangulated mesh.

## API Documentation

### `mchorse.bbs_mod.simulation.FluidSimulation`

#### Constructor
```java
public FluidSimulation(int width, int height)
```
Creates a new simulation grid.

#### Methods
-   `void update()`: Advances the simulation by one tick.
-   `void addForce(int x, int y, float force)`: Adds an instantaneous force at grid coordinates (x, y).
-   `float getHeight(int x, int y)`: Returns the current wave height at (x, y).

### Interaction
Interaction is handled in `BillboardFormRenderer.tick(IEntity entity)`.
-   Monitors entity position changes.
-   If movement > threshold, applies force to the center of the fluid grid.

## Testing
-   **Unit Tests**: `src/test/java/mchorse/bbs_mod/simulation/FluidSimulationTest.java`
-   **Interaction Tests**: `src/test/java/mchorse/bbs_mod/simulation/FluidInteractionTest.java`
-   **Benchmarks**: `src/test/java/mchorse/bbs_mod/simulation/FluidBenchmarkTest.java`
