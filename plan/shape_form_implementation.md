# ShapeForm Implementation Plan

## 1. Core Data Structure (`ShapeForm`)
**Location:** `src/main/java/mchorse/bbs_mod/forms/forms/ShapeForm.java`

This class defines the properties of the volumetric shape.

### Properties
*   **Geometry:**
    *   `type` (`ValueEnum<ShapeType>`): `SPHERE`, `BOX`, `CYLINDER`, `CAPSULE`.
    *   `size` (`ValueVector3` or `ValueFloat` x/y/z): Dimensions of the shape.
    *   `subdivisions` (`ValueInt`): Quality/Vertex count (important for displacement).
*   **Appearance:**
    *   `color` (`ValueColor`): Base RGBA color.
    *   `texture` (`ValueLink`): Resource location for the texture.
    *   `textureScale` (`ValueFloat`): UV scaling factor.
    *   `textureScroll` (`ValueVector2`): UV animation speed (X, Y).
*   **Node-Based Shader Graph:**
    *   `shaderGraph` (`ValueGraph`): A data structure storing nodes (Input, Math, Texture, Output) and their connections.
    *   *Note:* This replaces the fixed `mode` Enum. The form will serialize a JSON representation of the shader nodes.

## 2. Rendering System (`ShapeFormRenderer`)
**Location:** `src/client/java/mchorse/bbs_mod/forms/renderers/ShapeFormRenderer.java`

Handles the 3D rendering of the form using a dynamic shader pipeline.

### Implementation Details
*   **Mesh Generation:**
    *   Use `GeometryBuilder` to generate VBOs for Sphere/Box/Cylinder.
    *   Cache meshes.
*   **Dynamic Shader Compilation:**
    *   **Graph-to-GLSL Transpiler:** A system that takes the `shaderGraph` data and generates a valid GLSL fragment shader string at runtime.
    *   **Shader Caching:** Compiled shaders must be cached by a hash of the graph to avoid recompiling every frame.
    *   **Uniform Management:** Automatically extract uniform inputs (like `Time`, `Color`) from the graph and bind them during rendering.

## 3. UI & Editor Integration: Node Editor
**Location:** `src/client/java/mchorse/bbs_mod/ui/forms/editors/forms/UIShapeForm.java` & `src/client/java/mchorse/bbs_mod/ui/node/UINodeEditor.java`

A Blender-like node editor for visual shader programming.

### UI Components
*   **Node Canvas:** A pannable/zoomable area to place nodes.
*   **Nodes:**
    *   **Input Nodes:** `Time`, `Position`, `UV`, `Normal`, `Camera Dir`.
    *   **Data Nodes:** `Float`, `Color`, `Vector3`, `Texture`.
    *   **Math Nodes:** `Add`, `Multiply`, `Sine`, `Cosine`, `Mix`, `Noise` (Perlin/Simplex), `Fresnel`.
    *   **Output Node:** `Master` (Final Color, Alpha, Displacement).
*   **Connections:** Bezier curves connecting Output sockets to Input sockets.
*   **Live Preview:** The 3D viewport should update the shader in real-time as nodes are connected.

## 4. Registration
*   **Backend:** Register `ShapeForm` in `FormArchitect`.
*   **Frontend:** Register `ShapeFormRenderer` in `FormRenderer` map.
*   **UI:** Register `UIShapeForm` in `UIForms`.

## 5. Development Steps
1.  **Core:** Define `ShapeForm.java` with a placeholder for graph data.
2.  **UI Base:** Create a generic `UINodeEditor` class (drag, zoom, connect logic).
3.  **UI Implementation:** Implement `UIShapeForm` using `UINodeEditor`.
4.  **Shader Gen:** Implement the logic to traverse the node graph and generate GLSL code.
5.  **Renderer:** Hook up the generated shader to `ShapeFormRenderer`.
6.  **Nodes:** Implement essential nodes (`Texture`, `Noise`, `Mix`, `Output`).
